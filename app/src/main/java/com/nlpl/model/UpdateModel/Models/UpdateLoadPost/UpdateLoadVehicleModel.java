package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadVehicleModel {

    private String vehicle_model;

    public UpdateLoadVehicleModel(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    @Override
    public String toString() {
        return "UpdateLoadVehicleModel{" +
                "vehicle_model='" + vehicle_model + '\'' +
                '}';
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }
}
