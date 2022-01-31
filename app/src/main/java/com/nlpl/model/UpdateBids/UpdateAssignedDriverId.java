package com.nlpl.model.UpdateBids;

public class UpdateAssignedDriverId {
    String assigned_driver_id;

    public UpdateAssignedDriverId(String assigned_driver_id) {
        this.assigned_driver_id = assigned_driver_id;
    }

    @Override
    public String toString() {
        return "UpdateAssignedDriverId{" +
                "assigned_driver_id='" + assigned_driver_id + '\'' +
                '}';
    }

}
