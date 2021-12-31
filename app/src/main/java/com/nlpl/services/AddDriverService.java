package com.nlpl.services;

import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverName;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverNumber;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverUploadLicense;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddDriverService {
    @POST("driver/addDriver")
    Call<AddDriverResponse> saveDriver(@Body AddDriverRequest addDriverRequest);

    @PATCH("/driver/{driverId}")
    Call<UpdateDriverName> updateDriverName(@Path("driverId") String driverId, @Body UpdateDriverName updateDriverName);

    @PATCH("/driver/{driverId}")
    Call<UpdateDriverUploadLicense> updateDriverUploadLicense(@Path("driverId") String driverId, @Body UpdateDriverUploadLicense updateDriverUploadLicense);

    @PATCH("/driver/{driverId}")
    Call<UpdateDriverNumber> updateDriverNumber(@Path("driverId") String driverId, @Body UpdateDriverNumber updateDriverNumber);

    @PATCH("/driver/{driverId}")
    Call<UpdateDriverEmailId> updateDriverEmailId(@Path("driverId") String driverId, @Body UpdateDriverEmailId updateDriverEmailId);
}
