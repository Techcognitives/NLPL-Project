package com.nlpl.model.Responses;

import java.util.List;

public class AddDriverResponseGet {
    private String success;
    private List<driverDetailsById> data;

    public List<driverDetailsById> getData() {
        return data;
    }

    public void setData(List<driverDetailsById> data) {
        this.data = data;
    }


    public class driverDetailsById {
        private String user_id, truck_id, driver_id,driver_name,upload_dl,driver_number,alternate_ph_no,driver_emailId,
                driver_selfie,created_at,updated_at,updated_by,is_driver_deleted,deleted_at,deleted_by,is_dl_verified
                ,is_selfie_verified,dl_number,driver_dob;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
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

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getUpload_dl() {
            return upload_dl;
        }

        public void setUpload_dl(String upload_dl) {
            this.upload_dl = upload_dl;
        }

        public String getDriver_number() {
            return driver_number;
        }

        public void setDriver_number(String driver_number) {
            this.driver_number = driver_number;
        }

        public String getAlternate_ph_no() {
            return alternate_ph_no;
        }

        public void setAlternate_ph_no(String alternate_ph_no) {
            this.alternate_ph_no = alternate_ph_no;
        }

        public String getDriver_emailId() {
            return driver_emailId;
        }

        public void setDriver_emailId(String driver_emailId) {
            this.driver_emailId = driver_emailId;
        }

        public String getDriver_selfie() {
            return driver_selfie;
        }

        public void setDriver_selfie(String driver_selfie) {
            this.driver_selfie = driver_selfie;
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

        public String getIs_driver_deleted() {
            return is_driver_deleted;
        }

        public void setIs_driver_deleted(String is_driver_deleted) {
            this.is_driver_deleted = is_driver_deleted;
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

        public String getIs_dl_verified() {
            return is_dl_verified;
        }

        public void setIs_dl_verified(String is_dl_verified) {
            this.is_dl_verified = is_dl_verified;
        }

        public String getIs_selfie_verified() {
            return is_selfie_verified;
        }

        public void setIs_selfie_verified(String is_selfie_verified) {
            this.is_selfie_verified = is_selfie_verified;
        }

        public String getDl_number() {
            return dl_number;
        }

        public void setDl_number(String dl_number) {
            this.dl_number = dl_number;
        }

        public String getDriver_dob() {
            return driver_dob;
        }

        public void setDriver_dob(String driver_dob) {
            this.driver_dob = driver_dob;
        }
    }

}
