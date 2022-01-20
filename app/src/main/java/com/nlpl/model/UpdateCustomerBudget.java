package com.nlpl.model;

public class UpdateCustomerBudget {
    private String budget;

    public UpdateCustomerBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "UpdateCustomerBudget{" +
                "customerBudget='" + budget + '\'' +
                '}';
    }

    public String getBid_status() {
        return budget;
    }

    public void setBid_status(String bid_status) {
        this.budget = bid_status;
    }
}
