package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadDropState {

    private String drop_state;

    public UpdateLoadDropState(String drop_state) {
        this.drop_state = drop_state;
    }

    @Override
    public String toString() {
        return "UpdateLoadDropState{" +
                "drop_state='" + drop_state + '\'' +
                '}';
    }
}
