package com.nlpl.services;

import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.ImageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageService {
    @POST("imgbucket/creatImg")
    Call<ImageResponse> saveImage(@Body ImageRequest imageRequest);
}
