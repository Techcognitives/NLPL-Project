package com.nlpl.services;

import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverName;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverNumber;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverTruckId;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddDriverService {
    @POST("driver/addDriver")
    Call<AddDriverResponse> saveDriver(@Body AddDriverRequest addDriverRequest);

    @PUT("/driver/udateDr/{driverId}")
    Call<UpdateDriverName> updateDriverName(@Path("driverId") String driverId, @Body UpdateDriverName updateDriverName);

    @PUT("/driver/udateDr/{driverId}")
    Call<UpdateDriverNumber> updateDriverNumber(@Path("driverId") String driverId, @Body UpdateDriverNumber updateDriverNumber);

    @PUT("/driver/udateDr/{driverId}")
    Call<UpdateDriverEmailId> updateDriverEmailId(@Path("driverId") String driverId, @Body UpdateDriverEmailId updateDriverEmailId);

    @PUT("/driver/udateDr/{driverId}")
    Call<UpdateDriverTruckId> updateDriverTruckId(@Path("driverId") String driverId, @Body UpdateDriverTruckId updateDriverTruckId);
}
