package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

    private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
    private final String IEX_BATCH_URL;

    private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
    private HttpClientConnectionManager httpClientConnectionManager;

    /**
     * Construct a MarketDataDao object with the given HttpClientConnectionManager
     * and MarketDataConfig.
     *
     * @param httpClientConnectionManager
     * @param marketDataConfig
     */
    @Autowired
    public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager, MarketDataConfig marketDataConfig){
        this.httpClientConnectionManager = httpClientConnectionManager;
        IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
    }

    /**
     * Get an IEX Quote (helper method)
     * @param ticker
     * @throws IllegalArgumentException if a given ticker is invalid.
     * @throws DataRetrievalFailureException if HTTP request failed.
     * @return IEX Quote
     */
    @Override
    public Optional<IexQuote> findById(String ticker) {
        Optional<IexQuote> iexQuote;
        List<IexQuote> quotes = findAllById(Collections.singletonList(ticker));

        if (quotes.size() == 0){
            return Optional.empty();
        } else if (quotes.size() == 1){
            iexQuote = Optional.of(quotes.get(0));
        } else {
            throw new DataRetrievalFailureException("Unexpected number of quotes");
        }

        return iexQuote;
    }

    /**
     * Get quotes from IEX.
     * @param tickers is a iterable of tickers.
     * @throws IllegalArgumentException if any ticker is invalid or tickers is empty.
     * @throws DataRetrievalFailureException if HTTP request failed.
     * @return a list of IexQuote objects.
     */
    @Override
    public List<IexQuote> findAllById(Iterable<String> tickers) {
        List<IexQuote> quotes = new ArrayList<IexQuote>();

        // Verify that the Iterable has at least one ticker.
        int numOfTickers = 0;
        for (String ticker: tickers){
            numOfTickers++;
        }
        if (numOfTickers == 0){
            throw new IllegalArgumentException("Given iterable is empty.");
        }

        // Obtain IEX Quote for every given ticker and collect them into
        // a list.
        for (String ticker: tickers){
            quotes.add(findById(ticker).get());
        }

        return quotes;
    }

    /**
     * Executes a GET and RETURN HTTP entity/body as a string.
     * Tip: use EntityUtils.toString to process HTTP entity.
     *
     * @param url resource URL
     * @return HTTP response body or Optional.empty for 404 response.
     * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected.
     */
    private Optional<String> executeHttpGet(String url) {
        // Creating the HTTP client and the HTTP GET request.
        HttpClient client = getHttpClient();
        HttpGet get = new HttpGet(url);

        try {
            HttpResponse response = client.execute(get);
            int responseStatus = response.getStatusLine().getStatusCode();
            // 200: Okay! Everything happened as expected!
            if (responseStatus == 200) {
                HttpEntity entity = response.getEntity();
                return Optional.of(EntityUtils.toString(entity));
            }
            // 404: Error! URL Not found.
            if (responseStatus == 404){
                return Optional.empty();
            }
            else {
                throw new DataRetrievalFailureException("Unexpected response code: " + responseStatus);
            }
        } catch (IOException e) {
            throw new DataRetrievalFailureException("Failure to execute HTTP GET.");
        }
    }

    /**
     * Borrow an HTTP client from the httpClientConnectionManager.
     * @return a httpClient
     */
    private CloseableHttpClient getHttpClient(){
        return HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .setConnectionManagerShared(true)
                .build();
    }

    @Override
    public boolean existsById(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterable<IexQuote> findAll() {
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteById(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(IexQuote entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAll(Iterable<? extends IexQuote> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <S extends IexQuote> S save(S entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
