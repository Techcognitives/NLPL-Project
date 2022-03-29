package com.nlpl.model.Requests;

public class AddDriverRequest {
    String user_id, driver_name, driver_number, alternate_ph_no, driver_emailId, truck_id, dl_number;

    public AddDriverRequest() {
        this.user_id = user_id;
        this.driver_name = driver_name;
        this.driver_number = driver_number;
        this.driver_emailId = driver_emailId;
        this.truck_id = truck_id;
        this.alternate_ph_no = alternate_ph_no;
        this.dl_number = dl_number;
    }

    public String getDriver_emailId() {
        return driver_emailId;
    }

    public void setDriver_emailId(String driver_emailId) {
        this.driver_emailId = driver_emailId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_number() {
        return driver_number;
    }

    public void setDriver_number(String driver_number) {
        this.driver_number = driver_number;
    }

    public String getTruck_id() {
        return truck_id;
    }

    public void setTruck_id(String truck_id) {
        this.truck_id = truck_id;
    }

    public String getAlternate_ph_no() {
        return alternate_ph_no;
    }

    public void setAlternate_ph_no(String alternate_ph_no) {
        this.alternate_ph_no = alternate_ph_no;
    }

    public String getDl_number() {
        return dl_number;
    }

    public void setDl_number(String dl_number) {
        this.dl_number = dl_number;
    }
}
