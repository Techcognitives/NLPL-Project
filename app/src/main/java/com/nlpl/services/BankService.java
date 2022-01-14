package com.nlpl.services;

import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.UpdateBankDetails.UpdateBankAccountHolderName;
import com.nlpl.model.UpdateBankDetails.UpdateBankAccountNumber;
import com.nlpl.model.UpdateBankDetails.UpdateBankCancelledCheque;
import com.nlpl.model.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateBankDetails.UpdateBankName;
import com.nlpl.model.UpdateBankDetails.UpdateBankReEnterAccountNumber;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyName;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
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

    @PUT("/bank/updateBkByBkId/{bankId}")
    Call<UpdateBankCancelledCheque> updateBankCancelledCheque(@Path("bankId") String bankId, @Body UpdateBankCancelledCheque updateBankCancelledCheque);
}
