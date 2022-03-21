package com.nlpl.model.Responses;

public class RatingResponse {
    String success, rated_no, average;

    @Override
    public String toString() {
        return "RatingResponse{" +
                "success='" + success + '\'' +
                ", rated_no='" + rated_no + '\'' +
                ", average='" + average + '\'' +
                '}';
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getRated_no() {
        return rated_no;
    }

    public void setRated_no(String rated_no) {
        this.rated_no = rated_no;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }
}
