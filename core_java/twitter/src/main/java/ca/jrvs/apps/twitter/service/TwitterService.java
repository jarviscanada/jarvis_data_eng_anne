package ca.jrvs.apps.twitter.service;

import ca.jrvs.apps.twitter.dao.CrdDao;
import ca.jrvs.apps.twitter.model.Tweet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TwitterService implements Service {

    private CrdDao<Tweet, String> dao;

    @Autowired
    public TwitterService(CrdDao dao){
        this.dao = dao;
    }

    /**
     * Create a Twitter post (Tweet) after validating the data contained in the Tweet object.
     * @param tweet Tweet to be created
     * @return created Tweet.
     */
    @Override
    public Tweet postTweet(Tweet tweet) {
        validatePostTweet(tweet);
        // Creating Tweet via DAO.
        return dao.create(tweet);
    }

    /**
     * Validate the Tweet by checking the Tweet text length and coordinates.
     * @param tweet Tweet to be validated.
     * @throws IllegalArgumentException
     */
    private void validatePostTweet(Tweet tweet) throws IllegalArgumentException {

        String text = tweet.getText();
        float longitude = tweet.getCoordinates().getCoordinates()[0];
        float latitude = tweet.getCoordinates().getCoordinates()[1];

        checkTextLength(text);
        checkCoordinates(longitude, latitude);
    }

    /**
     * Checks that the given text is under the allowed character count.
     * @param text
     */
    private void checkTextLength(String text){
        if (text.length() > 140){
            throw new IllegalArgumentException("Tweet length exceeds 140 characters. Please revise tweet text.");
        }
    }

    /**
     * Checks that the given coordinates fall within the range allowed for
     * longiturade and latitude.
     * @param longitude coordinate
     * @param latitude coordinate
     */
    private void checkCoordinates(float longitude, float latitude){
        boolean longitudeOutOfRange;
        boolean latitudeOutOfRange;

        // Checking longitude coordinate to make sure it falls in valid range.
        longitudeOutOfRange = longitude > 180.1 || longitude < -180.1;

        // Checking latitude coordinate to make sure it falls in valid range.
        latitudeOutOfRange = latitude > 90.1 || latitude < -90.1;

        // Different IllegalArgumentExceptions based on if one or both coordinates are invalid coordinate numbers.
        if (longitudeOutOfRange && latitudeOutOfRange){
            throw new IllegalArgumentException("Coordinates out of range. Please provide valid coordinates.");
        } else if (longitudeOutOfRange) {
            throw new IllegalArgumentException("Longitude coordinate out of range. Please provide valid longitude coordinate within range -180 and 180.");
        } else if (latitudeOutOfRange) {
            throw new IllegalArgumentException("Latitude coordinate out of range. Please provide valid latitude coordinate within range -90 to 90.");
        }
    }

    /**
     * Show/pull Tweet based on ID containing information specified.
     * @param id Tweet ID
     * @return Tweet object.
     */
    @Override
    public Tweet showTweet(String id) {
        validateTweetID(id);
        //validateTweetField(fields);
        return dao.findById(id);
    }

    /**
     * Delete Tweets based on provided Tweet IDs.
     * @param ids tweet IDs which will be deleted
     * @return a list of deleted Tweets.
     */
    @Override
    public List<Tweet> deleteTweets(String[] ids) {

        // Create a String stream from the String array and check each Tweet ID to make sure that they are valid Tweet IDs.
        List<Tweet> deletedTweets = new ArrayList<Tweet>();

        // For every Tweet ID provided in the String array:
        for (String id : ids){
            // Check that the tweet ID is valid.
            validateTweetID(id);
            // If valid, create Tweet object using the Tweet ID.
            Tweet deletedTweet = dao.deleteById(id);
            // Collect the Tweet object into the list of tweets to be deleted.
            deletedTweets.add(deletedTweet);
        }

        return deletedTweets;
    }

    /**
     * Validates Tweet ID. Checks that the given String contains only numerical digits.
     * @param id Tweet ID
     */
    private void validateTweetID(String id){
        if (!id.matches("[0-9]+")){
            throw new IllegalArgumentException("Invalid Tweet ID. Please provide another Tweet ID.");
        }

        try {
            Long.valueOf(id);
        } catch (NumberFormatException e){
            throw new NumberFormatException("Invalid Tweet ID. Tweet ID value exceeds limitations of long format.");
        }
    }

    /**
     * Validate given Tweet fields. Matches against a Tweet Data Dictionary containing all of the allowed/expected fields.
     * @param fields that the Tweet should return
     *
     * private void validateTweetField(String[] fields){

        List<String> validFields = Arrays.asList("created_at", "id", "id_str", "text", "source", "truncated", "in_reply_to_status_id", "in_reply_to_status_id_str", "in_reply_to_user_id", "in_reply_to_user_id_str", "in_reply_to_screen_name", "user", "coordinates", "place", "quoted_status_id", "quoted_status_id_str", "is_quote_status", "quoted_status", "retweeted_status", "quote_count", "reply_count", "retweet_count", "favorite_count", "entities", "extended_entities", "favorited", "retweeted", "possibly_sensitive", "filter_level", "lang", "matching_rules", "current_user_retweet", "scopes", "withheld_copyright", "withheld_in_countries", "withheld_scope");

        HashSet<String> tweetDataDictionary = new HashSet<String>();
        tweetDataDictionary.addAll(validFields);

        for (String field : fields){
            if (tweetDataDictionary.contains(field)) {
                continue;
            } else {
                throw new IllegalArgumentException("Invalid data field included in request.");
            }
        }
    }*/
}