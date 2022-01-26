package com.nlpl.model.UpdateLoadPost;

public class UpdateLoadCapacity {

    private String capacity;

    public UpdateLoadCapacity(String capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "UpdateLoadCapacity{" +
                "capacity='" + capacity + '\'' +
                '}';
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
