package com.nlpl.model;

public class AddTruckRequest {
    String user_id, vehicle_no, vehicle_body_type;

    public AddTruckRequest() {
        this.user_id = user_id;
        this.vehicle_no = vehicle_no ;
        this.vehicle_body_type = vehicle_body_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getVehicle_body_type() {
        return vehicle_body_type;
    }

    public void setVehicle_body_type(String vehicle_body_type) {
        this.vehicle_body_type = vehicle_body_type;
    }
}
