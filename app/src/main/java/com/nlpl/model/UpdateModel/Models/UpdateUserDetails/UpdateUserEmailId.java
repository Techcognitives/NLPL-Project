package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserEmailId {

    private String email_id;

    public UpdateUserEmailId(String email_id) {
        this.email_id = email_id;
    }

    @Override
    public String toString() {
        return "UpdateUserEmailId{" +
                "email_id='" + email_id + '\'' +
                '}';
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}
