package com.nlpl.model.Responses;

import java.util.List;

public class AadharInfoResponse {
    private String success;

    private List<localAadhar> data;

    public List<localAadhar> getData() {
        return data;
    }

    public void setData(List<localAadhar> data) {
        this.data = data;
    }

    public class localAadhar{
        private String id, success, aadhaar_no, user_full_name,user_dob,
                user_gender,country,dist,state,po,street,house,address_zip,user_id,created_at,aadhaar_id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getAadhaar_no() {
            return aadhaar_no;
        }

        public void setAadhaar_no(String aadhaar_no) {
            this.aadhaar_no = aadhaar_no;
        }

        public String getUser_full_name() {
            return user_full_name;
        }

        public void setUser_full_name(String user_full_name) {
            this.user_full_name = user_full_name;
        }

        public String getUser_dob() {
            return user_dob;
        }

        public void setUser_dob(String user_dob) {
            this.user_dob = user_dob;
        }

        public String getUser_gender() {
            return user_gender;
        }

        public void setUser_gender(String user_gender) {
            this.user_gender = user_gender;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getDist() {
            return dist;
        }

        public void setDist(String dist) {
            this.dist = dist;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPo() {
            return po;
        }

        public void setPo(String po) {
            this.po = po;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getHouse() {
            return house;
        }

        public void setHouse(String house) {
            this.house = house;
        }

        public String getAddress_zip() {
            return address_zip;
        }

        public void setAddress_zip(String address_zip) {
            this.address_zip = address_zip;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getAadhaar_id() {
            return aadhaar_id;
        }

        public void setAadhaar_id(String aadhaar_id) {
            this.aadhaar_id = aadhaar_id;
        }
    }
}
