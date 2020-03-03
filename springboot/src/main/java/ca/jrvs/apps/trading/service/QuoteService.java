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
import java.util.Optional;

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
     * TODO: get all quotes from the database
     * TODO: for each ticker, get IEX Quote
     * TODO: Convert IEX Quote to quote entity
     * TODO: Persist quote to database.
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
            Optional<IexQuote> updatedMarketData;

            // For each ticker, obtain IEX quote.
            try {
                ticker = quote.getTicker();
                IexQuote iexQuote = findIexQuoteByTicker(ticker);
                quote = buildIdQuoteFromIexQuote(iexQuote);
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
     * Note: 'iexQuote.getLatestPrice() == null' if the stock market is closed.
     * TODO: set default value for number field(s).
     * @param iexQuote
     * @return Quote
     */
    protected static Quote buildIdQuoteFromIexQuote(IexQuote iexQuote){
        Quote quote = new Quote();

        double askPrice = iexQuote.getIexAskPrice();
        double askSize = iexQuote.getIexAskSize();
        double bidPrice = iexQuote.getIexBidPrice();
        double bidSize = iexQuote.getIexBidSize();
        double lastPrice = iexQuote.getLatestPrice();

        quote.setAskPrice(askPrice);
        quote.setAskSize((int)askSize);
        quote.setBidPrice(bidPrice);
        quote.setBidSize((int)bidSize);
        quote.setLastPrice(lastPrice);

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
}
