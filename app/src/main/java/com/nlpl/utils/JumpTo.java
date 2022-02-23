package com.nlpl.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.CpuUsageInfo;

import com.nlpl.ui.ui.activities.BankDetailsActivity;
import com.nlpl.ui.ui.activities.CompanyDetailsActivity;
import com.nlpl.ui.ui.activities.CustomerDashboardActivity;
import com.nlpl.ui.ui.activities.CustomerLoadsHistoryActivity;
import com.nlpl.ui.ui.activities.DriverDetailsActivity;
import com.nlpl.ui.ui.activities.FindLoadsActivity;
import com.nlpl.ui.ui.activities.LogInActivity;
import com.nlpl.ui.ui.activities.OtpCodeActivity;
import com.nlpl.ui.ui.activities.PersonalDetailsActivity;
import com.nlpl.ui.ui.activities.PersonalDetailsAndIdProofActivity;
import com.nlpl.ui.ui.activities.PostALoadActivity;
import com.nlpl.ui.ui.activities.RegistrationActivity;
import com.nlpl.ui.ui.activities.ServiceProviderDashboardActivity;
import com.nlpl.ui.ui.activities.SliderActivity;
import com.nlpl.ui.ui.activities.SplashScreenActivity;
import com.nlpl.ui.ui.activities.VehicleDetailsActivity;
import com.nlpl.ui.ui.activities.ViewBankDetailsActivity;
import com.nlpl.ui.ui.activities.ViewDriverDetailsActivity;
import com.nlpl.ui.ui.activities.ViewPersonalDetailsActivity;
import com.nlpl.ui.ui.activities.ViewTruckDetailsActivity;

public class JumpTo {

    public static void goToLogInActivity(Activity activity){
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToOTPActivity(Activity activity, String mobileNumber, Boolean isEditMobileNumber, String userId){
        Intent intent = new Intent(activity, OtpCodeActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEditPhone", isEditMobileNumber);
        intent.putExtra("userId", userId);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToRegistrationActivity(Activity activity, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, RegistrationActivity.class);
        intent.putExtra("mobile1", mobileNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }

        activity.overridePendingTransition(0, 0);
    }

    public static void goToServiceProviderDashboard(Activity activity, String mobileNumber, Boolean isFromLoadNotification){
        Intent intent = new Intent(activity, ServiceProviderDashboardActivity.class);
        intent.putExtra("mobile2", mobileNumber);
        intent.putExtra("loadNotification", isFromLoadNotification);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToCustomerDashboard(Activity activity, String mobileNumber, Boolean isBidsReceived){
        Intent intent = new Intent(activity, CustomerDashboardActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("bidsReceived", isBidsReceived);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToSliderActivity(Activity activity){
        Intent intent = new Intent(activity, SliderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewPersonalDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, ViewPersonalDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewBankDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, ViewBankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToBankDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFinish, String bankId){
        Intent intent = new Intent(activity, BankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("bankDetailsID", bankId);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToPostALoad(Activity activity, String userId, String mobileNumber, Boolean reActivate, Boolean isEdit, String loadId, Boolean isFinish){
        Intent intent = new Intent(activity, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("reActivate", reActivate);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("loadId", loadId);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToCustomerLoadHistoryActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, CustomerLoadsHistoryActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToFindLoadsActivity(Activity activity, String userId, String mobileNumber){
        Intent intent = new Intent(activity, FindLoadsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void goToFindTrucksActivity(Activity activity, String userId, String mobileNumber){
        Intent intent = new Intent(activity, FindLoadsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void goToVehicleDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFromBidNow, Boolean isFromAssignTruck, Boolean isFinish, String driverId, String truckId){
        Intent intent = new Intent(activity, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("fromBidNow", isFromBidNow);
        intent.putExtra("assignTruck", isFromAssignTruck);
        intent.putExtra("driverId", driverId);
        intent.putExtra("truckId", truckId);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewVehicleDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, ViewTruckDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToDriverDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFromBidNow, Boolean isFinish, String truckId, String driverId){
        Intent intent = new Intent(activity, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("fromBidNow", isFromBidNow);
        intent.putExtra("truckIdPass", truckId);
        intent.putExtra("driverId", driverId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToViewDriverDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, ViewDriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToPersonalDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean profile, Boolean isFinish){
        Intent intent = new Intent(activity, PersonalDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("profile", profile);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToPersonalDetailsIdProofActivity(Activity activity, String userId, String mobileNumber, Boolean isFinish){
        Intent intent = new Intent(activity, PersonalDetailsAndIdProofActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }

    public static void goToCompanyDetailsActivity(Activity activity, String userId, String mobileNumber, Boolean isEdit, Boolean isFinish){
        Intent intent = new Intent(activity, CompanyDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        activity.startActivity(intent);
        if (isFinish){
            activity.finish();
        }
        activity.overridePendingTransition(0, 0);
    }
}
