package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserDeviceId {

    private String device_id;

    public UpdateUserDeviceId(String device_id) {
        this.device_id = device_id;
    }

    @Override
    public String toString() {
        return "UpdateUserDeviceId{" +
                "device_id='" + device_id + '\'' +
                '}';
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
