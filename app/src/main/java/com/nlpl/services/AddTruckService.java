package com.nlpl.services;

import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckCarryingCapacity;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckDriverId;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckFeet;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckRcBook;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckType;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleInsurance;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleNumber;
import com.nlpl.model.UpdateTruckDetails.UpdateVehicleType;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddTruckService {
    @POST("/truck/addtruck")
    Call<AddTruckResponse> saveTruck(@Body AddTruckRequest addTruckRequest);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckVehicleNumber> updateTruckVehicleNumber(@Path("truckId") String truckId, @Body UpdateTruckVehicleNumber updateTruckVehicleNumber);

    @PUT("/truck/{truckId}")
    Call<UpdateVehicleType> updateVehicleType(@Path("truckId") String truckId, @Body UpdateVehicleType updateVehicleType);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckFeet> updateTruckFeet(@Path("truckId") String truckId, @Body UpdateTruckFeet updateTruckFeet);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckCarryingCapacity> updateTruckCarryingCapacity(@Path("truckId") String truckId, @Body UpdateTruckCarryingCapacity updateTruckCarryingCapacity);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckRcBook> updateTruckRcBook(@Path("truckId") String truckId, @Body UpdateTruckRcBook updateTruckRcBook);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckVehicleInsurance> updateTruckVehicleInsurance(@Path("truckId") String truckId, @Body UpdateTruckVehicleInsurance updateTruckVehicleInsurance);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckType> updateTruckType(@Path("truckId") String truckId, @Body UpdateTruckType updateTruckType);

    @PUT("/truck/{truckId}")
    Call<UpdateTruckDriverId> updateTruckDriverId(@Path("truckId") String truckId, @Body UpdateTruckDriverId updateTruckDriverId);
}

















