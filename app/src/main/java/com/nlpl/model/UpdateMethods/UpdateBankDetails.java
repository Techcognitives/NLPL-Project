package com.nlpl.model.UpdateMethods;

import android.util.Log;

import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankAccountHolderName;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankAccountNumber;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankName;
import com.nlpl.model.UpdateModel.Models.UpdateBankDetails.UpdateBankReEnterAccountNumber;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBankDetails {

    //-------------------------------------- Update Bank Name --------------------------------------
    public static void updateBankName(String bankId, String bankName) {
        UpdateBankName updateBankName = new UpdateBankName(bankName);
        Call<UpdateBankName> call = ApiClient.getBankService().updateBankName("" + bankId, updateBankName);
        call.enqueue(new Callback<UpdateBankName>() {
            @Override
            public void onResponse(Call<UpdateBankName> call, Response<UpdateBankName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankName> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
    }

    //---------------------------- Update Account Holder Name --------------------------------------
    public static void updateAccountHolderName(String bankId, String accountHolderName) {
        UpdateBankAccountHolderName updateBankAccountHolderName = new UpdateBankAccountHolderName(accountHolderName);
        Call<UpdateBankAccountHolderName> call = ApiClient.getBankService().updateBankAccountHolderName("" + bankId, updateBankAccountHolderName);
        call.enqueue(new Callback<UpdateBankAccountHolderName>() {
            @Override
            public void onResponse(Call<UpdateBankAccountHolderName> call, Response<UpdateBankAccountHolderName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankAccountHolderName> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
    }

    //-------------------------------------- Update Bank Account Number ----------------------------
    public static void updateBankAccountNumber(String bankId, String accountNo) {
        UpdateBankAccountNumber updateBankAccountNumber = new UpdateBankAccountNumber(accountNo);
        Call<UpdateBankAccountNumber> call = ApiClient.getBankService().updateBankAccountNumber("" + bankId, updateBankAccountNumber);
        call.enqueue(new Callback<UpdateBankAccountNumber>() {
            @Override
            public void onResponse(Call<UpdateBankAccountNumber> call, Response<UpdateBankAccountNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankAccountNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
    }

    //---------------------------- Update Bank ReEntered Account Number ----------------------------
    public static void updateBankReEnterAccountNumber(String bankId, String reAccountNo) {
        UpdateBankReEnterAccountNumber updateBankReEnterAccountNumber = new UpdateBankReEnterAccountNumber(reAccountNo);
        Call<UpdateBankReEnterAccountNumber> call = ApiClient.getBankService().updateBankReEnterAccountNumber("" + bankId, updateBankReEnterAccountNumber);
        call.enqueue(new Callback<UpdateBankReEnterAccountNumber>() {
            @Override
            public void onResponse(Call<UpdateBankReEnterAccountNumber> call, Response<UpdateBankReEnterAccountNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankReEnterAccountNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
    }

    //---------------------------- Update Bank IFSC Code -------------------------------------------
    public static void updateBankIFSICode(String bankId, String ifscCode) {
        UpdateBankIFSICode updateBankIFSICode = new UpdateBankIFSICode(ifscCode);
        Call<UpdateBankIFSICode> call = ApiClient.getBankService().updateBankIFSICode("" + bankId, updateBankIFSICode);
        call.enqueue(new Callback<UpdateBankIFSICode>() {
            @Override
            public void onResponse(Call<UpdateBankIFSICode> call, Response<UpdateBankIFSICode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankIFSICode> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
    }
}
