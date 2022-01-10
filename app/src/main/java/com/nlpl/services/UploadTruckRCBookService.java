package com.nlpl.services;

import com.nlpl.model.Responses.UploadTruckInsuranceResponse;
import com.nlpl.model.Responses.UploadTruckRCResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadTruckRCBookService {
    @Multipart
    @PUT("truck/uploadRCAndInsurence/{truckId}")
    Call<UploadTruckRCResponse> UploadTruckRCBook(@Path("truckId") String truckId, @Part MultipartBody.Part rc);
}
