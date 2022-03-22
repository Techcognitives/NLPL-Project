package com.nlpl.utils;

import android.widget.TextView;

import com.nlpl.model.Responses.RatingResponse;

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
                if (averageInt.equals("null") || averageInt == null){
                    setAverage.setText("3.5");
                }else{
                    setAverage.setText(averageInt);
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {

            }
        });
    }
}
