package com.nlpl.services;

import com.nlpl.model.ImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ImageUploadService {
    @Multipart
    @PATCH("/updateImage/{userId}")
    Call<ImageResponse> uploadImage(@Path("user_id") String userId, @Part MultipartBody.Part image);
}
