package com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails;

public class UpdateCompanyType {

    private String company_type;

    public UpdateCompanyType(String company_type) {
        this.company_type = company_type;
    }

    @Override
    public String toString() {
        return "UpdateCompanyType{" +
                "company_type='" + company_type + '\'' +
                '}';
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }
}
