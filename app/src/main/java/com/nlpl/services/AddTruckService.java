package com.nlpl.services;

import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.TruckResponse;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckCarryingCapacity;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckDriverId;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckType;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckVehicleNumber;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddTruckService {
    @POST("/truck/addtruck")
    Call<AddTruckResponse> saveTruck(@Body AddTruckRequest addTruckRequest);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckVehicleNumber> updateTruckVehicleNumber(@Path("truckId") String truckId, @Body UpdateTruckVehicleNumber updateTruckVehicleNumber);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckCarryingCapacity> updateTruckCarryingCapacity(@Path("truckId") String truckId, @Body UpdateTruckCarryingCapacity updateTruckCarryingCapacity);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckType> updateTruckType(@Path("truckId") String truckId, @Body UpdateTruckType updateTruckType);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckDriverId> updateTruckDriverId(@Path("truckId") String truckId, @Body UpdateTruckDriverId updateTruckDriverId);

    @DELETE("/truck/deleteTruck/{truckId}")
    Call<AddTruckResponse> deleteTruckDetails(@Path("truckId") String truckId);

    @GET("/truck/truckbyuserID/{userId}")
    Call<TruckResponse> getTruckDetails(@Path("userId")String userId);
}

















