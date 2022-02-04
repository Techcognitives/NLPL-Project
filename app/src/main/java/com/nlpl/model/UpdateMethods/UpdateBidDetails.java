package com.nlpl.model.UpdateMethods;

import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateAssignedDriverId;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateAssignedTruckIdToBid;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateBidStatus;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateBudgetCustomerForSP;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateSPQuoteFinal;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateSpNoteForCustomer;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBidDetails {

    //---------------------------- Update Bid Assigned DriverId ------------------------------------
    public static void updateAssignedDriverId(String bidId, String assignedDriverId) {
        UpdateAssignedDriverId updateAssignedDriverId = new UpdateAssignedDriverId(assignedDriverId);
        Call<UpdateAssignedDriverId> call = ApiClient.getBidLoadService().updateAssignedDriverId("" + bidId, updateAssignedDriverId);
        call.enqueue(new Callback<UpdateAssignedDriverId>() {
            @Override
            public void onResponse(Call<UpdateAssignedDriverId> call, retrofit2.Response<UpdateAssignedDriverId> response) {

            }

            @Override
            public void onFailure(Call<UpdateAssignedDriverId> call, Throwable t) {

            }
        });
    }

    //---------------------------- Update Bid Assigned TruckId -------------------------------------
    public static void updateAssignedTruckId(String bidId, String assignedTruckId) {
        UpdateAssignedTruckIdToBid updateAssignedTruckIdToBid = new UpdateAssignedTruckIdToBid(assignedTruckId);
        Call<UpdateAssignedTruckIdToBid> call = ApiClient.getBidLoadService().updateAssignedTruckId("" + bidId, updateAssignedTruckIdToBid);
        call.enqueue(new Callback<UpdateAssignedTruckIdToBid>() {
            @Override
            public void onResponse(Call<UpdateAssignedTruckIdToBid> call, retrofit2.Response<UpdateAssignedTruckIdToBid> response) {

            }

            @Override
            public void onFailure(Call<UpdateAssignedTruckIdToBid> call, Throwable t) {

            }
        });
    }

    //---------------------------- Update Bid Status -----------------------------------------------
    public static void updateBidStatus(String bidId, String status) {
        UpdateBidStatus updateBidStatus = new UpdateBidStatus(status);
        Call<UpdateBidStatus> call = ApiClient.getBidLoadService().updateBidStatusAccepted("" + bidId, updateBidStatus);
        call.enqueue(new Callback<UpdateBidStatus>() {
            @Override
            public void onResponse(Call<UpdateBidStatus> call, Response<UpdateBidStatus> response) {

            }

            @Override
            public void onFailure(Call<UpdateBidStatus> call, Throwable t) {

            }
        });
    }

    //---------------------------- Update Bid Customer Budget --------------------------------------
    public static void updateCustomerBudgetForSP(String bidId, String cQuote) {
        UpdateBudgetCustomerForSP updateBudgetCustomerForSP = new UpdateBudgetCustomerForSP(cQuote);
        Call<UpdateBudgetCustomerForSP> call = ApiClient.getBidLoadService().updateBudgetCustomerForSP("" + bidId, updateBudgetCustomerForSP);
        call.enqueue(new Callback<UpdateBudgetCustomerForSP>() {
            @Override
            public void onResponse(Call<UpdateBudgetCustomerForSP> call, Response<UpdateBudgetCustomerForSP> response) {

            }

            @Override
            public void onFailure(Call<UpdateBudgetCustomerForSP> call, Throwable t) {

            }
        });
    }

    //---------------------------- Update Bid Notes ------------------------------------------------
    public static void updateSPNoteForCustomer(String bidId, String spNote) {
        UpdateSpNoteForCustomer updateSpNoteForCustomer = new UpdateSpNoteForCustomer(spNote);
        Call<UpdateSpNoteForCustomer> call = ApiClient.getBidLoadService().updateSPNoteForCustomer("" + bidId, updateSpNoteForCustomer);
        call.enqueue(new Callback<UpdateSpNoteForCustomer>() {
            @Override
            public void onResponse(Call<UpdateSpNoteForCustomer> call, retrofit2.Response<UpdateSpNoteForCustomer> response) {

            }

            @Override
            public void onFailure(Call<UpdateSpNoteForCustomer> call, Throwable t) {

            }
        });

    }

    //---------------------------- Update Bid SP Quote Final ---------------------------------------
    public static void updateSPQuoteFinal(String bidId, String spQuote) {
        UpdateSPQuoteFinal updateSPQuoteFinal = new UpdateSPQuoteFinal(spQuote);
        Call<UpdateSPQuoteFinal> call = ApiClient.getBidLoadService().updateSPQuoteFinal("" + bidId, updateSPQuoteFinal);
        call.enqueue(new Callback<UpdateSPQuoteFinal>() {
            @Override
            public void onResponse(Call<UpdateSPQuoteFinal> call, retrofit2.Response<UpdateSPQuoteFinal> response) {

            }

            @Override
            public void onFailure(Call<UpdateSPQuoteFinal> call, Throwable t) {

            }
        });

    }
}
