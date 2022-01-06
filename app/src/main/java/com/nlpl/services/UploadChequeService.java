package com.nlpl.services;

import com.nlpl.model.Responses.UploadChequeResponse;
import com.nlpl.model.Responses.UploadImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadChequeService {
    @Multipart
    @PUT("bank/uploadCancelledCheque/{bankId}")
    Call<UploadChequeResponse> uploadCheque(@Path("bankId") String bankId, @Part MultipartBody.Part file);
}

