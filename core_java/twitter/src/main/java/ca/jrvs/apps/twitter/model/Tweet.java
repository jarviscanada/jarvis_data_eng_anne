package ca.jrvs.apps.twitter.model;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@JsonPropertyOrder(
        {
                "created_at",
                "id",
                "id_str",
                "text",
                "entities",
                "coordinates",
                "retweet_count",
                "favorite_count",
                "favorited",
                "retweeted"
        }
)

@JsonIgnoreProperties(ignoreUnknown = true)

public class Tweet {

    // Declaring JSON properties.
    @JsonProperty("created_at")
    private String created_at;
    @JsonProperty("id")
    private long id;
    @JsonProperty("id_str")
    private String id_str;
    @JsonProperty("text")
    private String text;
    @JsonProperty("entities")
    private Entities entities;
    @JsonProperty("coordinates")
    private Coordinates coordinates;
    @JsonProperty("retweet_count")
    private int retweet_count;
    @JsonProperty("favorite_count")
    private int favorite_count;
    @JsonProperty("favorited")
    boolean favorited;
    @JsonProperty("retweeted")
    boolean retweeted;

    @JsonGetter("created_at")
    public String getCreatedAt() {
        return created_at;
    }
    @JsonSetter("created_at")
    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    @JsonGetter("id")
    public long getId() {
        return id;
    }
    @JsonSetter("id")
    public void setId(long id) {
        this.id = id;
    }

    @JsonGetter("id_str")
    public String getIdStr() {
        return id_str;
    }
    @JsonSetter("id_str")
    public void setIdStr(String id_str) {
        this.id_str = id_str;
    }

    @JsonGetter("text")
    public String getText() {
        return text;
    }
    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonGetter("entities")
    public Entities getEntities() {
        return entities;
    }
    @JsonSetter("entities")
    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    @JsonGetter("coordinates")
    public Coordinates getCoordinates() {
        return coordinates;
    }
    @JsonSetter("coordinates")
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @JsonGetter("retweet_count")
    public int getRetweetCount() {
        return retweet_count;
    }
    @JsonSetter("retweet_count")
    public void setRetweetCount(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    @JsonGetter("favorite_count")
    public int getFavoriteCount() {
        return favorite_count;
    }
    @JsonSetter("favorite_count")
    public void setFavoriteCount(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    @JsonGetter("favorited")
    public boolean isFavorited() {
        return favorited;
    }
    @JsonSetter("favorited")
    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    @JsonGetter("retweeted")
    public boolean isRetweeted() {
        return retweeted;
    }
    @JsonSetter("retweeted")
    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }
}