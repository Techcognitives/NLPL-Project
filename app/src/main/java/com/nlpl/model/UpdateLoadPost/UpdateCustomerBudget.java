package com.nlpl.model.UpdateLoadPost;

public class UpdateCustomerBudget {
    private String budget;

    public UpdateCustomerBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "UpdateCustomerBudget{" +
                "budget='" + budget + '\'' +
                '}';
    }
}
