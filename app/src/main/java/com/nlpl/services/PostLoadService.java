package com.nlpl.services;

import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.PostLoadResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostLoadService {
    @POST("/loadpost/postAload")
    Call<PostLoadResponse> saveLoad(@Body PostLoadRequest postLoadRequest);
}
