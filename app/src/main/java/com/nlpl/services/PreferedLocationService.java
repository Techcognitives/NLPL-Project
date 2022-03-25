package com.nlpl.services;

import com.nlpl.model.Requests.PreferredLocationRequest;
import com.nlpl.model.Requests.RatingRequest;
import com.nlpl.model.Responses.PreferedLocationResponse;
import com.nlpl.model.Responses.RatingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PreferedLocationService {
    @POST("/prefLacations/addPrefLocation")
    Call<PreferedLocationResponse> savePreferredLocation(@Body PreferredLocationRequest preferredLocationRequest);

    @GET("/prefLacations/userPrefLocations/{userId}")
    Call<PreferedLocationResponse> getPreferredLocation(@Path("userId") String userId);

    @DELETE("/prefLacations/detelLocation/{locationId}")
    Call<PreferedLocationResponse> deleteLocation(@Path("locationId") String locationId);
}
