package com.nlpl.utils;

import android.app.Activity;
import android.content.Intent;

import com.nlpl.ui.activities.BankDetailsActivity;
import com.nlpl.ui.activities.CompanyDetailsActivity;
import com.nlpl.ui.activities.CustomerDashboardActivity;
import com.nlpl.ui.activities.CustomerLoadsHistoryActivity;
import com.nlpl.ui.activities.FindTripLPActivity;
import com.nlpl.ui.activities.PostATripActivity;
import com.nlpl.ui.activities.SettingsAndPreferences;
import com.nlpl.ui.activities.DriverDetailsActivity;
import com.nlpl.ui.activities.FindLoadsActivity;
import com.nlpl.ui.activities.FindTrucksActivity;
import com.nlpl.ui.activities.LanguageActivity;
import com.nlpl.ui.activities.LogInActivity;
import com.nlpl.ui.activities.OtpCodeActivity;
import com.nlpl.ui.activities.PersonalDetailsActivity;
import com.nlpl.ui.activities.PostALoadActivity;
import com.nlpl.ui.activities.RegistrationActivity;
import com.nlpl.ui.activities.ServiceProviderDashboardActivity;
import com.nlpl.ui.activities.SliderActivity;
import com.nlpl.ui.activities.TrackForLoadPosterActivity;
import com.nlpl.ui.activities.TrackForServiceProviderActivity;
import com.nlpl.ui.activities.VehicleDetailsActivity;
import com.nlpl.ui.activities.ViewBankDetailsActivity;
import com.nlpl.ui.activities.ViewDriverDetailsActivity;
import com.nlpl.ui.activities.ViewPersonalDetailsActivity;
import com.nlpl.ui.activities.ViewTruckDetailsActivity;

public class JumpTo {

    public static void goToLogInActivity(Activity activity) {
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToOTPActivity(Activity activity, String mobileNumber, Boolean isEditMobileNumber, String userId) {
        Intent intent = new Intent(activity, OtpCodeActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEditPhone", isEditMobileNumber);
        intent.putExtra("userId", userId);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void getToSettingAndPreferences(Activity activity, String mobileNumber, String userId, String role, Boolean isFinish) {
        Intent intent = new Intent(activity, SettingsAndPreferences.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("userId", userId);
        intent.putExtra("role", role);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToRegistrationActivity(Activity activity, String mobileNumber, Boolean isEdit, String userId, Boolean isFinish) {
        Intent intent = new Intent(activity, RegistrationActivity.class);
        intent.putExtra("mobile1", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("userId", userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToLanguageActivity(Activity activity, String mobileNumber) {
        Intent intent = new Intent(activity, LanguageActivity.class);
        intent.putExtra("mobile1", mobileNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToServiceProviderDashboard(Activity activity, String mobileNumber, Boolean isFromLoadNotification) {
        Intent intent = new Intent(activity, ServiceProviderDashboardActivity.class);
        intent.putExtra("mobile2", mobileNumber);
        intent.putExtra("loadNotification", isFromLoadNotification);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToCustomerDashboard(Activity activity, String mobileNumber, Boolean isBidsReceived) {
        Intent intent = new Intent(activity, CustomerDashboardActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("bidsReceived", isBidsReceived);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToSliderActivity(Activity activity, String mobileNumber) {
        Intent intent = new Intent(activity, SliderActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewPersonalDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, ViewPersonalDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewBankDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, ViewBankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToBankDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFinish, String bankId) {
        Intent intent = new Intent(activity, BankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("bankDetailsID", bankId);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToPostALoad(Activity activity, String userId, String mobileNumber, Boolean reActivate, Boolean isEdit, String loadId, Boolean isFinish) {
        Intent intent = new Intent(activity, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("reActivate", reActivate);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("loadId", loadId);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToCustomerLoadHistoryActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, CustomerLoadsHistoryActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToFindLoadsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, FindLoadsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToFindTrucksActivity(Activity activity, String userId, String mobileNumber) {
        Intent intent = new Intent(activity, FindTrucksActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void goToVehicleDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFromBidNow, Boolean isFromAssignTruck, Boolean isFinish, String driverId, String truckId) {
        Intent intent = new Intent(activity, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("fromBidNow", isFromBidNow);
        intent.putExtra("assignTruck", isFromAssignTruck);
        intent.putExtra("driverId", driverId);
        intent.putExtra("truckId", truckId);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewVehicleDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, ViewTruckDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToDriverDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFromBidNow, Boolean isFinish, String truckId, String driverId) {
        Intent intent = new Intent(activity, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("fromBidNow", isFromBidNow);
        intent.putExtra("truckIdPass", truckId);
        intent.putExtra("driverId", driverId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewDriverDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, ViewDriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToPersonalDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean profile, Boolean isFinish) {
        Intent intent = new Intent(activity, PersonalDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("profile", profile);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToCompanyDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFinish) {
        Intent intent = new Intent(activity, CompanyDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToSPTrackActivity(Activity activity, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, TrackForServiceProviderActivity.class);
        intent.putExtra("mobile2", mobileNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToLPTrackActivity(Activity activity, String mobileNumber, Boolean isFinish) {
        Intent intent = new Intent(activity, TrackForLoadPosterActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToPostATrip(Activity activity, String mobileNumber, String userId, Boolean isEdit, String tripId, Boolean isFinish) {
        Intent intent = new Intent(activity, PostATripActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("tripId", tripId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToFindTripLPActivity(Activity activity, String mobileNumber, String userId, Boolean isFinish) {
        Intent intent = new Intent(activity, FindTripLPActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("userId", userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }
}
