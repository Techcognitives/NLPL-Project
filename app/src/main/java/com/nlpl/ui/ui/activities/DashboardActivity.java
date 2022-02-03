package com.nlpl.ui.ui.activities;

import static com.nlpl.R.drawable.blue_profile_small;
import static com.nlpl.R.drawable.pan_card;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidSubmittedModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateAssignedDriverId;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateAssignedTruckIdToBid;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateBidStatusRespondedBySP;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateSPQuoteFinal;
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateSpNoteForCustomer;
import com.nlpl.ui.ui.adapters.LoadNotificationAdapter;
import com.nlpl.ui.ui.adapters.LoadSubmittedAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DashboardActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    FusedLocationProviderClient fusedLocationProviderClient;

    private RequestQueue mQueue;
    boolean isBackPressed = false;
    String img_type;

    private int CAMERA_PIC_REQUEST2 = 4;
    private int GET_FROM_GALLERY2 = 5;

    private ArrayList<LoadNotificationModel> loadList = new ArrayList<>();
    private ArrayList<LoadNotificationModel> loadListToCompare = new ArrayList<>();

    private ArrayList<BidSubmittedModel> loadSubmittedList = new ArrayList<>();
    private ArrayList<BidSubmittedModel> updatedLoadSubmittedList = new ArrayList<>();

    private LoadNotificationAdapter loadListAdapter;
    private LoadSubmittedAdapter loadSubmittedAdapter;
    private RecyclerView loadListRecyclerView, loadSubmittedRecyclerView;

    Dialog setBudget, selectTruckDialog, previewDialogBidNow, dialogAcceptRevisedBid, dialogViewConsignment;

    String updateAssignedDriverId, updateAssignedTruckId, spQuoteOnClickBidNow, bidStatus, vehicle_no, truckId, isProfileAdded, isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    SwipeListener swipeListener;
    double latitude1, latitude2, longitude1, longitude2;

    View actionBar;
    TextView customerNumber, customerNumberHeading, customerName, customerNameHeading, customerFirstBudget, customerSecondBudget, cancel2, cancel, acceptAndBid, spQuote, addDriver, selectDriver, addTruck, selectTruck, selectedTruckModel, selectedTruckFeet, selectedTruckCapacity, selectedTruckBodyType, actionBarTitle;
    EditText notesSp;
    CheckBox declaration;
    RadioButton negotiable_yes, negotiable_no;
    Boolean profileAdded, isTruckSelectedToBid = false, negotiable = null, isNegotiableSelected = false, fromAdapter = false;
    ImageView actionBarBackButton, actionBarMenuButton, profilePic;

    Dialog menuDialog, previewDialogProfile;
    ConstraintLayout drawerLayout;
    TextView timeLeft00, timeLeftTextview, partitionTextview, menuUserNameTextView, mobileText, personalDetailsButton, bankDetailsTextView, addTrucksTextView;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView, truckDetailsLogoImageView, driverDetailsLogoImageView;

    ConstraintLayout loadNotificationConstrain, bidsSubmittedConstrain;
    TextView loadNotificationTextView, bidsSubmittedTextView, currentLocationText;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String loadId, selectedDriverId, selectedDriverName, userId, userIdAPI, phone, mobileNoAPI;
    ArrayList<String> arrayUserId, arrayTruckId, arrayDriverId, arrayDriverName, arrayTruckList, arrayMobileNo, arrayDriverMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

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
        mQueue = Volley.newRequestQueue(DashboardActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        currentLocationText = (TextView) findViewById(R.id.dashboard_current_location_text_view);
        getLocation();

        actionBar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Dashboard");
        actionBarMenuButton.setVisibility(View.VISIBLE);
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.profile_registration_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText("Find Loads");
        profileImageView.setImageDrawable(getDrawable(R.drawable.find_small));

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
        arrayTruckId = new ArrayList<>();
        arrayDriverId = new ArrayList<>();
        arrayDriverName = new ArrayList<>();

        getUserId(phone);

        loadListRecyclerView = (RecyclerView) findViewById(R.id.dashboard_load_notification_recycler_view);
        loadSubmittedRecyclerView = (RecyclerView) findViewById(R.id.dashboard_load_notification_submitted_recycler_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.profile_registration_constrain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });

        menuDialog = new Dialog(DashboardActivity.this);
        menuDialog.setContentView(R.layout.dialog_menu);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogProfile = new Dialog(DashboardActivity.this);
        previewDialogProfile.setContentView(R.layout.dialog_preview_profile);
        previewDialogProfile.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        drawerLayout = (ConstraintLayout) menuDialog.findViewById(R.id.drawer_menu);
        menuUserNameTextView = (TextView) menuDialog.findViewById(R.id.menu_name_text);
        mobileText = (TextView) menuDialog.findViewById(R.id.menu_mobile);
        personalDetailsButton = (TextView) menuDialog.findViewById(R.id.menu_personal_details_button);
        bankDetailsTextView = (TextView) menuDialog.findViewById(R.id.menu_bank_details_button);
        addTrucksTextView = (TextView) menuDialog.findViewById(R.id.menu_truck_details);
        personalDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_personal_details_logo_image_view);
        bankDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_bank_details_logo_image_view);
        truckDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_truck_details_logo_image_view);
        driverDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.menu_driver_details_logo_image_view);
        profilePic = (ImageView) menuDialog.findViewById(R.id.profile_picture_on_sp_menu);

