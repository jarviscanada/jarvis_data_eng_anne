package ca.jrvs.apps.twitter.dao;

import ca.jrvs.apps.twitter.dao.helper.HttpHelper;
import ca.jrvs.apps.twitter.helper.TwitterHttpHelper;
import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.util.TweetUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class TwitterDaoIntTest {

    private TwitterDao dao;
    private Tweet tweet;
    private String id;

    @Before
    public void setup() {

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
    }

    @Before
    public void create() throws Exception {

        String hashtag = "#testing";
        String text = "testing! @khepria hey, " + hashtag + " " + System.currentTimeMillis();
        // Coordinates for Toronto, approximately.
        float longitude = 43.6532f;
        float latitude = -79.3832f;

        Tweet postTweet = TweetUtil.buildTweet(text, longitude, latitude);

        tweet = dao.create(postTweet);
        id = tweet.getIdStr();

        assertEquals(text, tweet.getText());

        assertNotNull(tweet.getCoordinates());
        assertEquals(2, tweet.getCoordinates().getCoordinates().length);
        assertEquals(longitude, tweet.getCoordinates().getCoordinates()[0]);
        assertEquals(latitude, tweet.getCoordinates().getCoordinates()[1]);

        assertTrue(hashtag.contains(tweet.getEntities().getHashtags()[0].getText()));
    }

    @Test
    public void find() throws Exception{

        Tweet findTweet = dao.findById(id);

        assertEquals(id, findTweet.getIdStr());
        assertEquals(tweet.getText(), findTweet.getText());
        assertEquals(tweet.getCoordinates().getCoordinates()[0], findTweet.getCoordinates().getCoordinates()[0], 0.5);
        assertEquals(tweet.getCoordinates().getCoordinates()[1], findTweet.getCoordinates().getCoordinates()[1], 0.5);
    }

    @After
    public void delete() throws Exception{

        Tweet deleteTweet = dao.deleteById(id);

        assertEquals(id, deleteTweet.getIdStr());
        assertEquals(tweet.getText(), deleteTweet.getText());
        assertEquals(tweet.getCoordinates().getCoordinates()[0], deleteTweet.getCoordinates().getCoordinates()[0], 0.5);
        assertEquals(tweet.getCoordinates().getCoordinates()[1], deleteTweet.getCoordinates().getCoordinates()[1], 0.5);
    }
}
