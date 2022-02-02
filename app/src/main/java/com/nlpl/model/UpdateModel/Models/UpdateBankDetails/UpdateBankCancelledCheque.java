package com.nlpl.model.UpdateModel.Models.UpdateBankDetails;

public class UpdateBankCancelledCheque {

    private String cancelled_cheque;

    public UpdateBankCancelledCheque(String cancelled_cheque) {
        this.cancelled_cheque = cancelled_cheque;
    }

    @Override
    public String toString() {
        return "UpdateBankCancelledCheque{" +
                "cancelled_cheque='" + cancelled_cheque + '\'' +
                '}';
    }

    public String getCancelled_cheque() {
        return cancelled_cheque;
    }

    public void setCancelled_cheque(String cancelled_cheque) {
        this.cancelled_cheque = cancelled_cheque;
    }
}
