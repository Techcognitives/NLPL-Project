package com.nlpl.model.UpdateDriverDetails;

public class UpdateDriverTruckId {

    private String truck_id;

    public UpdateDriverTruckId(String truck_id) {
        this.truck_id = truck_id;
    }

    @Override
    public String toString() {
        return "UpdateDriverTruckId{" +
                "truck_id='" + truck_id + '\'' +
                '}';
    }

    public String getTruck_id() {
        return truck_id;
    }

    public void setTruck_id(String truck_id) {
        this.truck_id = truck_id;
    }
}
