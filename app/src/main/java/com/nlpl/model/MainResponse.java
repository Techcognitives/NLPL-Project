package com.nlpl.model;

import java.util.ArrayList;

public class MainResponse {
    private String succss;
    public Data data;


    // Getter Methods

    public String getSuccss() {
        return succss;
    }

    public Data getData() {
        return data;
    }

    // Setter Methods

    public void setSuccss(String succss) {
        this.succss = succss;
    }

    public Data setData() {
        return data;
    }

    public class Data {
        //personal user details
        private String user_id;
        private String name;
        private String phone_number;
        private String alternate_ph_no;
        private String user_type;
        private String preferred_location;
        private String preferred_language = null;
        private String address;
        private String state_code;
        private String pin_code;
        private String email_id;
        private String pay_type = null;
        private String isRegistration_done;
        private String isProfile_pic_added;
        private String isTruck_added;
        private String isDriver_added;
        private String isBankDetails_given;
        private String isCompany_added;
        private String isPersonal_dt_added;
        private String is_Addhar_verfied;
        private String is_pan_verfied;
        private String is_user_verfied;
        private String is_account_active;
        private String created_at;
        private String updated_at;
        private String updated_by = null;
        private String deleted_at = null;
        private String deleted_by = null;
        private String id;
        private String latitude;
        private String longitude;
        private String device_id;
        private String pan_number = null;
        private String aadhaar_number;
        private String is_self_added_asDriver;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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


        /**************************Personal user detail end**********************************/

        /***************************Truck details getters and setters start ************************/
        ArrayList<TruckDetails> truckdetails = new ArrayList<TruckDetails>();

        //Array getters and setters
        public ArrayList<TruckDetails> getTruckdetails() {
            return truckdetails;
        }

        public void setTruckdetails(ArrayList<TruckDetails> truckdetails) {
            this.truckdetails = truckdetails;
        }

        //TruckDetails object getters and setters
        public class TruckDetails {
            public String user_id, vehicle_no, truck_type, vehicle_type, truck_ft, truck_carrying_capacity, rc_book,
                    vehicle_insurance, truck_id, driver_id, created_at, updated_at, updated_by, deleted_at, deleted_by,
                    is_rc_verified, is_insurance_verified;

            //getters and setters

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
        /***********************************Truck details end ************************************/

        /***********************************Driver details Start**********************************/

        //Array getters and setters
        ArrayList<DriverDetails> driverdetails = new ArrayList<DriverDetails>();

        public ArrayList<DriverDetails> getDriverDetails() {
            return driverdetails;
        }

        public void setDriverDetails(ArrayList<DriverDetails> driverDetails) {
            this.driverdetails = driverDetails;
        }

        //Object Getters and setters
        public class DriverDetails {
            public String user_id, truck_id, driver_id, driver_name, upload_dl, driver_number, alternate_ph_no,
                    driver_emailId, driver_selfie, created_at, updated_at, updated_by, is_driver_deleted, deleted_at,
                    deleted_by, is_dl_verified, is_selfie_verified, dl_number, driver_dob;

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
        /*****************************************Driver Details end **********************************/

        /*****************************************Bank Details start **********************************/

        //Array getters and setters
        ArrayList<BankDetails> bankDetails = new ArrayList<BankDetails>();

        public ArrayList<DriverDetails> getDriverdetails() {
            return driverdetails;
        }

        public void setDriverdetails(ArrayList<DriverDetails> driverdetails) {
            this.driverdetails = driverdetails;
        }

        public ArrayList<BankDetails> getBankDetails() {
            return bankDetails;
        }

        public void setBankDetails(ArrayList<BankDetails> bankDetails) {
            this.bankDetails = bankDetails;
        }

        public class BankDetails {
            public String user_id, accountholder_name, account_number, re_enter_acc_num, IFSI_CODE, bank_id,
                    bank_name, cancelled_cheque, created_at, updated_at, updated_by, deleted_at, deleted_by,
                    bank_ref_no, beneficiary_name, transaction_remark, verification_status;

