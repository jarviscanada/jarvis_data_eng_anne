package ca.jrvs.apps.twitter.controller;

import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ca.jrvs.apps.twitter.util.TweetUtil.buildTweet;

@Service
public class TwitterController implements Controller {

    // COORD_SEP and COMMA include regex to account for possible leading or trailing spaces
    // around punctuation. This allows for the app to parse the arguments given without program
    // failure due to extraneous whitespace surrounding argument parameters. For example,
    // " id_str" would be rejected by the field validator because it is a strict match.
    private static final String COORD_SEP = "\\s*:\\s*";
    private static final String COMMA = "\\s*,\\s*";

    private TwitterService service;

    @Autowired
    public TwitterController(TwitterService service) {
        this.service = service;
    }

    /**
     * Takes the parsed command line given by the terminal and acts on the options given.
     * @param args -> in the form and order of [TwitterCLIApp, post, "given tweet text", "longitude:latitude"]
     * @return service layer postTweet()
     */
    @Override
    public Tweet postTweet(String[] args) {
        if (args.length != 3){
            throw new IllegalArgumentException("Format for use: TwitterCLIApp post \"tweet_text\" \"longitude:latitude\"");
        }

        String tweetText = args[1];
        String coordinate = args[2];
        String[] coordinateArray = coordinate.split(COORD_SEP);

        if (coordinateArray.length != 2) {
            throw new IllegalArgumentException("Invalid coordinate information. Format for use: TwitterCLIApp post \"tweet_text\" \"longitude:latitude\"");
        }

        if (tweetText.isEmpty()) {
            throw new IllegalArgumentException("Tweet text is empty. Please provide tweet text. Format for use: TwitterCLIApp post \"tweet_text\" \"longitude:latitude\"");
        }

        Float longitude = null;
        Float latitude = null;

        try {
            longitude = Float.parseFloat(coordinateArray[0]);
            latitude = Float.parseFloat(coordinateArray[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid coordinate information. Format for use: TwitterCLIApp post \"tweet_text\" \"longitude:latitude\"");
        }

        Tweet postTweet = buildTweet(tweetText, longitude, latitude);
        return service.postTweet(postTweet);
    }

    /**
     * Takes the parsed command line given by the terminal and acts on the options given.
     * @param args -> in the form and order of [TwitterCLIApp, show, "tweet ID"]
     * @return service layer showTweet()
     */
    @Override
    public Tweet showTweet(String[] args) {
        if (args.length != 2){
            throw new IllegalArgumentException("Format for use: TWitterCLIApp show \"tweet_id\"");
        }

        String idStr = args[1];

        return service.showTweet(idStr);
    }

    /**
     * Takes the parsed command line given by the terminal and acts on the options given.
     * @param args -> in the form and order of [TwitterCLIApp, delete, "tweet ID(, tweet ID,
     *             as many tweet IDs as the user would like to include as long as they are
     *             separated by comma)"]
     * @return service layer deleteTweets()
     */
    @Override
    public List<Tweet> deleteTweet(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Format for usage: TwitterCLIApp delete \"tweet_id\" [1 or more, separated by comma]");
        }

        String ids = args[1];
        String[] tweetIDArray = ids.split(COMMA);

        if (tweetIDArray.length < 1){
            throw new IllegalArgumentException("Failed to include Tweet IDs. Format for usage: TwitterCLIApp delete \"tweet_id\" [1 or more, separated by comma]");
        }

        return service.deleteTweets(tweetIDArray);
    }
}
