package com.nlpl.services;

import com.nlpl.model.BankRequest;
import com.nlpl.model.BankResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BankService {
    @POST("/bank/createAccount")
    Call<BankResponse> saveBank(@Body BankRequest bankRequest);
}