            //Object getters and setters

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getAccountholder_name() {
                return accountholder_name;
            }

            public void setAccountholder_name(String accountholder_name) {
                this.accountholder_name = accountholder_name;
            }

            public String getAccount_number() {
                return account_number;
            }

            public void setAccount_number(String account_number) {
                this.account_number = account_number;
            }

            public String getRe_enter_acc_num() {
                return re_enter_acc_num;
            }

            public void setRe_enter_acc_num(String re_enter_acc_num) {
                this.re_enter_acc_num = re_enter_acc_num;
            }

            public String getIFSI_CODE() {
                return IFSI_CODE;
            }

            public void setIFSI_CODE(String IFSI_CODE) {
                this.IFSI_CODE = IFSI_CODE;
            }

            public String getBank_id() {
                return bank_id;
            }

            public void setBank_id(String bank_id) {
                this.bank_id = bank_id;
            }

            public String getBank_name() {
                return bank_name;
            }

            public void setBank_name(String bank_name) {
                this.bank_name = bank_name;
            }

            public String getCancelled_cheque() {
                return cancelled_cheque;
            }

            public void setCancelled_cheque(String cancelled_cheque) {
                this.cancelled_cheque = cancelled_cheque;
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

            public String getBank_ref_no() {
                return bank_ref_no;
            }

            public void setBank_ref_no(String bank_ref_no) {
                this.bank_ref_no = bank_ref_no;
            }

            public String getBeneficiary_name() {
                return beneficiary_name;
            }

            public void setBeneficiary_name(String beneficiary_name) {
                this.beneficiary_name = beneficiary_name;
            }

            public String getTransaction_remark() {
                return transaction_remark;
            }

            public void setTransaction_remark(String transaction_remark) {
                this.transaction_remark = transaction_remark;
            }

            public String getVerification_status() {
                return verification_status;
            }

            public void setVerification_status(String verification_status) {
                this.verification_status = verification_status;
            }
        }
        /******************************************Bank details end ***********************************/

        /******************************************Company details start ***********************************/

        ArrayList<CompanyDetails> companyDetails = new ArrayList<CompanyDetails>();

        //Array getters and setters
        public ArrayList<CompanyDetails> getCompanyDetails() {
            return companyDetails;
        }

        public void setCompanyDetails(ArrayList<CompanyDetails> companyDetails) {
            this.companyDetails = companyDetails;
        }

        //Getters and setters
        public class CompanyDetails {
            public String company_id, company_name, company_gst_no, company_pan, comp_state, comp_city,
                    comp_add, comp_zip, user_id, company_type, created_at, updated_at, updated_by, deleted_at,
                    deleted_by;

            public String getCompany_id() {
                return company_id;
            }

            public void setCompany_id(String company_id) {
                this.company_id = company_id;
            }

            public String getCompany_name() {
                return company_name;
            }

            public void setCompany_name(String company_name) {
                this.company_name = company_name;
            }

            public String getCompany_gst_no() {
                return company_gst_no;
            }

            public void setCompany_gst_no(String company_gst_no) {
                this.company_gst_no = company_gst_no;
            }

            public String getCompany_pan() {
                return company_pan;
            }

            public void setCompany_pan(String company_pan) {
                this.company_pan = company_pan;
            }

            public String getComp_state() {
                return comp_state;
            }

            public void setComp_state(String comp_state) {
                this.comp_state = comp_state;
            }

            public String getComp_city() {
                return comp_city;
            }

            public void setComp_city(String comp_city) {
                this.comp_city = comp_city;
            }

            public String getComp_add() {
                return comp_add;
            }

            public void setComp_add(String comp_add) {
                this.comp_add = comp_add;
            }

            public String getComp_zip() {
                return comp_zip;
            }

