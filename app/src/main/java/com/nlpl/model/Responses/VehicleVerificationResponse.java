package com.nlpl.model.Responses;

import java.util.List;

public class VehicleVerificationResponse {
    private String success;
    private List<UserList> data;

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
    }

    public class UserList {
    private String id,
                user_id,
                success,
                rc_blacklist_status,
                rc_chassis_no,
                rc_engine_no,
                financier,
                rc_expiry_date,
                vehicle_fuel_description,
                insurance_expiry_date,
                rc_registration_location,
                vehicle_maker_description,
                rc_tax_upto,
                user_name,
                rc_registration_date,
                rc_registration_number,
                rc_status,
                vehicle_class_description,
                rc_id,
                created_at;

        //getters and setters

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getRc_blacklist_status() {
            return rc_blacklist_status;
        }

        public void setRc_blacklist_status(String rc_blacklist_status) {
            this.rc_blacklist_status = rc_blacklist_status;
        }

        public String getRc_chassis_no() {
            return rc_chassis_no;
        }

        public void setRc_chassis_no(String rc_chassis_no) {
            this.rc_chassis_no = rc_chassis_no;
        }

        public String getRc_engine_no() {
            return rc_engine_no;
        }

        public void setRc_engine_no(String rc_engine_no) {
            this.rc_engine_no = rc_engine_no;
        }

        public String getFinancier() {
            return financier;
        }

        public void setFinancier(String financier) {
            this.financier = financier;
        }

        public String getRc_expiry_date() {
            return rc_expiry_date;
        }

        public void setRc_expiry_date(String rc_expiry_date) {
            this.rc_expiry_date = rc_expiry_date;
        }

        public String getVehicle_fuel_description() {
            return vehicle_fuel_description;
        }

        public void setVehicle_fuel_description(String vehicle_fuel_description) {
            this.vehicle_fuel_description = vehicle_fuel_description;
        }

        public String getInsurance_expiry_date() {
            return insurance_expiry_date;
        }

        public void setInsurance_expiry_date(String insurance_expiry_date) {
            this.insurance_expiry_date = insurance_expiry_date;
        }

        public String getRc_registration_location() {
            return rc_registration_location;
        }

        public void setRc_registration_location(String rc_registration_location) {
            this.rc_registration_location = rc_registration_location;
        }

        public String getVehicle_maker_description() {
            return vehicle_maker_description;
        }

        public void setVehicle_maker_description(String vehicle_maker_description) {
            this.vehicle_maker_description = vehicle_maker_description;
        }

        public String getRc_tax_upto() {
            return rc_tax_upto;
        }

        public void setRc_tax_upto(String rc_tax_upto) {
            this.rc_tax_upto = rc_tax_upto;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getRc_registration_date() {
            return rc_registration_date;
        }

        public void setRc_registration_date(String rc_registration_date) {
            this.rc_registration_date = rc_registration_date;
        }

        public String getRc_registration_number() {
            return rc_registration_number;
        }

        public void setRc_registration_number(String rc_registration_number) {
            this.rc_registration_number = rc_registration_number;
        }

        public String getRc_status() {
            return rc_status;
        }

        public void setRc_status(String rc_status) {
            this.rc_status = rc_status;
        }

        public String getVehicle_class_description() {
            return vehicle_class_description;
        }

        public void setVehicle_class_description(String vehicle_class_description) {
            this.vehicle_class_description = vehicle_class_description;
        }

        public String getRc_id() {
            return rc_id;
        }

        public void setRc_id(String rc_id) {
            this.rc_id = rc_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
