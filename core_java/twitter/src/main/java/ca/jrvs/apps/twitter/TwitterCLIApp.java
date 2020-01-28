package ca.jrvs.apps.twitter;

import ca.jrvs.apps.twitter.controller.TwitterController;
import ca.jrvs.apps.twitter.dao.TwitterDao;
import ca.jrvs.apps.twitter.helper.TwitterHttpHelper;
import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TwitterCLIApp {

    private TwitterController controller;

    @Autowired
    public TwitterCLIApp(TwitterController controller){
        this.controller = controller;
    }


    public final static String USAGE = "Invalid command. Please use: TwitterCLIApp post/create/delete [options]";

    public void main(String[] args){
        // Set keys and tokens from the environment.
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

        // Declare and instantiate all of the components required to run TwitterCLIApp.
        TwitterHttpHelper httpHelper = new TwitterHttpHelper(consumerKey, consumerSecret, accessToken, tokenSecret);
        TwitterDao dao = new TwitterDao(httpHelper);
        TwitterService service = new TwitterService(dao);
        TwitterController controller = new TwitterController(service);
        TwitterCLIApp app = new TwitterCLIApp(controller);

        app.run(args);

    }

    public void run(String[] args){

        if (args.length == 0){
            throw new IllegalArgumentException(USAGE);
        }

        // Routing the different commands to the appropriate method.
        String command = args[0];
        switch (command.toLowerCase()){
            case "post":
                printTweet(controller.postTweet(args));
                break;
            case "show":
                printTweet(controller.showTweet(args));
                break;
            case "delete":
                printTweet(controller.deleteTweet(args));
                break;
            default:
                throw new IllegalArgumentException(USAGE);
        }
    }

    private void printTweet(List<Tweet> listOfTweets){
        listOfTweets.forEach(tweet -> printTweet(tweet));
    }

    private void printTweet(Tweet tweet) {
        String id = tweet.getIdStr();
        String created_at = tweet.getCreatedAt();
        String text = tweet.getText();
        float longitude = tweet.getCoordinates().getCoordinates()[0];
        float latitude = tweet.getCoordinates().getCoordinates()[1];

        // Printing: Tweet ID, publish date, Tweet text, and the location coordinates associated with the Tweet.
        System.out.println("Tweet ID: " + id);
        System.out.println("Created at: " + created_at);
        System.out.println("Content: " + text);
        System.out.println("Tweet sent from location: " + String.valueOf(longitude) + ":" + String.valueOf(latitude));
    }
}
