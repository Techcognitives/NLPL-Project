package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserIsDriverAddedAlready {

    private String is_self_added_asDriver;

    public UpdateUserIsDriverAddedAlready(String is_self_added_asDriver) {
        this.is_self_added_asDriver = is_self_added_asDriver;
    }

    @Override
    public String toString() {
        return "UpdateUserIsDriverAddedAlready{" +
                "is_self_added_asDriver='" + is_self_added_asDriver + '\'' +
                '}';
    }

    public String getIs_self_added_asDriver() {
        return is_self_added_asDriver;
    }

    public void setIs_self_added_asDriver(String is_self_added_asDriver) {
        this.is_self_added_asDriver = is_self_added_asDriver;
    }
}
