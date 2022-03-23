package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserLat {

    private String latitude;

    public UpdateUserLat(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "UpdateUserLat{" +
                "latitude='" + latitude + '\'' +
                '}';
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
