package com.nlpl.model.Responses;

import java.util.List;

public class BankResponseGet {
    private String success;
    private List<bankDetailById> data;


    public List<bankDetailById> getData() {
        return data;
    }

    public void setData(List<bankDetailById> data) {
        this.data = data;
    }

    public class bankDetailById {
        private String user_id, accountholder_name, account_number, re_enter_acc_num, IFSI_CODE, bank_id, bank_name, cancelled_cheque, created_at, updated_at, updated_by, deleted_at, deleted_by, bank_ref_no, beneficiary_name, transaction_remark, verification_status;

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
}
