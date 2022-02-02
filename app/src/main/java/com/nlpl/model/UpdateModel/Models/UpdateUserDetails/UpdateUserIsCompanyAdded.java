package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserIsCompanyAdded {

    private String isCompany_added;

    public UpdateUserIsCompanyAdded(String isCompany_added) {
        this.isCompany_added = isCompany_added;
    }

    @Override
    public String toString() {
        return "UpdateUserIsCompanyAdded{" +
                "isCompany_added='" + isCompany_added + '\'' +
                '}';
    }

    public String getIsCompany_added() {
        return isCompany_added;
    }

    public void setIsCompany_added(String isCompany_added) {
        this.isCompany_added = isCompany_added;
    }
}
