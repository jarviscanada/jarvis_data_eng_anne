package ca.jrvs.apps.twitter.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonPropertyOrder(
        {
                "hashtags",
                "user_mentions"
        }
)

@JsonIgnoreProperties(ignoreUnknown = true)

public class Entities {

    // Declaring JSON properties.
    @JsonProperty("hashtags")
    private Hashtag[] hashtags;
    @JsonProperty("user_mentions")
    private UserMention[] user_mentions;

    @JsonGetter("hashtags")
    public Hashtag[] getHashtags() {
        return hashtags;
    }
    @JsonSetter("hashtags")
    public void setHashtags(Hashtag[] hashtags) {
        this.hashtags = hashtags;
    }

    @JsonGetter("user_mentions")
    public UserMention[] getUserMentions() {
        return user_mentions;
    }
    @JsonSetter("user_mentions")
    public void setUserMentions(UserMention[] userMentions) {
        this.user_mentions = user_mentions;
    }
}
