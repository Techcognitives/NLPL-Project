package com.nlpl.services;

import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverName;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverNumber;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverTruckId;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverUploadLicense;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddDriverService {
    @POST("driver/addDriver")
    Call<AddDriverResponse> saveDriver(@Body AddDriverRequest addDriverRequest);

    @PUT("/driver/updateDr/{driverId}")
    Call<UpdateDriverName> updateDriverName(@Path("driverId") String driverId, @Body UpdateDriverName updateDriverName);

    @PUT("/driver/updateDr/{driverId}")
    Call<UpdateDriverUploadLicense> updateDriverUploadLicense(@Path("driverId") String driverId, @Body UpdateDriverUploadLicense updateDriverUploadLicense);

    @PUT("/driver/updateDr/{driverId}")
    Call<UpdateDriverNumber> updateDriverNumber(@Path("driverId") String driverId, @Body UpdateDriverNumber updateDriverNumber);

    @PUT("/driver/updateDr/{driverId}")
    Call<UpdateDriverEmailId> updateDriverEmailId(@Path("driverId") String driverId, @Body UpdateDriverEmailId updateDriverEmailId);

    @PUT("/driver/updateDr/{driverId}")
    Call<UpdateDriverTruckId> updateDriverTruckId(@Path("driverId") String driverId, @Body UpdateDriverTruckId updateDriverTruckId);
}
