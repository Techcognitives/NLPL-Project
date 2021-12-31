package com.nlpl.model.UpdateCompanyDetails;

public class UpdateCompanyAddress {

    private String comp_add;

    public UpdateCompanyAddress(String comp_add) {
        this.comp_add = comp_add;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", comp_add='" + comp_add + '\'' +
                '}';
    }

    public String getComp_add() {
        return comp_add;
    }

    public void setComp_add(String comp_add) {
        this.comp_add = comp_add;
    }
}
