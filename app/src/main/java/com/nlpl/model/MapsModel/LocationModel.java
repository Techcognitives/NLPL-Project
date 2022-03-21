package com.nlpl.model.MapsModel;

import com.google.gson.annotations.SerializedName;

public class LocationModel {

    @SerializedName("City")
    private String City;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("url")
    private String url;

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
