package com.nlpl.model.UpdateCompanyDetails;

public class UpdateCompanyName {

    private String company_name;

    public UpdateCompanyName(String company_name) {
        this.company_name = company_name;
    }

    @Override
    public String toString() {
        return "UpdateCompanyName{" +
                "company_name='" + company_name + '\'' +
                '}';
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
}
