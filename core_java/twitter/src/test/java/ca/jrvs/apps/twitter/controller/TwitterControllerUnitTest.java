package ca.jrvs.apps.twitter.controller;

import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.service.TwitterService;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class TwitterControllerUnitTest {

    @Mock
    TwitterService mockService;

    @InjectMocks
    TwitterController controller;

    @Test
    public void postTweet(){

        float longitude = 43.6532f;
        float latitude = -79.3832f;

        when(mockService.postTweet(any())).thenReturn(new Tweet());

        // Failed pathway.
        try {
            String[] invalidArgs = {"post", "this is just a test!!", "1455:-140.4"};
            Tweet invalidTweet = controller.postTweet(invalidArgs);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        // Successful pathway.
        String coordinate = String.valueOf(longitude) + ":" + String.valueOf(latitude);
        String[] args = {"post", "this is just a test!!", coordinate};
        Tweet validTweet = controller.postTweet(args);
        assertNotNull(validTweet);

    }

    @Test
    public void showTweet(){
        when(mockService.showTweet(any())).thenReturn(new Tweet());

        // Failed pathway.
        try{
            String[] invalidArgs = {"show", "1231jasd91"};
            Tweet invalidTweet = controller.showTweet(invalidArgs);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        // Successful pathway
        String[] args = {"show", "1231481285"};
        Tweet validTweet = controller.showTweet(args);
        assertNotNull(validTweet);
    }

    @Test
    public void deleteTweet(){

        // Failed pathway
        try{
            String[] invalidArgs = {"delete", "12312412412,1412312j1,124121"};
            List<Tweet> invalidTweets = controller.deleteTweet(invalidArgs);
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }

        // Successful pathway
        String[] args = {"delete", "3465234523, 1241321314, 151241312"};
        List<Tweet> validTweets = controller.deleteTweet(args);
        validTweets.forEach(tweet -> assertNotNull(tweet));
    }
}
