package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BankModel;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.ui.adapters.BanksAdapter;
import com.nlpl.ui.ui.adapters.DriversAdapter;
import com.nlpl.ui.ui.adapters.LoadNotificationAdapter;
import com.nlpl.ui.ui.adapters.TrucksAdapter;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private RequestQueue mQueue;

    private ArrayList<LoadNotificationModel> loadList = new ArrayList<>();
    private LoadNotificationAdapter loadListAdapter;
    private RecyclerView loadListRecyclerView;

//    private ArrayList<DriverModel> driverList = new ArrayList<>();
//    private DriversAdapter driverListAdapter;
//    private RecyclerView driverListRecyclerView;

//    Dialog previewDialogDriverDetails;
//    TextView previewDriverDetailsDriverBankAdd;
//    TextView previewDriverDetailsDriverName, previewDriverDetailsDriverNumber, previewDriverDetailsEmailId;
//    ImageView previewDrivingLicense, previewDriverSelfie;
//    TextView addDriver;
//    String mobileNoDriverAPI, userDriverIdAPI, driverUserIdGet;

    Dialog setBudget, selectTruckDialog, previewDialogBidNow;

    String isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    ConstraintLayout profileAndRegistrationLayout;
    SwipeListener swipeListener;

    View actionBar;
    TextView spQuote, selectTruck, actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    Dialog menuDialog;
    ConstraintLayout drawerLayout;
    TextView menuUserNameTextView, mobileText, personalDetailsButton, bankDetailsTextView, addTrucksTextView;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView, truckDetailsLogoImageView;

    ConstraintLayout loadNotificationConstrain, bidsSubmittedConstrain;
    TextView loadNotificationTextView, bidsSubmittedTextView;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String userId, userIdAPI, phone, mobileNoAPI;
    ArrayList<String> arrayUserId, arrayTruckList, arrayMobileNo, arrayDriverMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

    String mobile, name, address, pinCode, city, role, emailIdAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile2");
            Log.i("Mobile No Registration", phone);
        }

        actionBar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Dashboard");
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.profile_registration_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));

        loadNotificationConstrain = (ConstraintLayout) findViewById(R.id.dashboard_load_notification_constrain);
        bidsSubmittedConstrain = (ConstraintLayout) findViewById(R.id.dashboard_bids_submitted_constrain);
        loadNotificationTextView = (TextView) findViewById(R.id.dashboard_load_notification_button);
        bidsSubmittedTextView = (TextView) findViewById(R.id.dashboard_bids_submitted_button);

        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayDriverMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();
        arrayTruckList = new ArrayList<>();

        profileAndRegistrationLayout = (ConstraintLayout) findViewById(R.id.profile_registration_constrain);

//        addDriver = findViewById(R.id.addDriverDone);

//        previewDialogDriverDetails = new Dialog(DashboardActivity.this);
//        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_details);
//        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        previewDriverDetailsDriverName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_name_text_view);
//        previewDriverDetailsDriverNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_phone_number_text_view);
//        previewDriverDetailsEmailId = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_email_id_text_view);
//        previewDrivingLicense = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driving_license_image_view);
//        previewDriverSelfie = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_selfie_image_view);
//        previewDriverDetailsDriverBankAdd = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_add_driver_bank);

        menuDialog = new Dialog(DashboardActivity.this);
        menuDialog.setContentView(R.layout.dialog_menu);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        drawerLayout = (ConstraintLayout) menuDialog.findViewById(R.id.drawer_menu);
        menuUserNameTextView = (TextView) menuDialog.findViewById(R.id.menu_name_text);
        mobileText = (TextView) menuDialog.findViewById(R.id.menu_mobile);
        personalDetailsButton = (TextView) menuDialog.findViewById(R.id.menu_personal_details_button);
        bankDetailsTextView = (TextView) menuDialog.findViewById(R.id.menu_bank_details_button);
        addTrucksTextView = (TextView) menuDialog.findViewById(R.id.menu_truck_details);
        personalDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_personal_details_logo_image_view);
        bankDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_bank_details_logo_image_view);
        truckDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_truck_details_logo_image_view);

        swipeListener = new SwipeListener(profileAndRegistrationLayout);

        previewDialogBidNow = new Dialog(DashboardActivity.this);
        previewDialogBidNow.setContentView(R.layout.dialog_bid_now);
        previewDialogBidNow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        previewDriverDetailsDriverBankAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DashboardActivity.this, BankDetailsActivity.class);
