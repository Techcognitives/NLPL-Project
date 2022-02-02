package com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails;

public class UpdateCompanyUserId {

    private String user_id;

    public UpdateCompanyUserId(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
