package ca.jrvs.apps.twitter.model;

import com.fasterxml.jackson.annotation.*;

@JsonPropertyOrder(
        {
                "text",
                "indices"
        }
)

@JsonIgnoreProperties(ignoreUnknown = true)

public class Hashtag {

    // Declaring JSON properties.
    @JsonProperty("text")
    String text;
    @JsonProperty("indices")
    int[] indices;

    @JsonGetter("text")
    public String getText() {
        return text;
    }
    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonGetter("indices")
    public int[] getIndices() {
        return indices;
    }
    @JsonSetter("indices")
    public void setIndices(int[] indices) {
        this.indices = indices;
    }
}
