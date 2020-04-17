package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    private QuoteDao quoteDao;
    private MarketDataDao marketDataDao;

    @Autowired
    public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao){
        this.quoteDao = quoteDao;
        this.marketDataDao = marketDataDao;
    }

    /**
     * Update quote table against IEX source.
     *
     * @throws ResourceNotFoundException if the ticker is not found from IEX.
     * @throws DataAccessException if unable to retrieve data.
     * @throws IllegalArgumentException for invalid input.
     */
    public void updateMarketData(){
        List<Quote> quotes = new ArrayList<Quote>();
        quotes = quoteDao.findAll();

        // Get all quotes from the database.
        for (Quote quote: quotes){
            String ticker;

            // For each ticker, obtain IEX quote.
            try {
                ticker = quote.getTicker();
                IexQuote iexQuote = findIexQuoteByTicker(ticker);
                quote = buildQuoteFromIexQuote(iexQuote);
            } catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Cannot update entry due to invalid ticker.");
            } catch (DataAccessException e){
                throw new RuntimeException("Failed to retrieve data from IEX.");
            }

            quoteDao.save(quote);
        }
    }

    /**
     * Helper method. Map an IEX Quote to a Quote entity.
     * @param iexQuote
     * @return Quote
     */
    protected static Quote buildQuoteFromIexQuote(IexQuote iexQuote){
        Quote quote = new Quote();

        Double askPrice = iexQuote.getIexAskPrice();
        Integer askSize = Math.toIntExact(iexQuote.getIexAskSize());
        Double bidPrice = iexQuote.getIexBidPrice();
        Integer bidSize = Math.toIntExact(iexQuote.getIexBidSize());
        Double lastPrice = iexQuote.getLatestPrice();

        if (askPrice == null){
            quote.setAskPrice(0d);
        } else {
            quote.setAskPrice(askPrice);
        }
        if (askSize == null){
            quote.setAskSize(0);
        } else {
            quote.setAskSize(askSize);
        }
        if (bidPrice == null){
            quote.setBidPrice(0d);
        } else {
            quote.setBidPrice(bidPrice);
        }
        if (bidSize == null){
            quote.setBidSize(0);
        } else {
            quote.setBidSize(bidSize);
        }
        if (lastPrice == null){
            quote.setLastPrice(0d);
        } else {
            quote.setLastPrice(lastPrice);
        }

        return quote;
    }

    /**
     * Find an IEX Quote.
     * @param ticker id
     * @return IexQuote object
     * @throws IllegalArgumentException if ticker is invalid.
     */
    public IexQuote findIexQuoteByTicker(String ticker){
        return marketDataDao.findById(ticker)
                .orElseThrow(() -> new IllegalArgumentException(ticker + " is invalid"));
    }

    /**
     * Validate (against IEX) and save given tickers to quote table.
     * TODO: Get IEX quote(s).
     * TODO: Convert each IEX quote to Quote entity.
     * TODO: Persist the quote to database.
     *
     * @param tickers a list of tickers/symbols.
     * @return a list of Quotes to save into the database.
     * @throws IllegalArgumentException if ticker is not found from IEX.
     */
    public List<Quote> saveQuotes(List<String> tickers){
        List<Quote> quotes = new ArrayList<Quote>();
        for (String ticker: tickers){
            Quote quoteToSave = saveQuote(ticker);
            quotes.add(quoteToSave);
        }
        return quotes;
    }

    /**
     * Helper method.
     */
    public Quote saveQuote(String ticker){
        IexQuote iexQuote = findIexQuoteByTicker(ticker);
        Quote quote = buildQuoteFromIexQuote(iexQuote);
        saveQuote(quote);
        return quote;
    }

    public Quote saveQuote(Quote quote){
        return quoteDao.save(quote);
    }

    /**
     * Find all quotes from the quote table.
     * @return a list of quotes
     */
    public List<Quote> findAllQuotes(){
        return quoteDao.findAll();
    }
}