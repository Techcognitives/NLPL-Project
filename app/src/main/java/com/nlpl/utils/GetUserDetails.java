package com.nlpl.utils;

import android.widget.TextView;

import com.nlpl.model.Responses.RatingResponse;
import com.nlpl.model.Responses.UserResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetUserDetails {

    public static void getRatings(String userIdForRatings, TextView setAverage){
        Call<RatingResponse> call = ApiClient.getRatingService().getRatings(userIdForRatings);
        call.enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
                RatingResponse nameResponse = response.body();
                String averageInt = nameResponse.getAverage();
                try {
                    if (averageInt.equals("null") || averageInt == null) {
                        setAverage.setText("3.5");
                    } else {
                        setAverage.setText(averageInt);
                    }
                }catch (Exception e){
                    setAverage.setText("3.5");
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {

            }
        });
    }

    public static void getAllUserDetails(){
        Call<UserResponse> call = ApiClient.getUserService().getAllUserDetails();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                ArrayList<UserResponse> userDetails = new ArrayList<>();
                userDetails.add(userResponse);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

}
