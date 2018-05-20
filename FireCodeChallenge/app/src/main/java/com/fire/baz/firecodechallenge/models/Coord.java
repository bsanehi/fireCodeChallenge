package com.fire.baz.firecodechallenge.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coord {

    @SerializedName("lon")
    @Expose
    private Double lon;

    @SerializedName("lat")
    @Expose
    private Double lat;

    public Double getLongitude() {
        return lon;
    }

    public void setLongitude(Double lon) {
        this.lon = lon;
    }

    public Double getLatitude() {
        return lat;
    }

    public void setLatitude(Double lat) {
        this.lat = lat;
    }
}
