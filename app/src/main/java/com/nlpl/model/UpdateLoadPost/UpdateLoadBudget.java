package com.nlpl.model.UpdateLoadPost;

public class UpdateLoadBudget {

    private String budget;

    public UpdateLoadBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "UpdateLoadBudget{" +
                "budget='" + budget + '\'' +
                '}';
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}
