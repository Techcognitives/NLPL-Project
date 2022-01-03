package com.nlpl.model.Requests;

public class AddTruckRequest {
    String user_id, vehicle_no, truck_type, vehicle_type, truck_ft, truck_carrying_capacity;

    public AddTruckRequest() {
        this.user_id = user_id;
        this.vehicle_no = vehicle_no;
        this.truck_type = truck_type;
        this.vehicle_type = vehicle_type;
        this.truck_ft = truck_ft;
        this.truck_carrying_capacity = truck_carrying_capacity;

    }

    public String getTruck_type() {
        return truck_type;
    }

    public void setTruck_type(String truck_type) {
        this.truck_type = truck_type;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getTruck_ft() {
        return truck_ft;
    }

    public void setTruck_ft(String truck_ft) {
        this.truck_ft = truck_ft;
    }

    public String getTruck_carrying_capacity() {
        return truck_carrying_capacity;
    }

    public void setTruck_carrying_capacity(String truck_carrying_capacity) {
        this.truck_carrying_capacity = truck_carrying_capacity;
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

}
