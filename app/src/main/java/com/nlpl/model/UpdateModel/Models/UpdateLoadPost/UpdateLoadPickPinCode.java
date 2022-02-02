package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadPickPinCode {

    private String pick_pin_code;

    public UpdateLoadPickPinCode(String pick_pin_code) {
        this.pick_pin_code = pick_pin_code;
    }

    @Override
    public String toString() {
        return "UpdateLoadBodyPickPinCode{" +
                "pick_pin_code='" + pick_pin_code + '\'' +
                '}';
    }
}
