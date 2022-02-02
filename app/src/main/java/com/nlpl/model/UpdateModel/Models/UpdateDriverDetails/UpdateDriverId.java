package com.nlpl.model.UpdateModel.Models.UpdateDriverDetails;

public class UpdateDriverId {

    private String driver_id;

    public UpdateDriverId(String driver_id) {
        this.driver_id = driver_id;
    }

    @Override
    public String toString() {
        return "UpdateDriverId{" +
                "driver_id='" + driver_id + '\'' +
                '}';
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }
}
