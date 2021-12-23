package com.nlpl.services;

import com.nlpl.model.AddTruckRequest;
import com.nlpl.model.AddTruckResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddTruckService {
    @POST("/truck/addtruck")
    Call<AddTruckResponse> saveTruck(@Body AddTruckRequest addTruckRequest);
}

















