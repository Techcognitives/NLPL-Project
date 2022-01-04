package com.nlpl.model.UpdateTruckDetails;

public class UpdateTruckCarryingCapacity {

    private String truck_carrying_capacity;

    public UpdateTruckCarryingCapacity(String truck_carrying_capacity) {
        this.truck_carrying_capacity = truck_carrying_capacity;
    }

    @Override
    public String toString() {
        return "UpdateTruckCarryingCapacity{" +
                "truck_carrying_capacity='" + truck_carrying_capacity + '\'' +
                '}';
    }

    public String getTruck_carrying_capacity() {
        return truck_carrying_capacity;
    }

    public void setTruck_carrying_capacity(String truck_carrying_capacity) {
        this.truck_carrying_capacity = truck_carrying_capacity;
    }
}
