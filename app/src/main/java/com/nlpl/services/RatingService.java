package com.nlpl.services;

import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Requests.RatingRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.Responses.RatingResponse;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateAssignedDriverId;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateAssignedTruckIdToBid;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateBidStatus;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateBudgetCustomerForSP;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateSPQuoteFinal;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateSpNoteForCustomer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RatingService {
    @POST("/rate/submitRating")
    Call<RatingResponse> saveRating(@Body RatingRequest ratingRequest);
}
