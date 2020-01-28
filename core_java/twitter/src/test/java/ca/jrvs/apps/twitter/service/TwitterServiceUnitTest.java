package ca.jrvs.apps.twitter.service;

import ca.jrvs.apps.twitter.dao.TwitterDao;
import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.util.JsonUtil;
import ca.jrvs.apps.twitter.util.TweetUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class TwitterServiceUnitTest {

    @Mock
    TwitterDao mockDao;

    @InjectMocks
    TwitterService service;

    Tweet validTweet;

    @Before
    public void setup() throws IOException {

        // Mock parseResponseBody JSON string.
        String tweetJsonStr = "{\n"
                +"    \"created_at\":\"Mon Feb 18 21:24:39 +0000 2019\",\n"
                +"    \"id\": 1097607853932564480,\n"
                +"    \"id_str\":\"1097607853932564480\",\n"
                +"    \"text\":\"testing, testing!\",\n"
                +"    \"entities\":{\n"
                +"        \"hastags\":[],\n"
                +"        \"user_mentions\":[]\n"
                +"    },\n"
                +"    \"coordinates\":null,\n"
                +"    \"retweet_count\":0,\n"
                +"    \"favorite_count\":0,\n"
                +"    \"favorited\":false,\n"
                +"    \"retweeted\":false\n"
                +"}";

        try{
            validTweet = JsonUtil.toObjectFromJson(tweetJsonStr, Tweet.class);
        } catch (IOException e){
            throw new IOException("Failed to create Tweet using given JSON.");
        }
    }

    @Test
    public void postTweet() throws IOException {
        when(mockDao.create(any())).thenReturn(new Tweet());

        String text = "testing, testing!!";
        float longitude = 43.6532f;
        float latitude = -79.3832f;

        // Failed test request.
        try{
            String illegalText = "testing, testing!! testing, testing!! testing, testing!! testing, testing!! testing, testing!! testing, testing!! how much more can we possibly test?";
            Tweet illegalTweet = TweetUtil.buildTweet(illegalText, longitude, latitude);
            service.postTweet(illegalTweet);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        Tweet testTweet = TweetUtil.buildTweet(text, longitude, latitude);
        service.postTweet(testTweet);
        assertNotNull(testTweet);
    }

    @Test
    public void showTweet(){
        when(mockDao.findById(any())).thenReturn(new Tweet());

        try{
            service.showTweet("1280734asd02");
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        Tweet testTweet = service.showTweet("127496192");
        assertNotNull(testTweet);
    }

    @Test
    public void deleteTweet(){

        when(mockDao.deleteById(any())).thenReturn(new Tweet());

        try{
            String[] invalidIDs = {"123412412", "1231243d212"};
            service.deleteTweets(invalidIDs);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        String[] validTweetIDs = {"123412412", "1234134124"};
        List<Tweet> testTweets = service.deleteTweets(validTweetIDs);
        testTweets.forEach(tweet -> assertNotNull(tweet));
    }
}
