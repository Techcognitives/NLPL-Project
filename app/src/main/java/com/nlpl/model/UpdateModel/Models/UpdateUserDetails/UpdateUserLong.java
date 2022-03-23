package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserLong {

    private String longitude;

    public UpdateUserLong(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "UpdateUserLong{" +
                "longitude='" + longitude + '\'' +
                '}';
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
