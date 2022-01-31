package com.nlpl.model.UpdateBids;

public class UpdateSpNoteForCustomer {
    String notes;

    public UpdateSpNoteForCustomer(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "UpdateSpNoteForCustomer{" +
                "notes='" + notes + '\'' +
                '}';
    }
}
