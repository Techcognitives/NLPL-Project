package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserIsBankDetailsGiven {

    private String isBankDetails_given;

    public UpdateUserIsBankDetailsGiven(String isBankDetails_given) {
        this.isBankDetails_given = isBankDetails_given;
    }

    @Override
    public String toString() {
        return "UpdateUserIsBankDetailsGiven{" +
                "isBankDetails_given='" + isBankDetails_given + '\'' +
                '}';
    }

    public String getIsBankDetails_given() {
        return isBankDetails_given;
    }

    public void setIsBankDetails_given(String isBankDetails_given) {
        this.isBankDetails_given = isBankDetails_given;
    }
}
