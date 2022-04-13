package com.nlpl.model.UpdateModel.Models.UpdateDriverDetails;

public class UpdateDriverDOB {

    private String driver_dob;

    public UpdateDriverDOB(String driver_dob) {
        this.driver_dob = driver_dob;
    }

    @Override
    public String toString() {
        return "UpdateDriverDOB{" +
                "driver_dob='" + driver_dob + '\'' +
                '}';
    }

    public String getDriver_dob() {
        return driver_dob;
    }

    public void setDriver_dob(String driver_dob) {
        this.driver_dob = driver_dob;
    }
}
