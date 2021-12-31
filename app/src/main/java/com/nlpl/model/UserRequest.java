package com.nlpl.model;

public class UserRequest {
    String name, phone_number, user_type, preferred_location, address, state_code, pin_code, preferred_language, email_id;
    int isRegistration_done;

    public UserRequest() {
        this.name = name;
        this.phone_number = phone_number ;
        this.user_type = user_type;
        this.preferred_location = preferred_location;
        this.address = address;
        this.state_code = state_code;
        this.isRegistration_done = isRegistration_done;
        this.pin_code = pin_code;
        this.preferred_language = preferred_language;
        this.email_id = email_id;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPreferred_language() {
        return preferred_language;
    }

    public void setPreferred_language(String preferred_language) {
        this.preferred_language = preferred_language;
    }

    public int getIsRegistration_done() {
        return isRegistration_done;
    }

    public void setIsRegistration_done(int isRegistration_done) {
        this.isRegistration_done = isRegistration_done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getPreferred_location() {
        return preferred_location;
    }

    public void setPreferred_location(String preferred_location) {
        this.preferred_location = preferred_location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getPin_code() {
        return pin_code;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }
}
