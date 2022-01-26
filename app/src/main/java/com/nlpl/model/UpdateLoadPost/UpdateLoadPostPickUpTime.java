package com.nlpl.model.UpdateLoadPost;

public class UpdateLoadPostPickUpTime {

    private String pick_up_time;

    public UpdateLoadPostPickUpTime(String pick_up_time) {
        this.pick_up_time = pick_up_time;
    }

    @Override
    public String toString() {
        return "UpdateLoadPostPickUpTime{" +
                "pick_up_time='" + pick_up_time + '\'' +
                '}';
    }

    public String getPick_up_time() {
        return pick_up_time;
    }

    public void setPick_up_time(String pick_up_time) {
        this.pick_up_time = pick_up_time;
    }
}
