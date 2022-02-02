package com.nlpl.model.UpdateModel.Models.UpdateBankDetails;

public class UpdateBankName {

    private String bank_name;

    public UpdateBankName(String bank_name) {
        this.bank_name = bank_name;
    }

    @Override
    public String toString() {
        return "UpdateBankName{" +
                "bank_name='" + bank_name + '\'' +
                '}';
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }
}
