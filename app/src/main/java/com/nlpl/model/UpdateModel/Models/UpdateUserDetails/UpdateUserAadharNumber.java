package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserAadharNumber {

    private String aadhaar_number;

    public UpdateUserAadharNumber(String aadhaar_number) {
        this.aadhaar_number = aadhaar_number;
    }

    @Override
    public String toString() {
        return "UpdateUserAadharNumber{" +
                "aadhaar_number='" + aadhaar_number + '\'' +
                '}';
    }

    public String getAadhaar_number() {
        return aadhaar_number;
    }

    public void setAadhaar_number(String aadhaar_number) {
        this.aadhaar_number = aadhaar_number;
    }
}
