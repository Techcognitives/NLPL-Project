package com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails;

public class UpdateCompanyZip {

    private String comp_zip;

    public UpdateCompanyZip(String comp_zip) {
        this.comp_zip = comp_zip;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", comp_zip=" + comp_zip +
                '}';
    }

    public String getComp_zip() {
        return comp_zip;
    }

    public void setComp_zip(String comp_zip) {
        this.comp_zip = comp_zip;
    }
}
