package com.nlpl.model.UpdateModel.Models.UpdateDriverDetails;

public class UpdateDriverDlNumber {

    private String dl_number;

    public UpdateDriverDlNumber(String dl_number) {
        this.dl_number = dl_number;
    }

    @Override
    public String toString() {
        return "UpdateDriverDlNumber{" +
                "dl_number='" + dl_number + '\'' +
                '}';
    }

    public String getDl_number() {
        return dl_number;
    }

    public void setDl_number(String dl_number) {
        this.dl_number = dl_number;
    }
}
