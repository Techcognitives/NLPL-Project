package com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails;

public class UpdateCompanyCity {

    private String comp_city;

    public UpdateCompanyCity(String comp_city) {
        this.comp_city = comp_city;
    }

    @Override
    public String toString() {
        return "CompanyUpdate{" +
                ", comp_city='" + comp_city + '\'' +
                '}';
    }

    public String getComp_city() {
        return comp_city;
    }

    public void setComp_city(String comp_city) {
        this.comp_city = comp_city;
    }
}
