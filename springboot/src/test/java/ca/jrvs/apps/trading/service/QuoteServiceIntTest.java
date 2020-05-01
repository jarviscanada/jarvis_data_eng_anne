package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteServiceIntTest {

    @Autowired
    private QuoteService service;

    @Autowired
    private QuoteDao dao;

    IexQuote sampleIexQuote;
    Quote sampleQuote;
    Quote updatedQuote;

    @Before
    public void setup(){
        dao.deleteAll();

        sampleQuote.setTicker("AAPL");
        sampleQuote.setId("AAPL");
        sampleQuote.setAskPrice(100d);
        sampleQuote.setAskSize(10);
        sampleQuote.setBidPrice(99.50d);
        sampleQuote.setBidSize(10);
        sampleQuote.setLastPrice(99.75d);

        updatedQuote = sampleQuote;
        updatedQuote.setBidPrice(99.99);
        updatedQuote.setBidSize(5);
    }

    @Test
    public void findIexQuoteByTicker(){
        sampleIexQuote = service.findIexQuoteByTicker("AAPL");
        assertNotNull(sampleIexQuote);
        assertEquals("AAPL", sampleIexQuote.getSymbol());

        try{
            IexQuote incorrectApple = service.findIexQuoteByTicker("APPLE");
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void updateMarketData(){
        String ticker = sampleQuote.getTicker();
        Double lastPrice = sampleQuote.getLastPrice();

        service.updateMarketData();
        Quote updatedQuote = dao.findById(ticker).get();

        assertNotEquals(lastPrice, updatedQuote.getLastPrice());
    }

    @Test
    public void saveQuotes(){
        List<String> companies = new ArrayList<>();
        companies = Arrays.asList("AAPL", "FB", "TSLA");

        List<Quote> companyQuotes = service.saveQuotes(companies);
        assertEquals(companies.get(0), companyQuotes.get(0).getTicker());
        assertEquals(companies.get(1), companyQuotes.get(1).getTicker());
        assertEquals(companies.get(2), companyQuotes.get(2).getTicker());

        List<String> incorrectCompanies = new ArrayList<>();
        incorrectCompanies = Arrays.asList("APPLE", "FACEBOOK", "TESLA");
        try {
            List <Quote> incorrectCompanyQuotes = service.saveQuotes(incorrectCompanies);
            fail();
        } catch(IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void saveQuote(){
        String apple = "AAPL";
        String tesla = "TSLA";
        Quote appleQuote = service.saveQuote(apple);
        Quote teslaQuote = service.saveQuote(tesla);

        assertNotNull(appleQuote);
        assertNotNull(teslaQuote);
        assertEquals(apple, appleQuote.getTicker());
        assertEquals(tesla, teslaQuote.getTicker());

        try {
            Quote incorrectAppleQuote = service.saveQuote("APPLE");
            Quote incorrectFacebookQuote = service.saveQuote("FACEBOOK");
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        Quote invalidQuote = appleQuote;
        // Testing each IllegalArgumentException for every possible invalid
        // data field in the Quote object for validateQuote(quote);
        try { // last price
            invalidQuote.setLastPrice(-1d);
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // ask price
            invalidQuote.setLastPrice(0d);
            invalidQuote.setAskPrice(-1d);
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // ask size
            invalidQuote.setAskPrice(0d);
            invalidQuote.setAskSize(-1);
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // bid price
            invalidQuote.setAskSize(0);
            invalidQuote.setBidPrice(-1d);
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // bid size
            invalidQuote.setBidPrice(0d);
            invalidQuote.setBidSize(-1);
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // id case: null
            invalidQuote.setBidSize(0);
            invalidQuote.setId(null);
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // id case: empty
            invalidQuote.setId("");
            service.saveQuote(invalidQuote);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void findAllQuotes(){
        List<String> tickers = new ArrayList<>();
        tickers = Arrays.asList("AAPL", "FB", "TSLA", "MSFT");
        List<Quote> quotes = new ArrayList<>();

        tickers.forEach(ticker -> quotes.add(dao.findById(ticker).get()));
        quotes.forEach(quote -> service.saveQuote(quote));
        List<Quote> foundQuotes = service.findAllQuotes();
        
        assertEquals(quotes.size(), foundQuotes.size());

        HashSet<Quote> quoteSet = new HashSet<>(quotes);
        HashSet<Quote> foundQuoteSet = new HashSet<>(foundQuotes);

        assertTrue(quoteSet.equals(foundQuoteSet));
    }
}
