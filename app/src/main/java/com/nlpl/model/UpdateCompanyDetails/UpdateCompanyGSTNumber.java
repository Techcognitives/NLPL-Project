package com.nlpl.model.UpdateCompanyDetails;

public class UpdateCompanyGSTNumber {

    private String company_gst_no;

    public UpdateCompanyGSTNumber(String company_gst_no) {
        this.company_gst_no = company_gst_no;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", company_gst_no='" + company_gst_no + '\'' +
                '}';
    }

    public String getCompany_gst_no() {
        return company_gst_no;
    }

    public void setCompany_gst_no(String company_gst_no) {
        this.company_gst_no = company_gst_no;
    }
}
