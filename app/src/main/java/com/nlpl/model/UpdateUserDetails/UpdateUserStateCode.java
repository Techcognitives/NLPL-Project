package com.nlpl.model.UpdateUserDetails;

public class UpdateUserStateCode {

    private String state_code;

    public UpdateUserStateCode(String state_code) {
        this.state_code = state_code;
    }

    @Override
    public String toString() {
        return "UpdateUserStateCode{" +
                "state_code='" + state_code + '\'' +
                '}';
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }
}
