package com.nlpl.model.UpdateModel.Models.UpdateTruckDetails;

public class UpdateTruckType {

    private String truck_type;

    public UpdateTruckType(String truck_type) {
        this.truck_type = truck_type;
    }

    @Override
    public String toString() {
        return "UpdateTruckType{" +
                "truck_type='" + truck_type + '\'' +
                '}';
    }

    public String getTruck_type() {
        return truck_type;
    }

    public void setTruck_type(String truck_type) {
        this.truck_type = truck_type;
    }
}
