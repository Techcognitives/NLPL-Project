package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadDropCountry {

    private String drop_country;

    public UpdateLoadDropCountry(String drop_country) {
        this.drop_country = drop_country;
    }


    @Override
    public String toString() {
        return "UpdateLoadDropCountry{" +
                "drop_country='" + drop_country + '\'' +
                '}';
    }
}
