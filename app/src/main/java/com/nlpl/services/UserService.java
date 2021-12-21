package com.nlpl.services;

import com.nlpl.model.BankRequest;
import com.nlpl.model.BankResponse;
import com.nlpl.model.UserRequest;
import com.nlpl.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("/user/create")
    Call<UserResponse> saveUser(@Body UserRequest userRequest);
}
