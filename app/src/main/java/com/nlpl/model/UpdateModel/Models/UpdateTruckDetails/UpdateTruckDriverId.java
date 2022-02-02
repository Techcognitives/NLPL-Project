package com.nlpl.model.UpdateModel.Models.UpdateTruckDetails;

public class UpdateTruckDriverId {

    private String driver_id;

    public UpdateTruckDriverId(String driver_id) {
        this.driver_id = driver_id;
    }

    @Override
    public String toString() {
        return "UpdateTruckDriverId{" +
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
