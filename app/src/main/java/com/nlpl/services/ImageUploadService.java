package com.nlpl.services;

import com.nlpl.model.ImageResponse;
import com.nlpl.model.UploadImageResponse;

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
    @PATCH("imgbucket/updateImage/{userId}/pan")
    Call<UploadImageResponse> uploadImage(@Path("userId") String userId, @Part MultipartBody.Part file);
}
