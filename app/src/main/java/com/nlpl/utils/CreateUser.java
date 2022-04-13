package com.nlpl.utils;

import android.system.StructTimespec;
import android.util.Log;

import com.nlpl.model.Requests.PreferredLocationRequest;
import com.nlpl.model.Requests.RatingRequest;
import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.PreferedLocationResponse;
import com.nlpl.model.Responses.RatingResponse;
import com.nlpl.model.Responses.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUser {
    //------------------------------------- Create User in API -------------------------------------
    public static UserRequest createUser(String userName, String mobileNumber, String alternateMobileNumber, String address, String role, String emailId, String pinCode, String city, String state, String deviceId, String latitude, String longitude) {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(userName);
        userRequest.setPhone_number(mobileNumber);
        userRequest.setAlternate_ph_no(alternateMobileNumber);
        userRequest.setUser_type(role);
        userRequest.setPreferred_location(city);
        userRequest.setAddress(address);
        userRequest.setState_code(state);
        userRequest.setPin_code(pinCode);
        userRequest.setEmail_id(emailId);
        userRequest.setIsRegistration_done(1);
        userRequest.setIsProfile_pic_added(0);
        userRequest.setIsTruck_added(0);
        userRequest.setIsDriver_added(0);
        userRequest.setIsBankDetails_given(0);
        userRequest.setIsCompany_added(0);
        userRequest.setIsPersonal_dt_added(0);
        userRequest.setIs_Addhar_verfied(0);
        userRequest.setIs_pan_verfied(0);
        userRequest.setIs_user_verfied(0);
        userRequest.setLatitude(latitude);
        userRequest.setLongitude(longitude);
        userRequest.setDevice_id(deviceId);
        userRequest.setIs_self_added_asDriver(0);

        return userRequest;
    }

    public static void saveUser(UserRequest userRequest) {
        Call<UserResponse> userResponseCall = ApiClient.getUserService().saveUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }


    //----------------------------------------------------------------------------------------------


    //------------------------------------- Create Rating in API -------------------------------------
    public static RatingRequest createRatings(String transactionId, String ratingsNumber, String ratingCommets, String toWhoUserID, String givenByUserID) {
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setTransection_id(transactionId);
        ratingRequest.setRated_no(ratingsNumber);
        ratingRequest.setRatings_comment(ratingCommets);
        ratingRequest.setUser_id(toWhoUserID);
        ratingRequest.setGiven_by(givenByUserID);
        return ratingRequest;
    }

    public static void saveRatings(RatingRequest ratingRequest) {
        Call<RatingResponse> ratingResponseCall = ApiClient.getRatingService().saveRating(ratingRequest);
        ratingResponseCall.enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
//                Log.i("Message UserCreated:", userResponse.getData().getPhone_number());
                RatingResponse ratingResponse = response.body();
                Log.i("Msg Success", String.valueOf(ratingResponse));
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //------------------------------- Create Preferred Location in API -----------------------------
    public static PreferredLocationRequest createPreferredLocation(String userId, String state, String city, String pinCode, String latitude, String longitude) {
        PreferredLocationRequest preferredLocationRequest = new PreferredLocationRequest();
        preferredLocationRequest.setUser_id(userId);
        preferredLocationRequest.setPref_state(state);
        preferredLocationRequest.setPref_city(city);
        preferredLocationRequest.setPref_pin_code(pinCode);
        preferredLocationRequest.setLatitude(latitude);
        preferredLocationRequest.setLongitude(longitude);
        return preferredLocationRequest;
    }

    public static void savePreferredLocation(PreferredLocationRequest preferredLocationRequest) {
        Call<PreferedLocationResponse> preferredLocationResponseCall = ApiClient.getPreferredLocationService().savePreferredLocation(preferredLocationRequest);
        preferredLocationResponseCall.enqueue(new Callback<PreferedLocationResponse>() {
            @Override
            public void onResponse(Call<PreferedLocationResponse> call, Response<PreferedLocationResponse> response) {

            }

            @Override
            public void onFailure(Call<PreferedLocationResponse> call, Throwable t) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------
}
