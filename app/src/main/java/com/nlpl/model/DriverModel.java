package com.nlpl.model;

public class DriverModel {
    private String user_id;
    private String driver_id;
    private String driver_name;
    private String upload_lc;
    private String driver_number;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getUpload_lc() {
        return upload_lc;
    }

    public void setUpload_lc(String upload_lc) {
        this.upload_lc = upload_lc;
    }

    public String getDriver_number() {
        return driver_number;
    }

    public void setDriver_number(String driver_number) {
        this.driver_number = driver_number;
    }
}
