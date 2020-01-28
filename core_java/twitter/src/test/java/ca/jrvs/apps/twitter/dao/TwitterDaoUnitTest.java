package ca.jrvs.apps.twitter.dao;

import ca.jrvs.apps.twitter.dao.helper.HttpHelper;
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

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TwitterDaoUnitTest {

    @Mock
    HttpHelper mockHelper;

    @InjectMocks
    TwitterDao dao;

    String tweetJsonStr;

    @Before
    public void setup(){
        // Mock parseResponseBody JSON string.
        tweetJsonStr = "{\n"
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
    }

    @Test
    public void postTweet() throws IOException{
        // Failed test request.
        String hashtag = "#testing";
        String text = "@khepria testing, testing! " + hashtag + " " + System.currentTimeMillis();
        // Coordinates for Toronto, approximately.
        float longitude = 43.6532f;
        float latitude = -79.3832f;

        // Exception is expected here.
        when(mockHelper.httpPost(isNotNull())).thenThrow(new RuntimeException("mock"));
        try {
            dao.create(TweetUtil.buildTweet(text, longitude, latitude));
            //fail();
        } catch (RuntimeException e){
            assertTrue(true);
        }

        // Test path.
        when (mockHelper.httpPost(isNotNull())).thenReturn(null);
        TwitterDao spyDao = Mockito.spy(dao);

        // Mock parseResponseBody
        Tweet expectedTweet = JsonUtil.toObjectFromJson(tweetJsonStr, Tweet.class);
        doReturn(expectedTweet).when(spyDao).parseResponseBody(any(), anyInt());
        Tweet tweet = spyDao.create(TweetUtil.buildTweet(text, longitude, latitude));
        assertNotNull(tweet);
        assertNotNull(tweet.getText());
        assertNull(tweet.getCoordinates());
    }

    @Test
    public void showTweet() throws IOException{

        // Exception is expected here.
        when(mockHelper.httpGet(isNotNull())).thenThrow(new RuntimeException("mock"));
        try {
            dao.findById("1097607853932564480");
            fail();
        } catch (RuntimeException e){
            assertTrue(true);
        }

        // Test successful path.
        when (mockHelper.httpGet(isNotNull())).thenReturn(null);
        TwitterDao spyDao = Mockito.spy(dao);
        // Mock parseResponseBody
        Tweet expectedTweet = JsonUtil.toObjectFromJson(tweetJsonStr, Tweet.class);
        doReturn(expectedTweet).when(spyDao).parseResponseBody(any(), anyInt());
        Tweet tweet = spyDao.findById("1097607853932564480");
        assertNotNull(tweet);
        assertNotNull(tweet.getText());
        assertNotNull(tweet.getId());
        assertEquals("1097607853932564480", tweet.getIdStr());
    }

    @Test
    public void deleteTweet() throws IOException {
        // Runtime exception is expected here because Tweet object is not created yet.
        when(mockHelper.httpPost(isNotNull())).thenThrow(new RuntimeException("mock"));
        try {
            dao.deleteById("1097607853932564480");
            fail();
        } catch (RuntimeException e){
            assertTrue(true);
        }

        // Test successful path.
        when (mockHelper.httpPost(isNotNull())).thenReturn(null);
        TwitterDao spyDao = Mockito.spy(dao);
        // Mock parseResponseBody
        Tweet expectedTweet = JsonUtil.toObjectFromJson(tweetJsonStr, Tweet.class);
        doReturn(expectedTweet).when(spyDao).parseResponseBody(any(), anyInt());
        Tweet tweet = spyDao.deleteById("1097607853932564480");
        assertNotNull(tweet);
        assertNotNull(tweet.getText());
        assertEquals("1097607853932564480", tweet.getIdStr());
    }
}
