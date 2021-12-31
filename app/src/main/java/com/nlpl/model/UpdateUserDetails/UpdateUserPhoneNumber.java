package com.nlpl.model.UpdateUserDetails;

public class UpdateUserPhoneNumber {

    private String phone_number;

    public UpdateUserPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "UpdateUserPhoneNumber{" +
                "phone_number='" + phone_number + '\'' +
                '}';
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
