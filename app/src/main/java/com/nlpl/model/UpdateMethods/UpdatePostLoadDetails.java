package com.nlpl.model.UpdateMethods;

import android.util.Log;

import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateCustomerBudget;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateCustomerNoteForSP;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadBodyType;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadCapacity;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropAdd;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropCity;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropCountry;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropPinCode;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropState;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadFeet;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadKmApprox;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickAdd;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickCity;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickCountry;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickPinCode;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickState;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPostPickUpDate;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPostPickUpTime;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadStatusSubmitted;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadVehicleModel;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostLoadDetails {

    //-------------------------------- Update Load PickUp Date -------------------------------------
    public static void updatePickUpDate(String loadId, String pickUpDate) {
        UpdateLoadPostPickUpDate updateLoadPostPickUpDate = new UpdateLoadPostPickUpDate(pickUpDate);
        Call<UpdateLoadPostPickUpDate> call = ApiClient.getPostLoadService().updateLoadPostPickUpDate("" + loadId, updateLoadPostPickUpDate);
        call.enqueue(new Callback<UpdateLoadPostPickUpDate>() {
            @Override
            public void onResponse(Call<UpdateLoadPostPickUpDate> call, Response<UpdateLoadPostPickUpDate> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post update Pick up Date");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPostPickUpDate> call, Throwable t) {
                Log.i("Not Successful", "Load Post update pick-up Not Updated");

            }
        });
    }

    //-------------------------------- Update Load PickUp Time -------------------------------------
    public static void updatePickUpTime(String loadId, String pickUpTime) {
        UpdateLoadPostPickUpTime updateLoadPostPickUpTime = new UpdateLoadPostPickUpTime(pickUpTime);
        Call<UpdateLoadPostPickUpTime> callPickUpTime = ApiClient.getPostLoadService().updateLoadPostPickUpTime("" + loadId, updateLoadPostPickUpTime);
        callPickUpTime.enqueue(new Callback<UpdateLoadPostPickUpTime>() {
            @Override
            public void onResponse(Call<UpdateLoadPostPickUpTime> call, Response<UpdateLoadPostPickUpTime> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPostPickUpTime> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Load Budget ------------------------------------------
    public static void updateBudget(String loadId, String budget) {
        UpdateCustomerBudget updateCustomerBudget = new UpdateCustomerBudget(budget);

        Call<UpdateCustomerBudget> callBudget = ApiClient.getPostLoadService().updateCustomerBudget("" + loadId, updateCustomerBudget);

        callBudget.enqueue(new Callback<UpdateCustomerBudget>() {
            @Override
            public void onResponse(Call<UpdateCustomerBudget> call, Response<UpdateCustomerBudget> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateCustomerBudget> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Load Vehicle Model -----------------------------------
    public static void updateVehicleModel(String loadId, String vehicleModel) {
        UpdateLoadVehicleModel updateLoadVehicleModel = new UpdateLoadVehicleModel(vehicleModel);
        Call<UpdateLoadVehicleModel> callVehicleModel = ApiClient.getPostLoadService().updateLoadVehicleModel("" + loadId, updateLoadVehicleModel);
        callVehicleModel.enqueue(new Callback<UpdateLoadVehicleModel>() {
            @Override
            public void onResponse(Call<UpdateLoadVehicleModel> call, Response<UpdateLoadVehicleModel> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadVehicleModel> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Load Vehicle Feet ------------------------------------
    public static void updateVehicleFeet(String loadId, String vehicleFeet) {
        UpdateLoadFeet updateLoadFeet = new UpdateLoadFeet(vehicleFeet);
        Call<UpdateLoadFeet> callFeet = ApiClient.getPostLoadService().updateLoadFeet("" + loadId, updateLoadFeet);
        callFeet.enqueue(new Callback<UpdateLoadFeet>() {
            @Override
            public void onResponse(Call<UpdateLoadFeet> call, Response<UpdateLoadFeet> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadFeet> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Load Vehicle Capacity --------------------------------
    public static void updateVehicleCapacity(String loadId, String vehicleCapacity) {
        UpdateLoadCapacity updateLoadCapacity = new UpdateLoadCapacity(vehicleCapacity);
        Call<UpdateLoadCapacity> callCapacity = ApiClient.getPostLoadService().updateLoadCapacity("" + loadId, updateLoadCapacity);
        callCapacity.enqueue(new Callback<UpdateLoadCapacity>() {
            @Override
            public void onResponse(Call<UpdateLoadCapacity> call, Response<UpdateLoadCapacity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadCapacity> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Load Vehicle Body Type -------------------------------
    public static void updateVehicleBodyType(String loadId, String vehicleBodyType) {
        UpdateLoadBodyType updateLoadBodyType = new UpdateLoadBodyType(vehicleBodyType);

        Call<UpdateLoadBodyType> callBodyType = ApiClient.getPostLoadService().updateLoadBodyType("" + loadId, updateLoadBodyType);

        callBodyType.enqueue(new Callback<UpdateLoadBodyType>() {
            @Override
            public void onResponse(Call<UpdateLoadBodyType> call, Response<UpdateLoadBodyType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadBodyType> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update PickUp Country ---------------------------------------
    public static void updatePickUpCountry(String loadId, String pickUpCountry) {
        UpdateLoadPickCountry updateLoadPickCountry = new UpdateLoadPickCountry(pickUpCountry);

        Call<UpdateLoadPickCountry> callPickCountry = ApiClient.getPostLoadService().updateLoadPickCountry("" + loadId, updateLoadPickCountry);

        callPickCountry.enqueue(new Callback<UpdateLoadPickCountry>() {
            @Override
            public void onResponse(Call<UpdateLoadPickCountry> call, Response<UpdateLoadPickCountry> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickCountry> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update PickUp Address ---------------------------------------
    public static void updatePickUpAddress(String loadId, String pickUpAddress) {
        UpdateLoadPickAdd updateLoadPickAdd = new UpdateLoadPickAdd(pickUpAddress);

        Call<UpdateLoadPickAdd> callPickAdd = ApiClient.getPostLoadService().updateLoadPickAdd("" + loadId, updateLoadPickAdd);

        callPickAdd.enqueue(new Callback<UpdateLoadPickAdd>() {
            @Override
            public void onResponse(Call<UpdateLoadPickAdd> call, Response<UpdateLoadPickAdd> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickAdd> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update PickUp Pin Code --------------------------------------
    public static void updatePickUpPinCode(String loadId, String pickUpPinCode) {
        UpdateLoadPickPinCode updateLoadPickPinCode = new UpdateLoadPickPinCode(pickUpPinCode);
        Call<UpdateLoadPickPinCode> callPinCode = ApiClient.getPostLoadService().updateLoadPickPinCode("" + loadId, updateLoadPickPinCode);
        callPinCode.enqueue(new Callback<UpdateLoadPickPinCode>() {
            @Override
            public void onResponse(Call<UpdateLoadPickPinCode> call, Response<UpdateLoadPickPinCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickPinCode> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update PickUp State -----------------------------------------
    public static void updatePickUpState(String loadId, String pickupState) {
        UpdateLoadPickState updateLoadPickState = new UpdateLoadPickState(pickupState);
        Call<UpdateLoadPickState> callPickState = ApiClient.getPostLoadService().updateLoadPickState("" + loadId, updateLoadPickState);
        callPickState.enqueue(new Callback<UpdateLoadPickState>() {
            @Override
            public void onResponse(Call<UpdateLoadPickState> call, Response<UpdateLoadPickState> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickState> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update PickUp City ------------------------------------------
    public static void updatePickUpCity(String loadId, String pickUpCity) {
        UpdateLoadPickCity updateLoadPickCity = new UpdateLoadPickCity(pickUpCity);
        Call<UpdateLoadPickCity> callPickCity = ApiClient.getPostLoadService().updateLoadPickCity("" + loadId, updateLoadPickCity);
        callPickCity.enqueue(new Callback<UpdateLoadPickCity>() {
            @Override
            public void onResponse(Call<UpdateLoadPickCity> call, Response<UpdateLoadPickCity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickCity> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Drop Country -----------------------------------------
    public static void updateDropCountry(String loadId, String dropCountry) {
        UpdateLoadDropCountry updateLoadDropCountry = new UpdateLoadDropCountry(dropCountry);

        Call<UpdateLoadDropCountry> callDropCountry = ApiClient.getPostLoadService().updateLoadDropCountry("" + loadId, updateLoadDropCountry);

        callDropCountry.enqueue(new Callback<UpdateLoadDropCountry>() {
            @Override
            public void onResponse(Call<UpdateLoadDropCountry> call, Response<UpdateLoadDropCountry> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropCountry> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Drop Address -----------------------------------------
    public static void updateDropAddress(String loadId, String dropAddress) {
        UpdateLoadDropAdd updateLoadDropAdd = new UpdateLoadDropAdd(dropAddress);

        Call<UpdateLoadDropAdd> callDropAdd = ApiClient.getPostLoadService().updateLoadDropAdd("" + loadId, updateLoadDropAdd);

        callDropAdd.enqueue(new Callback<UpdateLoadDropAdd>() {
            @Override
            public void onResponse(Call<UpdateLoadDropAdd> call, Response<UpdateLoadDropAdd> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropAdd> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Drop Pin Code ----------------------------------------
    public static void updateDropPinCode(String loadId, String dropPinCode) {
        UpdateLoadDropPinCode updateLoadDropPinCode = new UpdateLoadDropPinCode(dropPinCode);

        Call<UpdateLoadDropPinCode> callDropPinCode = ApiClient.getPostLoadService().updateLoadDropPinCode("" + loadId, updateLoadDropPinCode);

        callDropPinCode.enqueue(new Callback<UpdateLoadDropPinCode>() {
            @Override
            public void onResponse(Call<UpdateLoadDropPinCode> call, Response<UpdateLoadDropPinCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropPinCode> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Drop State -------------------------------------------
    public static void updateDropState(String loadId, String dropState) {
        UpdateLoadDropState updateLoadDropState = new UpdateLoadDropState(dropState);

        Call<UpdateLoadDropState> callDropState = ApiClient.getPostLoadService().updateLoadDropState("" + loadId, updateLoadDropState);

        callDropState.enqueue(new Callback<UpdateLoadDropState>() {
            @Override
            public void onResponse(Call<UpdateLoadDropState> call, Response<UpdateLoadDropState> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropState> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Drop City --------------------------------------------
    public static void updateDropCity(String loadId, String dropCiy) {
        UpdateLoadDropCity updateLoadDropCity = new UpdateLoadDropCity(dropCiy);

        Call<UpdateLoadDropCity> callDropCity = ApiClient.getPostLoadService().updateLoadDropCity("" + loadId, updateLoadDropCity);

        callDropCity.enqueue(new Callback<UpdateLoadDropCity>() {
            @Override
            public void onResponse(Call<UpdateLoadDropCity> call, Response<UpdateLoadDropCity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropCity> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Approx KM --------------------------------------------
    public static void updateApproxKM(String loadId, String kmApprox) {
        UpdateLoadKmApprox updateLoadKmApprox = new UpdateLoadKmApprox(kmApprox);

        Call<UpdateLoadKmApprox> callKmApprox = ApiClient.getPostLoadService().updateLoadKmApprox("" + loadId, updateLoadKmApprox);

        callKmApprox.enqueue(new Callback<UpdateLoadKmApprox>() {
            @Override
            public void onResponse(Call<UpdateLoadKmApprox> call, Response<UpdateLoadKmApprox> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadKmApprox> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Notes ------------------------------------------------
    public static void updateNotes(String loadId, String notes) {
        UpdateCustomerNoteForSP updateCustomerNoteForSP = new UpdateCustomerNoteForSP(notes);

        Call<UpdateCustomerNoteForSP> call = ApiClient.getPostLoadService().updateCustomerNoteForSP("" + loadId, updateCustomerNoteForSP);

        call.enqueue(new Callback<UpdateCustomerNoteForSP>() {
            @Override
            public void onResponse(Call<UpdateCustomerNoteForSP> call, Response<UpdateCustomerNoteForSP> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateCustomerNoteForSP> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
    }

    //-------------------------------- Update Load Status ------------------------------------------
    public static void updateStatus(String loadId, String status){
        UpdateLoadStatusSubmitted updateLoadStatusSubmitted = new UpdateLoadStatusSubmitted(status);

        Call<UpdateLoadStatusSubmitted> call = ApiClient.getPostLoadService().updateBidStatusSubmitted("" + loadId, updateLoadStatusSubmitted);

        call.enqueue(new Callback<UpdateLoadStatusSubmitted>() {
            @Override
            public void onResponse(Call<UpdateLoadStatusSubmitted> call, retrofit2.Response<UpdateLoadStatusSubmitted> response) {

            }

            @Override
            public void onFailure(Call<UpdateLoadStatusSubmitted> call, Throwable t) {

            }
        });
    }
}
