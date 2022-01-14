package com.nlpl.services;

import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.Responses.PostLoadResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BidLoadService {
    @POST("/spbid/bidapost")
    Call<BidLadResponse> saveBid(@Body BidLoadRequest bidLoadRequest);
}
