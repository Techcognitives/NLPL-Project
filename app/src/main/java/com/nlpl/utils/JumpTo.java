package com.nlpl.utils;

import android.app.Activity;
import android.content.Intent;

import com.nlpl.ui.ui.activities.LogInActivity;
import com.nlpl.ui.ui.activities.OtpCodeActivity;
import com.nlpl.ui.ui.activities.RegistrationActivity;
import com.nlpl.ui.ui.activities.ServiceProviderDashboardActivity;
import com.nlpl.ui.ui.activities.SplashScreenActivity;

public class JumpTo {

    public static void goToLogInActivity(Activity activity){
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToOTPActivity(Activity activity, String mobileNumber, Boolean isEditMobileNumber){
        Intent i5 = new Intent(activity, OtpCodeActivity.class);
        i5.putExtra("mobile", mobileNumber);
        i5.putExtra("isEditPhone", isEditMobileNumber);
        activity.startActivity(i5);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToRegistrationActivity(Activity activity, String mobileNumber){
        Intent i8 = new Intent(activity, RegistrationActivity.class);
        i8.putExtra("mobile1", mobileNumber);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i8);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void goToServiceProviderDashboard(Activity activity, String mobileNumber, Boolean isFromLoadNotification){
        Intent i8 = new Intent(activity, ServiceProviderDashboardActivity.class);
        i8.putExtra("mobile2", mobileNumber);
        i8.putExtra("loadNotification", isFromLoadNotification);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i8);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }

    public static void goToCustomerDashboard(){

    }

}
