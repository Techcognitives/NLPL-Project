package com.nlpl.model.UpdateMethods;

import android.util.Log;

import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserAddress;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserEmailId;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsBankDetailsGiven;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsCompanyAdded;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsDriverAdded;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsPersonalDetailsAdded;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsProfileAdded;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsRegistrationDone;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsTruckAdded;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserName;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserPhoneNumber;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserPinCode;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserPreferredLanguage;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserPreferredLocation;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserRating;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserStateCode;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserType;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateUserDetails {

    //-------------------------------- Update User Name --------------------------------------------
    public static void updateUserName(String userId, String userName) {
        UpdateUserName updateUserName = new UpdateUserName(userName);
        Call<UpdateUserName> call = ApiClient.getUserService().updateUserName("" + userId, updateUserName);
        call.enqueue(new Callback<UpdateUserName>() {
            @Override
            public void onResponse(Call<UpdateUserName> call, Response<UpdateUserName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "UserName");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserName> call, Throwable t) {
                Log.i("Not Successful", "UserName");
            }
        });
    }

    //-------------------------------- Update User Email Id ----------------------------------------
    public static void updateUserEmailId(String userId, String emailId) {
        UpdateUserEmailId updateUserEmailId = new UpdateUserEmailId(emailId);
        Call<UpdateUserEmailId> call = ApiClient.getUserService().updateUserEmailId("" + userId, updateUserEmailId);
        call.enqueue(new Callback<UpdateUserEmailId>() {
            @Override
            public void onResponse(Call<UpdateUserEmailId> call, Response<UpdateUserEmailId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Email Id");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserEmailId> call, Throwable t) {
                Log.i("Not Successful", "User Email Id");

            }
        });
    }

    //------------------------------------ Update User Address -------------------------------------
    public static void updateUserAddress(String userId, String address) {
        UpdateUserAddress updateUserAddress = new UpdateUserAddress(address);
        Call<UpdateUserAddress> call = ApiClient.getUserService().updateUserAddress("" + userId, updateUserAddress);
        call.enqueue(new Callback<UpdateUserAddress>() {
            @Override
            public void onResponse(Call<UpdateUserAddress> call, Response<UpdateUserAddress> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Address");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserAddress> call, Throwable t) {
                Log.i("Not Successful", "UserAddress");

            }
        });
    }

    //-------------------------------- Update User Pin Code ----------------------------------------
    public static void updateUserPinCode(String userId, String pinCode) {
        UpdateUserPinCode updateUserStateCode = new UpdateUserPinCode(pinCode);
        Call<UpdateUserPinCode> call = ApiClient.getUserService().updateUserPinCode("" + userId, updateUserStateCode);
        call.enqueue(new Callback<UpdateUserPinCode>() {
            @Override
            public void onResponse(Call<UpdateUserPinCode> call, Response<UpdateUserPinCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Pin Code");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPinCode> call, Throwable t) {
                Log.i("Not Successful", "User Pin Code");

            }
        });
    }

    //-------------------------------- Update User State -------------------------------------------
    public static void updateUserState(String userId, String state) {
        UpdateUserStateCode updateUserStateCode = new UpdateUserStateCode(state);
        Call<UpdateUserStateCode> call = ApiClient.getUserService().updateUserStateCode("" + userId, updateUserStateCode);
        call.enqueue(new Callback<UpdateUserStateCode>() {
            @Override
            public void onResponse(Call<UpdateUserStateCode> call, Response<UpdateUserStateCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User State Code");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserStateCode> call, Throwable t) {
                Log.i("Not Successful", "User State Code");

            }
        });
    }

    //-------------------------------- Update User City --------------------------------------------
    public static void updateUserCity(String userId, String city) {
        UpdateUserPreferredLocation updateUserPreferredLocation = new UpdateUserPreferredLocation(city);
        Call<UpdateUserPreferredLocation> call = ApiClient.getUserService().updateUserPreferredLocation("" + userId, updateUserPreferredLocation);
        call.enqueue(new Callback<UpdateUserPreferredLocation>() {
            @Override
            public void onResponse(Call<UpdateUserPreferredLocation> call, Response<UpdateUserPreferredLocation> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Preferred Location");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPreferredLocation> call, Throwable t) {
                Log.i("Not Successful", "User Preferred Location");

            }
        });
    }

    //-------------------------------- Update User Type --------------------------------------------
    public static void updateUserType(String userId, String role) {
        UpdateUserType updateUserType = new UpdateUserType(role);
        Call<UpdateUserType> call = ApiClient.getUserService().updateUserType("" + userId, updateUserType);
        call.enqueue(new Callback<UpdateUserType>() {
            @Override
            public void onResponse(Call<UpdateUserType> call, Response<UpdateUserType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "UserType");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserType> call, Throwable t) {
                Log.i("Not Successful", "UserType");

            }
        });
    }

    //-------------------------------- Update User Phone Number ------------------------------------
    public static void updateUserPhoneNumber(String userId, String mobile) {
        Log.i("Mobile No Update", mobile);
        Log.i("user Id at update", userId);
        UpdateUserPhoneNumber updateUserPhoneNumber = new UpdateUserPhoneNumber(mobile);

        Call<UpdateUserPhoneNumber> call = ApiClient.getUserService().updateUserPhoneNumber("" + userId, updateUserPhoneNumber);

        call.enqueue(new Callback<UpdateUserPhoneNumber>() {
            @Override
            public void onResponse(Call<UpdateUserPhoneNumber> call, Response<UpdateUserPhoneNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "PhoneNumber");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPhoneNumber> call, Throwable t) {
                Log.i("Not Successful", "PhoneNumber");

            }
        });
    }

    //-------------------------------- Update User Preferred Language ------------------------------
    public static void updateUserPreferredLanguage(String userId, String language) {
        UpdateUserPreferredLanguage updateUserPreferredLanguage = new UpdateUserPreferredLanguage(language);
        Call<UpdateUserPreferredLanguage> call = ApiClient.getUserService().updateUserPreferredLanguage("" + userId, updateUserPreferredLanguage);
        call.enqueue(new Callback<UpdateUserPreferredLanguage>() {
            @Override
            public void onResponse(Call<UpdateUserPreferredLanguage> call, Response<UpdateUserPreferredLanguage> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Preferred Language");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPreferredLanguage> call, Throwable t) {
                Log.i("Not Successful", "User Preferred Language");

            }
        });
    }

    //---------------------------- Update User is Registration Done --------------------------------
    public static void updateUserIsRegistrationDone(String userId, String isRegistrationDone) {
        UpdateUserIsRegistrationDone updateUserIsRegistrationDone = new UpdateUserIsRegistrationDone(isRegistrationDone);
        Call<UpdateUserIsRegistrationDone> call = ApiClient.getUserService().updateUserIsRegistrationDone("" + userId, updateUserIsRegistrationDone);
        call.enqueue(new Callback<UpdateUserIsRegistrationDone>() {
            @Override
            public void onResponse(Call<UpdateUserIsRegistrationDone> call, Response<UpdateUserIsRegistrationDone> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Registration Done");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsRegistrationDone> call, Throwable t) {
                Log.i("Not Successful", "User is Registration Done");

            }
        });
    }

    //-------------------------------- Update User is Truck Added ----------------------------------
    public static void updateUserIsTruckAdded(String userId, String isTruckAdded) {
        UpdateUserIsTruckAdded updateUserIsTruckAdded = new UpdateUserIsTruckAdded(isTruckAdded);
        Call<UpdateUserIsTruckAdded> call = ApiClient.getUserService().updateUserIsTruckAdded("" + userId, updateUserIsTruckAdded);
        call.enqueue(new Callback<UpdateUserIsTruckAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsTruckAdded> call, Response<UpdateUserIsTruckAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsTruckAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    public static void updateUserIsDriverAdded(String userId, String isDriverAdded) {
        UpdateUserIsDriverAdded updateUserIsDriverAdded = new UpdateUserIsDriverAdded(isDriverAdded);
        Call<UpdateUserIsDriverAdded> call = ApiClient.getUserService().updateUserIsDriverAdded("" + userId, updateUserIsDriverAdded);
        call.enqueue(new Callback<UpdateUserIsDriverAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsDriverAdded> call, Response<UpdateUserIsDriverAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsDriverAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
    }

    //-------------------------------- Update User is Bank Added -----------------------------------
    public static void updateUserIsBankDetailsGiven(String userId, String isBankAdded) {
        UpdateUserIsBankDetailsGiven updateUserIsDriverAdded = new UpdateUserIsBankDetailsGiven(isBankAdded);
        Call<UpdateUserIsBankDetailsGiven> call = ApiClient.getUserService().updateUserIsBankDetailsGiven("" + userId, updateUserIsDriverAdded);
        call.enqueue(new Callback<UpdateUserIsBankDetailsGiven>() {
            @Override
            public void onResponse(Call<UpdateUserIsBankDetailsGiven> call, Response<UpdateUserIsBankDetailsGiven> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsBankDetailsGiven> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
    }

    //-------------------------------- Update User is Company Added -------------------------------
    public static void updateUserIsCompanyAdded(String userId, String isCompanyAdded) {
        UpdateUserIsCompanyAdded updateUserIsCompanyAdded = new UpdateUserIsCompanyAdded(isCompanyAdded);
        Call<UpdateUserIsCompanyAdded> call = ApiClient.getUserService().updateUserIsCompanyAdded("" + userId, updateUserIsCompanyAdded);

        call.enqueue(new Callback<UpdateUserIsCompanyAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsCompanyAdded> call, Response<UpdateUserIsCompanyAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Company Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsCompanyAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Company Added");

            }
        });
    }

    //-------------------------------- Update User is Personal Details -----------------------------
    public static void updateUserIsPersonalDetailsAdded(String userId, String isPersonalAdded) {
        UpdateUserIsPersonalDetailsAdded updateUserIsPersonalDetailsAdded = new UpdateUserIsPersonalDetailsAdded(isPersonalAdded);
        Call<UpdateUserIsPersonalDetailsAdded> call = ApiClient.getUserService().updateUserIsPersonalDetailsAdded("" + userId, updateUserIsPersonalDetailsAdded);
        call.enqueue(new Callback<UpdateUserIsPersonalDetailsAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsPersonalDetailsAdded> call, Response<UpdateUserIsPersonalDetailsAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Personal Details");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsPersonalDetailsAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Personal Details");

            }
        });
    }

    //-------------------------------- Update User is Profile Details -----------------------------
    public static void updateUserIsProfileAdded(String userId, String isProfileAdded) {
        UpdateUserIsProfileAdded updateUserIsProfileAdded = new UpdateUserIsProfileAdded(isProfileAdded);
        Call<UpdateUserIsProfileAdded> call = ApiClient.getUserService().updateUserIsProfileAdded("" + userId, updateUserIsProfileAdded);
        call.enqueue(new Callback<UpdateUserIsProfileAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsProfileAdded> call, Response<UpdateUserIsProfileAdded> response) {

            }

            @Override
            public void onFailure(Call<UpdateUserIsProfileAdded> call, Throwable t) {

            }
        });
    }

    //-------------------------------- Update User Rating --------------------------------------------
    public static void updateUserRating(String spId, String rating) {
        UpdateUserRating updateUserRating = new UpdateUserRating(rating);
        Call<UpdateUserRating> call = ApiClient.getUserService().updateUserRating("" + spId, updateUserRating);
        call.enqueue(new Callback<UpdateUserRating>() {
            @Override
            public void onResponse(Call<UpdateUserRating> call, Response<UpdateUserRating> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Rating");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserRating> call, Throwable t) {
                Log.i("Not Successful", "User Rating");

            }
        });
    }
}
