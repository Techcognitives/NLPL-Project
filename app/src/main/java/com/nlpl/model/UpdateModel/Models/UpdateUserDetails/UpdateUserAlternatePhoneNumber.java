package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserAlternatePhoneNumber {

    private String alternate_ph_no;

    public UpdateUserAlternatePhoneNumber(String alternate_ph_no) {
        this.alternate_ph_no = alternate_ph_no;
    }

    @Override
    public String toString() {
        return "UpdateUserAlternatePhoneNumber{" +
                "alternate_ph_no='" + alternate_ph_no + '\'' +
                '}';
    }

    public String getAlternate_ph_no() {
        return alternate_ph_no;
    }

    public void setAlternate_ph_no(String alternate_ph_no) {
        this.alternate_ph_no = alternate_ph_no;
    }
}