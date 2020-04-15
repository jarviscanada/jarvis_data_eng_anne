package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
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
        marketDataConfig.setToken(System.getenv("IEX_PUB_TOKEN"));

        dao = new MarketDataDao(connectionManager, marketDataConfig);
    }

    @Test
    public void findIexQuoteByTickers() throws IOException{
        // Successful pathway.
        List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL", "FB", "MSFT", "TSLA"));
        assertEquals(4, quoteList.size());
        assertEquals("AAPL", quoteList.get(0).getSymbol());
        assertEquals("FB", quoteList.get(1).getSymbol());

        List<IexQuote> someWrongQuoteList = dao.findAllById(Arrays.asList("AAPLE", "FBOOK", "MSFT", "TESLA"));
        assertEquals(1, someWrongQuoteList.size());
        assertEquals("MSFT", someWrongQuoteList.get(0).getSymbol());

        //Unsuccessful pathway.
        try{
            dao.findAllById(Arrays.asList("AAPLS", "FB2"));
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void findByTicker(){
        String ticker = "AAPL";
        Optional<IexQuote> iexQuote = dao.findById(ticker);
        assertEquals(ticker, iexQuote.get().getSymbol());
    }
}