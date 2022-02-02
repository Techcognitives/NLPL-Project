package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserIsDriverAdded {

    private String isDriver_added;

    public UpdateUserIsDriverAdded(String isDriver_added) {
        this.isDriver_added = isDriver_added;
    }

    @Override
    public String toString() {
        return "UpdateUserIsDriverAdded{" +
                "isDriver_added='" + isDriver_added + '\'' +
                '}';
    }

    public String getIsDriver_added() {
        return isDriver_added;
    }

    public void setIsDriver_added(String isDriver_added) {
        this.isDriver_added = isDriver_added;
    }
}