//        swipeListener = new SwipeListener(loadListRecyclerView);

        previewDialogBidNow = new Dialog(DashboardActivity.this);
        previewDialogBidNow.setContentView(R.layout.dialog_bid_now);
        previewDialogBidNow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogAcceptRevisedBid = new Dialog(DashboardActivity.this);
        dialogAcceptRevisedBid.setContentView(R.layout.dialog_bid_now);
        dialogAcceptRevisedBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogViewConsignment = new Dialog(DashboardActivity.this);
        dialogViewConsignment.setContentView(R.layout.dialog_bid_now);
        dialogViewConsignment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void RearrangeItems() {
        getLocation();
        Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
        i8.putExtra("mobile2", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);
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
                    //---------------------------- Get Load Details -------------------------------------------
                    getLoadNotificationList();

                    LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
//                    linearLayoutManagerBank.setReverseLayout(false);
                    loadListRecyclerView.setLayoutManager(linearLayoutManagerBank);
                    loadListRecyclerView.setHasFixedSize(true);

                    LinearLayoutManager linearLayoutManagerBank1 = new LinearLayoutManager(getApplicationContext());
//                    linearLayoutManagerBank1.setReverseLayout(false);
                    loadSubmittedRecyclerView.setLayoutManager(linearLayoutManagerBank1);
                    loadSubmittedRecyclerView.setHasFixedSize(true);

                    loadSubmittedAdapter = new LoadSubmittedAdapter(DashboardActivity.this, updatedLoadSubmittedList);
                    loadSubmittedRecyclerView.setAdapter(loadSubmittedAdapter);
                    loadSubmittedRecyclerView.scrollToPosition(loadSubmittedAdapter.getItemCount() - 1);

                    loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadListToCompare);
