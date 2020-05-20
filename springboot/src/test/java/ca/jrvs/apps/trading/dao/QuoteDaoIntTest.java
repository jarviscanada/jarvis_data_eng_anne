package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteDaoIntTest {

    private QuoteDao quoteDao;

    private Logger logger = LoggerFactory.getLogger(QuoteDaoIntTest.class);

    private Quote sampleQuote;
    private Quote updatedQuote;

    @Before
    public void setup(){
        sampleQuote.setAskPrice(300d);
        sampleQuote.setAskSize(100);
        sampleQuote.setBidPrice(250d);
        sampleQuote.setBidSize(100);
        sampleQuote.setId("AAPL");
        sampleQuote.setLastPrice(273.25d);
        quoteDao.save(sampleQuote);

        assertTrue(quoteDao.existsById(sampleQuote.getId()));
        assertEquals(1, quoteDao.count());
    }

    @After
    public void cleanup(){
        quoteDao.deleteAll();
    }

    @Test
    public void save(){
        updatedQuote = sampleQuote;
        updatedQuote.setBidSize(50);
        quoteDao.save(updatedQuote);
        assertEquals(updatedQuote.getBidSize(), quoteDao.findById("AAPL").get().getBidSize());
    }

    @Test
    public void saveAll(){
        String ticker;

        Quote facebook = new Quote();
        ticker = "FB";
        facebook.setId(ticker);
        facebook.setTicker(ticker);
        facebook.setLastPrice(174.79d);

        Quote microsoft = new Quote();
        ticker = "MSFT";
        microsoft.setId(ticker);
        microsoft.setTicker(ticker);
        microsoft.setLastPrice(165.51d);

        Quote tesla = new Quote();
        ticker = "TSLA";
        tesla.setId(ticker);
        tesla.setTicker(ticker);
        tesla.setLastPrice(650.95d);

        List<Quote> newQuotes = Arrays.asList(facebook, microsoft, tesla);
        quoteDao.saveAll(newQuotes);
    }
}