            public void setComp_zip(String comp_zip) {
                this.comp_zip = comp_zip;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getCompany_type() {
                return company_type;
            }

            public void setCompany_type(String company_type) {
                this.company_type = company_type;
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
        }
        /********************************************Company details end **************************************/

        /********************************************Post a Load details start*********************************/
        ArrayList<PostaLoadDetails> postaLoadDetails = new ArrayList<PostaLoadDetails>();

        //Array getters and setters
        public ArrayList<PostaLoadDetails> getPostaLoadDetails() {
            return postaLoadDetails;
        }

        public void setPostaLoadDetails(ArrayList<PostaLoadDetails> postaLoadDetails) {
            this.postaLoadDetails = postaLoadDetails;
        }

        //getters and setters
        public class PostaLoadDetails {
            public String idpost_load, user_id, pick_up_date, pick_up_time, budget, revised_budget_one, revised_budget_two, bid_status,
                    vehicle_model, feet, capacity, body_type, pick_add, pick_pin_code, pick_city, pick_state, pick_country, drop_add,
                    drop_pin_code, drop_city, drop_state, drop_country, km_approx, notes_meterial_des, bid_posted_at, bid_ends_at,
                    sp_count, updated_at, updated_by, deleted_at, deleted_by, payment_type;

            public String getIdpost_load() {
                return idpost_load;
            }

            public void setIdpost_load(String idpost_load) {
                this.idpost_load = idpost_load;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getPick_up_date() {
                return pick_up_date;
            }

            public void setPick_up_date(String pick_up_date) {
                this.pick_up_date = pick_up_date;
            }

            public String getPick_up_time() {
                return pick_up_time;
            }

            public void setPick_up_time(String pick_up_time) {
                this.pick_up_time = pick_up_time;
            }

            public String getBudget() {
                return budget;
            }

            public void setBudget(String budget) {
                this.budget = budget;
            }

            public String getRevised_budget_one() {
                return revised_budget_one;
            }

            public void setRevised_budget_one(String revised_budget_one) {
                this.revised_budget_one = revised_budget_one;
            }

            public String getRevised_budget_two() {
                return revised_budget_two;
            }

            public void setRevised_budget_two(String revised_budget_two) {
                this.revised_budget_two = revised_budget_two;
            }

            public String getBid_status() {
                return bid_status;
            }

            public void setBid_status(String bid_status) {
                this.bid_status = bid_status;
            }

            public String getVehicle_model() {
                return vehicle_model;
            }

            public void setVehicle_model(String vehicle_model) {
                this.vehicle_model = vehicle_model;
            }

            public String getFeet() {
                return feet;
            }

            public void setFeet(String feet) {
                this.feet = feet;
            }

            public String getCapacity() {
                return capacity;
            }

            public void setCapacity(String capacity) {
                this.capacity = capacity;
            }

            public String getBody_type() {
                return body_type;
            }

            public void setBody_type(String body_type) {
                this.body_type = body_type;
            }

            public String getPick_add() {
                return pick_add;
            }

            public void setPick_add(String pick_add) {
                this.pick_add = pick_add;
            }

            public String getPick_pin_code() {
                return pick_pin_code;
            }

            public void setPick_pin_code(String pick_pin_code) {
                this.pick_pin_code = pick_pin_code;
            }

            public String getPick_city() {
                return pick_city;
            }

            public void setPick_city(String pick_city) {
                this.pick_city = pick_city;
            }

            public String getPick_state() {
                return pick_state;
            }

            public void setPick_state(String pick_state) {
                this.pick_state = pick_state;
            }

            public String getPick_country() {
                return pick_country;
            }

            public void setPick_country(String pick_country) {
                this.pick_country = pick_country;
            }

            public String getDrop_add() {
                return drop_add;
            }

            public void setDrop_add(String drop_add) {
                this.drop_add = drop_add;
            }

            public String getDrop_pin_code() {
                return drop_pin_code;
            }

            public void setDrop_pin_code(String drop_pin_code) {
                this.drop_pin_code = drop_pin_code;
            }

            public String getDrop_city() {
                return drop_city;
            }

            public void setDrop_city(String drop_city) {
                this.drop_city = drop_city;
            }

            public String getDrop_state() {
                return drop_state;
            }

            public void setDrop_state(String drop_state) {
                this.drop_state = drop_state;
            }

            public String getDrop_country() {
                return drop_country;
            }

            public void setDrop_country(String drop_country) {
                this.drop_country = drop_country;
            }

            public String getKm_approx() {
                return km_approx;
            }

            public void setKm_approx(String km_approx) {
                this.km_approx = km_approx;
            }

            public String getNotes_meterial_des() {
                return notes_meterial_des;
            }

            public void setNotes_meterial_des(String notes_meterial_des) {
                this.notes_meterial_des = notes_meterial_des;
            }

            public String getBid_posted_at() {
                return bid_posted_at;
            }

            public void setBid_posted_at(String bid_posted_at) {
                this.bid_posted_at = bid_posted_at;
            }

            public String getBid_ends_at() {
                return bid_ends_at;
            }

            public void setBid_ends_at(String bid_ends_at) {
                this.bid_ends_at = bid_ends_at;
            }

            public String getSp_count() {
                return sp_count;
            }

            public void setSp_count(String sp_count) {
                this.sp_count = sp_count;
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

            public String getPayment_type() {
                return payment_type;
            }

            public void setPayment_type(String payment_type) {
                this.payment_type = payment_type;
            }
        }
        /***************************************Company details end ***************************************/

        /***************************************Preferred locations start *********************************/

        ArrayList<PreferredLocation> preferredLocations = new ArrayList<PreferredLocation>();

        //Array getters and setters
        public ArrayList<PreferredLocation> getPreferredLocations() {
            return preferredLocations;
        }

        public void setPreferredLocations(ArrayList<PreferredLocation> preferredLocations) {
            this.preferredLocations = preferredLocations;
        }

        public class PreferredLocation {
            public String pref_locations_id, pref_state, pref_city, pref_pin_code, latitude, longitude, user_id, created_at;

            //getters and setters

            public String getPref_locations_id() {
                return pref_locations_id;
            }

            public void setPref_locations_id(String pref_locations_id) {
                this.pref_locations_id = pref_locations_id;
            }

            public String getPref_state() {
                return pref_state;
            }

            public void setPref_state(String pref_state) {
                this.pref_state = pref_state;
            }

            public String getPref_city() {
                return pref_city;
            }

            public void setPref_city(String pref_city) {
                this.pref_city = pref_city;
            }

            public String getPref_pin_code() {
                return pref_pin_code;
            }

            public void setPref_pin_code(String pref_pin_code) {
                this.pref_pin_code = pref_pin_code;
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
        }
        /********************************************Preferred location end*******************************/

        /********************************************User Ratings start***********************************/

        ArrayList<UserRatings> userRatings = new ArrayList<UserRatings>();

        //Array getters and setters

        public ArrayList<UserRatings> getUserRatings() {
            return userRatings;
        }

        public void setUserRatings(ArrayList<UserRatings> userRatings) {
            this.userRatings = userRatings;
        }

        public class UserRatings {
            public String rating_id, transection_id, rated_no, user_id, given_by, ratings_comment, rated_date, updated_at,
                    updated_by, deleted_at, deleted_by;

            //getters and setters

            public String getRating_id() {
                return rating_id;
            }

            public void setRating_id(String rating_id) {
                this.rating_id = rating_id;
            }

            public String getTransection_id() {
                return transection_id;
            }

            public void setTransection_id(String transection_id) {
                this.transection_id = transection_id;
            }

            public String getRated_no() {
                return rated_no;
            }

            public void setRated_no(String rated_no) {
                this.rated_no = rated_no;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getGiven_by() {
                return given_by;
            }

            public void setGiven_by(String given_by) {
                this.given_by = given_by;
            }

            public String getRatings_comment() {
                return ratings_comment;
            }

            public void setRatings_comment(String ratings_comment) {
                this.ratings_comment = ratings_comment;
            }

            public String getRated_date() {
                return rated_date;
            }

            public void setRated_date(String rated_date) {
                this.rated_date = rated_date;
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
        }
        /*******************************************************User ratings end****************************/

        /*******************************************************User images start **************************/

        ArrayList<UserImages> userImages = new ArrayList<UserImages>();

        //array getters and setters

        public ArrayList<UserImages> getUserImages() {
            return userImages;
        }

        public void setUserImages(ArrayList<UserImages> userImages) {
            this.userImages = userImages;
        }

        public class UserImages {
            public String image_id, user_id, image_type, image_url, created_at, updated_at, updated_by, deleted_at, deleted_by;

            public String getImage_id() {
                return image_id;
            }

            public void setImage_id(String image_id) {
                this.image_id = image_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getImage_type() {
                return image_type;
            }

            public void setImage_type(String image_type) {
                this.image_type = image_type;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
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
        }
        /************************************************User images end ***********************************/

        /************************************************Sp Bid Details start ***********************************/

        ArrayList<SpBidDetails> spBidDetails = new ArrayList<SpBidDetails>();

        //Array getters and setters

        public ArrayList<SpBidDetails> getSpBidDetails() {
            return spBidDetails;
        }

        public void setSpBidDetails(ArrayList<SpBidDetails> spBidDetails) {
            this.spBidDetails = spBidDetails;
        }

        public class SpBidDetails {
            public String sp_bid_id, user_id, idpost_load, sp_quote, revised_bid_quote_one, revised_bid_quote_two, is_negatiable,
                    assigned_truck_id, assigned_driver_id, vehicle_model, feet, capacity, body_type, notes, bid_status, is_bid_accpted_by_sp,
                    updated_at, updated_by, deleted_at, deleted_by;

            //getters and setters

            public String getSp_bid_id() {
                return sp_bid_id;
            }

            public void setSp_bid_id(String sp_bid_id) {
                this.sp_bid_id = sp_bid_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getIdpost_load() {
                return idpost_load;
            }

            public void setIdpost_load(String idpost_load) {
                this.idpost_load = idpost_load;
            }

            public String getSp_quote() {
                return sp_quote;
            }

            public void setSp_quote(String sp_quote) {
                this.sp_quote = sp_quote;
            }

            public String getRevised_bid_quote_one() {
                return revised_bid_quote_one;
            }

            public void setRevised_bid_quote_one(String revised_bid_quote_one) {
                this.revised_bid_quote_one = revised_bid_quote_one;
            }

            public String getRevised_bid_quote_two() {
                return revised_bid_quote_two;
            }

            public void setRevised_bid_quote_two(String revised_bid_quote_two) {
                this.revised_bid_quote_two = revised_bid_quote_two;
            }

            public String getIs_negatiable() {
                return is_negatiable;
            }

            public void setIs_negatiable(String is_negatiable) {
                this.is_negatiable = is_negatiable;
            }

            public String getAssigned_truck_id() {
                return assigned_truck_id;
            }

            public void setAssigned_truck_id(String assigned_truck_id) {
                this.assigned_truck_id = assigned_truck_id;
            }

            public String getAssigned_driver_id() {
                return assigned_driver_id;
            }

            public void setAssigned_driver_id(String assigned_driver_id) {
                this.assigned_driver_id = assigned_driver_id;
            }

            public String getVehicle_model() {
                return vehicle_model;
            }

            public void setVehicle_model(String vehicle_model) {
                this.vehicle_model = vehicle_model;
            }

            public String getFeet() {
                return feet;
            }

            public void setFeet(String feet) {
                this.feet = feet;
            }

            public String getCapacity() {
                return capacity;
            }

            public void setCapacity(String capacity) {
                this.capacity = capacity;
            }

            public String getBody_type() {
                return body_type;
            }

            public void setBody_type(String body_type) {
                this.body_type = body_type;
            }

            public String getNotes() {
                return notes;
            }

            public void setNotes(String notes) {
                this.notes = notes;
            }

            public String getBid_status() {
                return bid_status;
            }

            public void setBid_status(String bid_status) {
                this.bid_status = bid_status;
            }

            public String getIs_bid_accpted_by_sp() {
                return is_bid_accpted_by_sp;
            }

            public void setIs_bid_accpted_by_sp(String is_bid_accpted_by_sp) {
                this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
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
        }
        /***************************************************Sp Bid Details end ********************************/
    }
}
