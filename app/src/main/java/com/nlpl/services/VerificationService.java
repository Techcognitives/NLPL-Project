package com.nlpl.services;

import com.nlpl.model.Responses.BankVerificationResponse;
import com.nlpl.model.Responses.PANVerificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VerificationService {
    @GET("/userPan/panDt/{userId}/{panId}")
    Call<PANVerificationResponse> checkPAN(@Path("userId") String userId, @Path("panId") String panId);

    @GET("/userBankAc/bankDetails/{userId}/{accountNumber}/{ifscCode}")
    Call<BankVerificationResponse> checkBankDetail(@Path("userId")String userId, @Path("accountNumber")String accountNumber, @Path("ifscCode")String ifscCode);
}
