package com.nlpl.model.UpdateBankDetails;

public class UpdateBankId {

    private String bank_id;

    public UpdateBankId(String bank_id) {
        this.bank_id = bank_id;
    }

    @Override
    public String toString() {
        return "UpdateBankId{" +
                "bank_id='" + bank_id + '\'' +
                '}';
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }
}
