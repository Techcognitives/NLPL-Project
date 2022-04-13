package com.nlpl.model.Requests;

public class UserRequest {
    String name, phone_number, user_type, preferred_location, address, state_code, pin_code, preferred_language, email_id, alternate_ph_no, latitude, longitude, device_id;
    int isProfile_pic_added, isRegistration_done, isTruck_added, isDriver_added, isBankDetails_given, isCompany_added, isPersonal_dt_added, is_Addhar_verfied, is_pan_verfied, is_user_verfied, is_account_active, is_self_added_asDriver;

    public UserRequest() {
        this.name = name;
        this.phone_number = phone_number;
        this.user_type = user_type;
        this.preferred_location = preferred_location;
        this.address = address;
        this.isPersonal_dt_added = isPersonal_dt_added;
        this.state_code = state_code;
        this.isRegistration_done = isRegistration_done;
        this.pin_code = pin_code;
        this.preferred_language = preferred_language;
        this.email_id = email_id;
        this.isBankDetails_given = isBankDetails_given;
        this.isTruck_added = isTruck_added;
        this.isDriver_added = isDriver_added;
        this.isCompany_added = isCompany_added;
        this.isProfile_pic_added = isProfile_pic_added;
        this.alternate_ph_no = alternate_ph_no;
        this.is_Addhar_verfied = is_Addhar_verfied;
        this.is_pan_verfied = is_pan_verfied;
        this.is_user_verfied = is_user_verfied;
        this.is_account_active = is_account_active;
        this.latitude = latitude;
        this.longitude = longitude;
        this.device_id = device_id;
        this.is_self_added_asDriver = is_self_added_asDriver;
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

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPreferred_language() {
        return preferred_language;
    }

    public void setPreferred_language(String preferred_language) {
        this.preferred_language = preferred_language;
    }

    public int getIsRegistration_done() {
        return isRegistration_done;
    }

    public void setIsRegistration_done(int isRegistration_done) {
        this.isRegistration_done = isRegistration_done;
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

    public String getAlternate_ph_no() {
        return alternate_ph_no;
    }

    public void setAlternate_ph_no(String alternate_ph_no) {
        this.alternate_ph_no = alternate_ph_no;
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

    public int getIs_self_added_asDriver() {
        return is_self_added_asDriver;
    }

    public void setIs_self_added_asDriver(int is_self_added_asDriver) {
        this.is_self_added_asDriver = is_self_added_asDriver;
    }
}
