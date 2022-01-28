package com.nlpl.model.UpdateBids;

public class UpdateBudgetCustomerForSP {
    private String is_bid_accpted_by_sp;

    public UpdateBudgetCustomerForSP(String is_bid_accpted_by_sp) {
        this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
    }

    @Override
    public String toString() {
        return "UpdateBudgetCustomerForSP{" +
                "is_bid_accpted_by_sp='" + is_bid_accpted_by_sp + '\'' +
                '}';
    }
}
