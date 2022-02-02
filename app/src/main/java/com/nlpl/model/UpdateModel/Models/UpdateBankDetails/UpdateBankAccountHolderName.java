package com.nlpl.model.UpdateModel.Models.UpdateBankDetails;

public class UpdateBankAccountHolderName {

    private String accountholder_name;

    public UpdateBankAccountHolderName(String accountholder_name) {
        this.accountholder_name = accountholder_name;
    }

    @Override
    public String toString() {
        return "UpdateBankAccountHolderName{" +
                "accountholder_name='" + accountholder_name + '\'' +
                '}';
    }

    public String getAccountholder_name() {
        return accountholder_name;
    }

    public void setAccountholder_name(String accountholder_name) {
        this.accountholder_name = accountholder_name;
    }
}
