package com.nlpl.model.UpdateTruckDetails;

public class UpdateTruckRcBook {

    private String rc_book;

    public UpdateTruckRcBook(String rc_book) {
        this.rc_book = rc_book;
    }

    @Override
    public String toString() {
        return "UpdateTruckRcBook{" +
                "rc_book='" + rc_book + '\'' +
                '}';
    }

    public String getRc_book() {
        return rc_book;
    }

    public void setRc_book(String rc_book) {
        this.rc_book = rc_book;
    }
}
