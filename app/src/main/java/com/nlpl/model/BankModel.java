package com.nlpl.model;

public class BankModel {
    private String user_id;
    private String accountholder_name;
    private String account_number;
    private String re_enter_acc_num;
    private String IFSI_CODE;
    private String bank_id;

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
}
