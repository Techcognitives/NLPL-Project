package com.nlpl.model.UpdateModel.Models.UpdateTruckDetails;

public class UpdateVehicleType {

    private String vehicle_type;

    public UpdateVehicleType(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    @Override
    public String toString() {
        return "UpdateVehicleType{" +
                "vehicle_type='" + vehicle_type + '\'' +
                '}';
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}
