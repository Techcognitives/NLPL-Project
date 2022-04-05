package com.nlpl.services;

import com.nlpl.model.Requests.PostATripRequest;
import com.nlpl.model.Responses.PostATripResponse;
import com.nlpl.model.Responses.TripResponse;
import com.nlpl.model.UpdateModel.Models.UpdateTripDetails;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostTripService {

    @POST("/trip/postATrip")
    Call<PostATripResponse> PostTrip(@Body PostATripRequest postATripRequest);

    @PUT("/trip/updateTripByTripId/{tripId}")
    Call<UpdateTripDetails> updateTripDetails(@Path("tripId") String tripId, @Body UpdateTripDetails updateTripDetails);

    @GET("/trip/getTripDtByUser/{userId}")
    Call<TripResponse> getTripDetailsByUserId(@Path("userId")String userId);

    @DELETE("/trip/deleteTripDetails/{tripId}")
    Call<TripResponse> deleteTrip(@Path("tripId") String tripId);

    @GET("/trip/getTripDtByTripId/{tripID}")
    Call<TripResponse> getTripDetails(@Path("tripID") String tripID);

    @GET("/trip/getAllTrips")
    Call<TripResponse> getAllTripDetails();

}
