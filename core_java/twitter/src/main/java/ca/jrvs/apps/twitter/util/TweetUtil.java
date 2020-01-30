package ca.jrvs.apps.twitter.util;

import ca.jrvs.apps.twitter.model.Coordinates;
import ca.jrvs.apps.twitter.model.Tweet;
import com.google.gdata.util.common.base.PercentEscaper;

public class TweetUtil {


    public static Tweet buildTweet(String text, float longitude, float latitude){

        Coordinates coordinates = new Coordinates();
        coordinates.setCoordinates(new float[]{longitude, latitude});
        coordinates.setType("Point");

        Tweet tweet = new Tweet();
        tweet.setCoordinates(coordinates);
        tweet.setText(text);

        return tweet;
    }
}
