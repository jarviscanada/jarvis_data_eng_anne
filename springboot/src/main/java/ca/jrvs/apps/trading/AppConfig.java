package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.controller.QuoteController;
import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.service.QuoteService;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class AppConfig {

    private Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public DataSource dataSource(){
        String url = System.getenv("PSQL_URL");
        String user = System.getenv("PSQL_USER");
        String password = System.getenv("PSQL_PASSWORD");

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
        return basicDataSource;
    }

    @Bean
    public MarketDataConfig marketDataConfig(){
        String token = System.getenv("IEX_CONSUMER_TOKEN");
        MarketDataConfig config = new MarketDataConfig();
        config.setToken(token);
        return config;
    }

    @Bean
    public MarketDataDao marketDataDao(HttpClientConnectionManager httpClientConnectionManager,
                                       MarketDataConfig marketDataConfig){
        return new MarketDataDao(httpClientConnectionManager, marketDataConfig);
    }

    @Bean
    public QuoteService quoteService(QuoteDao quoteDao, MarketDataDao marketDataDao){
        return new QuoteService(quoteDao, marketDataDao);
    }

    @Bean
    public QuoteController quoteController(QuoteService quoteService){
        return new QuoteController(quoteService);
    }
}