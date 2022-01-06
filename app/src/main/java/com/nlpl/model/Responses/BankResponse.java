package com.nlpl.model.Responses;

public class BankResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    private BankResponse.Data data;

    public BankResponse.Data getData() {
        return data;
    }

    public void setData(BankResponse.Data data) {
        this.data = data;
    }

    public class Data {

        private String user_id,accountholder_name, account_number, re_enter_acc_num, IFSI_CODE, isBankDetails_Given, bank_name, bank_id;

        public Data(String bank_id, String user_id, String accountholder_name, String account_number, String re_enter_acc_num, String IFSI_CODE, String isBankDetails_Given, String bank_name) {
            this.user_id = user_id;
            this.bank_id = bank_id;
            this.accountholder_name = accountholder_name ;
            this.account_number = account_number;
            this.re_enter_acc_num = re_enter_acc_num;
            this.IFSI_CODE = IFSI_CODE;
            this.isBankDetails_Given = isBankDetails_Given;
            this.bank_name = bank_name;
        }

        public String getBank_id() {
            return bank_id;
        }

        public void setBank_id(String bank_id) {
            this.bank_id = bank_id;
        }

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

        public String getIsBankDetails_Given() {
            return isBankDetails_Given;
        }

        public void setIsBankDetails_Given(String isBankDetails_Given) {
            this.isBankDetails_Given = isBankDetails_Given;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }
    }
}
