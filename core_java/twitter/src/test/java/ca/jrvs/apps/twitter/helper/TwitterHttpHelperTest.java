package ca.jrvs.apps.twitter.helper;

import ca.jrvs.apps.twitter.dao.helper.HttpHelper;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URL;

import static org.junit.Assert.*;

public class TwitterHttpHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void httpPost() throws Exception{
        String consumerKey = System.getenv("consumerKey");
        String consumerSecret = System.getenv("consumerSecret");
        String accessToken = System.getenv("accessToken");
        String tokenSecret = System.getenv("tokenSecret");

        // Checking that the environment variables are passed.
        System.out.println(consumerKey + "/" + consumerSecret + "/" + accessToken + "/" + tokenSecret);

        HttpHelper httpHelper = new TwitterHttpHelper(consumerKey, consumerSecret, accessToken, tokenSecret);
        HttpResponse response = httpHelper.httpPost(new URI("https://api.twitter.com/1.1/statuses/update.json?status=testing"));
    }

    @Test
    public void httpGet() throws Exception{
        String consumerKey = System.getenv("consumerKey");
        String consumerSecret = System.getenv("consumerSecret");
        String accessToken = System.getenv("accessToken");
        String tokenSecret = System.getenv("tokenSecret");

        // Checking that the environment variables are passed.
        System.out.println(consumerKey + "/" + consumerSecret + "/" + accessToken + "/" + tokenSecret);

        // Open connection to Twitter using account credentials.
        HttpHelper httpHelper = new TwitterHttpHelper(consumerKey, consumerSecret, accessToken, tokenSecret);
        // Get
        HttpResponse response = httpHelper.httpGet(new URI("https://twitter.com/paparirikaka/status/1214257458899750918"));
    }
}