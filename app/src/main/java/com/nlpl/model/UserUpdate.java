package com.nlpl.model;

public class UserUpdate {

    private String user_id;
    private String name;
    private int phone_number;
    private String user_type;
    private String preferred_location;
    private String address;
    private String state_code;
    private String pin_code;
    private int isRegistration_done;
    private String preferred_language;
    private String upload_aadhar;
    private String upload_pan;
    private int isTruck_added;
    private int isDriver_added;
    private int isBankDetails_Given;
    private int isCompany_added;
    private int isPersonal_dt_Added;

    public UserUpdate(String user_id, String name, Integer phone_number, String user_type, String preferred_location, String address, String state_code, String pin_code, Object isRegistration_done, String preferred_language, String upload_aadhar, String upload_pan, Object isTruck_added, Object isDriver_added, Object isBankDetails_Given, Object isCompany_added, Object isPersonal_dt_Added) {
        this.user_id = user_id;
        this.name = name;
        this.phone_number = phone_number;
        this.user_type = user_type;
        this.preferred_location = preferred_location;
        this.address = address;
        this.state_code = state_code;
        this.pin_code = pin_code;
        this.isRegistration_done = (int) isRegistration_done;
        this.preferred_language = preferred_language;
        this.upload_aadhar = upload_aadhar;
        this.upload_pan = upload_pan;
        this.isTruck_added = (int) isTruck_added;
        this.isDriver_added = (int) isDriver_added;
        this.isBankDetails_Given = (int) isBankDetails_Given;
        this.isCompany_added = (int) isCompany_added;
        this.isPersonal_dt_Added = (int) isPersonal_dt_Added;
    }

    @Override
    public String toString() {
        return "UserUpdate{" +
                "user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", user_type='" + user_type + '\'' +
                ", preferred_location='" + preferred_location + '\'' +
                ", address='" + address + '\'' +
                ", state_code='" + state_code + '\'' +
                ", pin_code='" + pin_code + '\'' +
                ", isRegistration_done=" + isRegistration_done +
                ", preferred_language='" + preferred_language + '\'' +
                ", upload_aadhar='" + upload_aadhar + '\'' +
                ", upload_pan='" + upload_pan + '\'' +
                ", isTruck_added=" + isTruck_added +
                ", isDriver_added=" + isDriver_added +
                ", isBankDetails_Given=" + isBankDetails_Given +
                ", isCompany_added=" + isCompany_added +
                ", isPersonal_dt_Added=" + isPersonal_dt_Added +
                '}';
    }

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

    public int getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(int phone_number) {
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

    public int getIsRegistration_done() {
        return isRegistration_done;
    }

    public void setIsRegistration_done(int isRegistration_done) {
        this.isRegistration_done = isRegistration_done;
    }

    public String getPreferred_language() {
        return preferred_language;
    }

    public void setPreferred_language(String preferred_language) {
        this.preferred_language = preferred_language;
    }

    public String getUpload_aadhar() {
        return upload_aadhar;
    }

    public void setUpload_aadhar(String upload_aadhar) {
        this.upload_aadhar = upload_aadhar;
    }

    public String getUpload_pan() {
        return upload_pan;
    }

    public void setUpload_pan(String upload_pan) {
        this.upload_pan = upload_pan;
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

    public int getIsBankDetails_Given() {
        return isBankDetails_Given;
    }

    public void setIsBankDetails_Given(int isBankDetails_Given) {
        this.isBankDetails_Given = isBankDetails_Given;
    }

    public int getIsCompany_added() {
        return isCompany_added;
    }

    public void setIsCompany_added(int isCompany_added) {
        this.isCompany_added = isCompany_added;
    }

    public int getIsPersonal_dt_Added() {
        return isPersonal_dt_Added;
    }

    public void setIsPersonal_dt_Added(int isPersonal_dt_Added) {
        this.isPersonal_dt_Added = isPersonal_dt_Added;
    }
}
