package com.nlpl.services;

import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.UpdateBankDetails.UpdateBankAccountHolderName;
import com.nlpl.model.UpdateBankDetails.UpdateBankAccountNumber;
import com.nlpl.model.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateBankDetails.UpdateBankReEnterAccountNumber;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyName;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BankService {
    @POST("bank/createBankAcc")
    Call<BankResponse> saveBank(@Body BankRequest bankRequest);

    @PUT("/bank/{bankId}")
    Call<UpdateBankAccountHolderName> updateBankAccountHolderName(@Path("bankId") String bankId, @Body UpdateBankAccountHolderName updateBankAccountHolderName);

    @PUT("/bank/{bankId}")
    Call<UpdateBankAccountNumber> updateBankAccountNumber(@Path("bankId") String bankId, @Body UpdateBankAccountNumber updateBankAccountNumber);

    @PUT("/bank/{bankId}")
    Call<UpdateBankReEnterAccountNumber> updateBankReEnterAccountNumber(@Path("bankId") String bankId, @Body UpdateBankReEnterAccountNumber updateBankReEnterAccountNumber);

    @PUT("/bank/{bankId}")
    Call<UpdateBankIFSICode> updateBankIFSICode(@Path("bankId") String bankId, @Body UpdateBankIFSICode updateBankIFSICode);
}
