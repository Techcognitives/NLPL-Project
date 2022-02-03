package com.nlpl.model.UpdateMethods;

import android.util.Log;

import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckCarryingCapacity;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckDriverId;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckFeet;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckType;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckVehicleNumber;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateVehicleType;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTruckDetails {

    //-------------------------------- Update Truck Number -----------------------------------------
    public static void updateTruckNumber(String truckId, String truckNumber) {
        UpdateTruckVehicleNumber updateTruckVehicleNumber = new UpdateTruckVehicleNumber(truckNumber);
        Call<UpdateTruckVehicleNumber> call = ApiClient.addTruckService().updateTruckVehicleNumber("" + truckId, updateTruckVehicleNumber);
        call.enqueue(new Callback<UpdateTruckVehicleNumber>() {
            @Override
            public void onResponse(Call<UpdateTruckVehicleNumber> call, Response<UpdateTruckVehicleNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckVehicleNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
    }

    //-------------------------------- Update Truck Model ------------------------------------------
    public static void updateTruckModel(String truckId, String truckModel) {
        UpdateTruckType updateTruckType = new UpdateTruckType(truckModel);
        Call<UpdateTruckType> call = ApiClient.addTruckService().updateTruckType("" + truckId, updateTruckType);
        call.enqueue(new Callback<UpdateTruckType>() {
            @Override
            public void onResponse(Call<UpdateTruckType> call, Response<UpdateTruckType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckType> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
    }

    //-------------------------------- Update Truck Capacity ---------------------------------------
    public static void updateTruckCarryingCapacity(String truckId, String truckCapacity) {
        UpdateTruckCarryingCapacity updateTruckCarryingCapacity = new UpdateTruckCarryingCapacity(truckCapacity);
        Call<UpdateTruckCarryingCapacity> call = ApiClient.addTruckService().updateTruckCarryingCapacity("" + truckId, updateTruckCarryingCapacity);
        call.enqueue(new Callback<UpdateTruckCarryingCapacity>() {
            @Override
            public void onResponse(Call<UpdateTruckCarryingCapacity> call, Response<UpdateTruckCarryingCapacity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckCarryingCapacity> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
    }

    //-------------------------------- Update Truck body Type --------------------------------------
    public static void updateTruckBodyType(String truckId, String bodyTypeSelected) {
        UpdateVehicleType updateVehicleType = new UpdateVehicleType(bodyTypeSelected);
        Call<UpdateVehicleType> call = ApiClient.addTruckService().updateVehicleType("" + truckId, updateVehicleType);
        call.enqueue(new Callback<UpdateVehicleType>() {
            @Override
            public void onResponse(Call<UpdateVehicleType> call, Response<UpdateVehicleType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateVehicleType> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
    }

    //-------------------------------- Update Truck Feet -------------------------------------------
    public static void updateTruckFeet(String truckId, String truckFeet) {
        UpdateTruckFeet updateTruckFeet = new UpdateTruckFeet(truckFeet);
        Call<UpdateTruckFeet> call = ApiClient.addTruckService().updateTruckFeet("" + truckId, updateTruckFeet);
        call.enqueue(new Callback<UpdateTruckFeet>() {
            @Override
            public void onResponse(Call<UpdateTruckFeet> call, Response<UpdateTruckFeet> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckFeet> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
    }

    //-------------------------------- Update Truck DriverId ---------------------------------------
    public static void updateTruckDriverId(String truckId, String truckDriverId) {

        UpdateTruckDriverId updateTruckDriverId = new UpdateTruckDriverId(truckDriverId);

        Call<UpdateTruckDriverId> call = ApiClient.addTruckService().updateTruckDriverId("" + truckId, updateTruckDriverId);

        call.enqueue(new Callback<UpdateTruckDriverId>() {
            @Override
            public void onResponse(Call<UpdateTruckDriverId> call, Response<UpdateTruckDriverId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckDriverId> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }
}
