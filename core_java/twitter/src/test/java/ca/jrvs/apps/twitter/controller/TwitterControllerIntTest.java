package ca.jrvs.apps.twitter.controller;

import ca.jrvs.apps.twitter.dao.TwitterDao;
import ca.jrvs.apps.twitter.dao.helper.HttpHelper;
import ca.jrvs.apps.twitter.helper.TwitterHttpHelper;
import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.service.TwitterService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TwitterControllerIntTest {

    private TwitterController controller;
    private TwitterService service;
    private TwitterDao dao;
    private List<String> ids;
    private float longitude;
    private float latitude;

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
        this.service = new TwitterService(dao);
        this.controller = new TwitterController(service);

        longitude = 43.6532f;
        latitude = -79.3832f;
        ids = new ArrayList<String>();
    }


    @Before
    public void post() {
        long millisecond = System.currentTimeMillis();
        String text = "testing, testing!! controller layer testing. sup, the time is approx " + millisecond;
        String coordinates = longitude + ":" + latitude;
        String args[] = {"post", text, coordinates};

        assertEquals(args[0], "post");
        assertEquals(args[1], text);
        assertEquals(args[2], coordinates);

        Tweet tweet = controller.postTweet(args);
        assertNotNull(tweet);
        assertEquals(tweet.getText(), text);

        // Add tweet ID to a list for a future test.
        ids.add(tweet.getIdStr());
        System.out.println("created tweet ID:");
        System.out.println(tweet.getIdStr());

        try {
            // This tweet should successfully post.
            String secondText = text + " second post!";
            String[] secondArgs = {"post", secondText, coordinates};
            Tweet secondTweet = controller.postTweet(secondArgs);
            // Adding its ID to a list for a future test.
            ids.add(secondTweet.getIdStr());
            System.out.println("created tweet ID:");
            System.out.println(secondTweet.getIdStr());

            // This should fail args[] is missing coordinate information.
            String missingCoordinates[] = {"post", text};
            Tweet failTweet = controller.postTweet(missingCoordinates);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void show() {
        String fields = "created_at, text, id_str, coordinates";
        ids.forEach(id -> {
            Tweet showTweet = controller.showTweet(new String[]{"show", id});
            assertNotNull(showTweet);
            assertEquals(showTweet.getIdStr(), id);
        });

        // This test ensures that when the incorrect number of arguments are fed into the controller, it is rejected. showTweet() takes maximum three arguments - "show" keyword, a Tweet ID, and optional field name(s) (fed as a single argument).
        try{
            String[] incorrectArgs = {"show", fields, fields, fields};
            controller.showTweet(incorrectArgs);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        // This test ensures that when an insufficient number of arguments are fed into the controller, it is rejected. showTweet() requires at least the keyword "show" and a Tweet ID to run.
        try{
            String[] missingArgs = {"show"};
            controller.showTweet(missingArgs);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @After
    public void delete() {

        // Transform ArrayList<String> to a String.
        StringBuilder tweetIDs = new StringBuilder();
        ids.forEach(id -> tweetIDs.append(id + ","));
        // This removes the last comma from StringBuilder before conversion to String.
        tweetIDs.setLength(tweetIDs.length() - 1);
        String tweetIDsStr = tweetIDs.toString();

        // Create a String array to send to the function.
        String[] tweetsToDelete = tweetIDsStr.split(",");
        controller.deleteTweet(tweetsToDelete);
    }
}