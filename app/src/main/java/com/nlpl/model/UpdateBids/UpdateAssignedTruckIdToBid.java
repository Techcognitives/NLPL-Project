package com.nlpl.model.UpdateBids;

public class UpdateAssignedTruckIdToBid {
    String assigned_truck_id;

    public UpdateAssignedTruckIdToBid(String assigned_truck_id) {
        this.assigned_truck_id = assigned_truck_id;
    }

    @Override
    public String toString() {
        return "UpdateAssignedTruckIdToBid{" +
                "assigned_truck_id='" + assigned_truck_id + '\'' +
                '}';
    }
}
