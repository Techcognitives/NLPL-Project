package com.nlpl.model.Responses;

import java.util.List;

public class BankVerificationResponse {
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
                success,
                account_no,
                ifsc_no,
                bank_ref_no,
                beneficiary_name,
                transaction_remark,
                verification_status,
                bank_ver_id,
                user_id,
                created_at;

    //getters and setters
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

        public String getAccount_no() {
            return account_no;
        }

        public void setAccount_no(String account_no) {
            this.account_no = account_no;
        }

        public String getIfsc_no() {
            return ifsc_no;
        }

        public void setIfsc_no(String ifsc_no) {
            this.ifsc_no = ifsc_no;
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

        public String getBank_ver_id() {
            return bank_ver_id;
        }

        public void setBank_ver_id(String bank_ver_id) {
            this.bank_ver_id = bank_ver_id;
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
}
