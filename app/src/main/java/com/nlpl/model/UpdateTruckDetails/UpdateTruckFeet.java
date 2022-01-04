package com.nlpl.model.UpdateTruckDetails;

public class UpdateTruckFeet {

    private String truck_ft;

    public UpdateTruckFeet(String truck_ft) {
        this.truck_ft = truck_ft;
    }

    @Override
    public String toString() {
        return "UpdateTruckFeet{" +
                "truck_ft='" + truck_ft + '\'' +
                '}';
    }

    public String getTruck_ft() {
        return truck_ft;
    }

    public void setTruck_ft(String truck_ft) {
        this.truck_ft = truck_ft;
    }
}
