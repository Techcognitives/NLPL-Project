package com.nlpl.model.UpdateModel.Models.UpdateDriverDetails;

public class UpdateDriverUserId {

    private String user_id;

    public UpdateDriverUserId(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UpdateDriverUserId{" +
                "user_id='" + user_id + '\'' +
                '}';
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
