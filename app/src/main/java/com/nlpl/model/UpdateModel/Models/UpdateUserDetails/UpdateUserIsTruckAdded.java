package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserIsTruckAdded {

    private String isTruck_added;

    public UpdateUserIsTruckAdded(String isTruck_added) {
        this.isTruck_added = isTruck_added;
    }

    @Override
    public String toString() {
        return "UpdateUserIsTruckAdded{" +
                "isTruck_added='" + isTruck_added + '\'' +
                '}';
    }

    public String getIsTruck_added() {
        return isTruck_added;
    }

    public void setIsTruck_added(String isTruck_added) {
        this.isTruck_added = isTruck_added;
    }
}
