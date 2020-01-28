package ca.jrvs.apps.twitter.dao;

import ca.jrvs.apps.twitter.dao.helper.HttpHelper;
import ca.jrvs.apps.twitter.helper.TwitterHttpHelper;
import ca.jrvs.apps.twitter.model.Tweet;
import ca.jrvs.apps.twitter.util.JsonUtil;
import com.google.gdata.util.common.base.PercentEscaper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

@Repository
public class TwitterDao implements CrdDao<Tweet, String> {

    // URI constants.
    private static final String API_BASE_URI = "https://api.twitter.com";
    private static final String POST_PATH_URI = "/1.1/statuses/update.json";
    private static final String SHOW_PATH = "/1.1/statuses/show.json";
    private static final String DELETE_PATH = "/1.1/statuses/destroy.json";

    // URI symbols.
    private static final String QUERY_SYM = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUAL = "=";

    // Response code
    private static final int HTTP_OK = 200;

    private TwitterHttpHelper httpHelper;

    @Autowired
    public TwitterDao(TwitterHttpHelper httpHelper){
        this.httpHelper = httpHelper;
    }

    @Override
    public Tweet create(Tweet tweet) {
        // Construct a URI for your tweet.
        URI uri;
        try {
            uri = getPostURI(tweet);
        } catch (URISyntaxException e){
            throw new IllegalArgumentException("Invalid Tweet input", e);
        }

        // Execute the HTTP request.
        HttpResponse response = httpHelper.httpPost(uri);

        // Validate response and defer response to Tweet object.
        return parseResponseBody(response, HTTP_OK);
    }

    /**
     * Check response status code.
     * If it checks out, converts response entity to Tweet.
     */
    Tweet parseResponseBody(HttpResponse response, Integer expectedStatusCode) {
        Tweet tweet = null;

        // Check response status.
        int status = response.getStatusLine().getStatusCode();
        if (status != expectedStatusCode){
            try{
                System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (IOException e){
                System.out.println("Response has no entity.");
            }
            throw new RuntimeException("Unexpected HTTP status: " + status);
        }

        if (response.getEntity() == null){
            throw new RuntimeException("Empty response body.");
        }

        // Convert response entity to string.
        String jsonStr;
        try{
            jsonStr = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert entity to String", e);
        }

        // Deserialize JSON string to Tweet object.
        try{
            tweet = JsonUtil.toObjectFromJson(jsonStr, Tweet.class);
        } catch (IOException e){
            throw new RuntimeException("Unable to convert JSON string to Object", e);
        }
        return tweet;
    }

    /**
     * Create a URI based on the action that we are performing.
     * Translates StringBuilder to URI.
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public URI setURI(StringBuilder uri) throws URISyntaxException {
        URI parsedURI = new URI(uri.toString());
        return parsedURI;
    }

    /**
     * Constructs the post/update action URI.
     * @param tweet object containing status update text and geographic data.
     * @return post/update URI
     * @throws URISyntaxException
     */
    private URI getPostURI(Tweet tweet) throws URISyntaxException{

        String status = tweet.getText();
        float longitude = tweet.getCoordinates().getCoordinates()[0];
        float latitude = tweet.getCoordinates().getCoordinates()[1];

        // Construct a StringBuilder object to create the URI string,
        // and the declare corresponding (post/publish) URI object that
        // the method will return.
        StringBuilder uri = new StringBuilder();
        URI postURI;
        PercentEscaper escaper = new PercentEscaper("",false);
        // Transform status to a URI-safe version.
        status = escaper.escape(status);
        // Piece together the intended URI.
        // Sample - https://api.twitter.com/1.1/statuses/update.json
        uri.append(API_BASE_URI).append(POST_PATH_URI).append(QUERY_SYM);
        appendParameter(uri, "status", status, true);
        appendParameter(uri, "long", Float.toString(longitude), false);
        appendParameter(uri, "lat", Float.toString(latitude), false);

        // Convert the StringBuilder to string so it can be parsed into an URI object.
        postURI = setURI(uri);

        return postURI;
    }

    /**
     * Constructs the show/view action URI.
     * @param id Tweet ID
     * @return Constructed Tweet URI
     * @throws URISyntaxException
     */
    private URI getShowURI(String id) throws URISyntaxException{
        // Construct a StringBuilder object to create the URI string,
        // and declare the corresponding (show/view) URI object that the
        // method will return.
        StringBuilder uri = new StringBuilder();
        URI showURI;
        // Piece together the intended URI.
        // Sample - https://
        uri.append(API_BASE_URI).append(SHOW_PATH).append(QUERY_SYM);
        appendParameter(uri, "id", id, true);
        // Convert the StringBuilder to string so it can be parsed into an URI object.
        showURI = setURI(uri);

        return showURI;
    }

    /**
     * Constructs the delete action URI.
     * @param id Tweet ID.
     * @return Delete Tweet URI.
     * @throws URISyntaxException
     */
    private URI getDeleteURI(String id) throws URISyntaxException{
        // Construct a StringBuilder object to create the URI string,
        // and declare the corresponding (delete action) URI object that
        // the method will return.
        StringBuilder uri = new StringBuilder();
        URI deleteURI;
        // Piece together the intended URI.

        uri.append(API_BASE_URI).append(DELETE_PATH).append(QUERY_SYM);
        uri.append("id").append(EQUAL).append(id);
        // Convert the StringBuilder to string so it can be parsed into an URI object.
        deleteURI = setURI(uri);

        return deleteURI;
    }

    /**
     * Helper method to add/pass parameters for URI builders.
     * @param actionURI the URI already built with the desired action (e.g. show/delete).
     * @param key JSON property key.
     * @param value JSON property value.
     * @param isFirstParameter determines if an ampersand is required to pass on additional parameters.
     */
    private void appendParameter(StringBuilder actionURI, String key, String value, boolean isFirstParameter){
        if (!isFirstParameter){
            actionURI.append(AMPERSAND);
        }
        actionURI.append(key).append(EQUAL).append(value);
    }

    /**
     * Construct a pre-existing Tweet (show) given an ID number.
     * @param id Tweet ID.
     * @return Tweet object.
     */
    @Override
    public Tweet findById(String id) {
        // Declare the URI.
        URI showURI;
        // Construct the URI.
        try{
            showURI = getShowURI(id);
        } catch (URISyntaxException e){
            throw new IllegalArgumentException("Invalid ID input", e);
        }
        // Execute the HTTP request.
        HttpResponse response = httpHelper.httpGet(showURI);
        // Validate and deserialize the response.
        return parseResponseBody(response, HTTP_OK);
    }

    /**
     * Deleting a Tweet given an ID number.
     * @param id of the Tweet to be deleted.
     * @return
     */
    @Override
    public Tweet deleteById(String id) {
        // Declare the URI.
        URI deleteURI;
        // Construct the URI.
        try{
            deleteURI = getDeleteURI(id);
        } catch (URISyntaxException e){
            throw new IllegalArgumentException("Invalid ID input", e);
        }
        // Execute the HTTP request.
        HttpResponse response = httpHelper.httpPost(deleteURI);
        // Validate and deserialize the response.
        return parseResponseBody(response, HTTP_OK);
    }
}
