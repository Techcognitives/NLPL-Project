package com.nlpl.services;

import com.nlpl.model.Responses.UploadChequeResponse;
import com.nlpl.model.Responses.UploadDriverLicenseResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadDriverLicenseService {
    @Multipart
    @PUT("/driver/uploadDrDlAndSelfie/{driverId}")
    Call<UploadDriverLicenseResponse> uploadDriverLicense(@Path("driverId") String driverId, @Part MultipartBody.Part file);
}
