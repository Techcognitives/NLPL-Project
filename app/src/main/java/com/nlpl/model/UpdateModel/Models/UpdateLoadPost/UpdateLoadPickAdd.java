package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadPickAdd {

    private String pick_add;

    public UpdateLoadPickAdd(String pick_add) {
        this.pick_add = pick_add;
    }

    @Override
    public String toString() {
        return "UpdateLoadBodyPickAdd{" +
                "pick_add='" + pick_add + '\'' +
                '}';
    }
}
