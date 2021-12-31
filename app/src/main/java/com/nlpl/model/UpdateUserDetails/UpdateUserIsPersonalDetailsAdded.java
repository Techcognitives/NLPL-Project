package com.nlpl.model.UpdateUserDetails;

public class UpdateUserIsPersonalDetailsAdded {

    private String isPersonal_dt_added;

    public UpdateUserIsPersonalDetailsAdded(String isPersonal_dt_added) {
        this.isPersonal_dt_added = isPersonal_dt_added;
    }

    @Override
    public String toString() {
        return "UpdateUserIsPersonalDetailsAdded{" +
                "isPersonal_dt_added='" + isPersonal_dt_added + '\'' +
                '}';
    }

    public String getIsPersonal_dt_added() {
        return isPersonal_dt_added;
    }

    public void setIsPersonal_dt_added(String isPersonal_dt_added) {
        this.isPersonal_dt_added = isPersonal_dt_added;
    }
}
