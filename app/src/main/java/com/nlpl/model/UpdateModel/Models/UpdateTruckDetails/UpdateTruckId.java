package com.nlpl.model.UpdateModel.Models.UpdateTruckDetails;

public class UpdateTruckId {

    private String truck_id;

    public UpdateTruckId(String truck_id) {
        this.truck_id = truck_id;
    }

    @Override
    public String toString() {
        return "UpdateTruckId{" +
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
