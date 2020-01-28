package ca.jrvs.apps.twitter.model;

import com.fasterxml.jackson.annotation.*;

@JsonPropertyOrder(
        {
                "name",
                "indices",
                "screen_name",
                "id",
                "id_str"
        }
)

@JsonIgnoreProperties(ignoreUnknown = true)

public class UserMention {

    @JsonProperty("name")
    private String name;
    @JsonProperty("indices")
    private int[] indices;
    @JsonProperty("screen_name")
    private String screen_name;
    @JsonProperty("id")
    private long id;
    @JsonProperty("id_str")
    private String id_str;

    @JsonGetter("name")
    public String getName() {
        return name;
    }
    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("indices")
    public int[] getIndices() {
        return indices;
    }
    @JsonSetter("indices")
    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    @JsonGetter("screen_name")
    public String getScreenName() {
        return screen_name;
    }
    @JsonSetter("screen_name")
    public void setScreenName(String screen_name) {
        this.screen_name = screen_name;
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
}
