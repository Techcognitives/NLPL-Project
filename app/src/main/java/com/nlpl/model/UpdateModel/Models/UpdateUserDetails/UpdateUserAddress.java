package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserAddress {

    private String address;

    public UpdateUserAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UpdateUserAddress{" +
                "address='" + address + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
