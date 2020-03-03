package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteServiceIntTest {

    @Autowired
    private QuoteService quoteService;

    private QuoteDao quoteDao;

    public QuoteServiceIntTest(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    @Before
    public void setup(){
        quoteDao.deleteAll();
    }

    @Test
    public void findIexQuoteByTicker(){
        quoteService.findIexQuoteByTicker("AAPL");
    }

    @Test
    public void updateMarketData(){
        quoteService.updateMarketData();
    }

    @Test
    public void saveQuotes(){
        List<Quote> quotes = quoteDao.findAll();
        quoteDao.saveAll(quotes);
    }

    @Test
    public void saveQuote(){
        Quote quote = quoteDao.findById("AAPL").get();
        quoteDao.save(quote);

        //TODO: assert that the AAPL quote exists in the database.
    }

    @Test
    public void findAllQuotes(){
        quoteDao.findAll();
    }
}
