package com.nlpl.model;

import java.util.List;

public class GetUserByPhoneResponse {
    private String success;
    private List<UserList> data;

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
    }

    public class UserList {
        public String user_id,name,phone_number,alternate_ph_no,user_type,preferred_location,preferred_language,address,
                state_code,pin_code,email_id,pay_type,isRegistration_done,isProfile_pic_added,isTruck_added,isDriver_added,
                isBankDetails_given,isCompany_added,isPersonal_dt_added,is_Addhar_verfied,is_pan_verfied,is_user_verfied,
                is_account_active,created_at,updated_at,updated_by,deleted_at,deleted_by,latitude,id,longitude,device_id,
                pan_number,aadhaar_number,is_self_added_asDriver;

        //getters and setters

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
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

        public String getAlternate_ph_no() {
            return alternate_ph_no;
        }

        public void setAlternate_ph_no(String alternate_ph_no) {
            this.alternate_ph_no = alternate_ph_no;
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

        public String getPreferred_language() {
            return preferred_language;
        }

        public void setPreferred_language(String preferred_language) {
            this.preferred_language = preferred_language;
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

        public String getEmail_id() {
            return email_id;
        }

        public void setEmail_id(String email_id) {
            this.email_id = email_id;
        }

        public String getPay_type() {
            return pay_type;
        }

        public void setPay_type(String pay_type) {
            this.pay_type = pay_type;
        }

        public String getIsRegistration_done() {
            return isRegistration_done;
        }

        public void setIsRegistration_done(String isRegistration_done) {
            this.isRegistration_done = isRegistration_done;
        }

        public String getIsProfile_pic_added() {
            return isProfile_pic_added;
        }

        public void setIsProfile_pic_added(String isProfile_pic_added) {
            this.isProfile_pic_added = isProfile_pic_added;
        }

        public String getIsTruck_added() {
            return isTruck_added;
        }

        public void setIsTruck_added(String isTruck_added) {
            this.isTruck_added = isTruck_added;
        }

        public String getIsDriver_added() {
            return isDriver_added;
        }

        public void setIsDriver_added(String isDriver_added) {
            this.isDriver_added = isDriver_added;
        }

        public String getIsBankDetails_given() {
            return isBankDetails_given;
        }

        public void setIsBankDetails_given(String isBankDetails_given) {
            this.isBankDetails_given = isBankDetails_given;
        }

        public String getIsCompany_added() {
            return isCompany_added;
        }

        public void setIsCompany_added(String isCompany_added) {
            this.isCompany_added = isCompany_added;
        }

        public String getIsPersonal_dt_added() {
            return isPersonal_dt_added;
        }

        public void setIsPersonal_dt_added(String isPersonal_dt_added) {
            this.isPersonal_dt_added = isPersonal_dt_added;
        }

        public String getIs_Addhar_verfied() {
            return is_Addhar_verfied;
        }

        public void setIs_Addhar_verfied(String is_Addhar_verfied) {
            this.is_Addhar_verfied = is_Addhar_verfied;
        }

        public String getIs_pan_verfied() {
            return is_pan_verfied;
        }

        public void setIs_pan_verfied(String is_pan_verfied) {
            this.is_pan_verfied = is_pan_verfied;
        }

        public String getIs_user_verfied() {
            return is_user_verfied;
        }

        public void setIs_user_verfied(String is_user_verfied) {
            this.is_user_verfied = is_user_verfied;
        }

        public String getIs_account_active() {
            return is_account_active;
        }

        public void setIs_account_active(String is_account_active) {
            this.is_account_active = is_account_active;
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

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getPan_number() {
            return pan_number;
        }

        public void setPan_number(String pan_number) {
            this.pan_number = pan_number;
        }

        public String getAadhaar_number() {
            return aadhaar_number;
        }

        public void setAadhaar_number(String aadhaar_number) {
            this.aadhaar_number = aadhaar_number;
        }

        public String getIs_self_added_asDriver() {
            return is_self_added_asDriver;
        }

        public void setIs_self_added_asDriver(String is_self_added_asDriver) {
            this.is_self_added_asDriver = is_self_added_asDriver;
        }
    }
}

