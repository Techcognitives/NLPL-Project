package com.nlpl.model.Requests;

public class RatingRequest {
    String transection_id, rated_no, ratings_comment, user_id, given_by;

    public RatingRequest(String transection_id, String rated_no, String ratings_comment, String user_id, String given_by) {
        this.transection_id = transection_id;
        this.rated_no = rated_no;
        this.ratings_comment = ratings_comment;
        this.user_id = user_id;
        this.given_by = given_by;
    }

    public String getTransection_id() {
        return transection_id;
    }

    public void setTransection_id(String transection_id) {
        this.transection_id = transection_id;
    }

    public String getRated_no() {
        return rated_no;
    }

    public void setRated_no(String rated_no) {
        this.rated_no = rated_no;
    }

    public String getRatings_comment() {
        return ratings_comment;
    }

    public void setRatings_comment(String ratings_comment) {
        this.ratings_comment = ratings_comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGiven_by() {
        return given_by;
    }

    public void setGiven_by(String given_by) {
        this.given_by = given_by;
    }
}