//                    loadListRecyclerView.setAdapter(loadListAdapter);
                    loadListRecyclerView.scrollToPosition(loadListAdapter.getItemCount() - 1);

                    //------------------------------------------------------------------------------------------

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
                        isProfileAdded = obj.getString("isProfile_pic_added");

                        Log.i("isProfileAdded at SP", isProfileAdded);

                        //-------------------------------------Personal details ---- -------------------------------------
                        menuUserNameTextView.setText(name);
                        String s1 = mobile.substring(2, 12);
                        mobileText.setText("+91 " + s1);

                        if (isProfileAdded.equals("1")) {
                            getProfilePic();
                        } else {
                            profilePic.setImageDrawable(getResources().getDrawable(blue_profile_small));
                        }

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

    public void onClickProfileAndRegister(View view) {
        switch (view.getId()) {
            case R.id.menu_personal_details_button:
                Intent i1 = new Intent(DashboardActivity.this, ViewPersonalDetailsActivity.class);
                i1.putExtra("userId", userId);
                i1.putExtra("mobile", phone);
                startActivity(i1);

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
                    intent2.putExtra("fromBidNow", false);
                    intent2.putExtra("assignTruck", false);
                    intent2.putExtra("mobile", phone);
                    startActivity(intent2);
                }
                break;

            case R.id.menu_driver_details:
                if (isDriverDetailsDone.equals("1")) {
                    Intent intent = new Intent(DashboardActivity.this, ViewDriverDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                } else {
                    Intent intent4 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                    intent4.putExtra("userId", userId);
                    intent4.putExtra("isEdit", false);
                    intent4.putExtra("fromBidNow", false);
                    intent4.putExtra("mobile", phone);
                    startActivity(intent4);
                }

                break;
        }
    }

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
        DashboardActivity.this.finish();
        overridePendingTransition(0, 0);
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
                Intent intent = new Intent(DashboardActivity.this, FindLoadsActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                startActivity(intent);
                break;
        }
    }

    private void compareAndRemove(ArrayList<LoadNotificationModel> loadListToCompare) {

        Log.i("Load list", String.valueOf(loadListToCompare.size()));

        for (int i = 0; i < loadListToCompare.size(); i++) {
            for (int j = 0; j < updatedLoadSubmittedList.size(); j++) {
                if (loadListToCompare.get(i).getIdpost_load().equals(updatedLoadSubmittedList.get(j).getIdpost_load())) {
                    loadListToCompare.remove(i);
                }
            }
        }

//        Collections.reverse(loadListToCompare);

        loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadListToCompare);
        loadListRecyclerView.setAdapter(loadListAdapter);

        if (loadListToCompare.size() > 0) {
            loadListAdapter.updateData(loadListToCompare);
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
                    loadSubmittedList = new ArrayList<>();

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
                        modelLoadNotification.setBid_ends_at(obj.getString("bid_ends_at"));

                        if (obj.getString("bid_status").equals("loadPosted") || obj.getString("bid_status").equals("loadReactivated")) {
                            loadList.add(modelLoadNotification);
                        }
                    }

                    Collections.reverse(loadList);
                    TextView noLoadAvailable = (TextView) findViewById(R.id.dashboard_load_here_text);

                    loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadList);
                    loadListRecyclerView.setAdapter(loadListAdapter);

                    if (loadList.size() > 0) {
                        noLoadAvailable.setVisibility(View.GONE);
                        loadListAdapter.updateData(loadList);
                    } else {
                        noLoadAvailable.setVisibility(View.VISIBLE);
                    }

                    getBidListByUserId(loadList);

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

        loadId = obj.getIdpost_load();
        bidStatus = obj.getBid_status();
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
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        previewDialogBidNow.show();
        previewDialogBidNow.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        customerFirstBudget = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_distance_textview);
        TextView reqModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_model_textview);
        TextView reqFeet = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_feet_textview);
        TextView reqCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_capacity_textview);
        TextView reqBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
        TextView pickUpLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
        TextView dropLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_drop_location_textview);
        TextView receivedNotes = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_received_notes_textview);
        TextView loadIdHeading = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_loadId_heading);

        pickUpDate.setText(pick_up_date);
        pickUpTime.setText(pick_up_time);
        customerFirstBudget.setText(required_budget);
        approxDistance.setText(distance);
        reqModel.setText(required_model);
        reqFeet.setText(required_feet);
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText("Load ID: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //-------------------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_driver_textview);
        addTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_add_truck_textview);
        addDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_add_driver_textview);
        selectedTruckModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckFeet = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_feet_textview);
        selectedTruckCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
        selectedTruckBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_body_type_textview);
        notesSp = (EditText) previewDialogBidNow.findViewById(R.id.dialog_bid_now_notes_editText);
        declaration = (CheckBox) previewDialogBidNow.findViewById(R.id.dialog_bid_now_declaration);
        acceptAndBid = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_accept_and_bid_btn);
        cancel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_cancel_btn);
        negotiable_yes = previewDialogBidNow.findViewById(R.id.dialog_bid_now_radio_btn_yes);
        negotiable_no = previewDialogBidNow.findViewById(R.id.dialog_bid_now_radio_btn_no);

        acceptAndBid.setEnabled(false);
        cancel.setEnabled(true);
        cancel.setBackgroundResource((R.drawable.button_active));

        negotiable_no.setChecked(false);
        negotiable_yes.setChecked(false);
        isNegotiableSelected = false;


        if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
            acceptAndBid.setEnabled(true);
            acceptAndBid.setBackgroundResource((R.drawable.button_active));
        } else {
            acceptAndBid.setEnabled(false);
            acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
        }

        declaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                i8.putExtra("mobile2", phone);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {

                    if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString()) || !negotiable) {
                        isNegotiableSelected = true;
                        saveBid(createBidRequest("RespondedBySP", spQuote.getText().toString()));
                    } else {
                        saveBid(createBidRequest("submitted", ""));
                    }

                    Log.i("loadId bidded", obj.getIdpost_load());
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(DashboardActivity.this);
                    alert.setContentView(R.layout.dialog_alert);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alert.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.gravity = Gravity.CENTER;

                    alert.show();
                    alert.getWindow().setAttributes(lp);
                    alert.setCancelable(false);

                    TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText("Post Bid");
                    alertMessage.setText("Bid Posted Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                            i8.putExtra("mobile2", phone);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            finish();
                            overridePendingTransition(0, 0);

                            previewDialogBidNow.dismiss();
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        });

        negotiable_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNegotiableSelected = true;
                negotiable = true;

                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }

                negotiable_yes.setChecked(true);
                negotiable_no.setChecked(false);
            }
        });

        negotiable_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNegotiableSelected = true;
                negotiable = false;

                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }

                negotiable_yes.setChecked(false);
                negotiable_no.setChecked(true);
            }
        });

        spQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetSet(spQuote.getText().toString());

            }
        });

        selectTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayTruckId.clear();
                getTrucksByUserId();
                arrayTruckList.clear();
            }
        });

        selectDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTruckSelectedToBid) {
                    arrayDriverId.clear();
                    getDriversByUserId();
                    arrayDriverName.clear();
                }
            }
        });

        addTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
                intent3.putExtra("userId", userId);
                intent3.putExtra("isEdit", false);
                intent3.putExtra("fromBidNow", true);
                intent3.putExtra("assignTruck", false);
                intent3.putExtra("mobile", phone);
                startActivity(intent3);
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                i8.putExtra("userId", userId);
                i8.putExtra("isEdit", false);
                i8.putExtra("fromBidNow", true);
                i8.putExtra("mobile", mobile);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
            }
        });


    }

    private void getDriversByUserId() {

        String url = getString(R.string.baseURL) + "/driver/userId/" + userId;
        Log.i("url for driverByUserId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        selectedDriverId = obj.getString("driver_id");
                        selectedDriverName = obj.getString("driver_name");
                        arrayDriverId.add(selectedDriverId);
                        arrayDriverName.add(selectedDriverName);
                    }
                    if (arrayDriverName.size() == 0) {
                        //----------------------- Alert Dialog -------------------------------------------------
                        Dialog alert = new Dialog(DashboardActivity.this);
                        alert.setContentView(R.layout.dialog_alert);
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(alert.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.gravity = Gravity.CENTER;

                        alert.show();
                        alert.getWindow().setAttributes(lp);
                        alert.setCancelable(true);

                        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                        alertTitle.setText("Add a Driver");
                        alertMessage.setText("Please add a Driver to submit your response");
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText("OK");
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                Intent i8 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                                i8.putExtra("userId", userId);
                                i8.putExtra("isEdit", false);
                                i8.putExtra("fromBidNow", true);
                                i8.putExtra("mobile", mobile);
                                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i8);
                                overridePendingTransition(0, 0);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    } else {
                        selectDriverToBid(arrayDriverId);
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

    private void selectTruckToBid(ArrayList<String> arrayTruckId) {

        selectTruckDialog = new Dialog(DashboardActivity.this);
        selectTruckDialog.setContentView(R.layout.dialog_spinner);
        selectTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTruckDialog.show();
        selectTruckDialog.setCancelable(true);
        TextView model_title = selectTruckDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText("Select Truck to Bid");

        ListView modelList = (ListView) selectTruckDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayTruckList);
        modelList.setAdapter(adapter1);


        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isTruckSelectedToBid = true;
                if (!fromAdapter) {
                    if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                        acceptAndBid.setEnabled(true);
                        acceptAndBid.setBackgroundResource((R.drawable.button_active));
                    } else {
                        acceptAndBid.setEnabled(false);
                        acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                    }
                } else {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                }
                getTruckDetailsByTruckId(arrayTruckId.get(i));
                selectTruckDialog.dismiss();
                arrayTruckList.clear();
            }
        });
    }

    private void selectDriverToBid(ArrayList<String> arrayDriverId) {

        selectTruckDialog = new Dialog(DashboardActivity.this);
        selectTruckDialog.setContentView(R.layout.dialog_spinner);
        selectTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTruckDialog.show();
        selectTruckDialog.setCancelable(true);
        TextView model_title = selectTruckDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText("Select Driver to Bid");

        ListView modelList = (ListView) selectTruckDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayDriverName);
        modelList.setAdapter(adapter1);

        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectDriver.setText(adapter1.getItem(i));
                getDriverDetailsByDriverId(arrayDriverId.get(i));
                if (!fromAdapter) {
                    if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                        acceptAndBid.setEnabled(true);
                        acceptAndBid.setBackgroundResource((R.drawable.button_active));
                    } else {
                        acceptAndBid.setEnabled(false);
                        acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                    }
                } else {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                }
                selectTruckDialog.dismiss();
                arrayDriverName.clear();
            }
        });
    }


    private void getDriverDetailsByDriverId(String driverIdSelected) {

        updateAssignedDriverId = driverIdSelected;

        Log.i("Driver selected", driverIdSelected);
        String url = getString(R.string.baseURL) + "/driver/driverId/" + driverIdSelected;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        selectedDriverName = obj.getString("driver_name");
                    }

                    selectDriver.setText(selectedDriverName);

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

    private void getTruckDetailsByTruckId(String truckIdSelected) {

        updateAssignedTruckId = truckIdSelected;


        Log.i("truckId selected", truckIdSelected);
        truckId = truckIdSelected;
        String url = getString(R.string.baseURL) + "/truck/" + truckIdSelected;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String truckModel = obj.getString("truck_type");
                        String truckFeet = obj.getString("truck_ft");
                        String truckCapacity = obj.getString("truck_carrying_capacity");
                        String bodyType = obj.getString("vehicle_type");
                        String vehicleNo = obj.getString("vehicle_no");
                        selectedDriverId = obj.getString("driver_id");

                        selectTruck.setText(vehicleNo);
                        selectedTruckModel.setText(truckModel);
                        selectedTruckFeet.setText(truckFeet);
                        selectedTruckBodyType.setText(bodyType);
                        selectedTruckCapacity.setText(truckCapacity);
                    }

                    if (selectedDriverId.equals("null")) {
                        selectDriver.setText("");
                        Log.i("driverId null", "There is no driver Id for this truck");
                    } else {
                        getDriverDetailsByDriverId(selectedDriverId);
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

    private void getBidListByUserId(ArrayList<LoadNotificationModel> loadListToCompare) {

        String url = getString(R.string.baseURL) + "/spbid/getBidDtByUserId/" + userId;
        Log.i("url betBidByUserID", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String postId = obj.getString("idpost_load");
                        String bidId = obj.getString("sp_bid_id");
                        getBidSubmittedList(postId, bidId, loadListToCompare);
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

    public void getBidSubmittedList(String loadIdReceived, String bidId, ArrayList<LoadNotificationModel> loadListToCompare) {
        //---------------------------- Get Bank Details ------------------------------------------
        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByPostId/" + loadIdReceived;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    loadSubmittedList = new ArrayList<>();
                    loadSubmittedList.clear();

                    JSONArray loadLists = response.getJSONArray("data");
                    for (int i = 0; i < loadLists.length(); i++) {
                        JSONObject obj = loadLists.getJSONObject(i);
                        BidSubmittedModel bidSubmittedModel = new BidSubmittedModel();
                        bidSubmittedModel.setIdpost_load(obj.getString("idpost_load"));
                        bidSubmittedModel.setUser_id(obj.getString("user_id"));
                        bidSubmittedModel.setPick_up_date(obj.getString("pick_up_date"));
                        bidSubmittedModel.setPick_up_time(obj.getString("pick_up_time"));
                        bidSubmittedModel.setBudget(obj.getString("budget"));
                        bidSubmittedModel.setBid_status(obj.getString("bid_status"));
                        bidSubmittedModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidSubmittedModel.setFeet(obj.getString("feet"));
                        bidSubmittedModel.setCapacity(obj.getString("capacity"));
                        bidSubmittedModel.setBody_type(obj.getString("body_type"));
                        bidSubmittedModel.setPick_add(obj.getString("pick_add"));
                        bidSubmittedModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        bidSubmittedModel.setPick_city(obj.getString("pick_city"));
                        bidSubmittedModel.setPick_state(obj.getString("pick_state"));
                        bidSubmittedModel.setPick_country(obj.getString("pick_country"));
                        bidSubmittedModel.setDrop_add(obj.getString("drop_add"));
                        bidSubmittedModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        bidSubmittedModel.setDrop_city(obj.getString("drop_city"));
                        bidSubmittedModel.setDrop_state(obj.getString("drop_state"));
                        bidSubmittedModel.setDrop_country(obj.getString("drop_country"));
                        bidSubmittedModel.setKm_approx(obj.getString("km_approx"));
                        bidSubmittedModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidSubmittedModel.setBid_ends_at(obj.getString("bid_ends_at"));
                        bidSubmittedModel.setBidId(bidId);

                        loadSubmittedList.add(bidSubmittedModel);

                    }

                    TextView noBidsSubmittedTextView = (TextView) findViewById(R.id.dashboard_no_bids_submitted_text);
                    if (loadSubmittedList.size() > 0) {
                        updatedLoadSubmittedList.addAll(loadSubmittedList);
                        loadSubmittedAdapter.updateData(updatedLoadSubmittedList);
                        if (updatedLoadSubmittedList.size() > 0) {
                            noBidsSubmittedTextView.setVisibility(View.GONE);
                        } else {
                            noBidsSubmittedTextView.setVisibility(View.VISIBLE);
                        }
                        compareAndRemove(loadListToCompare);
                    }
//
//                    else {
//                        loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadListToCompare);
//                        loadListRecyclerView.setAdapter(loadListAdapter);
//
//                        if (loadListToCompare.size() > 0) {
//                            loadListAdapter.updateData(loadListToCompare);
//                        }
//                    }

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


//    private void getDriverDetailsBySelectedDriver(String driverId){
//        Log.i("driver selected", driverId);
//        String url = getString(R.string.baseURL) + "/driver/driverId/" + driverId;
//        Log.i("url for truckByTruckId", url);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray truckLists = response.getJSONArray("data");
//                    for (int i = 0; i < truckLists.length(); i++) {
//                        JSONObject obj = truckLists.getJSONObject(i);
//                        selectedDriverName = obj.getString("driver_name");
//                    }
//                    selectDriver.setText(selectedDriverName);
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
//
//    }


    private void budgetSet(String previousBudget) {

        setBudget = new Dialog(DashboardActivity.this);
        setBudget.setContentView(R.layout.dialog_budget);

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(setBudget.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;

        setBudget.show();
        setBudget.setCancelable(true);
        setBudget.getWindow().setAttributes(lp2);

        EditText budget = setBudget.findViewById(R.id.dialog_budget_edit);
        Button okBudget = setBudget.findViewById(R.id.dialog_budget_ok_btn);
        budget.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String newPreviousBudget = previousBudget.replaceAll(",", "");
        budget.setText(newPreviousBudget);

        if (!previousBudget.isEmpty()) {
            okBudget.setEnabled(true);
            okBudget.setBackgroundResource((R.drawable.button_active));
        } else {
            okBudget.setEnabled(false);
            okBudget.setBackgroundResource((R.drawable.button_de_active));
        }

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {

                    String finalBudget, lastThree = "";
                    String budget1 = budget.getText().toString();
                    if (budget1.length() > 3) {
                        lastThree = budget1.substring(budget1.length() - 3);
                    }
                    if (budget1.length() == 1) {
                        finalBudget = budget1;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 2) {
                        finalBudget = budget1;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 3) {
                        finalBudget = budget1;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 4) {
                        Character fourth = budget1.charAt(0);
                        finalBudget = fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 5) {
                        Character fifth = budget1.charAt(0);
                        Character fourth = budget1.charAt(1);
                        finalBudget = fifth + "" + fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 6) {
                        Character fifth = budget1.charAt(1);
                        Character fourth = budget1.charAt(2);
                        Character sixth = budget1.charAt(0);
                        finalBudget = sixth + "," + fifth + "" + fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 7) {
                        Character seventh = budget1.charAt(0);
                        Character sixth = budget1.charAt(1);
                        Character fifth = budget1.charAt(2);
                        Character fourth = budget1.charAt(3);
                        finalBudget = seventh + "" + sixth + "," + fifth + "" + fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    }

                    if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString())) {
                        spQuote.setTextColor(getResources().getColor(R.color.green));
                        negotiable_no.setChecked(true);
                        negotiable_yes.setChecked(false);
                        negotiable_yes.setEnabled(false);
                        isNegotiableSelected = true;
                    } else {
                        spQuote.setTextColor(getResources().getColor(R.color.redDark));
                    }
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                } else {
                    okBudget.setEnabled(false);
                    okBudget.setBackgroundResource((R.drawable.button_de_active));
                }

                TextView amountInWords = setBudget.findViewById(R.id.dialog_budget_amount_in_words);
                if (budgetEditText.length() > 0) {
                    String return_val_in_english = EnglishNumberToWords.convert(Long.parseLong(budgetEditText));
                    amountInWords.setText(return_val_in_english);
                } else {
                    amountInWords.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        okBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }
                setBudget.dismiss();
            }
        });
    }

    private void getTrucksByUserId() {

        String url = getString(R.string.baseURL) + "/truck/truckbyuserID/" + userId;
        Log.i("url for truckByUserId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_no = obj.getString("vehicle_no");
                        truckId = obj.getString("truck_id");
                        Log.i("vehicle no", vehicle_no);
                        arrayTruckList.add(vehicle_no);
                        arrayTruckId.add(truckId);
                    }
                    if (arrayTruckId.size() == 0) {
                        //----------------------- Alert Dialog -------------------------------------------------
                        Dialog alert = new Dialog(DashboardActivity.this);
                        alert.setContentView(R.layout.dialog_alert);
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(alert.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.gravity = Gravity.CENTER;

                        alert.show();
                        alert.getWindow().setAttributes(lp);
                        alert.setCancelable(true);

                        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                        alertTitle.setText("Add a Truck");
                        alertMessage.setText("Please add a Truck to submit your response");
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText("OK");
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                Intent intent3 = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
                                intent3.putExtra("userId", userId);
                                intent3.putExtra("isEdit", false);
                                intent3.putExtra("fromBidNow", true);
                                intent3.putExtra("assignTruck", false);
                                intent3.putExtra("mobile", phone);
                                startActivity(intent3);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    } else {
                        selectTruckToBid(arrayTruckId);
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

    //--------------------------------------create Bank Details in API -------------------------------------
    public BidLoadRequest createBidRequest(String status, String spFinal) {
        BidLoadRequest bidLoadRequest = new BidLoadRequest();
        bidLoadRequest.setUser_id(userId);
        bidLoadRequest.setAssigned_truck_id(truckId);
        bidLoadRequest.setAssigned_driver_id(selectedDriverId);
        bidLoadRequest.setIdpost_load(loadId);
        bidLoadRequest.setBid_status(status);
        bidLoadRequest.setBody_type(selectedTruckBodyType.getText().toString());
        bidLoadRequest.setVehicle_model(selectedTruckModel.getText().toString());
        bidLoadRequest.setFeet(selectedTruckFeet.getText().toString());
        bidLoadRequest.setCapacity(selectedTruckCapacity.getText().toString());
        bidLoadRequest.setNotes(notesSp.getText().toString());
        bidLoadRequest.setIs_negatiable(negotiable);
        bidLoadRequest.setSp_quote(spQuote.getText().toString());
        bidLoadRequest.setIs_bid_accpted_by_sp(spFinal);
        return bidLoadRequest;
    }

    public void saveBid(BidLoadRequest bidLoadRequest) {
        Call<BidLadResponse> bidLadResponseCall = ApiClient.getBidLoadService().saveBid(bidLoadRequest);
        bidLadResponseCall.enqueue(new Callback<BidLadResponse>() {
            @Override
            public void onResponse(Call<BidLadResponse> call, retrofit2.Response<BidLadResponse> response) {

            }

            @Override
            public void onFailure(Call<BidLadResponse> call, Throwable t) {

            }
        });
    }

    public void acceptRevisedBid(BidSubmittedModel obj) {

        fromAdapter = true;
        loadId = obj.getIdpost_load();
        bidStatus = obj.getBid_status();
        String pick_up_date = obj.getPick_up_date();
        String pick_up_time = obj.getPick_up_time();
        String distance = obj.getKm_approx();
        String required_model = obj.getVehicle_model();
        String required_feet = obj.getFeet();
        String required_capacity = obj.getCapacity();
        String required_truck_body = obj.getBody_type();
        String pick_up_location = obj.getPick_add() + " " + obj.getPick_city() + " " + obj.getPick_state() + " " + obj.getPick_pin_code();
        String drop_location = obj.getDrop_add() + " " + obj.getDrop_city() + " " + obj.getDrop_state() + " " + obj.getDrop_pin_code();
        String received_notes_description = obj.getNotes_meterial_des();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAcceptRevisedBid.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialogAcceptRevisedBid.show();
        dialogAcceptRevisedBid.setCancelable(false);
        dialogAcceptRevisedBid.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        customerSecondBudget = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_distance_textview);
        TextView reqModel = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_req_model_textview);
        TextView reqFeet = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_req_feet_textview);
        TextView reqCapacity = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_req_capacity_textview);
        TextView reqBodyType = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
        TextView pickUpLocation = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
        TextView dropLocation = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_drop_location_textview);
        TextView receivedNotes = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_received_notes_textview);
        TextView loadIdHeading = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_loadId_heading);
        customerNameHeading = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customerName_heading);
        customerName = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customerName);
        customerNumberHeading = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customer_phone_heading);
        customerNumber = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customer_mobile_no);

        customerNameHeading.setVisibility(View.GONE);
        customerName.setVisibility(View.GONE);
        customerNumber.setVisibility(View.GONE);
        customerNumberHeading.setVisibility(View.GONE);

        pickUpDate.setText(pick_up_date);
        pickUpTime.setText(pick_up_time);
        approxDistance.setText(distance);
        reqModel.setText(required_model);
        reqFeet.setText(required_feet);
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText("Load ID: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_select_driver_textview);
        addTruck = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_add_truck_textview);
        addDriver = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_add_driver_textview);
        selectedTruckModel = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckFeet = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_truck_feet_textview);
        selectedTruckCapacity = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
        selectedTruckBodyType = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_truck_body_type_textview);
        notesSp = (EditText) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_notes_editText);
        declaration = (CheckBox) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_declaration);
        acceptAndBid = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_accept_and_bid_btn);
        cancel = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_cancel_btn);
        negotiable_yes = dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_radio_btn_yes);
        negotiable_no = dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_radio_btn_no);

        negotiable_yes.setEnabled(false);
        negotiable_yes.setChecked(false);
        negotiable_no.setChecked(true);
        declaration.setVisibility(View.INVISIBLE);

        getBidDetailsByBidId(obj.getBidId());
        spQuote.setTextColor(getResources().getColor(R.color.green));

        cancel.setEnabled(true);
        cancel.setBackgroundResource((R.drawable.button_active));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                i8.putExtra("mobile2", phone);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                finish();
                overridePendingTransition(0, 0);
                previewDialogBidNow.dismiss();
            }
        });

        acceptAndBid.setEnabled(true);
        acceptAndBid.setBackgroundResource((R.drawable.button_active));

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateSPNoteForCustomer(obj.getBidId(), notesSp.getText().toString());
                updateBidStatusRespondedBySP(obj.getBidId());
                updateSPQuoteFinal(obj.getBidId(), spQuote.getText().toString());
                updateAssignedTruckId(obj.getBidId(), updateAssignedTruckId);
                updateAssignedDriverId(obj.getBidId(), updateAssignedDriverId);

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DashboardActivity.this);
                alert.setContentView(R.layout.dialog_alert);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText("Bid Revised and Responded");
                alertMessage.setText("Bid Revised and Responded Successfully");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                        i8.putExtra("mobile2", phone);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        finish();
                        overridePendingTransition(0, 0);

                        previewDialogBidNow.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        });

        selectTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayTruckId.clear();
                getTrucksByUserId();
                arrayTruckList.clear();
            }
        });

        selectDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTruckSelectedToBid) {
                    arrayDriverId.clear();
                    getDriversByUserId();
                    arrayDriverName.clear();
                }
            }
        });

        addTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
                intent3.putExtra("userId", userId);
                intent3.putExtra("isEdit", false);
                intent3.putExtra("fromBidNow", true);
                intent3.putExtra("assignTruck", false);
                intent3.putExtra("mobile", phone);
                startActivity(intent3);
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                i8.putExtra("userId", userId);
                i8.putExtra("isEdit", false);
                i8.putExtra("fromBidNow", true);
                i8.putExtra("mobile", mobile);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
            }
        });

    }
    //-----------------------------------------------------------------------------------------------------

    private void getBidDetailsByBidId(String bidId) {
        //-------------------------------------------------------------------------------------------
        String url = getString(R.string.baseURL) + "/spbid/bidDtByBidId/" + bidId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj1 = truckLists.getJSONObject(i);
                        String truck_id = obj1.getString("assigned_truck_id");
                        getTruckDetailsByTruckId(truck_id);
                        String driver_id = obj1.getString("assigned_driver_id");
                        getDriverDetailsByDriverId(driver_id);
                        spQuote.setText(obj1.getString("is_bid_accpted_by_sp"));
                        customerSecondBudget.setText(obj1.getString("is_bid_accpted_by_sp"));
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
        //----------------------------------------------------------

    }

    public void viewConsignment(BidSubmittedModel obj) {

        loadId = obj.getIdpost_load();
        bidStatus = obj.getBid_status();
        String pick_up_date = obj.getPick_up_date();
        String pick_up_time = obj.getPick_up_time();
        String distance = obj.getKm_approx();
        String required_model = obj.getVehicle_model();
        String required_feet = obj.getFeet();
        String required_capacity = obj.getCapacity();
        String required_truck_body = obj.getBody_type();
        String pick_up_location = obj.getPick_add() + " " + obj.getPick_city() + " " + obj.getPick_state() + " " + obj.getPick_pin_code();
        String drop_location = obj.getDrop_add() + " " + obj.getDrop_city() + " " + obj.getDrop_state() + " " + obj.getDrop_pin_code();
        String received_notes_description = obj.getNotes_meterial_des();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogViewConsignment.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialogViewConsignment.show();
        dialogViewConsignment.setCancelable(false);
        dialogViewConsignment.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        customerSecondBudget = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_distance_textview);
        TextView reqModel = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_req_model_textview);
        TextView reqFeet = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_req_feet_textview);
        TextView reqCapacity = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_req_capacity_textview);
        TextView reqBodyType = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
        TextView pickUpLocation = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
        TextView dropLocation = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_drop_location_textview);
        TextView receivedNotes = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_received_notes_textview);
        TextView loadIdHeading = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_loadId_heading);
        customerNameHeading = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customerName_heading);
        customerName = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customerName);
        customerNumberHeading = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customer_phone_heading);
        customerNumber = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_customer_mobile_no);

        customerNameHeading.setVisibility(View.VISIBLE);
        customerName.setVisibility(View.VISIBLE);
        customerNumber.setVisibility(View.VISIBLE);
        customerNumberHeading.setVisibility(View.VISIBLE);

        pickUpDate.setText(pick_up_date);
        pickUpTime.setText(pick_up_time);
        approxDistance.setText(distance);
        reqModel.setText(required_model);
        reqFeet.setText(required_feet);
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText("Load ID: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_select_driver_textview);
        addTruck = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_add_truck_textview);
        addDriver = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_add_driver_textview);
        selectedTruckModel = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckFeet = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_feet_textview);
        selectedTruckCapacity = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
        selectedTruckBodyType = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_body_type_textview);
        notesSp = (EditText) dialogViewConsignment.findViewById(R.id.dialog_bid_now_notes_editText);
        declaration = (CheckBox) dialogViewConsignment.findViewById(R.id.dialog_bid_now_declaration);
        acceptAndBid = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_accept_and_bid_btn);
        cancel = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_cancel_btn);
        negotiable_yes = dialogViewConsignment.findViewById(R.id.dialog_bid_now_radio_btn_yes);
        negotiable_no = dialogViewConsignment.findViewById(R.id.dialog_bid_now_radio_btn_no);
        partitionTextview = dialogViewConsignment.findViewById(R.id.bid_now_middle_textview_partition);
        timeLeftTextview = dialogViewConsignment.findViewById(R.id.bid_now_time_left_textView);
        timeLeft00 = dialogViewConsignment.findViewById(R.id.bid_now_time_left_00_textview);
        cancel2 = dialogViewConsignment.findViewById(R.id.dialog_bid_now_cancel_btn2);

        cancel2.setVisibility(View.VISIBLE);
        cancel2.setEnabled(true);
        cancel2.setBackgroundTintList(getResources().getColorStateList(R.color.button_blue));

        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                i8.putExtra("mobile2", phone);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        notesSp.setVisibility(View.GONE);
        addTruck.setVisibility(View.INVISIBLE);
        addDriver.setVisibility(View.INVISIBLE);
        timeLeft00.setVisibility(View.GONE);
        partitionTextview.setText("My Bid Response");
        timeLeftTextview.setText("CONSIGNMENT");
        timeLeftTextview.setTextColor(getResources().getColorStateList(R.color.black));
        timeLeftTextview.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        negotiable_yes.setEnabled(false);
        negotiable_yes.setChecked(false);
        negotiable_no.setChecked(true);
        declaration.setVisibility(View.INVISIBLE);

        spQuote.setTextColor(getResources().getColor(R.color.green));

        getBidDetailsByBidId(obj.getBidId());
        getCustomerNameAndNumber(obj.getUser_id());

        cancel.setText("Withdraw");
        cancel.setEnabled(false);
        cancel.setBackgroundTintList(getResources().getColorStateList(R.color.grey));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                i8.putExtra("mobile2", phone);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                finish();
                overridePendingTransition(0, 0);
                dialogViewConsignment.dismiss();
            }
        });

        acceptAndBid.setText("Start Trip");
        acceptAndBid.setEnabled(false);
        acceptAndBid.setBackgroundResource((R.drawable.button_de_active));

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DashboardActivity.this);
                alert.setContentView(R.layout.dialog_alert);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText("Trip Started Successfully");
                alertMessage.setText("You can track your trip in track section");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                        i8.putExtra("mobile2", phone);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        finish();
                        overridePendingTransition(0, 0);

                        dialogViewConsignment.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });
    }

    private void getCustomerNameAndNumber(String user_id) {
        //-------------------------------------------------------------------------------------------
        String url = getString(R.string.baseURL) + "/user/" + user_id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj1 = truckLists.getJSONObject(i);
                        customerName.setText(obj1.getString("name"));
                        customerNumber.setText(obj1.getString("phone_number"));
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
        //----------------------------------------------------------
    }

    public void ViewSPProfile(View view) {
        if (isProfileAdded.equals("1")) {
            String url1 = getString(R.string.baseURL) + "/imgbucket/Images/" + userId;
            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray imageList = response.getJSONArray("data");
                        for (int i = 0; i < imageList.length(); i++) {
                            JSONObject obj = imageList.getJSONObject(i);
                            String imageType = obj.getString("image_type");

                            String profileImgUrl;
                            if (imageType.equals("profile")) {
                                profileImgUrl = obj.getString("image_url");
                                profileAdded = true;
                                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                lp2.copyFrom(previewDialogProfile.getWindow().getAttributes());
                                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                                lp2.gravity = Gravity.CENTER;

                                previewDialogProfile.show();
                                previewDialogProfile.getWindow().setAttributes(lp2);
                                new DownloadImageTask((ImageView) previewDialogProfile.findViewById(R.id.dialog_preview_image_view_profile)).execute(profileImgUrl);

                                TextView editProfilePic = previewDialogProfile.findViewById(R.id.editProfilePic);

                                editProfilePic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        uploadProfileDialogChoose();
                                    }
                                });
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
            mQueue.add(request1);
        } else {
            profileAdded = false;
            uploadProfileDialogChoose();
        }
    }

    private void uploadProfileDialogChoose() {
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "profile";

        Dialog chooseDialog;
        chooseDialog = new Dialog(DashboardActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST2);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
                chooseDialog.dismiss();
            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        Log.i("file uri: ", String.valueOf(fileUri));
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void uploadImage(String picPath) {

        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

        MultipartBody.Part body = prepareFilePart("file", Uri.fromFile(file));

        Call<UploadImageResponse> call = ApiClient.getImageUploadService().uploadImage(userId, img_type, body);
        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, retrofit2.Response<UploadImageResponse> response) {
                Log.i("successful:", "success");
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("failed:", "failed");
            }
        });
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }


    public void openMaps(LoadNotificationModel obj) {
        String sDestination = obj.getPick_add() + obj.getPick_city();
        DisplayTrack("", sDestination);

    }

    private void DisplayTrack(String sSource, String sDestination) {
        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDestination);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    public void onClickOpenPhone(View view) {
        String numberOpen = customerNumber.getText().toString();
        Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + numberOpen));
        startActivity(i2);
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

    //----------------------------------------------------------------------------------------------------------------
    private void updateBidStatusRespondedBySP(String bidId) {

        UpdateBidStatusRespondedBySP updateBidStatusRespondedBySP = new UpdateBidStatusRespondedBySP("RespondedBySP");

        Call<UpdateBidStatusRespondedBySP> call = ApiClient.getBidLoadService().updateBidStatusRespondedBySP("" + bidId, updateBidStatusRespondedBySP);

        call.enqueue(new Callback<UpdateBidStatusRespondedBySP>() {
            @Override
            public void onResponse(Call<UpdateBidStatusRespondedBySP> call, retrofit2.Response<UpdateBidStatusRespondedBySP> response) {

            }

            @Override
            public void onFailure(Call<UpdateBidStatusRespondedBySP> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------
    private void updateAssignedTruckId(String bidId, String assignedTruckId) {

        UpdateAssignedTruckIdToBid updateAssignedTruckIdToBid = new UpdateAssignedTruckIdToBid(assignedTruckId);

        Call<UpdateAssignedTruckIdToBid> call = ApiClient.getBidLoadService().updateAssignedTruckId("" + bidId, updateAssignedTruckIdToBid);

        call.enqueue(new Callback<UpdateAssignedTruckIdToBid>() {
            @Override
            public void onResponse(Call<UpdateAssignedTruckIdToBid> call, retrofit2.Response<UpdateAssignedTruckIdToBid> response) {

            }

            @Override
            public void onFailure(Call<UpdateAssignedTruckIdToBid> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------
    private void updateAssignedDriverId(String bidId, String assignedDriverId) {

        UpdateAssignedDriverId updateAssignedDriverId = new UpdateAssignedDriverId(assignedDriverId);

        Call<UpdateAssignedDriverId> call = ApiClient.getBidLoadService().updateAssignedDriverId("" + bidId, updateAssignedDriverId);

        call.enqueue(new Callback<UpdateAssignedDriverId>() {
            @Override
            public void onResponse(Call<UpdateAssignedDriverId> call, retrofit2.Response<UpdateAssignedDriverId> response) {

            }

            @Override
            public void onFailure(Call<UpdateAssignedDriverId> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------------------------
    private void updateSPQuoteFinal(String bidId, String spQuote) {

        UpdateSPQuoteFinal updateSPQuoteFinal = new UpdateSPQuoteFinal(spQuote);

        Call<UpdateSPQuoteFinal> call = ApiClient.getBidLoadService().updateSPQuoteFinal("" + bidId, updateSPQuoteFinal);

        call.enqueue(new Callback<UpdateSPQuoteFinal>() {
            @Override
            public void onResponse(Call<UpdateSPQuoteFinal> call, retrofit2.Response<UpdateSPQuoteFinal> response) {

            }

            @Override
            public void onFailure(Call<UpdateSPQuoteFinal> call, Throwable t) {

            }
        });

    }
    //--------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------
    private void updateSPNoteForCustomer(String bidId, String spNote) {

        UpdateSpNoteForCustomer updateSpNoteForCustomer = new UpdateSpNoteForCustomer(spNote);

        Call<UpdateSpNoteForCustomer> call = ApiClient.getBidLoadService().updateSPNoteForCustomer("" + bidId, updateSpNoteForCustomer);

        call.enqueue(new Callback<UpdateSpNoteForCustomer>() {
            @Override
            public void onResponse(Call<UpdateSpNoteForCustomer> call, retrofit2.Response<UpdateSpNoteForCustomer> response) {

            }

            @Override
            public void onFailure(Call<UpdateSpNoteForCustomer> call, Throwable t) {

            }
        });

    }
    //--------------------------------------------------------------------------------------------------


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(DashboardActivity.this, Locale.getDefault());
                        try {
                            String latitudeCurrent, longitudeCurrent, countryCurrent, stateCurrent, cityCurrent, subCityCurrent, addressCurrent, pinCodeCurrent;
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLatitude()));
                            longitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLongitude()));
                            countryCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getCountryName()));
                            stateCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getAdminArea()));
                            cityCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLocality()));
                            subCityCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getSubLocality()));
                            addressCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getAddressLine(0)));
                            pinCodeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getPostalCode()));

                            currentLocationText.setText(addressCurrent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            finishAffinity();
            System.exit(0);
        } else {
            Toast.makeText(getApplicationContext(), "Please click back again to exit", Toast.LENGTH_SHORT).show();
            isBackPressed = true;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        };
        new Handler().postDelayed(runnable, 3000);
    }

    private void getProfilePic() {

        String url1 = getString(R.string.baseURL) + "/imgbucket/Images/" + userId;
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");
                        String profileImgUrl;

                        if (imageType.equals("profile")) {
                            profileImgUrl = obj.getString("image_url");
                            new DownloadImageTask(profilePic).execute(profileImgUrl);
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
        mQueue.add(request1);
    }


    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(DashboardActivity.this);
            alert.setContentView(R.layout.dialog_alert);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Personal Details");
            alertMessage.setText("Profile Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            saveImage(imageRequest());
            uploadImage(picturePath);

            profileAddedAlert();

        } else if (requestCode == CAMERA_PIC_REQUEST2) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(DashboardActivity.this);
            alert.setContentView(R.layout.dialog_alert);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Personal Details");
            alertMessage.setText("Profile Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();

                }
            });
            //------------------------------------------------------------------------------------------

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            saveImage(imageRequest());
            uploadImage(path);

            profileAddedAlert();

        }
    }

    //-------------------------------------------------------------------------------------------------------------------
    public ImageRequest imageRequest() {
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setUser_id(userId);
        imageRequest.setImage_type(img_type);
        return imageRequest;
    }

    public void saveImage(ImageRequest imageRequest) {
        Call<ImageResponse> imageResponseCall = ApiClient.getImageService().saveImage(imageRequest);
        imageResponseCall.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, retrofit2.Response<ImageResponse> response) {

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    private void profileAddedAlert() {
        UpdateUserDetails.updateUserIsProfileAdded(userId, "1");

        Dialog alert = new Dialog(DashboardActivity.this);
        alert.setContentView(R.layout.dialog_alert);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(false);

        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText("Profile Picture");
        alertMessage.setText("Profile Picture added successfully");
        alertPositiveButton.setVisibility(View.GONE);
        alertNegativeButton.setText("OK");
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                i8.putExtra("mobile2", phone);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}