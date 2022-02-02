package com.nlpl.model.UpdateModel.Models.UpdateBankDetails;

public class UpdateBankAccountNumber {

    private String account_number;

    public UpdateBankAccountNumber(String account_number) {
        this.account_number = account_number;
    }

    @Override
    public String toString() {
        return "UpdateBankAccountNumber{" +
                "account_number='" + account_number + '\'' +
                '}';
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }
}
