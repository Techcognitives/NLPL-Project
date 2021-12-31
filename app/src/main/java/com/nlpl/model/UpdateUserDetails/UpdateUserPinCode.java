package com.nlpl.model.UpdateUserDetails;

public class UpdateUserPinCode {

    private String pin_code;

    public UpdateUserPinCode(String pin_code) {
        this.pin_code = pin_code;
    }

    @Override
    public String toString() {
        return "UpdateUserPinCode{" +
                "pin_code='" + pin_code + '\'' +
                '}';
    }

    public String getPin_code() {
        return pin_code;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }
}
