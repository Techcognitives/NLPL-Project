package com.nlpl.model.UpdateModel.Models.UpdateBankDetails;

public class UpdateBankUserId {

    private String user_id;

    public UpdateBankUserId(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UpdateBankUserId{" +
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
