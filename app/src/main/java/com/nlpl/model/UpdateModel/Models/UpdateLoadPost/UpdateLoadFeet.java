package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadFeet {

    private String feet;

    public UpdateLoadFeet(String feet) {
        this.feet = feet;
    }

    @Override
    public String toString() {
        return "UpdateLoadFeet{" +
                "feet='" + feet + '\'' +
                '}';
    }

    public String getFeet() {
        return feet;
    }

    public void setFeet(String feet) {
        this.feet = feet;
    }
}
