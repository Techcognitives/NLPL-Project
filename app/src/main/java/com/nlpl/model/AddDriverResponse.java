package com.nlpl.model;

public class AddDriverResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    private AddDriverResponse.Data data;

    public AddDriverResponse.Data getData() {
        return data;
    }

    public void setData(AddDriverResponse.Data data) {
        this.data = data;
    }

    public class Data {

        private String user_id, driver_name, driver_number, driver_id;

        public Data(String user_id, String driver_name, String driver_number, String driver_id) {
            this.user_id = user_id;
            this.driver_name = driver_name;
            this.driver_number = driver_number;
            this.driver_id = driver_id;

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

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }
    }
}
