package com.example.apidressing.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    // Getters
    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
