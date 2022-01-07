package com.nlpl.services;

import com.nlpl.model.Responses.UploadDriverLicenseResponse;
import com.nlpl.model.Responses.UploadDriverSelfieResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadDriverSelfieService {
    @Multipart
    @PUT("uploadDrDlAndSelfie/{driverId}")
    Call<UploadDriverSelfieResponse> uploadDriverSelfie(@Path("driverId") String driverId, @Part MultipartBody.Part file);
}
