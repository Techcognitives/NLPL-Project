package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserPANNumber {

    private String pan_number;

    public UpdateUserPANNumber(String pan_number) {
        this.pan_number = pan_number;
    }

    @Override
    public String toString() {
        return "UpdateUserPANNumber{" +
                "pan_number='" + pan_number + '\'' +
                '}';
    }

    public String getPan_number() {
        return pan_number;
    }

    public void setPan_number(String pan_number) {
        this.pan_number = pan_number;
    }
}
