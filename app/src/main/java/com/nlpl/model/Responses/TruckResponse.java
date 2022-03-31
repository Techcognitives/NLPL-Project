package com.nlpl.model.Responses;

import java.util.List;

public class TruckResponse {
    private String success;
    private List<TruckList> data;

    public String getSuccess() {
        return success;
    }

    public List<TruckList> getData() {
        return data;
    }

    public void setData(List<TruckList> data) {
        this.data = data;
    }

    public class TruckList {
        private String user_id,
                vehicle_no,
                truck_type,
                vehicle_type,
                truck_ft,
                truck_carrying_capacity,
                rc_book,
                vehicle_insurance,
                truck_id,
                driver_id,
                created_at,
                updated_at,
                updated_by,
                deleted_at,
                deleted_by,
                is_rc_verified,
                is_insurance_verified;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getVehicle_no() {
            return vehicle_no;
        }

        public void setVehicle_no(String vehicle_no) {
            this.vehicle_no = vehicle_no;
        }

        public String getTruck_type() {
            return truck_type;
        }

        public void setTruck_type(String truck_type) {
            this.truck_type = truck_type;
        }

        public String getVehicle_type() {
            return vehicle_type;
        }

        public void setVehicle_type(String vehicle_type) {
            this.vehicle_type = vehicle_type;
        }

        public String getTruck_ft() {
            return truck_ft;
        }

        public void setTruck_ft(String truck_ft) {
            this.truck_ft = truck_ft;
        }

        public String getTruck_carrying_capacity() {
            return truck_carrying_capacity;
        }

        public void setTruck_carrying_capacity(String truck_carrying_capacity) {
            this.truck_carrying_capacity = truck_carrying_capacity;
        }

        public String getRc_book() {
            return rc_book;
        }

        public void setRc_book(String rc_book) {
            this.rc_book = rc_book;
        }

        public String getVehicle_insurance() {
            return vehicle_insurance;
        }

        public void setVehicle_insurance(String vehicle_insurance) {
            this.vehicle_insurance = vehicle_insurance;
        }

        public String getTruck_id() {
            return truck_id;
        }

        public void setTruck_id(String truck_id) {
            this.truck_id = truck_id;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getUpdated_by() {
            return updated_by;
        }

        public void setUpdated_by(String updated_by) {
            this.updated_by = updated_by;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(String deleted_at) {
            this.deleted_at = deleted_at;
        }

        public String getDeleted_by() {
            return deleted_by;
        }

        public void setDeleted_by(String deleted_by) {
            this.deleted_by = deleted_by;
        }

        public String getIs_rc_verified() {
            return is_rc_verified;
        }

        public void setIs_rc_verified(String is_rc_verified) {
            this.is_rc_verified = is_rc_verified;
        }

        public String getIs_insurance_verified() {
            return is_insurance_verified;
        }

        public void setIs_insurance_verified(String is_insurance_verified) {
            this.is_insurance_verified = is_insurance_verified;
        }
    }
}
