package com.nlpl.model.Responses;

import java.util.List;

public class UserResponse {

    String success;
    List<UserList> data;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
    }

    public class UserList {
        String user_id, name, phone_number, alternate_ph_no, user_type, preferred_location, preferred_language, address, state_code, pin_code, email_id, pay_type, created_at, updated_at, updated_by, deleted_at, deleted_by, latitude, longitude, device_id;
        int isRegistration_done, isProfile_pic_added, isTruck_added, isDriver_added, isBankDetails_given, isCompany_added, isPersonal_dt_added, is_Addhar_verfied, is_pan_verfied, is_user_verfied, is_account_active;

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

        public int getIsRegistration_done() {
            return isRegistration_done;
        }

        public void setIsRegistration_done(int isRegistration_done) {
            this.isRegistration_done = isRegistration_done;
        }

        public int getIsProfile_pic_added() {
            return isProfile_pic_added;
        }

        public void setIsProfile_pic_added(int isProfile_pic_added) {
            this.isProfile_pic_added = isProfile_pic_added;
        }

        public int getIsTruck_added() {
            return isTruck_added;
        }

        public void setIsTruck_added(int isTruck_added) {
            this.isTruck_added = isTruck_added;
        }

        public int getIsDriver_added() {
            return isDriver_added;
        }

        public void setIsDriver_added(int isDriver_added) {
            this.isDriver_added = isDriver_added;
        }

        public int getIsBankDetails_given() {
            return isBankDetails_given;
        }

        public void setIsBankDetails_given(int isBankDetails_given) {
            this.isBankDetails_given = isBankDetails_given;
        }

        public int getIsCompany_added() {
            return isCompany_added;
        }

        public void setIsCompany_added(int isCompany_added) {
            this.isCompany_added = isCompany_added;
        }

        public int getIsPersonal_dt_added() {
            return isPersonal_dt_added;
        }

        public void setIsPersonal_dt_added(int isPersonal_dt_added) {
            this.isPersonal_dt_added = isPersonal_dt_added;
        }

        public int getIs_Addhar_verfied() {
            return is_Addhar_verfied;
        }

        public void setIs_Addhar_verfied(int is_Addhar_verfied) {
            this.is_Addhar_verfied = is_Addhar_verfied;
        }

        public int getIs_pan_verfied() {
            return is_pan_verfied;
        }

        public void setIs_pan_verfied(int is_pan_verfied) {
            this.is_pan_verfied = is_pan_verfied;
        }

        public int getIs_user_verfied() {
            return is_user_verfied;
        }

        public void setIs_user_verfied(int is_user_verfied) {
            this.is_user_verfied = is_user_verfied;
        }

        public int getIs_account_active() {
            return is_account_active;
        }

        public void setIs_account_active(int is_account_active) {
            this.is_account_active = is_account_active;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
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
    }
}
