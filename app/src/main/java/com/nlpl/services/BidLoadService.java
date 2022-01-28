package com.nlpl.services;

import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.UpdateBids.UpdateBidStatusAccepted;
import com.nlpl.model.UpdateBids.UpdateBidStatusFinalAccepted;
import com.nlpl.model.UpdateBids.UpdateBidStatusRespondedBySP;
import com.nlpl.model.UpdateBids.UpdateBudgetCustomerForSP;
import com.nlpl.model.UpdateBids.UpdateSPQuoteFinal;

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

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateBidStatusRespondedBySP> updateBidStatusRespondedBySP(@Path("bidId") String bidId, @Body UpdateBidStatusRespondedBySP updateBidStatusRespondedBySP);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateSPQuoteFinal> updateSPQuoteFinal(@Path("bidId") String bidId, @Body UpdateSPQuoteFinal updateSPQuoteFinal);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateBudgetCustomerForSP> updateBudgetCustomerForSP(@Path("bidId") String bidId, @Body UpdateBudgetCustomerForSP updateBudgetCustomerForSP);

    @PUT("/spbid/updateBidByBID/{bidId}")
    Call<UpdateBidStatusFinalAccepted> updateFinalAccepted(@Path("bidId") String bidId, @Body UpdateBidStatusFinalAccepted updateBidStatusFinalAccepted);

}
