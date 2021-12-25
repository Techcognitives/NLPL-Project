package com.nlpl.services;

import com.nlpl.model.UserRequest;
import com.nlpl.model.UserResponse;
import com.nlpl.model.UserUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserService {

    @POST("/user/create")
    Call<UserResponse> saveUser(@Body UserRequest userRequest);

    @PUT("/user/")
    Call<UserUpdate> updateUserDetails(@Query("id") String id, @Body UserUpdate userUpdate);
}
