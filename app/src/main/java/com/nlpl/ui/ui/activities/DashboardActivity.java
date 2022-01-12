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
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.ui.adapters.DriversAdapter;
import com.nlpl.ui.ui.adapters.TrucksAdapter;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private RequestQueue mQueue;

    private ArrayList<DriverModel> driverList = new ArrayList<>();
    private DriversAdapter driverListAdapter;
    private RecyclerView driverListRecyclerView;

    String isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    ConstraintLayout profileAndRegistrationLayout;
    SwipeListener swipeListener;

    View actionBar;
    TextView addDriver, addTruck, accNoDone, actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    Dialog menuDialog;
    ConstraintLayout drawerLayout;
    TextView menuUserNameTextView, mobileText, personalDetailsButton, bankDetailsTextView, addTrucksTextView, addDriversTextView;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView, truckDetailsLogoImageView, driverDetailsLogoImageView;

    View bottomNav;
    TextView truckLoadText;

    String userId, userIdAPI, phone, mobileNoAPI, mobileNoDriverAPI, userDriverIdAPI, driverUserIdGet;
    ArrayList<String> arrayUserId, arrayUserDriverId, arrayMobileNo, arrayDriverMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

    String mobile, name, address, pinCode, city, role, emailIdAPI;
    ConstraintLayout driverDone;

    Dialog previewDialogDriverDetails;
    TextView previewDriverDetailsDriverBankAdd;
    TextView previewDriverDetailsDriverName, previewDriverDetailsDriverNumber, previewDriverDetailsEmailId;
    ImageView previewDrivingLicense, previewDriverSelfie;

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

        arrayUserId = new ArrayList<>();
        arrayUserDriverId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayDriverMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();

        actionBarTitle.setText("Dashboard");
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.profile_registration_bottom_nav_bar);
        truckLoadText = (TextView) bottomNav.findViewById(R.id.dhuejsfcb);
        profileAndRegistrationLayout = (ConstraintLayout) findViewById(R.id.profile_registration_constrain);

        accNoDone = findViewById(R.id.bank_list_account_number_text);
        addTruck = findViewById(R.id.addTruck);
        driverDone = findViewById(R.id.driverDone);
        addDriver = findViewById(R.id.addDriverDone);

        previewDialogDriverDetails = new Dialog(DashboardActivity.this);
        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_details);
        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverDetailsDriverName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_name_text_view);
        previewDriverDetailsDriverNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_phone_number_text_view);
        previewDriverDetailsEmailId = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_email_id_text_view);
        previewDrivingLicense = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driving_license_image_view);
        previewDriverSelfie = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_selfie_image_view);
        previewDriverDetailsDriverBankAdd = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_add_driver_bank);

        menuDialog = new Dialog(DashboardActivity.this);
        menuDialog.setContentView(R.layout.dialog_menu);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        drawerLayout = (ConstraintLayout) menuDialog.findViewById(R.id.drawer_menu);
        menuUserNameTextView = (TextView) menuDialog.findViewById(R.id.menu_name_text);
        mobileText = (TextView) menuDialog.findViewById(R.id.menu_mobile);
        personalDetailsButton = (TextView) menuDialog.findViewById(R.id.menu_personal_details_button);
        bankDetailsTextView = (TextView) menuDialog.findViewById(R.id.menu_bank_details_button);
        addTrucksTextView = (TextView) menuDialog.findViewById(R.id.menu_truck_details);
        addDriversTextView = (TextView) menuDialog.findViewById(R.id.menu_driver_details);
        personalDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_personal_details_logo_image_view);
        bankDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_bank_details_logo_image_view);
        truckDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_truck_details_logo_image_view);
        driverDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_driver_details_logo_image_view);

        swipeListener = new SwipeListener(profileAndRegistrationLayout);

        previewDriverDetailsDriverBankAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, BankDetailsActivity.class);
                intent.putExtra("isEdit", false);
                intent.putExtra("userId", driverUserIdGet);
                intent.putExtra("mobile", phone);
                startActivity(intent);
            }
        });

        mQueue = Volley.newRequestQueue(DashboardActivity.this);
        getUserId(phone);

        driverDone.setVisibility(View.GONE);

    }

    private void getUserId(String userMobileNumber){

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

                    //---------------------------- Get Driver Details -------------------------------------------
                    driverListRecyclerView = (RecyclerView) findViewById(R.id.driver_list_view);

                    LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManagerDriver.setReverseLayout(true);
                    driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
                    driverListRecyclerView.setHasFixedSize(true);

                    driverListAdapter = new DriversAdapter(DashboardActivity.this, driverList);
                    driverListRecyclerView.setAdapter(driverListAdapter);
                    getDriverDetailsList();
                    //------------------------------------------------------------------------------------------

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

                        if (isDriverDetailsDone.equals("1")) {
                            driverDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.driver_success));
                        } else {
                            driverDetailsLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.driver));
                        }

                        if (role.equals("Customer")) {
                            truckLoadText.setText("Post a Load");
                        } else {
                            truckLoadText.setText("Post a Trip");
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

    public void getDriverDetailsList() {
        //---------------------------- Get Driver Details ------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/userId/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    driverList = new ArrayList<>();
                    JSONArray driverLists = response.getJSONArray("data");
                    for (int i = 0; i < driverLists.length(); i++) {
                        JSONObject obj = driverLists.getJSONObject(i);
                        DriverModel modelDriver = new DriverModel();
                        modelDriver.setUser_id(obj.getString("user_id"));
                        modelDriver.setDriver_id(obj.getString("driver_id"));
                        modelDriver.setDriver_name(obj.getString("driver_name"));
                        modelDriver.setUpload_lc(obj.getString("upload_dl"));
                        modelDriver.setDriver_selfie(obj.getString("driver_selfie"));
                        modelDriver.setDriver_number(obj.getString("driver_number"));
                        modelDriver.setDriver_emailId(obj.getString("driver_emailId"));
                        driverList.add(modelDriver);
                    }
                    if (driverList.size() > 0) {
                        driverListAdapter.updateData(driverList);
                    } else {

                    }

                    if (driverList.size() > 5) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.height = 235; //height recycleviewer
                        driverListRecyclerView.setLayoutParams(params);
                    } else {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        driverListRecyclerView.setLayoutParams(params);
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

    public void onClickPreviewDriverDetails(DriverModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogDriverDetails.show();
        previewDialogDriverDetails.getWindow().setAttributes(lp);

        previewDriverDetailsDriverName.setText(" Driver Name: " + obj.getDriver_name());
        previewDriverDetailsDriverNumber.setText(" Mobile Number: +" + obj.getDriver_number());
        getUserDriverId(obj.getDriver_number());
        previewDriverDetailsEmailId.setText(" Email Id: " + obj.getDriver_emailId());

        String drivingLicenseURL =  obj.getUpload_lc();
        Log.i("IMAGE DL URL", drivingLicenseURL);
        new DownloadImageTask(previewDrivingLicense).execute(drivingLicenseURL);

        String selfieURL = obj.getDriver_selfie();
        Log.i("IMAGE Selfie URL", selfieURL);
        new DownloadImageTask(previewDriverSelfie).execute(selfieURL);
    }

    public void getDriverDetails(DriverModel obj) {
        Intent intent = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("driverId", obj.getDriver_id());
        intent.putExtra("mobile", phone);

        startActivity(intent);
    }

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

            case R.id.menu_driver_details:
                if (isDriverDetailsDone.equals("1")) {

                } else {
                    Intent intent = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("isEdit", false);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;

            case R.id.addTruck:
                Intent intent3 = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
                intent3.putExtra("userId", userId);
                intent3.putExtra("isEdit", false);
                intent3.putExtra("mobile", phone);
                startActivity(intent3);
                break;

            case R.id.addDriverDone:
                Intent intent4 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                intent4.putExtra("userId", userId);
                intent4.putExtra("isEdit", false);
                intent4.putExtra("mobile", phone);
                startActivity(intent4);
                break;
        }
    }

    private void getUserDriverId(String getMobile) {
        String receivedMobile = getMobile;
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userDriverIdAPI = data.getString("user_id");
                        mobileNoDriverAPI = data.getString("phone_number");

                        arrayUserDriverId.add(userDriverIdAPI);
                        arrayDriverMobileNo.add(mobileNoDriverAPI);

                    }

                    for (int j = 0; j < arrayDriverMobileNo.size(); j++) {
                        if (arrayDriverMobileNo.get(j).equals(receivedMobile)) {
                            driverUserIdGet = arrayUserDriverId.get(j);
                            Log.i("DriverUserId", driverUserIdGet);

                            getUserDriverBankDetails(driverUserIdGet);
                        }
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }

    private void getUserDriverBankDetails(String driverUserId) {

        String url = getString(R.string.baseURL) + "/bank/getBkByUserId/" + driverUserId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String bankName = obj.getString("bank_name");
                        String bankAccountNumber = obj.getString("account_number");
                        String ifsiCode = obj.getString("IFSI_CODE");

                        TextView previewDriverDetailsDriverBankName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_bank_name);
                        TextView previewDriverDetailsDriverBankAccountNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_account_number);
                        TextView previewDriverDetailsDriverBankIFSICode = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_ifsc_code);

                        previewDriverDetailsDriverBankName.setText(" Bank Name: " + bankName);
                        previewDriverDetailsDriverBankAccountNumber.setText(" Account Number: " + bankAccountNumber);
                        previewDriverDetailsDriverBankIFSICode.setText(" IFSI Code: " + ifsiCode);


                        if (previewDriverDetailsDriverBankName.getText().toString() == null) {
                            previewDriverDetailsDriverBankAdd.setVisibility(View.VISIBLE);
                        } else {
                            previewDriverDetailsDriverBankAdd.setVisibility(View.INVISIBLE);
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

    public void onCLickShowMenu (View view){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(menuDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.END;
        menuDialog.show();
        menuDialog.setCancelable(true);
        menuDialog.getWindow().setAttributes(lp);
    }

    public void onClickLogOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DashboardActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onClickDismiss (View view){
        menuDialog.dismiss();
    }

    private class SwipeListener implements View.OnTouchListener {
        GestureDetector gestureDetector;

        SwipeListener(View view){
            int threshold = 100;
            int velocity_threshold = 100;

            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try {
                        if (Math.abs(xDiff) > Math.abs(yDiff)){
                            if (Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocity_threshold){
                                if (xDiff < 0){
                                    //Swiped Left
                                    actionBarMenuButton.performClick();
                                }else{
                                    //Swiped Right
                                    menuDialog.dismiss();
                                }
                                return true;
                            }
                        }else{
                            if (Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocity_threshold){
                                if (yDiff > 0){
                                    //Swiped Down
                                }else{
                                    //Swiped Up
                                }
                                return true;
                            }
                        }
                    }catch (Exception e){
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