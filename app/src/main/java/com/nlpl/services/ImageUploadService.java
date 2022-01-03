package com.nlpl.services;

import com.nlpl.model.Responses.UploadImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageUploadService {
    @Multipart
    @PATCH("imgbucket/updateImage/{userId}/{img_type}")
    Call<UploadImageResponse> uploadImage(@Path("userId") String userId, @Path("img_type") String img_type, @Part MultipartBody.Part file);
}
