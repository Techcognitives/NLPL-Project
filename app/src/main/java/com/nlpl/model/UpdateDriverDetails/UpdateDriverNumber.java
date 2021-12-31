package com.nlpl.model.UpdateDriverDetails;

public class UpdateDriverNumber {

    private String driver_number;

    public UpdateDriverNumber(String driver_number) {
        this.driver_number = driver_number;
    }

    @Override
    public String toString() {
        return "UpdateDriverNumber{" +
                "driver_number='" + driver_number + '\'' +
                '}';
    }

    public String getDriver_number() {
        return driver_number;
    }

    public void setDriver_number(String driver_number) {
        this.driver_number = driver_number;
    }
}