//                intent.putExtra("isEdit", false);
//                intent.putExtra("userId", driverUserIdGet);
//                intent.putExtra("mobile", phone);
//                startActivity(intent);
//            }
//        });

        mQueue = Volley.newRequestQueue(DashboardActivity.this);
        getUserId(phone);

    }

    private void getUserId(String userMobileNumber) {

        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userIdAPI = data.getString("user_id");
                        arrayUserId.add(userIdAPI);
                        mobileNoAPI = data.getString("phone_number");
                        arrayMobileNo.add(mobileNoAPI);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(userMobileNumber)) {
                            userId = arrayUserId.get(j);
                            Log.i("userIDAPI:", userId);
                        }
                    }

                    getUserDetails();

                    //---------------------------- Get Bank Details -------------------------------------------
                    loadListRecyclerView = (RecyclerView) findViewById(R.id.dashboard_load_notification_recycler_view);

                    LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManagerBank.setReverseLayout(true);
                    loadListRecyclerView.setLayoutManager(linearLayoutManagerBank);
                    loadListRecyclerView.setHasFixedSize(true);

                    loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadList);
                    loadListRecyclerView.setAdapter(loadListAdapter);
                    getLoadNotificationList();
                    //------------------------------------------------------------------------------------------

//                    //---------------------------- Get Driver Details -------------------------------------------
//                    driverListRecyclerView = (RecyclerView) findViewById(R.id.driver_list_view);
//
//                    LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
//                    linearLayoutManagerDriver.setReverseLayout(true);
//                    driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
//                    driverListRecyclerView.setHasFixedSize(true);
//
//                    driverListAdapter = new DriversAdapter(DashboardActivity.this, driverList);
//                    driverListRecyclerView.setAdapter(driverListAdapter);
//                    getDriverDetailsList();
//                    //------------------------------------------------------------------------------------------

