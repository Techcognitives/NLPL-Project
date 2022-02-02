package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadPostPickUpDate {

    private String pick_up_date;

    public UpdateLoadPostPickUpDate(String pick_up_date) {
        this.pick_up_date = pick_up_date;
    }

    @Override
    public String toString() {
        return "UpdateLoadPostPickUpDate{" +
                "pick_up_date='" + pick_up_date + '\'' +
                '}';
    }
}
