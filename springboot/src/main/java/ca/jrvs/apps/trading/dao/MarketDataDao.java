package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

    private static final String IEX_SINGLETON_PATH = "stock/%s/quote/?token=";
    private static final String IEX_BATCH_PATH = "stock/market/batch?symbols=%s&types=quote&token=";
    private final String IEX_SINGLETON_URL;
    private final String IEX_BATCH_URL;

    private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
    private HttpClientConnectionManager httpClientConnectionManager;

    @Autowired
    public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager, MarketDataConfig marketDataConfig){
        this.httpClientConnectionManager = httpClientConnectionManager;
        IEX_SINGLETON_URL = marketDataConfig.getHost() + IEX_SINGLETON_PATH + marketDataConfig.getToken();
        IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
    }

    @Override
    public <S extends IexQuote> S save(S entity) {
        return null;
    }

    @Override
    public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     * Obtains an IexQuote.
     * @param ticker
     * @return an IexQuote with the ticker.
     * @throws IllegalArgumentException if a given ticker is invalid.
     * @throws DataRetrievalFailureException if the HTTP request fails.
     */
    @Override
    public Optional<IexQuote> findById(String ticker) {
        Optional<IexQuote> iexQuote = null;
        ObjectMapper mapper = new ObjectMapper();

        String requestURL = String.format(IEX_SINGLETON_URL, ticker);
        Optional<String> retrievedIexQuote = executeHttpGet(requestURL);

        if (retrievedIexQuote.isPresent()){
            try {
                iexQuote = Optional.of(mapper.readValue(retrievedIexQuote.get(), IexQuote.class));
            } catch (IOException e){
                logger.error("Unable to convert JSON into IEX quote object: " + e.toString());
            }
        } else {
            iexQuote = Optional.empty();
        }

        return iexQuote;
    }

    /**
     * Obtains quotes from IEX.
     * @param tickers from a given list.
     * @return a list of IexQuote objects.
     * @throws IllegalArgumentException if any ticker is invalid or the given list is empty.
     * @throws DataRetrievalFailureException if the HTTP request fails.
     */
    @Override
    public List<IexQuote> findAllById(Iterable<String> tickers) {
        List<IexQuote> quotes = new ArrayList<>();
        Iterator tickerIterator = tickers.iterator();
        StringBuilder listOfTickers = new StringBuilder();

        tickers.forEach(ticker -> listOfTickers.append(ticker).append(","));
        if  (listOfTickers.length() > 0){
            // To remove the last delimiter.
            listOfTickers.setLength(listOfTickers.length() - 1);
        }

        String batchURL = String.format(IEX_BATCH_URL, listOfTickers.toString());
        System.out.println(batchURL);

        try{
            Optional<String> jsonsForQuotes = executeHttpGet(batchURL);
            if (jsonsForQuotes.isPresent()){
                JSONObject jsons = new JSONObject(jsonsForQuotes.get());
                ObjectMapper mapper = new ObjectMapper();

                for (String ticker: tickers){
                    try {
                        JSONObject json = jsons.getJSONObject(ticker.toUpperCase());
                        IexQuote quote = mapper.readValue(json.getJSONObject("quote").toString(), IexQuote.class);
                        quotes.add(quote);
                    } catch (JSONException e){
                        logger.error("Ticker invalid: " + e);
                        throw new IllegalArgumentException("Ticker is invalid.");
                    }
                }
            } else {
                throw new IllegalArgumentException("Empty JSON response.");
            }
        } catch (DataRetrievalFailureException e){
            throw new RuntimeException("Did not successfully execute an HTTP GET request.");
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        throw new UnsupportedOperationException("Not implemented in this project.");
    }

    @Override
    public Iterable<IexQuote> findAll() {
        throw new UnsupportedOperationException("Not implemented in this project.");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented in this project.");
    }

    @Override
    public void deleteById(String s) {
        throw new UnsupportedOperationException("Not implemented in this project.");
    }

    @Override
    public void delete(IexQuote entity) {
        throw new UnsupportedOperationException("Not implemented in this project.");
    }

    @Override
    public void deleteAll(Iterable<? extends IexQuote> entities) {
        throw new UnsupportedOperationException("Not implemented in this project.");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented in this project.");
    }
}
