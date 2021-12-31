package com.nlpl.model.UpdateCompanyDetails;

public class UpdateCompanyState {

    private String comp_state;

    public UpdateCompanyState(String comp_state) {
        this.comp_state = comp_state;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", comp_state='" + comp_state + '\'' +
                '}';
    }

    public String getComp_state() {
        return comp_state;
    }

    public void setComp_state(String comp_state) {
        this.comp_state = comp_state;
    }
}
