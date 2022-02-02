package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadPickCity {

    private String pick_city;

    public UpdateLoadPickCity(String pick_city) {
        this.pick_city = pick_city;
    }

    @Override
    public String toString() {
        return "UpdateLoadBodyPickCity{" +
                "pick_city='" + pick_city + '\'' +
                '}';
    }
}
