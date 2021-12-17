package com.nlpl.model;

public class UserResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private String user_id;
        private String name;
        private String phone_number;
        private String user_type;
        private String preferred_location;
        private String address;
        private String state_code;
        private String pin_code;


        public Data(String name, String phone_number, String user_type, String preferred_location, String address, String state_code, String pin_code, String user_id ) {
            this.user_id = user_id;
            this.name = name;
            this.phone_number = phone_number;
            this.user_type = user_type;
            this.preferred_location = preferred_location;
            this.address = address;
            this.state_code = state_code;
            this.pin_code = pin_code;
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

}
