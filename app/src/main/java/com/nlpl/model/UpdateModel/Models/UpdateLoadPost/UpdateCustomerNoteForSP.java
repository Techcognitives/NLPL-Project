package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateCustomerNoteForSP {
    String notes_meterial_des;

    public UpdateCustomerNoteForSP(String notes_meterial_des) {
        this.notes_meterial_des = notes_meterial_des;
    }

    @Override
    public String toString() {
        return "UpdateCustomerNoteForSP{" +
                "notes_meterial_des='" + notes_meterial_des + '\'' +
                '}';
    }
}