//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }

    private void getUserDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        name = obj.getString("name");
                        mobile = obj.getString("phone_number");
                        address = obj.getString("address");
                        city = obj.getString("preferred_location");
                        pinCode = obj.getString("pin_code");
                        role = obj.getString("user_type");

                        emailIdAPI = obj.getString("email_id");

                        isPersonalDetailsDone = obj.getString("isPersonal_dt_added");
                        isFirmDetailsDone = obj.getString("isCompany_added");
                        isBankDetailsDone = obj.getString("isBankDetails_given");
                        isTruckDetailsDone = obj.getString("isTruck_added");
                        isDriverDetailsDone = obj.getString("isDriver_added");

                        //-------------------------------------Personal details ---- -------------------------------------
                        menuUserNameTextView.setText(" Hello, " + name + "!");
                        String s1 = mobile.substring(2, 12);
                        mobileText.setText("+91 " + s1);

                        //--------------------------------------------------------------------------------------------------------
                        if (isPersonalDetailsDone.equals("1")) {
                            personalDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.personal_success));
                        } else {
                            personalDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.personal));
                        }

                        if (isBankDetailsDone.equals("1")) {
                            bankDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.bank_success));
                        } else {
                            bankDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.bank));
                        }

                        if (isTruckDetailsDone.equals("1")) {
                            truckDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.truck_success));
                        } else {
                            truckDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.truck));
                        }

                        if (role.equals("Customer")) {

                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

//    public void getDriverDetailsList() {
//        //---------------------------- Get Driver Details ------------------------------------------
//        String url1 = getString(R.string.baseURL) + "/driver/userId/" + userId;
//        Log.i("URL: ", url1);
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    driverList = new ArrayList<>();
//                    JSONArray driverLists = response.getJSONArray("data");
//                    for (int i = 0; i < driverLists.length(); i++) {
//                        JSONObject obj = driverLists.getJSONObject(i);
//                        DriverModel modelDriver = new DriverModel();
//                        modelDriver.setUser_id(obj.getString("user_id"));
//                        modelDriver.setDriver_id(obj.getString("driver_id"));
//                        modelDriver.setDriver_name(obj.getString("driver_name"));
//                        modelDriver.setUpload_lc(obj.getString("upload_dl"));
//                        modelDriver.setDriver_selfie(obj.getString("driver_selfie"));
//                        modelDriver.setDriver_number(obj.getString("driver_number"));
//                        modelDriver.setDriver_emailId(obj.getString("driver_emailId"));
//                        driverList.add(modelDriver);
//                    }
//                    if (driverList.size() > 0) {
//                        driverListAdapter.updateData(driverList);
//                    } else {
//
//                    }
//
//                    if (driverList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        driverListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        driverListRecyclerView.setLayoutParams(params);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        mQueue.add(request);
//        //-------------------------------------------------------------------------------------------
//    }

//    public void onClickPreviewDriverDetails(DriverModel obj) {
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//        previewDialogDriverDetails.show();
//        previewDialogDriverDetails.getWindow().setAttributes(lp);
//
//        previewDriverDetailsDriverName.setText(" Driver Name: " + obj.getDriver_name());
//        previewDriverDetailsDriverNumber.setText(" Mobile Number: +" + obj.getDriver_number());
//        getUserDriverId(obj.getDriver_number());
//        previewDriverDetailsEmailId.setText(" Email Id: " + obj.getDriver_emailId());
//
//        String drivingLicenseURL = obj.getUpload_lc();
//        Log.i("IMAGE DL URL", drivingLicenseURL);
//        new DownloadImageTask(previewDrivingLicense).execute(drivingLicenseURL);
//
//        String selfieURL = obj.getDriver_selfie();
//        Log.i("IMAGE Selfie URL", selfieURL);
//        new DownloadImageTask(previewDriverSelfie).execute(selfieURL);
//    }

//    public void getDriverDetails(DriverModel obj) {
//        Intent intent = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
//        intent.putExtra("userId", userId);
//        intent.putExtra("isEdit", true);
//        intent.putExtra("driverId", obj.getDriver_id());
//        intent.putExtra("mobile", phone);
//
//        startActivity(intent);
//    }

    public void onClickProfileAndRegister(View view) {
        switch (view.getId()) {
            case R.id.menu_personal_details_button:
                if (isPersonalDetailsDone.equals("1")) {
                    Intent intent = new Intent(DashboardActivity.this, ViewPersonalDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DashboardActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;

            case R.id.menu_bank_details_button:
                if (isBankDetailsDone.equals("1")) {
                    Intent intent = new Intent(DashboardActivity.this, ViewBankDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DashboardActivity.this, BankDetailsActivity.class);
                    intent.putExtra("isEdit", false);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;

            case R.id.menu_truck_details:
                if (isTruckDetailsDone.equals("1")) {
                    Intent intent = new Intent(DashboardActivity.this, ViewTruckDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                } else {
                    Intent intent2 = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
                    intent2.putExtra("userId", userId);
                    intent2.putExtra("isEdit", false);
                    intent2.putExtra("mobile", phone);
                    startActivity(intent2);
                }
                break;

//            case R.id.addDriverDone:
//                Intent intent4 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
//                intent4.putExtra("userId", userId);
//                intent4.putExtra("isEdit", false);
//                intent4.putExtra("mobile", phone);
//                startActivity(intent4);
//                break;
        }
    }

//    private void getUserDriverId(String getMobile) {
//        String receivedMobile = getMobile;
//        //------------------------------get user details by mobile Number---------------------------------
//        //-----------------------------------Get User Details---------------------------------------
//        String url = getString(R.string.baseURL) + "/user/get";
//        Log.i("URL at Profile:", url);
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray jsonArray = response.getJSONArray("data");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject data = jsonArray.getJSONObject(i);
//                        userDriverIdAPI = data.getString("user_id");
//                        mobileNoDriverAPI = data.getString("phone_number");
//                        arrayUserDriverId = new ArrayList<>();
//                        arrayUserDriverId.add(userDriverIdAPI);
//                        arrayDriverMobileNo.add(mobileNoDriverAPI);
//
//                    }
//
//                    for (int j = 0; j < arrayDriverMobileNo.size(); j++) {
//                        if (arrayDriverMobileNo.get(j).equals(receivedMobile)) {
//                            driverUserIdGet = arrayUserDriverId.get(j);
//                            Log.i("DriverUserId", driverUserIdGet);
//
//                            getUserDriverBankDetails(driverUserIdGet);
//                        }
//                    }
////
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        mQueue.add(request);
//
//        //------------------------------------------------------------------------------------------------
//
//    }

//    private void getUserDriverBankDetails(String driverUserId) {
//
//        String url = getString(R.string.baseURL) + "/bank/getBkByUserId/" + driverUserId;
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray truckLists = response.getJSONArray("data");
//                    for (int i = 0; i < truckLists.length(); i++) {
//                        JSONObject obj = truckLists.getJSONObject(i);
//                        String bankName = obj.getString("bank_name");
//                        String bankAccountNumber = obj.getString("account_number");
//                        String ifsiCode = obj.getString("IFSI_CODE");
//
//                        TextView previewDriverDetailsDriverBankName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_bank_name);
//                        TextView previewDriverDetailsDriverBankAccountNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_account_number);
//                        TextView previewDriverDetailsDriverBankIFSICode = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_ifsc_code);
//
//                        previewDriverDetailsDriverBankName.setText(" Bank Name: " + bankName);
//                        previewDriverDetailsDriverBankAccountNumber.setText(" Account Number: " + bankAccountNumber);
//                        previewDriverDetailsDriverBankIFSICode.setText(" IFSI Code: " + ifsiCode);
//
//
//                        if (previewDriverDetailsDriverBankName.getText().toString() == null) {
//                            previewDriverDetailsDriverBankAdd.setVisibility(View.VISIBLE);
//                        } else {
//                            previewDriverDetailsDriverBankAdd.setVisibility(View.INVISIBLE);
//                        }
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        mQueue.add(request);
//    }

    public void onCLickShowMenu(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(menuDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.END;
        menuDialog.show();
        menuDialog.setCancelable(true);
        menuDialog.getWindow().setAttributes(lp);
    }

    public void onClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DashboardActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onClickDismiss(View view) {
        menuDialog.dismiss();
    }

    public void onClickLoadAndBids(View view) {
        switch (view.getId()) {
            case R.id.dashboard_load_notification_button:
                loadNotificationConstrain.setVisibility(View.VISIBLE);
                bidsSubmittedConstrain.setVisibility(View.INVISIBLE);
                loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                break;

            case R.id.dashboard_bids_submitted_button:
                loadNotificationConstrain.setVisibility(View.INVISIBLE);
                bidsSubmittedConstrain.setVisibility(View.VISIBLE);
                loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                break;
        }
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:

                break;

            case R.id.bottom_nav_customer_dashboard:
                Intent intent = new Intent(DashboardActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                startActivity(intent);
                break;
        }
    }

    public void getLoadNotificationList() {
        //---------------------------- Get Bank Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/loadpost/getAllPosts";
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    loadList = new ArrayList<>();
                    JSONArray loadLists = response.getJSONArray("data");
                    for (int i = 0; i < loadLists.length(); i++) {
                        JSONObject obj = loadLists.getJSONObject(i);
                        LoadNotificationModel modelLoadNotification = new LoadNotificationModel();
                        modelLoadNotification.setIdpost_load(obj.getString("idpost_load"));
                        modelLoadNotification.setUser_id(obj.getString("user_id"));
                        modelLoadNotification.setPick_up_date(obj.getString("pick_up_date"));
                        modelLoadNotification.setPick_up_time(obj.getString("pick_up_time"));
                        modelLoadNotification.setBudget(obj.getString("budget"));
                        modelLoadNotification.setBid_status(obj.getString("bid_status"));
                        modelLoadNotification.setVehicle_model(obj.getString("vehicle_model"));
                        modelLoadNotification.setFeet(obj.getString("feet"));
                        modelLoadNotification.setCapacity(obj.getString("capacity"));
                        modelLoadNotification.setBody_type(obj.getString("body_type"));
                        modelLoadNotification.setPick_add(obj.getString("pick_add"));
                        modelLoadNotification.setPick_pin_code(obj.getString("pick_pin_code"));
                        modelLoadNotification.setPick_city(obj.getString("pick_city"));
                        modelLoadNotification.setPick_state(obj.getString("pick_state"));
                        modelLoadNotification.setPick_country(obj.getString("pick_country"));
                        modelLoadNotification.setDrop_add(obj.getString("drop_add"));
                        modelLoadNotification.setDrop_pin_code(obj.getString("drop_pin_code"));
                        modelLoadNotification.setDrop_city(obj.getString("drop_city"));
                        modelLoadNotification.setDrop_state(obj.getString("drop_state"));
                        modelLoadNotification.setDrop_country(obj.getString("drop_country"));
                        modelLoadNotification.setKm_approx(obj.getString("km_approx"));
                        modelLoadNotification.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        loadList.add(modelLoadNotification);
                    }
                    if (loadList.size() > 0) {
                        loadListAdapter.updateData(loadList);
                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
        //-------------------------------------------------------------------------------------------
    }

    public void onClickBidNow(LoadNotificationModel obj) {

        String postLoadId = obj.getIdpost_load();
        String pick_up_date = obj.getPick_up_date();
        String pick_up_time = obj.getPick_up_time();
        String required_budget = obj.getBudget();
        String distance = obj.getKm_approx();
        String required_model = obj.getVehicle_model();
        String required_feet = obj.getFeet();
        String required_capacity = obj.getCapacity();
        String required_truck_body = obj.getBody_type();
        String pick_up_location = obj.getPick_add() + " " + obj.getPick_city() + " " + obj.getPick_state() + " " + obj.getPick_pin_code();
        String drop_location = obj.getDrop_add() + " " + obj.getDrop_city() + " " + obj.getDrop_state() + " " + obj.getDrop_pin_code();
        String received_notes_description = obj.getNotes_meterial_des();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogBidNow.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogBidNow.show();
        previewDialogBidNow.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        TextView reqBudget = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_distance_textview);
        TextView reqModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_model_textview);
        TextView reqFeet = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_feet_textview);
        TextView reqCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_capacity_textview);
        TextView reqBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
        TextView pickUpLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
        TextView dropLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_drop_location_textview);
        TextView receivedNotes = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_received_notes_textview);

        pickUpDate.setText(pick_up_date);
        pickUpTime.setText(pick_up_time);
        reqBudget.setText(required_budget);
        approxDistance.setText(distance);
        reqModel.setText(required_model);
        reqFeet.setText(required_feet);
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        //----------------------------------------------------------------------------------------------------------------

        //-------------------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_truck_textview);
        TextView selectDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_driver_textview);
        TextView addTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_add_truck_textview);
        TextView addDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_add_driver_textview);
        TextView selectedTruckModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_model_textview);
        TextView selectedTruckFeet = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_feet_textview);
        TextView selectedTruckCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
        TextView selectedTruckBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_body_type_textview);
        EditText notesSP = (EditText) previewDialogBidNow.findViewById(R.id.dialog_bid_now_notes_editText);
        CheckBox declaration = (CheckBox) previewDialogBidNow.findViewById(R.id.dialog_bid_now_declaration);
        TextView acceptAndBid = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_accept_and_bid_btn);
        TextView cancel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_cancel_btn);

        spQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetSet();
            }
        });

        selectTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTruckToBid();
            }
        });

    }



    private void selectTruckToBid(){
        selectTruckDialog = new Dialog(DashboardActivity.this);
        selectTruckDialog.setContentView(R.layout.dialog_spinner);
        selectTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTruckDialog.show();
        selectTruckDialog.setCancelable(false);
        TextView model_title = selectTruckDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText("Select Truck to Bid");

        ListView modelList = (ListView) selectTruckDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayTruckList);
        modelList.setAdapter(adapter1);

        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectTruck.setText(adapter1.getItem(i));
                selectTruckDialog.dismiss();
            }
        });
    }

    private void budgetSet() {

        setBudget = new Dialog(DashboardActivity.this);
        setBudget.setContentView(R.layout.dialog_budget);

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(setBudget.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.TOP;

        setBudget.show();
        setBudget.setCancelable(false);
        setBudget.getWindow().setAttributes(lp2);

        EditText budget = setBudget.findViewById(R.id.dialog_budget_edit);
        Button okBudget = setBudget.findViewById(R.id.dialog_budget_ok_btn);
        budget.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {
                    spQuote.setText(budgetEditText);
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                } else {
                    okBudget.setEnabled(false);
                    okBudget.setBackgroundResource((R.drawable.button_de_active));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        okBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBudget.dismiss();
            }
        });
    }


    private class SwipeListener implements View.OnTouchListener {
        GestureDetector gestureDetector;

        SwipeListener(View view) {
            int threshold = 100;
            int velocity_threshold = 100;

            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try {
                        if (Math.abs(xDiff) > Math.abs(yDiff)) {
                            if (Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocity_threshold) {
                                if (xDiff < 0) {
                                    //Swiped Left
                                    actionBarMenuButton.performClick();
                                } else {
                                    //Swiped Right
                                    menuDialog.dismiss();
                                }
                                return true;
                            }
                        } else {
                            if (Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocity_threshold) {
                                if (yDiff > 0) {
                                    //Swiped Down
                                } else {
                                    //Swiped Up
                                }
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            };
            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }
}