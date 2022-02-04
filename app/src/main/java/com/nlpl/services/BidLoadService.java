package com.nlpl.services;

import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Responses.BidLadResponse;
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

public interface BidLoadService {
    @POST("/spbid/bidapost")
    Call<BidLadResponse> saveBid(@Body BidLoadRequest bidLoadRequest);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateBidStatus> updateBidStatusAccepted(@Path("bidId") String bidId, @Body UpdateBidStatus updateBidStatus);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateSPQuoteFinal> updateSPQuoteFinal(@Path("bidId") String bidId, @Body UpdateSPQuoteFinal updateSPQuoteFinal);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateBudgetCustomerForSP> updateBudgetCustomerForSP(@Path("bidId") String bidId, @Body UpdateBudgetCustomerForSP updateBudgetCustomerForSP);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateAssignedTruckIdToBid> updateAssignedTruckId(@Path("bidId") String bidId, @Body UpdateAssignedTruckIdToBid updateAssignedTruckIdToBid);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateAssignedDriverId> updateAssignedDriverId(@Path("bidId") String bidId, @Body UpdateAssignedDriverId updateAssignedDriverId);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateSpNoteForCustomer> updateSPNoteForCustomer(@Path("bidId") String bidId, @Body UpdateSpNoteForCustomer updateSpNoteForCustomer);

}
