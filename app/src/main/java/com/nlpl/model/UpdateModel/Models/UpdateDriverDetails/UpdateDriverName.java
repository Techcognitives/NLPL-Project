package com.nlpl.model.UpdateModel.Models.UpdateDriverDetails;

public class UpdateDriverName {

    private String driver_name;

    public UpdateDriverName(String driver_name) {
        this.driver_name = driver_name;
    }

    @Override
    public String toString() {
        return "UpdateDriverName{" +
                "driver_name='" + driver_name + '\'' +
                '}';
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }
}
