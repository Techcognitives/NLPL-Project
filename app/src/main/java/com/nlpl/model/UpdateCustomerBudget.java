package com.nlpl.model;

public class UpdateCustomerBudget {
    private String is_bid_accpted_by_sp;

    public UpdateCustomerBudget(String is_bid_accpted_by_sp) {
        this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
    }

    @Override
    public String toString() {
        return "UpdateCustomerBudget{" +
                "is_bid_accpted_by_sp='" + is_bid_accpted_by_sp + '\'' +
                '}';
    }

    public String getIs_bid_accpted_by_sp() {
        return is_bid_accpted_by_sp;
    }

    public void setIs_bid_accpted_by_sp(String is_bid_accpted_by_sp) {
        this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
    }
}
