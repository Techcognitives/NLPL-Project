package com.nlpl.model.UpdateDriverDetails;

public class UpdateDriverEmailId {

    private String driver_emailId;

    public UpdateDriverEmailId(String driver_emailId) {
        this.driver_emailId = driver_emailId;
    }

    @Override
    public String toString() {
        return "UpdateDriverEmailId{" +
                "driver_emailId='" + driver_emailId + '\'' +
                '}';
    }

    public String getDriver_emailId() {
        return driver_emailId;
    }

    public void setDriver_emailId(String driver_emailId) {
        this.driver_emailId = driver_emailId;
    }
}
