package com.nlpl.model.UpdateMethods;

import android.util.Log;

import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyAddress;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyCity;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyGSTNumber;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyName;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyPAN;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyState;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyType;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyZip;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;

public class UpdateCompanyDetails {

    //-------------------------------------- Update Company Name -----------------------------------
    public static void updateCompanyName(String companyId, String companyName) {
        UpdateCompanyName updateCompanyName = new UpdateCompanyName(companyName);
        Call<UpdateCompanyName> call = ApiClient.getCompanyService().updateCompanyName("" + companyId, updateCompanyName);
        call.enqueue(new Callback<UpdateCompanyName>() {
            @Override
            public void onResponse(Call<UpdateCompanyName> call, retrofit2.Response<UpdateCompanyName> response) {
                if (response.isSuccessful()) {
                    UpdateCompanyName updateCompanyName1 = response.body();
                    Log.i("Updated", String.valueOf(updateCompanyName1));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyName> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company Address --------------------------------
    public static void updateCompanyAddress(String companyId, String companyAddress) {
        UpdateCompanyAddress updateCompanyAddress = new UpdateCompanyAddress(companyAddress);
        Call<UpdateCompanyAddress> call = ApiClient.getCompanyService().updateCompanyAddress("" + companyId, updateCompanyAddress);
        call.enqueue(new Callback<UpdateCompanyAddress>() {
            @Override
            public void onResponse(Call<UpdateCompanyAddress> call, retrofit2.Response<UpdateCompanyAddress> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyAddress> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company Pin Code -------------------------------
    public static void updateCompanyPinCode(String companyId, String companyPinCode) {
        UpdateCompanyZip updateCompanyZip = new UpdateCompanyZip(companyPinCode);
        Call<UpdateCompanyZip> call = ApiClient.getCompanyService().updateCompanyZip("" + companyId, updateCompanyZip);
        call.enqueue(new Callback<UpdateCompanyZip>() {
            @Override
            public void onResponse(Call<UpdateCompanyZip> call, retrofit2.Response<UpdateCompanyZip> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyZip> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company State ----------------------------------
    public static void updateCompanyState(String companyId, String companyState) {
        UpdateCompanyState updateCompanyState = new UpdateCompanyState(companyState);
        Call<UpdateCompanyState> call = ApiClient.getCompanyService().updateCompanyState("" + companyId, updateCompanyState);
        call.enqueue(new Callback<UpdateCompanyState>() {
            @Override
            public void onResponse(Call<UpdateCompanyState> call, retrofit2.Response<UpdateCompanyState> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyState> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company City -----------------------------------
    public static void updateCompanyCity(String companyId, String companyCity) {
        UpdateCompanyCity updateCompanyCity = new UpdateCompanyCity(companyCity);
        Call<UpdateCompanyCity> call = ApiClient.getCompanyService().updateCompanyCity("" + companyId, updateCompanyCity);
        call.enqueue(new Callback<UpdateCompanyCity>() {
            @Override
            public void onResponse(Call<UpdateCompanyCity> call, retrofit2.Response<UpdateCompanyCity> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyCity> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company Type -----------------------------------
    public static void updateCompanyType(String companyId, String companyType) {
        UpdateCompanyType updateCompanyType = new UpdateCompanyType(companyType);
        Call<UpdateCompanyType> call = ApiClient.getCompanyService().updateCompanyType("" + companyId, updateCompanyType);
        call.enqueue(new Callback<UpdateCompanyType>() {
            @Override
            public void onResponse(Call<UpdateCompanyType> call, retrofit2.Response<UpdateCompanyType> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyType> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company GST Number -----------------------------
    public static void updateCompanyGstNumber(String companyId, String gstNumber) {
        UpdateCompanyGSTNumber updateCompanyGSTNumber = new UpdateCompanyGSTNumber(gstNumber);
        Call<UpdateCompanyGSTNumber> call = ApiClient.getCompanyService().updateCompanyGSTNumber("" + companyId, updateCompanyGSTNumber);
        call.enqueue(new Callback<UpdateCompanyGSTNumber>() {
            @Override
            public void onResponse(Call<UpdateCompanyGSTNumber> call, retrofit2.Response<UpdateCompanyGSTNumber> response) {
                if (response.isSuccessful()) {
                    UpdateCompanyGSTNumber updateCompanyGSTNumber1 = response.body();
                    Log.i("Updated", String.valueOf(updateCompanyGSTNumber1));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyGSTNumber> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }

    //-------------------------------------- Update Company PAN Number -----------------------------
    public static void updateCompanyPanNumber(String companyId, String panNumber) {
        UpdateCompanyPAN updateCompanyPAN = new UpdateCompanyPAN(panNumber);
        Call<UpdateCompanyPAN> call = ApiClient.getCompanyService().updateCompanyPAN("" + companyId, updateCompanyPAN);
        call.enqueue(new Callback<UpdateCompanyPAN>() {
            @Override
            public void onResponse(Call<UpdateCompanyPAN> call, retrofit2.Response<UpdateCompanyPAN> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyPAN> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
    }
}
