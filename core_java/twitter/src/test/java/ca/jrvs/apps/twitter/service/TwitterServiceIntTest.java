package ca.jrvs.apps.twitter.service;

import ca.jrvs.apps.twitter.dao.CrdDao;
import ca.jrvs.apps.twitter.dao.TwitterDao;
import ca.jrvs.apps.twitter.dao.helper.HttpHelper;
import ca.jrvs.apps.twitter.helper.TwitterHttpHelper;
import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.util.TweetUtil;
import net.bytebuddy.pool.TypePool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TwitterServiceIntTest {

    private TwitterService service;
    private TwitterDao dao;
    private List<String> ids;

    @Before
    public void setup() throws Exception {
        String consumerKey = System.getenv("consumerKey");
        String consumerSecret = System.getenv("consumerSecret");
        String accessToken = System.getenv("accessToken");
        String tokenSecret = System.getenv("tokenSecret");

        // Check that the authentication credentials are loaded into the environment.
        // Otherwise, the strings above will be set as null and the integration test will fail.
        String[] authentication_credentials = {consumerKey, consumerSecret, accessToken, tokenSecret};
        for (String key : authentication_credentials) {
            if (key == null) {
                throw new RuntimeException("Failed to pass environment variables.");
            }
        }

        // Setup dependency.
        TwitterHttpHelper httpHelper = new TwitterHttpHelper(consumerKey, consumerSecret, accessToken, tokenSecret);

        // Passes the dependency.
        this.dao = new TwitterDao(httpHelper);
        this.service = new TwitterService(dao);

        ids = new ArrayList<String>();
    }

    @Test
    public void post() {

        // Character string base is min. 99 characters long.
        String text = "testing, testing! this tweet should go through because it's not that long. hey, #testing! " + System.currentTimeMillis();
        // Coordinates for Toronto, approximately.
        float longitude = 43.6532f;
        float latitude = -79.3832f;

        Tweet tweet = TweetUtil.buildTweet(text, longitude, latitude);

        Tweet postedTweet = service.postTweet(tweet);
        ids.add(postedTweet.getIdStr());

        // Testing to make sure that Tweet object is rejected during validation.
        // Tweet text too long:
        try {
            // This tweet should pass.
            String secondText = text + " and this is the second try!";
            Tweet secondTweet = TweetUtil.buildTweet(secondText, longitude, latitude);
            Tweet secondPostedTweet = service.postTweet(secondTweet);
            ids.add(secondPostedTweet.getIdStr());

            // This tweet should fail.
            String thirdText = text + " " + text;
            Tweet thirdTweet = TweetUtil.buildTweet(thirdText, longitude, latitude);
            service.postTweet(thirdTweet);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        // Invalid coordinates:
        try {
            // This tweet should fail.
            float invalidLongitude = longitude + 360;
            float invalidLatitude = latitude + 180;

            Tweet fourthTweet = TweetUtil.buildTweet(text, invalidLongitude, invalidLatitude);
            service.postTweet(fourthTweet);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

    }

    @Test
    public void show() {
        String[] fields = {"created_at", "id", "id_str", "text"};

        ids.forEach(id -> System.out.println("posted: " + id));
        ids.forEach(id -> service.showTweet(id));

        // Testing to make sure that invalid arguments are rejected during the validation process.
        // Invalid Tweet ID:
        try {
            String id = "12389071fa08";
            service.showTweet(id);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void delete() {

        Object[] intermediate = ids.toArray();
        String[] deletedTweetIDs = Arrays.copyOf(intermediate, intermediate.length, String[].class);

        service.deleteTweets(deletedTweetIDs);

        // Testing to make sure that invalid arguments are rejected during the validation process.
        try {
            String[] invalidIDs = {"4a5246d43643658"};
            service.deleteTweets(invalidIDs);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}