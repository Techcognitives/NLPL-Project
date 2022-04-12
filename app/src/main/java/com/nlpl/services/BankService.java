package com.nlpl.services;

import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.BankResponseGet;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankAccountHolderName;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankAccountNumber;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankName;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankReEnterAccountNumber;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BankService {
    @POST("/bank/createBkAcc")
    Call<BankResponse> saveBank(@Body BankRequest bankRequest);

    @PUT("/bank/updateBkByBkId/{bankId}")
    Call<UpdateBankAccountHolderName> updateBankAccountHolderName(@Path("bankId") String bankId, @Body UpdateBankAccountHolderName updateBankAccountHolderName);

    @PUT("/bank/updateBkByBkId/{bankId}")
    Call<UpdateBankAccountNumber> updateBankAccountNumber(@Path("bankId") String bankId, @Body UpdateBankAccountNumber updateBankAccountNumber);

    @PUT("/bank/updateBkByBkId/{bankId}")
    Call<UpdateBankReEnterAccountNumber> updateBankReEnterAccountNumber(@Path("bankId") String bankId, @Body UpdateBankReEnterAccountNumber updateBankReEnterAccountNumber);

    @PUT("/bank/updateBkByBkId/{bankId}")
    Call<UpdateBankIFSICode> updateBankIFSICode(@Path("bankId") String bankId, @Body UpdateBankIFSICode updateBankIFSICode);

    @PUT("/bank/updateBkByBkId/{bankId}")
    Call<UpdateBankName> updateBankName(@Path("bankId") String bankId, @Body UpdateBankName updateBankName);

    @DELETE("/bank/deleteBank/{bankId}")
    Call<BankResponse> deleteBankDetails(@Path("bankId") String bankId);

    @GET("/bank/getBkByUserId/{userId}")
    Call<BankResponseGet> getBankByUserId(@Path("userId") String userId);
}
