package com.nlpl.services;

import com.nlpl.model.Responses.UploadDriverLicenseResponse;
import com.nlpl.model.Responses.UploadTruckRCResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadTruckRCService {
    @Multipart
    @PUT("truck/uploadRCAndInsurence/{truckId}")
    Call<UploadTruckRCResponse> uploadTruckRC(@Path("truckId") String truckId, @Part MultipartBody.Part rc);
}
