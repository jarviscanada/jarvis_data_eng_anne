package ca.jrvs.apps.twitter.model;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;

@JsonPropertyOrder(
        {
                "coordinates",
                "type"
        }
)

@JsonIgnoreProperties(ignoreUnknown = true)

public class Coordinates {

        // Declaring JSON properties.
        @JsonProperty("coordinates")
        private float[] coordinates;
        @JsonProperty("type")
        private String type;

        // Getters and setters.
        @JsonGetter("coordinates")
        public float[] getCoordinates() {
                return coordinates;
        }
        @JsonSetter("coordinates")
        public void setCoordinates(float[] coordinates) {
                this.coordinates = coordinates;
        }

        @JsonGetter("type")
        public String getType() {
                return type;
        }
        @JsonSetter("type")
        public void setType(String type) {
                this.type = type;
        }
}
