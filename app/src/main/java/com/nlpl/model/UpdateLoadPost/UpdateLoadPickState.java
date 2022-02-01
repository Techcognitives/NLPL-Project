package com.nlpl.model.UpdateLoadPost;

public class UpdateLoadPickState {

    private String pick_state;

    public UpdateLoadPickState(String pick_state) {
        this.pick_state = pick_state;
    }

    @Override
    public String toString() {
        return "UpdateLoadBodyPickState{" +
                "pick_state='" + pick_state + '\'' +
                '}';
    }
}
