package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserRating {

    private String user_ratings;

    public UpdateUserRating(String user_ratings) {
        this.user_ratings = user_ratings;
    }

    @Override
    public String toString() {
        return "UpdateUserRating{" +
                "user_ratings='" + user_ratings + '\'' +
                '}';
    }

    public String getUser_ratings() {
        return user_ratings;
    }

    public void setUser_ratings(String user_ratings) {
        this.user_ratings = user_ratings;
    }
}
