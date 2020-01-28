package ca.jrvs.apps.twitter.util;

import ca.jrvs.apps.twitter.model.Tweet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JsonUtil {
    /**
     * Convert a Java object to JSON string (serialize)
     * @param object input object
     * @return JSON String
     * @throws JsonProcessingException
     */
    public static String toJson(Object object, boolean prettyJson, boolean includedNullValues) throws JsonProcessingException{
        ObjectMapper map = new ObjectMapper();
        if (!includedNullValues){
            map.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        if (prettyJson){
            map.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return map.writeValueAsString(object);
    }

    /**
     * Parse JSON string to an object (deserialize).
     * @param json JSON String
     * @param objClass Object class
     * @param <T> type
     * @return Object
     * @throws IOException
     */
    public static <T>T toObjectFromJson(String json, Class objClass) throws IOException{
        ObjectMapper map = new ObjectMapper();
        return (T)map.readValue(json, objClass);
    }
}
