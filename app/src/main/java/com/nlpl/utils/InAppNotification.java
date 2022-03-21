package com.nlpl.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.nlpl.R;
import com.nlpl.ui.activities.BankDetailsActivity;
import com.nlpl.ui.activities.CompanyDetailsActivity;
import com.nlpl.ui.activities.CustomerDashboardActivity;
import com.nlpl.ui.activities.CustomerLoadsHistoryActivity;
import com.nlpl.ui.activities.DriverDetailsActivity;
import com.nlpl.ui.activities.FindLoadsActivity;
import com.nlpl.ui.activities.LogInActivity;
import com.nlpl.ui.activities.OtpCodeActivity;
import com.nlpl.ui.activities.PersonalDetailsActivity;
import com.nlpl.ui.activities.PersonalDetailsAndIdProofActivity;
import com.nlpl.ui.activities.PostALoadActivity;
import com.nlpl.ui.activities.RegistrationActivity;
import com.nlpl.ui.activities.ServiceProviderDashboardActivity;
import com.nlpl.ui.activities.SliderActivity;
import com.nlpl.ui.activities.VehicleDetailsActivity;
import com.nlpl.ui.activities.ViewBankDetailsActivity;
import com.nlpl.ui.activities.ViewDriverDetailsActivity;
import com.nlpl.ui.activities.ViewPersonalDetailsActivity;
import com.nlpl.ui.activities.ViewTruckDetailsActivity;

public class InAppNotification {

    public static void SendNotificationJumpToLogIn(Activity activity, String title, String message) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent notificationIntent = new Intent(activity, LogInActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, notificationIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToOTPCodeActivity(Activity activity, String title, String message, String mobileNumber, String userId) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, OtpCodeActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEditPhone", false);
        intent.putExtra("userId", userId);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToRegistrationActivity(Activity activity, String title, String message, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10007";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, RegistrationActivity.class);
        intent.putExtra("mobile1", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToServiceProviderDashboardActivity(Activity activity, String title, String message, String mobileNumber, Boolean isFromLoadNotification) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, ServiceProviderDashboardActivity.class);
        intent.putExtra("mobile2", mobileNumber);
        intent.putExtra("loadNotification", isFromLoadNotification);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToCustomerDashboardActivity(Activity activity, String title, String message, String mobileNumber, Boolean isBidsReceived) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, CustomerDashboardActivity.class);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("bidsReceived", isBidsReceived);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToSliderActivity(Activity activity, String title, String message) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, SliderActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }


    public static void SendNotificationJumpToViewPersonalDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, ViewPersonalDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToViewBankDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, ViewBankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToBankDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber, Boolean isEdit, String bankId) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, BankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("bankDetailsID", bankId);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToPostALoadActivity(Activity activity, String title, String message, String userId, String mobileNumber, Boolean reActivate, Boolean isEdit, String loadId) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("reActivate", reActivate);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("loadId", loadId);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToCustomerLoadHistoryActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, CustomerLoadsHistoryActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToFindLoadsActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, FindLoadsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToVehicleDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber, Boolean isEdit, Boolean isFromBidNow, Boolean isFromAssignTruck, String driverId, String truckId) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("fromBidNow", isFromBidNow);
        intent.putExtra("assignTruck", isFromAssignTruck);
        intent.putExtra("driverId", driverId);
        intent.putExtra("truckId", truckId);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToViewVehicleDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, ViewTruckDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToDriverDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber, Boolean isEdit, Boolean isFromBidNow, String truckId, String driverId) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.putExtra("fromBidNow", isFromBidNow);
        intent.putExtra("truckIdPass", truckId);
        intent.putExtra("driverId", driverId);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToViewDriverDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, ViewDriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToPersonalDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber, Boolean profile) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, PersonalDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("profile", profile);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToPersonalDetailsIdProofActivity(Activity activity, String title, String message, String userId, String mobileNumber) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, PersonalDetailsAndIdProofActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static void SendNotificationJumpToCompanyDetailsActivity(Activity activity, String title, String message, String userId, String mobileNumber, Boolean isEdit) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";

        Intent intent = new Intent(activity, CompanyDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("isEdit", isEdit);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_truck_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
