package com.nlpl.services;

import com.nlpl.model.AddDriverRequest;
import com.nlpl.model.AddDriverResponse;
import com.nlpl.model.AddTruckRequest;
import com.nlpl.model.AddTruckResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddDriverService {
    @POST("/truck/addtruck")
    Call<AddDriverResponse> saveDriver(@Body AddDriverRequest addDriverRequest);
}
