package com.nlpl.model.UpdateTruckDetails;

public class UpdateTruckVehicleNumber {

    private String vehicle_no;

    public UpdateTruckVehicleNumber(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    @Override
    public String toString() {
        return "UpdateTruckVehicleNumber{" +
                "vehicle_no='" + vehicle_no + '\'' +
                '}';
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }
}
