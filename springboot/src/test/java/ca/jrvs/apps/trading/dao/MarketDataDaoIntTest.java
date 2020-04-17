package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class MarketDataDaoIntTest {

    private MarketDataDao dao;

    @Before
    public void init(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(50);
        MarketDataConfig marketDataConfig = new MarketDataConfig();
        marketDataConfig.setHost("https://cloud.iexapis.com/v1/");
        //marketDataConfig.setToken(System.getenv("IEX_PUB_TOKEN"));
        marketDataConfig.setToken("pk_c1454665e42c428f9a3dc9e99009ee5b");

        dao = new MarketDataDao(connectionManager, marketDataConfig);
    }

    @Test
    public void findIexQuoteByTickers() throws IOException{
        // Successful pathway.
        List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL", "FB", "MSFT", "TSLA"));
        assertEquals(4, quoteList.size());
        assertEquals("AAPL", quoteList.get(0).getSymbol());
        assertEquals("FB", quoteList.get(1).getSymbol());

        //Unsuccessful pathway.
        try{
            List<IexQuote> failedList = dao.findAllById(Arrays.asList("AAPLS", "FB"));
            assertEquals(1, failedList.size());
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{

        }
    }

    @Test
    public void findByTicker() {
        // Proper input.
        String ticker = "AAPL";
        Optional<IexQuote> iexQuote = dao.findById(ticker);
        assertEquals(ticker, iexQuote.get().getSymbol());
        // Invalid input, returns Optional.empty().
        String badTicker = "APPLE";
        Optional<IexQuote> badIexQuote = dao.findById(badTicker);
        assertFalse(badIexQuote.isPresent());
    }

    @Test
    public void existsById(){
        try {
            dao.existsById("AAPL");
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }

    @Test
    public void findAll(){
        try{
            dao.findAll();
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }

    @Test
    public void count(){
        try{
            dao.count();
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }

    @Test
    public void deleteById(){
        try{
            dao.deleteById("AAPL");
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }

    @Test
    public void delete(){
        try{
            IexQuote quote = dao.findById("AAPL").get();
            dao.delete(quote);
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }

    @Test
    public void deleteAll(){
        try{
            dao.deleteAll();
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }

        try{
            IexQuote apple = dao.findById("AAPL").get();
            IexQuote tesla = dao.findById("TSLA").get();
            IexQuote microsoft = dao.findById("MSFT").get();

            List<IexQuote> entries = new ArrayList<>();
            entries = Arrays.asList(apple, tesla, microsoft);
            dao.deleteAll(entries);
            fail();
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }
}