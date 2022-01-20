package com.nlpl.services;

import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.UpdateBidStatusAccepted;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BidLoadService {
    @POST("/spbid/bidapost")
    Call<BidLadResponse> saveBid(@Body BidLoadRequest bidLoadRequest);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateBidStatusAccepted> updateBidStatusAccepted(@Path("bidId") String bidId, @Body UpdateBidStatusAccepted updateBidStatusAccepted);
}
