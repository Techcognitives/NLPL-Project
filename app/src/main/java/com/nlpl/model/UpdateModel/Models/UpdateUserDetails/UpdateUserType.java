package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserType {

    private String user_type;

    public UpdateUserType(String user_type) {
        this.user_type = user_type;
    }

    @Override
    public String toString() {
        return "UpdateUserType{" +
                "user_type='" + user_type + '\'' +
                '}';
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
