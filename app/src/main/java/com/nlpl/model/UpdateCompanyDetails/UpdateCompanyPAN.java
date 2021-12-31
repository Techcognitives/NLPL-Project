package com.nlpl.model.UpdateCompanyDetails;

public class UpdateCompanyPAN {

    private String company_pan;

    public UpdateCompanyPAN(String company_pan) {
        this.company_pan = company_pan;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", company_pan='" + company_pan + '\'' +
                '}';
    }

    public String getCompany_pan() {
        return company_pan;
    }

    public void setCompany_pan(String company_pan) {
        this.company_pan = company_pan;
    }

}
