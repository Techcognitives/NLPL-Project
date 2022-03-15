package com.nlpl.utils;

import android.util.Log;

import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUser {
    //------------------------------------- Create User in API -------------------------------------
    public static UserRequest createUser(String userName, String mobileNumber, String alternateMobileNumber, String address, String role, String emailId, String pinCode, String city, String state) {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(userName);
        userRequest.setPhone_number(mobileNumber);
        userRequest.setAlternate_ph_no(alternateMobileNumber);
        userRequest.setAddress(address);
        userRequest.setUser_type(role);
        userRequest.setEmail_id(emailId);
        userRequest.setIsRegistration_done(1);
        userRequest.setPin_code(pinCode);
        userRequest.setPreferred_location(city);
        userRequest.setState_code(state);
        userRequest.setIsCompany_added(0);
        userRequest.setIsBankDetails_given(0);
        userRequest.setIsPersonal_dt_added(0);
        userRequest.setIsDriver_added(0);
        userRequest.setIsTruck_added(0);
        userRequest.setIsProfile_pic_added(0);
        return userRequest;
    }

    public static void saveUser(UserRequest userRequest) {
        Call<UserResponse> userResponseCall = ApiClient.getUserService().saveUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                Log.i("Message UserCreated:", userResponse.getData().getPhone_number());
                UserResponse userResponse = response.body();
                Log.i("Msg Success", String.valueOf(userResponse));
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------
}
