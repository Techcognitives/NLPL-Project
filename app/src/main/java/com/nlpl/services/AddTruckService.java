package com.nlpl.services;

import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckRcBook;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckType;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleInsurance;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleNumber;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddTruckService {
    @POST("/truck/addtruck")
    Call<AddTruckResponse> saveTruck(@Body AddTruckRequest addTruckRequest);

    @PATCH("/truck/{truckId}")
    Call<UpdateTruckVehicleNumber> updateTruckVehicleNumber(@Path("truckId") String truckId, @Body UpdateTruckVehicleNumber updateTruckVehicleNumber);

    @PATCH("/truck/{truckId}")
    Call<UpdateTruckRcBook> updateTruckRcBook(@Path("truckId") String truckId, @Body UpdateTruckRcBook updateTruckRcBook);

    @PATCH("/truck/{truckId}")
    Call<UpdateTruckVehicleInsurance> updateTruckVehicleInsurance(@Path("truckId") String truckId, @Body UpdateTruckVehicleInsurance updateTruckVehicleInsurance);

    @PATCH("/truck/{truckId}")
    Call<UpdateTruckType> updateTruckVehicleType(@Path("truckId") String truckId, @Body UpdateTruckType updateTruckType);
}

















