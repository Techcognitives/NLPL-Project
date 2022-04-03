package com.nlpl.ui.activities;

import static com.nlpl.R.drawable.blue_profile_small;
import static com.nlpl.R.drawable.find;

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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidSubmittedModel;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateBidDetails;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserDeviceId;
import com.nlpl.ui.adapters.DriversListAdapterBid;
import com.nlpl.ui.adapters.LoadNotificationAdapter;
import com.nlpl.ui.adapters.LoadSubmittedAdapter;
import com.nlpl.ui.adapters.SearchLoadAdapter;
import com.nlpl.ui.adapters.SearchLoadAdapterDrop;
import com.nlpl.ui.adapters.StateLoadAdapter;
import com.nlpl.ui.adapters.TrucksListAdapterBid;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.FooThread;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ServiceProviderDashboardActivity extends AppCompat {

    SwipeRefreshLayout swipeRefreshLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
    private RequestQueue mQueue;
    boolean isBackPressed = false;
    String img_type;

    private int CAMERA_PIC_REQUEST_profile = 8;
    private int GET_FROM_GALLERY_profile = 5;

    GetCurrentLocation getCurrentLocation;

    private ArrayList<LoadNotificationModel> loadList = new ArrayList<>();
    private ArrayList<LoadNotificationModel> loadListToCompare = new ArrayList<>();

    private ArrayList<BidSubmittedModel> loadSubmittedList = new ArrayList<>();
    private ArrayList<BidSubmittedModel> updatedLoadSubmittedList = new ArrayList<>();

    private LoadNotificationAdapter loadListAdapter;
    private LoadSubmittedAdapter loadSubmittedAdapter;
    private RecyclerView loadListRecyclerView, loadSubmittedRecyclerView;

    Dialog loadingDialog, setBudget, dialogSelectDriver, dialogSelectTruck, previewDialogBidNow, dialogAcceptRevisedBid, dialogViewConsignment;
    String updateAssignedDriverId, s1, required_capacity, required_truck_body, truckIdPass, updateAssignedTruckId, spQuoteOnClickBidNow, bidStatus, vehicle_no, truckId, isProfileAdded, isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    SwipeListener swipeListener;

    //-------------------- Select Truck ------------------------------------------------------------
    private ArrayList<TruckModel> truckList = new ArrayList<>();
    private TrucksListAdapterBid truckListAdapter;
    private RecyclerView truckListRecyclerView;
    //----------------------------------------------------------------------------------------------
    private ArrayList<DriverModel> driverList = new ArrayList<>();
    private DriversListAdapterBid driverListAdapter;
    private RecyclerView driverListRecyclerView;

    View actionBar, loadNotificationUnderline, bidSubmittedUnderline, findLoadsUnderline;
    TextView customerNumber, startTrip, customerNumberHeading, customerName, customerNameHeading, customerFirstBudget, customerSecondBudget, cancel2, cancel, acceptAndBid, spQuote, selectDriver, selectTruck, selectedTruckModel, selectedTruckCapacity, actionBarTitle;
    EditText notesSp;
    CheckBox declaration;
    RadioButton negotiable_yes, negotiable_no;
    Boolean isLoadNotificationSelected, loadNotificationSelected, profileAdded, isTruckSelectedToBid = false, negotiable = null, isNegotiableSelected = false, fromAdapter = false;
    ImageView actionBarBackButton, actionBarMenuButton, profilePic;

    Dialog menuDialog, previewDialogProfile;
    ConstraintLayout drawerLayout;
    TextView timeLeft00, timeLeftTextview, partitionTextview, menuUserNameTextView, mobileText, personalDetailsButton, bankDetailsTextView, addTrucksTextView;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView, truckDetailsLogoImageView, driverDetailsLogoImageView, arrowImage, actionBarWhatsApp;

    ConstraintLayout loadNotificationConstrain, bidsSubmittedConstrain, stateConstrainView;
    TextView loadNotificationTextView, bidsSubmittedTextView, findLoadsConstrain;

    View bottomNav;

    String loadId, selectedDriverId, selectedDriverName, userId, userIdAPI, phone, mobileNoAPI, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayDriverMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

    String mobile, name, address, pinCode, city, role, emailIdAPI;

    //------------------------------------ Find Loads ----------------------------------------------
    private RecyclerView searchListRecyclerView;
    private SearchLoadAdapter searchLoadAdapter;
    private ArrayList<SearchLoadModel> searchLoadModels = new ArrayList<>();
    ArrayList<SearchLoadModel> searchList;
    private ArrayList<LoadNotificationModel> anList, apList, arList, asList, brList, chList, cgList, ddList,
            dd2List, dlList, gaList, gjList, hrList, hpList, jkList, jhList, kaList, klList, laList,
            ldList, mpList, mhList, mnList, mlList, mzList, nlList, odList, pyList, pbList, rjList,
            skList, tnList, tsList, trList, ukList, upList, wbList;
    ConstraintLayout stateConstrain;
    Spinner findLoadSpinner;

    //-------------------------------- drop --------------------------------------------------------
    private RecyclerView searchListRecyclerViewDrop;
    SearchLoadAdapterDrop searchLoadAdapterDrop;
    private ArrayList<LoadNotificationModel> anListD, apListD, arListD, asListD, brListD, chListD, cgListD, ddListD,
            dd2ListD, dlListD, gaListD, gjListD, hrListD, hpListD, jkListD, jhListD, kaListD, klListD, laListD,
            ldListD, mpListD, mhListD, mnListD, mlListD, mzListD, nlListD, odListD, pyListD, pbListD, rjListD,
            skListD, tnListD, tsListD, trListD, ukListD, upListD, wbListD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_dashboard);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile2");
            Log.i("Mobile No Registration", phone);
        }
        if (bundle != null) {
            isLoadNotificationSelected = bundle.getBoolean("loadNotification");
        } else {
            isLoadNotificationSelected = true;
        }

        loadNotificationConstrain = findViewById(R.id.dashboard_load_notification_constrain);
        bidsSubmittedConstrain = findViewById(R.id.dashboard_bids_submitted_constrain);
        loadNotificationTextView = findViewById(R.id.dashboard_load_notification_button);
        bidsSubmittedTextView = findViewById(R.id.dashboard_bids_submitted_button);
        loadNotificationUnderline = findViewById(R.id.dashboard_load_notification_view);
        bidSubmittedUnderline = findViewById(R.id.dashboard_bids_submitted_view);
        findLoadsConstrain = findViewById(R.id.find_loads_find_truck_text);
        findLoadsUnderline = findViewById(R.id.find_loads_find_truck_view);
        stateConstrainView = findViewById(R.id.find_load_constrain);
        findLoadSpinner = findViewById(R.id.find_loads_spinner);
        findLoadSpinner.setOnItemSelectedListener(onPickOrDrop);

        getNotification();
        getCurrentLocation = new GetCurrentLocation();

        if (isLoadNotificationSelected) {
            loadNotificationSelected = true;
            loadNotificationConstrain.setVisibility(View.VISIBLE);
            bidsSubmittedConstrain.setVisibility(View.INVISIBLE);
            loadNotificationUnderline.setVisibility(View.VISIBLE);
            bidSubmittedUnderline.setVisibility(View.INVISIBLE);
            loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
            bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
        } else {
            loadNotificationSelected = false;
            loadNotificationConstrain.setVisibility(View.INVISIBLE);
            bidsSubmittedConstrain.setVisibility(View.VISIBLE);
            loadNotificationUnderline.setVisibility(View.INVISIBLE);
            bidSubmittedUnderline.setVisibility(View.VISIBLE);
            bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
            loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
        }

        mQueue = Volley.newRequestQueue(ServiceProviderDashboardActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        actionBar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);
        actionBarWhatsApp = (ImageView) actionBar.findViewById(R.id.action_bar_whats_app);
        actionBarWhatsApp.setVisibility(View.VISIBLE);

        actionBarTitle.setText(getString(R.string.Dashboard));
        actionBarMenuButton.setVisibility(View.VISIBLE);
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.profile_registration_bottom_nav_bar);
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileImageView.setImageDrawable(getDrawable(R.drawable.black_truck_small));
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
        profileText.setText("Trips");
        ConstraintLayout truck = findViewById(R.id.bottom_nav_trip);
        truck.setVisibility(View.GONE);

        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayDriverMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();

        anList = new ArrayList<>();
        apList = new ArrayList<>();
        arList = new ArrayList<>();
        asList = new ArrayList<>();
        brList = new ArrayList<>();
        chList = new ArrayList<>();
        cgList = new ArrayList<>();
        ddList = new ArrayList<>();
        dd2List = new ArrayList<>();
        dlList = new ArrayList<>();
        gaList = new ArrayList<>();
        gjList = new ArrayList<>();
        hrList = new ArrayList<>();
        hpList = new ArrayList<>();
        jkList = new ArrayList<>();
        jhList = new ArrayList<>();
        kaList = new ArrayList<>();
        klList = new ArrayList<>();
        laList = new ArrayList<>();
        ldList = new ArrayList<>();
        mpList = new ArrayList<>();
        mhList = new ArrayList<>();
        mnList = new ArrayList<>();
        mlList = new ArrayList<>();
        mzList = new ArrayList<>();
        nlList = new ArrayList<>();
        odList = new ArrayList<>();
        pyList = new ArrayList<>();
        pbList = new ArrayList<>();
        rjList = new ArrayList<>();
        skList = new ArrayList<>();
        tnList = new ArrayList<>();
        tsList = new ArrayList<>();
        trList = new ArrayList<>();
        ukList = new ArrayList<>();
        upList = new ArrayList<>();
        wbList = new ArrayList<>();

        anListD = new ArrayList<>();
        apListD = new ArrayList<>();
        arListD = new ArrayList<>();
        asListD = new ArrayList<>();
        brListD = new ArrayList<>();
        chListD = new ArrayList<>();
        cgListD = new ArrayList<>();
        ddListD = new ArrayList<>();
        dd2ListD = new ArrayList<>();
        dlListD = new ArrayList<>();
        gaListD = new ArrayList<>();
        gjListD = new ArrayList<>();
        hrListD = new ArrayList<>();
        hpListD = new ArrayList<>();
        jkListD = new ArrayList<>();
        jhListD = new ArrayList<>();
        kaListD = new ArrayList<>();
        klListD = new ArrayList<>();
        laListD = new ArrayList<>();
        ldListD = new ArrayList<>();
        mpListD = new ArrayList<>();
        mhListD = new ArrayList<>();
        mnListD = new ArrayList<>();
        mlListD = new ArrayList<>();
        mzListD = new ArrayList<>();
        nlListD = new ArrayList<>();
        odListD = new ArrayList<>();
        pyListD = new ArrayList<>();
        pbListD = new ArrayList<>();
        rjListD = new ArrayList<>();
        skListD = new ArrayList<>();
        tnListD = new ArrayList<>();
        tsListD = new ArrayList<>();
        trListD = new ArrayList<>();
        ukListD = new ArrayList<>();
        upListD = new ArrayList<>();
        wbListD = new ArrayList<>();

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

        menuDialog = new Dialog(ServiceProviderDashboardActivity.this);
        menuDialog.setContentView(R.layout.dialog_menu);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogProfile = new Dialog(ServiceProviderDashboardActivity.this);
        previewDialogProfile.setContentView(R.layout.dialog_preview_profile);
        previewDialogProfile.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        loadingDialog = new Dialog(ServiceProviderDashboardActivity.this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;

        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

//        loadingDialog.show();
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);

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

        previewDialogBidNow = new Dialog(ServiceProviderDashboardActivity.this);
        previewDialogBidNow.setContentView(R.layout.dialog_bid_now);
        previewDialogBidNow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogAcceptRevisedBid = new Dialog(ServiceProviderDashboardActivity.this);
        dialogAcceptRevisedBid.setContentView(R.layout.dialog_bid_now);
        dialogAcceptRevisedBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogViewConsignment = new Dialog(ServiceProviderDashboardActivity.this);
        dialogViewConsignment.setContentView(R.layout.dialog_bid_now);
        dialogViewConsignment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //------------------------------------- Truck Select --------------------------------------
        dialogSelectTruck = new Dialog(ServiceProviderDashboardActivity.this);
        dialogSelectTruck.setContentView(R.layout.dialog_spinner_bind);
        dialogSelectTruck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogSelectTruckTitle = (TextView) dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_title);
        TextView dialogSelectTruckAddTruck = (TextView) dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView dialogSelectTruckOkButton = (TextView) dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_cancel);

        dialogSelectTruckTitle.setText(getString(R.string.selectTruck));
        dialogSelectTruckAddTruck.setVisibility(View.GONE);
        dialogSelectTruckOkButton.setVisibility(View.GONE);

        truckListRecyclerView = dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        truckListAdapter = new TrucksListAdapterBid(ServiceProviderDashboardActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);

        //--------------------------------- Select Driver ------------------------------------------
        dialogSelectDriver = new Dialog(ServiceProviderDashboardActivity.this);
        dialogSelectDriver.setContentView(R.layout.dialog_spinner_bind);
        dialogSelectDriver.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView previewSpinnerTitle = (TextView) dialogSelectDriver.findViewById(R.id.dialog_spinner_bind_title);
        TextView previewSpinnerAddTruck = (TextView) dialogSelectDriver.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView previewSpinnerOkButton = (TextView) dialogSelectDriver.findViewById(R.id.dialog_spinner_bind_cancel);

        previewSpinnerTitle.setText(getString(R.string.selectDriver));
        previewSpinnerAddTruck.setVisibility(View.GONE);
        previewSpinnerOkButton.setVisibility(View.GONE);

        driverListRecyclerView = dialogSelectDriver.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversListAdapterBid(ServiceProviderDashboardActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);

        //--------------------------------- Find Loads ---------------------------------------------
        searchListRecyclerView = (RecyclerView) findViewById(R.id.find_loads_search_recycler_view);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        searchListRecyclerView.setLayoutManager(linearLayoutManager1);
        searchListRecyclerView.setHasFixedSize(true);

        searchLoadAdapter = new SearchLoadAdapter(ServiceProviderDashboardActivity.this, searchLoadModels);
        searchListRecyclerView.setAdapter(searchLoadAdapter);

        searchList = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.array_indian_states)));
        for (int i = 0; i < searchList.size(); i++) {
            SearchLoadModel searchLoadModel = new SearchLoadModel();
            searchLoadModel.setSearchList(String.valueOf(searchList.get(i)));
            searchLoadModels.add(searchLoadModel);
        }

        //------------------------------ Drop ------------------------------------------------------
        searchListRecyclerViewDrop = (RecyclerView) findViewById(R.id.find_loads_search_recycler_view_drop);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager2.setReverseLayout(false);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        searchListRecyclerViewDrop.setLayoutManager(linearLayoutManager2);
        searchListRecyclerViewDrop.setHasFixedSize(true);

        searchLoadAdapterDrop = new SearchLoadAdapterDrop(ServiceProviderDashboardActivity.this, searchLoadModels);
        searchListRecyclerViewDrop.setAdapter(searchLoadAdapterDrop);

        stateConstrain = (ConstraintLayout) findViewById(R.id.find_loads_state_constrain);
    }

    public void getTruckList() {
        //---------------------------- Get Truck Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/truck/truckbyuserID/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    truckList = new ArrayList<>();
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        TruckModel model = new TruckModel();
                        model.setUser_id(obj.getString("user_id"));
                        model.setVehicle_no(obj.getString("vehicle_no"));
                        model.setTruck_type(obj.getString("truck_type"));
                        model.setTruck_carrying_capacity(obj.getString("truck_carrying_capacity"));
                        model.setRc_book(obj.getString("rc_book"));
                        model.setVehicle_insurance(obj.getString("vehicle_insurance"));
                        model.setTruck_id(obj.getString("truck_id"));
                        model.setDriver_id(obj.getString("driver_id"));
                        truckList.add(model);
                    }
                    if (truckList.size() > 0) {
                        truckListAdapter.updateData(truckList);
                    } else {
                    }

//                    if (truckList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        truckListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        truckListRecyclerView.setLayoutParams(params);
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
                        modelDriver.setTruck_id(obj.getString("truck_id"));
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

    public void RearrangeItems() {
        getLocation();
        ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
        JumpTo.goToServiceProviderDashboard(ServiceProviderDashboardActivity.this, phone, loadNotificationSelected);
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

                    if (userId == null) {
                        bidsSubmittedTextView.setVisibility(View.GONE);
                    } else {
                        bidsSubmittedTextView.setVisibility(View.VISIBLE);
                    }

                    getUserDetails();
                    //---------------------------- Get Load Details -------------------------------------------
                    getLoadNotificationList();
                    getTruckList();
                    getDriverDetailsList();

                    LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
//                    linearLayoutManagerBank.setReverseLayout(false);
                    loadListRecyclerView.setLayoutManager(linearLayoutManagerBank);
                    loadListRecyclerView.setHasFixedSize(true);

                    LinearLayoutManager linearLayoutManagerBank1 = new LinearLayoutManager(getApplicationContext());
//                    linearLayoutManagerBank1.setReverseLayout(false);
                    loadSubmittedRecyclerView.setLayoutManager(linearLayoutManagerBank1);
                    loadSubmittedRecyclerView.setHasFixedSize(true);

                    loadSubmittedAdapter = new LoadSubmittedAdapter(ServiceProviderDashboardActivity.this, updatedLoadSubmittedList);
                    loadSubmittedRecyclerView.setAdapter(loadSubmittedAdapter);
                    loadSubmittedRecyclerView.scrollToPosition(loadSubmittedAdapter.getItemCount() - 1);

                    loadListAdapter = new LoadNotificationAdapter(ServiceProviderDashboardActivity.this, loadListToCompare);
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

                        String deviceIdFromAPI = obj.getString("device_id");
                        if (deviceIdFromAPI.equals("null") || deviceIdFromAPI == null) {
                            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            UpdateUserDetails.updateUserDeviceId(userId, deviceId);
                        }

                        String isRegistrationDone = obj.getString("isRegistration_done");
                        Log.i("IsREg", isRegistrationDone);
                        isPersonalDetailsDone = obj.getString("isPersonal_dt_added");
                        isFirmDetailsDone = obj.getString("isCompany_added");
                        isBankDetailsDone = obj.getString("isBankDetails_given");
                        isTruckDetailsDone = obj.getString("isTruck_added");
                        isDriverDetailsDone = obj.getString("isDriver_added");
                        isProfileAdded = obj.getString("isProfile_pic_added");

                        Log.i("isProfileAdded at SP", isProfileAdded);

                        //-------------------------------------Personal details ---- -------------------------------------
                        menuUserNameTextView.setText(name);
                        s1 = mobile.substring(2, 12);
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
        ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
        switch (view.getId()) {
            case R.id.menu_personal_details_button:
                JumpTo.goToViewPersonalDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false);
                break;

            case R.id.menu_bank_details_button:
                if (isBankDetailsDone.equals("1")) {
                    JumpTo.goToViewBankDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false);
                } else {
                    JumpTo.goToBankDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false, false, null);
                }
                break;

            case R.id.menu_truck_details:
                if (isTruckDetailsDone.equals("1")) {
                    JumpTo.goToViewVehicleDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false);
                } else {
                    JumpTo.goToVehicleDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false, false, false, false, null, null);
                }
                break;

            case R.id.menu_driver_details:
                if (isDriverDetailsDone.equals("1")) {
                    JumpTo.goToViewDriverDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false);
                } else {
                    JumpTo.goToDriverDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false, false, false, null, null);
                }
                break;

            case R.id.menu_settings_button:
                JumpTo.getToSettingAndPreferences(ServiceProviderDashboardActivity.this, phone, userId, role, false);
                break;

            case R.id.menu_kyc:
                JumpTo.goToPersonalDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false, false);
                break;
        }
    }

    public void onCLickShowMenu(View view) {
        if (userId == null) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

            alertTitle.setText(getString(R.string.Please_Register));
            alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
            alertPositiveButton.setText(getString(R.string.Register_Now));
            alertNegativeButton.setText(getString(R.string.cancel));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });

            alertPositiveButton.setOnClickListener(view1 -> {
                alert.dismiss();
                ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
                JumpTo.goToRegistrationActivity(ServiceProviderDashboardActivity.this, phone, true);
            });
            //------------------------------------------------------------------------------------------
        } else {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(menuDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.END;
            try {
                menuDialog.show();
            } catch (Exception e) {
            }
            menuDialog.setCancelable(true);
            menuDialog.getWindow().setAttributes(lp);
        }
    }

    public void onClickLogOut(View view) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

        alertTitle.setText(getString(R.string.Log_Out));
        alertMessage.setText(getString(R.string.Log_Out_message));
        alertPositiveButton.setText(getString(R.string.yes));
        alertNegativeButton.setText(getString(R.string.no));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alertPositiveButton.setOnClickListener(view1 -> {
            alert.dismiss();
            FirebaseAuth.getInstance().signOut();
            JumpTo.goToLogInActivity(ServiceProviderDashboardActivity.this);
        });
        //------------------------------------------------------------------------------------------

    }

    public void onClickDismiss(View view) {
        menuDialog.dismiss();
    }

    public void onClickLoadAndBids(View view) {
        switch (view.getId()) {
            case R.id.dashboard_load_notification_button:
                loadNotificationSelected = true;
                loadNotificationConstrain.setVisibility(View.VISIBLE);
                bidsSubmittedConstrain.setVisibility(View.INVISIBLE);
                loadNotificationUnderline.setVisibility(View.VISIBLE);
                bidSubmittedUnderline.setVisibility(View.INVISIBLE);
                loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                findLoadsConstrain.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                findLoadsUnderline.setVisibility(View.INVISIBLE);
                stateConstrainView.setVisibility(View.INVISIBLE);
                break;

            case R.id.dashboard_bids_submitted_button:
                loadNotificationSelected = false;
                loadNotificationConstrain.setVisibility(View.INVISIBLE);
                bidsSubmittedConstrain.setVisibility(View.VISIBLE);
                loadNotificationUnderline.setVisibility(View.INVISIBLE);
                bidSubmittedUnderline.setVisibility(View.VISIBLE);
                loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                findLoadsConstrain.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                findLoadsUnderline.setVisibility(View.INVISIBLE);
                stateConstrainView.setVisibility(View.INVISIBLE);
                break;

            case R.id.find_loads_find_truck_text:
                loadNotificationConstrain.setVisibility(View.INVISIBLE);
                bidsSubmittedConstrain.setVisibility(View.INVISIBLE);
                loadNotificationUnderline.setVisibility(View.INVISIBLE);
                bidSubmittedUnderline.setVisibility(View.INVISIBLE);
                loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                findLoadsConstrain.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                findLoadsUnderline.setVisibility(View.VISIBLE);
                stateConstrainView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                RearrangeItems();
                break;

            case R.id.bottom_nav_customer_dashboard:
                if (userId == null) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

                    alertTitle.setText(getString(R.string.Please_Register));
                    alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
                    alertPositiveButton.setText(getString(R.string.Register_Now));
                    alertNegativeButton.setText(getString(R.string.cancel));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });

                    alertPositiveButton.setOnClickListener(view1 -> {
                        alert.dismiss();
                        JumpTo.goToRegistrationActivity(ServiceProviderDashboardActivity.this, phone, true);
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    JumpTo.goToFindLoadsActivity(ServiceProviderDashboardActivity.this, userId, phone, false);
                }
                break;

            case R.id.bottom_nav_track:
                if (userId == null) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

                    alertTitle.setText(getString(R.string.Please_Register));
                    alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
                    alertPositiveButton.setText(getString(R.string.Register_Now));
                    alertNegativeButton.setText(getString(R.string.cancel));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });

                    alertPositiveButton.setOnClickListener(view1 -> {
                        alert.dismiss();
                        JumpTo.goToRegistrationActivity(ServiceProviderDashboardActivity.this, phone, true);
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    JumpTo.goToSPTrackActivity(ServiceProviderDashboardActivity.this, phone, false);
                }
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

        loadListAdapter = new LoadNotificationAdapter(ServiceProviderDashboardActivity.this, loadListToCompare);
        loadListRecyclerView.setAdapter(loadListAdapter);

        if (loadListToCompare.size() > 0) {
            loadListAdapter.updateData(loadListToCompare);
        }


//        getStateBidsPick(loadListToCompare);
        getStateBidsDrop(loadListToCompare);
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
                        modelLoadNotification.setPayment_type(obj.getString("payment_type"));

                        if (obj.getString("bid_status").equals("loadPosted") || obj.getString("bid_status").equals("loadReactivated")) {
                            loadList.add(modelLoadNotification);
                        }
                    }

                    Collections.reverse(loadList);
                    TextView noLoadAvailable = (TextView) findViewById(R.id.dashboard_load_here_text);

                    loadListAdapter = new LoadNotificationAdapter(ServiceProviderDashboardActivity.this, loadList);
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
        if (userId == null) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

            alertTitle.setText(getString(R.string.Please_Register));
            alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
            alertPositiveButton.setText(getString(R.string.Register_Now));
            alertNegativeButton.setText(getString(R.string.cancel));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });

            alertPositiveButton.setOnClickListener(view -> {
                alert.dismiss();
                ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
                JumpTo.goToRegistrationActivity(ServiceProviderDashboardActivity.this, phone, true);
            });
            //------------------------------------------------------------------------------------------
        } else {
            if (!isTruckDetailsDone.equals("1")) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

                alertTitle.setText("Truck Details");
                alertMessage.setText("You cannot bid unless you have a Truck");
                alertPositiveButton.setText("+ Add");
                alertNegativeButton.setText("Cancel");

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });

                alertPositiveButton.setOnClickListener(view -> {
                    alert.dismiss();
                    ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
                    JumpTo.goToVehicleDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false, false, false, false, null, null);
                });
                //------------------------------------------------------------------------------------------
            } else if (!isDriverDetailsDone.equals("1")) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

                alertTitle.setText("Truck Details");
                alertMessage.setText("You cannot bid unless you have a Driver");
                alertPositiveButton.setText("+ Add");
                alertNegativeButton.setText("Cancel");

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });

                alertPositiveButton.setOnClickListener(view -> {
                    alert.dismiss();
                    ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
                    JumpTo.goToDriverDetailsActivity(ServiceProviderDashboardActivity.this, userId, phone, false, false, false, null, null);

                });
                //------------------------------------------------------------------------------------------
            } else {
                loadId = obj.getIdpost_load();
                bidStatus = obj.getBid_status();
                String pick_up_date = obj.getPick_up_date();
                String pick_up_time = obj.getPick_up_time();
                String required_budget = obj.getBudget();
                String distance = obj.getKm_approx();
                required_capacity = obj.getCapacity();
                required_truck_body = obj.getBody_type();
                String pick_up_location = obj.getPick_add() + " " + obj.getPick_city() + " " + obj.getPick_state() + " " + obj.getPick_pin_code();
                String drop_location = obj.getDrop_add() + " " + obj.getDrop_city() + " " + obj.getDrop_state() + " " + obj.getDrop_pin_code();
                String received_notes_description = obj.getNotes_meterial_des();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(previewDialogBidNow.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                previewDialogBidNow.show();
                previewDialogBidNow.setCancelable(true);
                previewDialogBidNow.getWindow().setAttributes(lp);

                //-------------------------------------------Display Load Information---------------------------------------------
                TextView pickUpDate = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
                TextView pickUpTime = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
                customerFirstBudget = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_budget_textview);
                TextView approxDistance = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_distance_textview);
                TextView reqCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_capacity_textview);
                TextView reqBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
                TextView pickUpLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
                TextView dropLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_drop_location_textview);
                TextView receivedNotes = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_received_notes_textview);
                TextView loadIdHeading = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_loadId_heading);
                arrowImage = (ImageView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_arrow_budget);
                TextView paymentType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_customer_payment_method);

                pickUpDate.setText(pick_up_date);
                pickUpTime.setText(pick_up_time);
                customerFirstBudget.setText(required_budget);
                approxDistance.setText(distance);
                reqCapacity.setText(required_capacity);
                reqBodyType.setText(required_truck_body);
                pickUpLocation.setText(pick_up_location);
                dropLocation.setText(drop_location);
                receivedNotes.setText(received_notes_description);
                paymentType.setText(obj.getPayment_type());
                loadIdHeading.setText(getString(R.string.Load_Details) + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
                //----------------------------------------------------------------------------------------------------------------

                //-------------------------------------------------Accept Load and Bid now-----------------------------------------
                spQuote = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_sp_quote_textview);
                selectTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_truck_textview);
                selectDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_driver_textview);
                selectedTruckModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_model_textview);
                selectedTruckCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
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

                cancel.setOnClickListener(view -> {
                    RearrangeItems();
                });

                acceptAndBid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {

                            if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString())) {
                                saveBid(createBidRequest("Accepted", spQuote.getText().toString()));
                            } else if (!spQuote.getText().toString().equals(customerFirstBudget.getText().toString()) && !negotiable) {
                                saveBid(createBidRequest("submittedNonNego", spQuote.getText().toString()));
                            } else if (!spQuote.getText().toString().equals(customerFirstBudget.getText().toString()) && negotiable) {
                                saveBid(createBidRequest("submittedNego", spQuote.getText().toString()));
                            }

                            Log.i("loadId bidded", obj.getIdpost_load());
                            //----------------------- Alert Dialog -------------------------------------------------
                            Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                            alert.setContentView(R.layout.dialog_alert_single_button);
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

                            alertTitle.setText(getString(R.string.Post_Bid));
                            alertMessage.setText(getString(R.string.Bid_Posted_Successfully));
                            alertPositiveButton.setVisibility(View.GONE);
                            alertNegativeButton.setText(getString(R.string.ok));
                            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alert.dismiss();
                                    JumpTo.goToServiceProviderDashboard(ServiceProviderDashboardActivity.this, phone, false);
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
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialogSelectTruck.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.CENTER;

                        dialogSelectTruck.show();
                        dialogSelectTruck.getWindow().setAttributes(lp);
                    }
                });

                selectDriver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isTruckSelectedToBid) {
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialogSelectDriver.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.gravity = Gravity.CENTER;

                            dialogSelectDriver.show();
                            dialogSelectDriver.getWindow().setAttributes(lp);
                        }
                    }
                });
            }
        }
    }

    AdapterView.OnItemSelectedListener onPickOrDrop = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selected = adapterView.getSelectedItem().toString();

            if (selected.equals("Pick-up Location")){
                searchListRecyclerViewDrop.setVisibility(View.INVISIBLE);
                searchListRecyclerView.setVisibility(View.VISIBLE);
            }else{
                searchListRecyclerViewDrop.setVisibility(View.VISIBLE);
                searchListRecyclerView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

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

    private void getTruckDetailsByTruckId(String truckIdSelected, Boolean acceptRevised) {

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
                        String truckCapacity = obj.getString("truck_carrying_capacity");
                        String vehicleNo = obj.getString("vehicle_no");
                        selectedDriverId = obj.getString("driver_id");

                        selectTruck.setText(vehicleNo);
                        selectedTruckModel.setText(truckModel);
                        selectedTruckCapacity.setText(truckCapacity);
                    }

                    if (selectedDriverId.equals("null")) {
                        selectDriver.setText("");
                        Log.i("driverId null", "There is no driver Id for this truck");
                    } else {
                        if (acceptRevised) {
                            getDriverDetailsByDriverId(selectedDriverId);
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

    public void getBidSubmittedList(String loadIdReceived, String
            bidId, ArrayList<LoadNotificationModel> loadListToCompare) {
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

                        if (!obj.getString("bid_status").equals("delete") && !obj.getString("bid_status").equals("loadExpired") && !obj.getString("bid_status").equals("start")) {
                            loadSubmittedList.add(bidSubmittedModel);
                        }
                    }


                    TextView noBidsSubmittedTextView = (TextView) findViewById(R.id.dashboard_no_bids_submitted_text);
                    if (loadSubmittedList.size() > 0) {
                        FooThread fooThread = new FooThread(handler);
                        fooThread.start();
                        updatedLoadSubmittedList.addAll(loadSubmittedList);
                        loadSubmittedAdapter.updateData(updatedLoadSubmittedList);
                        if (updatedLoadSubmittedList.size() > 0) {
                            noBidsSubmittedTextView.setVisibility(View.GONE);
                        } else {
                            noBidsSubmittedTextView.setVisibility(View.VISIBLE);
                        }
                        compareAndRemove(loadListToCompare);
                    } else {
                        getStateBidsDrop(loadListToCompare);
                        getStateBidsPick(loadListToCompare);
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

        setBudget = new Dialog(ServiceProviderDashboardActivity.this);
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
        Button cancelButton = setBudget.findViewById(R.id.dialog_budget_cancel_button);
        budget.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String newPreviousBudget = previousBudget.replaceAll(",", "");
        budget.setText(newPreviousBudget);

        cancelButton.setOnClickListener(view -> setBudget.dismiss());

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
                        arrowImage.setVisibility(View.GONE);
                        spQuote.setTextColor(getResources().getColor(R.color.green));
                        negotiable_no.setChecked(true);
                        negotiable_yes.setChecked(false);
                        negotiable_yes.setEnabled(false);
                        negotiable = false;
                        isNegotiableSelected = true;
                    } else {
                        try {
                            int spBudgetsInt = Integer.parseInt(spQuote.getText().toString().replaceAll(",", ""));
                            int lpBudgetInt = Integer.parseInt(customerFirstBudget.getText().toString().replaceAll(",", ""));
                            if (spBudgetsInt > lpBudgetInt) {
                                arrowImage.setOnClickListener(view -> {
                                    //----------------------- Alert Dialog -------------------------------------------------
                                    Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                                    alertTitle.setText(getString(R.string.Your_Budget_is_High));
                                    alertMessage.setText(getString(R.string.Your_Budget_is_Higher_than_the_Load_Poster));
                                    alertPositiveButton.setVisibility(View.GONE);
                                    alertNegativeButton.setText(getString(R.string.ok));
                                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alert.dismiss();
                                        }
                                    });
                                    //------------------------------------------------------------------------------------------
                                });
                                arrowImage.setVisibility(View.VISIBLE);
                                arrowImage.setImageDrawable(getDrawable(R.drawable.ic_up_arrow));
                            } else {
                                arrowImage.setVisibility(View.VISIBLE);
                                arrowImage.setOnClickListener(view -> {
                                    //----------------------- Alert Dialog -------------------------------------------------
                                    Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                                    alertTitle.setText(getString(R.string.Your_Budget_is_Low));
                                    alertMessage.setText(getString(R.string.Your_Budget_is_Lower_than_the_Load_Poster));
                                    alertPositiveButton.setVisibility(View.GONE);
                                    alertNegativeButton.setText(getString(R.string.ok));
                                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alert.dismiss();
                                        }
                                    });
                                    //------------------------------------------------------------------------------------------
                                });
                                arrowImage.setImageDrawable(getDrawable(R.drawable.ic_down_arrow));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        negotiable_yes.setEnabled(true);
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
                String cb = customerFirstBudget.getText().toString().replaceAll(",", "");
                int customer50Budget = Integer.valueOf(cb) / 2;
                String sb = budget.getText().toString().replaceAll(",", "");
                int spBudget = Integer.valueOf(sb);

                if (spBudget < customer50Budget) {
                    ShowAlert.showAlert(ServiceProviderDashboardActivity.this, getString(R.string.Enter_Appropriate_Quote), getString(R.string.You_cannot_bid_less_than_50_of_customer_Budget), true, false, getString(R.string.ok), "null");
                } else {
                    if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                        acceptAndBid.setEnabled(true);
                        acceptAndBid.setBackgroundResource((R.drawable.button_active));
                    } else {
                        acceptAndBid.setEnabled(false);
                        acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                    }
                    setBudget.dismiss();
                }

            }
        });
    }

    //--------------------------------------create Bank Details in API -------------------------------------
    public BidLoadRequest createBidRequest(String status, String spFinal) {
        BidLoadRequest bidLoadRequest = new BidLoadRequest();
        bidLoadRequest.setUser_id(userId);
        bidLoadRequest.setAssigned_truck_id(truckId);
        bidLoadRequest.setAssigned_driver_id(selectedDriverId);
        bidLoadRequest.setIdpost_load(loadId);
        bidLoadRequest.setBid_status(status);
        bidLoadRequest.setBody_type(selectedTruckModel.getText().toString());
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
        dialogAcceptRevisedBid.setCancelable(true);
        dialogAcceptRevisedBid.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        customerSecondBudget = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_distance_textview);
        TextView reqCapacity = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_req_capacity_textview);
        TextView reqBodyType = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
        TextView pickUpLocation = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
        TextView dropLocation = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_drop_location_textview);
        TextView receivedNotes = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_received_notes_textview);
        TextView loadIdHeading = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_loadId_heading);
        customerNameHeading = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_customerName_heading);
        customerName = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_customerName);
        customerNumberHeading = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_customer_phone_heading);
        customerNumber = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_customer_mobile_no);

        customerNameHeading.setVisibility(View.VISIBLE);
        customerName.setVisibility(View.VISIBLE);
        customerNumber.setVisibility(View.VISIBLE);
        customerNumberHeading.setVisibility(View.VISIBLE);

        getCustomerNameAndNumber(obj.getUser_id());
        pickUpDate.setText(pick_up_date);
        pickUpTime.setText(pick_up_time);
        approxDistance.setText(distance);
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText(getString(R.string.Load_Details) + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_select_driver_textview);
        selectedTruckModel = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckCapacity = (TextView) dialogAcceptRevisedBid.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
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

        getBidDetailsByBidId(obj.getBidId(), false);
        spQuote.setTextColor(getResources().getColor(R.color.green));

        cancel.setEnabled(true);
        cancel.setBackgroundResource((R.drawable.button_active));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RearrangeItems();
                previewDialogBidNow.dismiss();
            }
        });

        acceptAndBid.setEnabled(true);
        acceptAndBid.setBackgroundResource((R.drawable.button_active));

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateBidDetails.updateSPNoteForCustomer(obj.getBidId(), notesSp.getText().toString());
                UpdateBidDetails.updateBidStatus(obj.getBidId(), "AcceptedBySp");
                UpdateBidDetails.updateSPQuoteFinal(obj.getBidId(), spQuote.getText().toString());
                UpdateBidDetails.updateAssignedTruckId(obj.getBidId(), updateAssignedTruckId);
                UpdateBidDetails.updateAssignedDriverId(obj.getBidId(), updateAssignedDriverId);

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText(getString(R.string.Bid_Revised_and_Responded));
                alertMessage.setText(getString(R.string.Bid_Revised_and_Responded_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        RearrangeItems();
                        previewDialogBidNow.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        });

        selectTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogSelectTruck.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialogSelectTruck.show();
                dialogSelectTruck.getWindow().setAttributes(lp);
            }
        });

        selectDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTruckSelectedToBid) {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialogSelectDriver.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;

                    dialogSelectDriver.show();
                    dialogSelectDriver.getWindow().setAttributes(lp);
                }
            }
        });
    }
    //-----------------------------------------------------------------------------------------------------

    private void getBidDetailsByBidId(String bidId, Boolean accept) {
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
                        getTruckDetailsByTruckId(truck_id, accept);
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
        dialogViewConsignment.setCancelable(true);
        dialogViewConsignment.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        customerSecondBudget = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_distance_textview);
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
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText(getString(R.string.Load_Details) + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_select_driver_textview);
        selectedTruckModel = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckCapacity = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
        notesSp = (EditText) dialogViewConsignment.findViewById(R.id.dialog_bid_now_notes_editText);
        declaration = (CheckBox) dialogViewConsignment.findViewById(R.id.dialog_bid_now_declaration);
        startTrip = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_accept_and_bid_btn);
        cancel = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_cancel_btn);
        negotiable_yes = dialogViewConsignment.findViewById(R.id.dialog_bid_now_radio_btn_yes);
        negotiable_no = dialogViewConsignment.findViewById(R.id.dialog_bid_now_radio_btn_no);
        partitionTextview = dialogViewConsignment.findViewById(R.id.bid_now_middle_textview_partition);
        timeLeftTextview = dialogViewConsignment.findViewById(R.id.bid_now_time_left_textView);
        timeLeft00 = dialogViewConsignment.findViewById(R.id.bid_now_time_left_00_textview);
        cancel2 = dialogViewConsignment.findViewById(R.id.dialog_bid_now_cancel_btn2);

        cancel2.setVisibility(View.VISIBLE);
        cancel2.setEnabled(true);
        cancel2.setBackgroundTintList(getResources().getColorStateList(R.color.light_black));

        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RearrangeItems();
            }
        });

        notesSp.setVisibility(View.GONE);
        timeLeft00.setVisibility(View.GONE);
        partitionTextview.setText(getString(R.string.My_Bid_Response));
        timeLeftTextview.setText(getString(R.string.CONSIGNMENT));
        timeLeftTextview.setTextColor(getResources().getColorStateList(R.color.black));
        timeLeftTextview.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        negotiable_yes.setEnabled(false);
        negotiable_yes.setChecked(false);
        negotiable_no.setChecked(true);
        declaration.setVisibility(View.INVISIBLE);

        spQuote.setTextColor(getResources().getColor(R.color.green));

        getBidDetailsByBidId(obj.getBidId(), false);
        getCustomerNameAndNumber(obj.getUser_id());

        cancel.setText(getString(R.string.Withdraw));
        cancel.setEnabled(true);
        cancel.setBackgroundTintList(getResources().getColorStateList(R.color.light_black));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
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

                alertTitle.setText(getString(R.string.Withdraw_Bid));
                alertMessage.setText(getString(R.string.Do_you_really_want_to_withdraw_bid));
                alertPositiveButton.setVisibility(View.VISIBLE);
                alertNegativeButton.setText(getString(R.string.cancel));
                alertPositiveButton.setText(getString(R.string.Withdraw));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });

                alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateBidDetails.updateBidStatus(obj.getBidId(), "withdrawnBySp");
                        //----------------------- Alert Dialog -------------------------------------------------
                        Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                        alert.setContentView(R.layout.dialog_alert_single_button);
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

                        alertTitle.setText(getString(R.string.Withdraw_Bid));
                        alertMessage.setText(getString(R.string.Bid_is_withdrawn_successfully));
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText(getString(R.string.ok));
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RearrangeItems();
                                dialogViewConsignment.dismiss();
                            }
                        });
                    }
                });
                //------------------------------------------------------------------------------------------

            }
        });

        startTrip.setText(getString(R.string.Start_Trip));
        startTrip.setEnabled(true);
        startTrip.setBackgroundResource((R.drawable.button_active));

        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "start");
                UpdateBidDetails.updateBidStatus(obj.getBidId(), "start");
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText(getString(R.string.Trip_Started_Successfully));
                alertMessage.setText(getString(R.string.You_can_track_your_trip_in_track_section));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        RearrangeItems();
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
                        String mobileNumberCustomer = obj1.getString("phone_number");
                        String s = mobileNumberCustomer.substring(2, 12);
                        customerNumber.setText("+91 " + s);
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

                            String profileImgUrl = "";
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
        chooseDialog = new Dialog(ServiceProviderDashboardActivity.this);
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
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST_profile);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY_profile);
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
        if (ContextCompat.checkSelfPermission(ServiceProviderDashboardActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderDashboardActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(ServiceProviderDashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderDashboardActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(ServiceProviderDashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderDashboardActivity.this, new String[]{
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

    public void onClickAssignTruckFromList(TruckModel obj) {
        if (obj.getTruck_type().toLowerCase().equals(required_truck_body.toLowerCase()) && obj.getTruck_carrying_capacity().toLowerCase().equals(required_capacity.toLowerCase())) {
            Log.i("Truck Type LP", required_truck_body);
            Log.i("Load Type LP", required_capacity);
            Log.i("Truck Type SP", obj.getTruck_type());
            Log.i("Load Type SP", obj.getTruck_carrying_capacity());
            dialogSelectTruck.dismiss();
            truckId = obj.getTruck_id();
            selectedDriverId = obj.getDriver_id();
            isTruckSelectedToBid = true;
            selectTruck.setText(obj.getVehicle_no());
            getDriverDetailsAssigned(obj.getDriver_id());
            selectedTruckModel.setText(obj.getTruck_type());
            selectedTruckCapacity.setText(obj.getTruck_carrying_capacity());
        } else {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog deleteLoad = new Dialog(ServiceProviderDashboardActivity.this);
            deleteLoad.setContentView(R.layout.dialog_alert);
            deleteLoad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(deleteLoad.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            deleteLoad.show();
            deleteLoad.getWindow().setAttributes(lp);
            deleteLoad.setCancelable(true);

            TextView alertTitle = (TextView) deleteLoad.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) deleteLoad.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) deleteLoad.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) deleteLoad.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Truck_doesnt_match));
            alertMessage.setText(getString(R.string.Truck_doesnt_match_message1) + getString(R.string.Truck_doesnt_match_message2));
            alertPositiveButton.setText(getString(R.string.ok));
            alertPositiveButton.setVisibility(View.VISIBLE);
            alertNegativeButton.setText(getString(R.string.cancel));

            alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteLoad.dismiss();
                    dialogSelectTruck.dismiss();
                    truckId = obj.getTruck_id();
                    selectedDriverId = obj.getDriver_id();
                    isTruckSelectedToBid = true;
                    selectTruck.setText(obj.getVehicle_no());
                    getDriverDetailsAssigned(obj.getDriver_id());
                    selectedTruckModel.setText(obj.getTruck_type());
                    selectedTruckCapacity.setText(obj.getTruck_carrying_capacity());
                }
            });

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteLoad.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------
        }
    }

    public void getDriverDetailsAssigned(String driverId) {
        //---------------------------- Get Driver Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/driverId/" + driverId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    truckList = new ArrayList<>();
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        selectDriver.setText(obj.getString("driver_name"));
                        String driverEmail = obj.getString("driver_emailId");
                        String driverDlURL = obj.getString("upload_dl");
                        String driverSelfieURL = obj.getString("driver_selfie");
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

    public void onClickReAssignDriver(DriverModel obj) {
        dialogSelectDriver.dismiss();
        selectedDriverId = obj.getDriver_id();
        selectDriver.setText(obj.getDriver_name());
    }

    public void onClickPostATrip(View view) {
        ShowAlert.loadingDialog(ServiceProviderDashboardActivity.this);
        JumpTo.goToPostATrip(ServiceProviderDashboardActivity.this, phone, userId, false, null, false);
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(ServiceProviderDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(ServiceProviderDashboardActivity.this, Locale.getDefault());
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


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(ServiceProviderDashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onBackPressed() {
        String visibility = String.valueOf(stateConstrain.getVisibility());
        Log.i("visibility", visibility); //visible = 0
        if (visibility.equals("0")) {
            RearrangeItems();
        } else if (isBackPressed) {
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
                        String profileImgUrl = "";

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
        profileImagePicker(requestCode, resultCode, data);
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

        Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
        alert.setContentView(R.layout.dialog_alert_single_button);
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

        alertTitle.setText(getString(R.string.Profile_Picture));
        alertMessage.setText(getString(R.string.Profile_Picture_added_successfully));
        alertPositiveButton.setVisibility(View.GONE);
        alertNegativeButton.setText(getString(R.string.ok));
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                RearrangeItems();
            }
        });
    }

    private void getNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("load")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                            Log.i("Message", msg);
                        } else {
                            Log.i("Message", "Success");
                        }
                    }
                });

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("fytNotification", "fytNotification", NotificationManager.IMPORTANCE_DEFAULT);
        }
        NotificationManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userId == null) {

        } else {
            if (!isPersonalDetailsDone.equals("1")) {
                InAppNotification.SendNotificationJumpToPersonalDetailsActivity(ServiceProviderDashboardActivity.this, "Complete Your Profile", "Upload PAN and Aadhar in the Personal Details Section", userId, phone, false);
            }

            if (!isBankDetailsDone.equals("1")) {
                InAppNotification.SendNotificationJumpToBankDetailsActivity(ServiceProviderDashboardActivity.this, "Complete Your Profile", "Upload Bank details and complete your Profile", userId, phone, false, null);
            }

            if (!isTruckDetailsDone.equals("1")) {
                InAppNotification.SendNotificationJumpToVehicleDetailsActivity(ServiceProviderDashboardActivity.this, "Complete Your Profile", "Truck Details missing!\nAdd a Truck to your Profile.", userId, phone, false, false, false, null, null);
            }

            if (!isDriverDetailsDone.equals("1")) {
                InAppNotification.SendNotificationJumpToDriverDetailsActivity(ServiceProviderDashboardActivity.this, "Complete Your Profile", "Driver Details missing!\nAdd a Driver to your Profile.", userId, phone, false, false, null, null);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int state = msg.getData().getInt("state");
            if (state == 1) {
                loadingDialog.dismiss();
            }
        }
    };

    private String profileImagePicker(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_FROM_GALLERY_profile && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
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

            alertTitle.setText(getString(R.string.Personal_Details));
            alertMessage.setText(getString(R.string.Profile_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

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

        } else if (requestCode == CAMERA_PIC_REQUEST_profile) {

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                saveImage(imageRequest());
                uploadImage(path);

                profileAddedAlert();

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Profile_Uploaded_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();

                    }
                });
                //------------------------------------------------------------------------------------------

            } catch (Exception e) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Profile_not_Uploaded_please_try_again));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        }
        return "";
    }

    public void onClickWhatsApp(View view) {
        Dialog chooseDialog = new Dialog(ServiceProviderDashboardActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        TextView cameraText = chooseDialog.findViewById(R.id.dialog_camera_text);
        cameraText.setText("Whats App");
        TextView galleryText = chooseDialog.findViewById(R.id.dialog_photo_library_text);
        galleryText.setText(getString(R.string.Call));

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        camera.setImageDrawable(getResources().getDrawable(R.drawable.whats_app_small));
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);
        gallery.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
        gallery.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDialog.dismiss();
                String mobileNumber = "8806930081";
                String message = "";
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + mobileNumber + "&text=" + message));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(ServiceProviderDashboardActivity.this, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + "+918806930081"));
                startActivity(intent);
            }
        });
    }

    //----------------------------------- Find Loads -----------------------------------------------
    public void setLoadCount(SearchLoadModel obj, TextView numberOfLoads, ConstraintLayout findConstrain, ArrayList<SearchLoadModel> array_indian_states) {
        try {
            if (obj.getSearchList().equals(searchList.get(0))) {
                numberOfLoads.setText(anList.size() + " Loads");
                obj.setItemCount(anList.size());
            }
            if (obj.getSearchList().equals(searchList.get(1))) {
                numberOfLoads.setText(apList.size() + " Loads");
                obj.setItemCount(apList.size());
            }
            if (obj.getSearchList().equals(searchList.get(2))) {
                numberOfLoads.setText(arList.size() + " Loads");
                obj.setItemCount(arList.size());
            }
            if (obj.getSearchList().equals(searchList.get(3))) {
                numberOfLoads.setText(asList.size() + " Loads");
                obj.setItemCount(asList.size());
            }
            if (obj.getSearchList().equals(searchList.get(4))) {
                numberOfLoads.setText(brList.size() + " Loads");
                obj.setItemCount(brList.size());
            }
            if (obj.getSearchList().equals(searchList.get(5))) {
                numberOfLoads.setText(chList.size() + " Loads");
                obj.setItemCount(chList.size());
            }
            if (obj.getSearchList().equals(searchList.get(6))) {
                numberOfLoads.setText(cgList.size() + " Loads");
                obj.setItemCount(cgList.size());
            }
            if (obj.getSearchList().equals(searchList.get(7))) {
                numberOfLoads.setText(ddList.size() + " Loads");
                obj.setItemCount(ddList.size());
            }
            if (obj.getSearchList().equals(searchList.get(8))) {
                numberOfLoads.setText(dd2List.size() + " Loads");
                obj.setItemCount(dd2List.size());
            }
            if (obj.getSearchList().equals(searchList.get(9))) {
                numberOfLoads.setText(dlList.size() + " Loads");
                obj.setItemCount(dlList.size());
            }
            if (obj.getSearchList().equals(searchList.get(10))) {
                numberOfLoads.setText(gaList.size() + " Loads");
                obj.setItemCount(gaList.size());
            }
            if (obj.getSearchList().equals(searchList.get(11))) {
                numberOfLoads.setText(gjList.size() + " Loads");
                obj.setItemCount(gjList.size());
            }
            if (obj.getSearchList().equals(searchList.get(12))) {
                numberOfLoads.setText(hrList.size() + " Loads");
                obj.setItemCount(hrList.size());
            }
            if (obj.getSearchList().equals(searchList.get(13))) {
                numberOfLoads.setText(hpList.size() + " Loads");
                obj.setItemCount(hpList.size());
            }
            if (obj.getSearchList().equals(searchList.get(14))) {
                numberOfLoads.setText(jkList.size() + " Loads");
                obj.setItemCount(jkList.size());
            }
            if (obj.getSearchList().equals(searchList.get(15))) {
                numberOfLoads.setText(jhList.size() + " Loads");
                obj.setItemCount(jhList.size());
            }
            if (obj.getSearchList().equals(searchList.get(16))) {
                numberOfLoads.setText(kaList.size() + " Loads");
                obj.setItemCount(kaList.size());
            }
            if (obj.getSearchList().equals(searchList.get(17))) {
                numberOfLoads.setText(klList.size() + " Loads");
                obj.setItemCount(klList.size());
            }
            if (obj.getSearchList().equals(searchList.get(18))) {
                numberOfLoads.setText(laList.size() + " Loads");
                obj.setItemCount(laList.size());
            }
            if (obj.getSearchList().equals(searchList.get(19))) {
                numberOfLoads.setText(ldList.size() + " Loads");
                obj.setItemCount(ldList.size());
            }
            if (obj.getSearchList().equals(searchList.get(20))) {
                numberOfLoads.setText(mpList.size() + " Loads");
                obj.setItemCount(mpList.size());
            }
            if (obj.getSearchList().equals(searchList.get(21))) {
                numberOfLoads.setText(mhList.size() + " Loads");
                obj.setItemCount(mhList.size());
            }
            if (obj.getSearchList().equals(searchList.get(22))) {
                numberOfLoads.setText(mnList.size() + " Loads");
                obj.setItemCount(mnList.size());
            }
            if (obj.getSearchList().equals(searchList.get(23))) {
                numberOfLoads.setText(mlList.size() + " Loads");
                obj.setItemCount(mlList.size());
            }
            if (obj.getSearchList().equals(searchList.get(24))) {
                numberOfLoads.setText(mzList.size() + " Loads");
                obj.setItemCount(mzList.size());
            }
            if (obj.getSearchList().equals(searchList.get(25))) {
                numberOfLoads.setText(nlList.size() + " Loads");
                obj.setItemCount(nlList.size());
            }
            if (obj.getSearchList().equals(searchList.get(26))) {
                numberOfLoads.setText(odList.size() + " Loads");
                obj.setItemCount(odList.size());
            }
            if (obj.getSearchList().equals(searchList.get(27))) {
                numberOfLoads.setText(pyList.size() + " Loads");
                obj.setItemCount(pyList.size());
            }
            if (obj.getSearchList().equals(searchList.get(28))) {
                numberOfLoads.setText(pbList.size() + " Loads");
                obj.setItemCount(pbList.size());
            }
            if (obj.getSearchList().equals(searchList.get(29))) {
                numberOfLoads.setText(rjList.size() + " Loads");
                obj.setItemCount(rjList.size());
            }
            if (obj.getSearchList().equals(searchList.get(30))) {
                numberOfLoads.setText(skList.size() + " Loads");
                obj.setItemCount(skList.size());
            }
            if (obj.getSearchList().equals(searchList.get(31))) {
                numberOfLoads.setText(tnList.size() + " Loads");
                obj.setItemCount(tnList.size());
            }
            if (obj.getSearchList().equals(searchList.get(32))) {
                numberOfLoads.setText(tsList.size() + " Loads");
                obj.setItemCount(tsList.size());
            }
            if (obj.getSearchList().equals(searchList.get(33))) {
                numberOfLoads.setText(trList.size() + " Loads");
                obj.setItemCount(trList.size());
            }
            if (obj.getSearchList().equals(searchList.get(34))) {
                numberOfLoads.setText(ukList.size() + " Loads");
                obj.setItemCount(ukList.size());
            }
            if (obj.getSearchList().equals(searchList.get(35))) {
                numberOfLoads.setText(upList.size() + " Loads");
                obj.setItemCount(upList.size());
            }
            if (obj.getSearchList().equals(searchList.get(36))) {
                numberOfLoads.setText(wbList.size() + " Loads");
                obj.setItemCount(wbList.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------- Find Loads -----------------------------------------------
    public void setLoadCountDrop(SearchLoadModel obj, TextView numberOfLoads, ConstraintLayout findConstrain, ArrayList<SearchLoadModel> array_indian_states) {
        try {
            if (obj.getSearchList().equals(searchList.get(0))) {
                numberOfLoads.setText(anListD.size() + " Loads");
                obj.setItemCount(anListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(1))) {
                numberOfLoads.setText(apListD.size() + " Loads");
                obj.setItemCount(apListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(2))) {
                numberOfLoads.setText(arListD.size() + " Loads");
                obj.setItemCount(arListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(3))) {
                numberOfLoads.setText(asListD.size() + " Loads");
                obj.setItemCount(asListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(4))) {
                numberOfLoads.setText(brListD.size() + " Loads");
                obj.setItemCount(brListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(5))) {
                numberOfLoads.setText(chListD.size() + " Loads");
                obj.setItemCount(chListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(6))) {
                numberOfLoads.setText(cgListD.size() + " Loads");
                obj.setItemCount(cgListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(7))) {
                numberOfLoads.setText(ddListD.size() + " Loads");
                obj.setItemCount(ddListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(8))) {
                numberOfLoads.setText(dd2ListD.size() + " Loads");
                obj.setItemCount(dd2ListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(9))) {
                numberOfLoads.setText(dlListD.size() + " Loads");
                obj.setItemCount(dlListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(10))) {
                numberOfLoads.setText(gaListD.size() + " Loads");
                obj.setItemCount(gaListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(11))) {
                numberOfLoads.setText(gjListD.size() + " Loads");
                obj.setItemCount(gjListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(12))) {
                numberOfLoads.setText(hrListD.size() + " Loads");
                obj.setItemCount(hrListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(13))) {
                numberOfLoads.setText(hpListD.size() + " Loads");
                obj.setItemCount(hpListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(14))) {
                numberOfLoads.setText(jkListD.size() + " Loads");
                obj.setItemCount(jkListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(15))) {
                numberOfLoads.setText(jhListD.size() + " Loads");
                obj.setItemCount(jhListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(16))) {
                numberOfLoads.setText(kaListD.size() + " Loads");
                obj.setItemCount(kaListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(17))) {
                numberOfLoads.setText(klListD.size() + " Loads");
                obj.setItemCount(klListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(18))) {
                numberOfLoads.setText(laListD.size() + " Loads");
                obj.setItemCount(laListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(19))) {
                numberOfLoads.setText(ldListD.size() + " Loads");
                obj.setItemCount(ldListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(20))) {
                numberOfLoads.setText(mpListD.size() + " Loads");
                obj.setItemCount(mpListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(21))) {
                numberOfLoads.setText(mhListD.size() + " Loads");
                obj.setItemCount(mhListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(22))) {
                numberOfLoads.setText(mnListD.size() + " Loads");
                obj.setItemCount(mnListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(23))) {
                numberOfLoads.setText(mlListD.size() + " Loads");
                obj.setItemCount(mlListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(24))) {
                numberOfLoads.setText(mzListD.size() + " Loads");
                obj.setItemCount(mzListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(25))) {
                numberOfLoads.setText(nlListD.size() + " Loads");
                obj.setItemCount(nlListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(26))) {
                numberOfLoads.setText(odListD.size() + " Loads");
                obj.setItemCount(odListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(27))) {
                numberOfLoads.setText(pyListD.size() + " Loads");
                obj.setItemCount(pyListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(28))) {
                numberOfLoads.setText(pbListD.size() + " Loads");
                obj.setItemCount(pbListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(29))) {
                numberOfLoads.setText(rjListD.size() + " Loads");
                obj.setItemCount(rjListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(30))) {
                numberOfLoads.setText(skListD.size() + " Loads");
                obj.setItemCount(skListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(31))) {
                numberOfLoads.setText(tnListD.size() + " Loads");
                obj.setItemCount(tnListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(32))) {
                numberOfLoads.setText(tsListD.size() + " Loads");
                obj.setItemCount(tsListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(33))) {
                numberOfLoads.setText(trListD.size() + " Loads");
                obj.setItemCount(trListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(34))) {
                numberOfLoads.setText(ukListD.size() + " Loads");
                obj.setItemCount(ukListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(35))) {
                numberOfLoads.setText(upListD.size() + " Loads");
                obj.setItemCount(upListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(36))) {
                numberOfLoads.setText(wbListD.size() + " Loads");
                obj.setItemCount(wbListD.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickFindLoadListItem(SearchLoadModel obj, TextView holder) {
        if (holder.getText().equals("0 Loads")) {
            stateConstrain.setVisibility(View.INVISIBLE);
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(ServiceProviderDashboardActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
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

            alertTitle.setText(getString(R.string.Load_Notifications));
            alertMessage.setText(getString(R.string.No_loads_available_for_the_state) + obj.getSearchList());
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------
        } else {
            stateConstrain.setVisibility(View.VISIBLE);

            //-------------------------------- Action Bar for state ----------------------------------------------
            View actionBarState = findViewById(R.id.find_loads_action_bar_for_state);
            TextView actionBarTitleState = (TextView) actionBarState.findViewById(R.id.action_bar_title);
            ImageView actionBarBackButtonState = (ImageView) actionBarState.findViewById(R.id.action_bar_back_button);
            ImageView actionBarMenuButtonState = (ImageView) actionBarState.findViewById(R.id.action_bar_menu);

            actionBarTitleState.setText(getString(R.string.Loads_for) + obj.getSearchList());
            actionBarMenuButtonState.setVisibility(View.GONE);
            actionBarBackButtonState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stateConstrain.setVisibility(View.INVISIBLE);
                }
            });
            //--------------------------------------------------------------------------------------
            RecyclerView stateLoadRecyclerView = (RecyclerView) findViewById(R.id.find_loads_state_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setReverseLayout(false);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            stateLoadRecyclerView.setLayoutManager(linearLayoutManager);
            stateLoadRecyclerView.setHasFixedSize(true);

            try {
                if (obj.getSearchList().equals(searchList.get(0))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, anList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(1))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, apList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(2))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, arList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(3))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, asList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(4))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, brList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(5))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, chList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(6))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, cgList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(7))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, ddList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(8))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, dd2List);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(9))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, dlList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(10))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, gaList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(11))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, gjList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(12))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, hrList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(13))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, hpList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(14))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, jkList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(15))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, jhList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(16))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, kaList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(17))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, klList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(18))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, laList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(19))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, ldList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(20))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, mpList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(21))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, mhList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(22))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, mnList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(23))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, mlList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(24))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, mzList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(25))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, nlList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(26))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, odList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(27))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, pyList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(28))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, pbList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(29))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, rjList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(30))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, skList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(31))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, tnList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(32))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, tsList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(33))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, trList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(34))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, ukList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(35))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, upList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(36))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(ServiceProviderDashboardActivity.this, wbList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getStateBidsDrop(ArrayList<LoadNotificationModel> loadListToCompare) {
        anListD.clear();
        apListD.clear();
        arListD.clear();
        asListD.clear();
        brListD.clear();
        chListD.clear();
        cgListD.clear();
        ddListD.clear();
        dd2ListD.clear();
        dlListD.clear();
        gaListD.clear();
        gjListD.clear();
        hrListD.clear();
        hpListD.clear();
        jkListD.clear();
        jhListD.clear();
        kaListD.clear();
        klListD.clear();
        laListD.clear();
        ldListD.clear();
        mpListD.clear();
        mhListD.clear();
        mnListD.clear();
        mlListD.clear();
        mzListD.clear();
        nlListD.clear();
        odListD.clear();
        pyListD.clear();
        pbListD.clear();
        rjListD.clear();
        skListD.clear();
        tnListD.clear();
        tsListD.clear();
        trListD.clear();
        ukListD.clear();
        upListD.clear();
        wbListD.clear();

        for (int i = 0; i < loadListToCompare.size(); i++) {
            if (loadListToCompare.get(i).getDrop_state().equals("AN")) {
                anListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("AP")) {
                apListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("AR")) {
                arListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("AS")) {
                asListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("BR")) {
                brListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("CH/PB")) {
                chListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("CG")) {
                cgListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("DD")) {
                ddListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("DD2")) {
                dd2ListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("DL")) {
                dlListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("GA")) {
                gaListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("GJ")) {
                gjListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("HR")) {
                hrListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("HP")) {
                hpListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("JK")) {
                jkListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("JH")) {
                jhListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("KA")) {
                kaListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("KL")) {
                klListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("LA")) {
                laListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("LD")) {
                ldListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MP")) {
                mpListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MH")) {
                mhListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MN")) {
                mnListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("ML")) {
                mlListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MZ")) {
                mzListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("NL")) {
                nlListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("OD")) {
                odListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("PY")) {
                pyListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("PB")) {
                pbListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("RJ")) {
                rjListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("SK")) {
                skListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("TN")) {
                tnListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("TS")) {
                tsListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("TR")) {
                trListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("UK")) {
                ukListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("UP")) {
                upListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("WB")) {
                wbListD.add(loadListToCompare.get(i));
            }
        }
    }


    private void getStateBidsPick(ArrayList<LoadNotificationModel> loadListToCompare) {
        anList.clear();
        apList.clear();
        arList.clear();
        asList.clear();
        brList.clear();
        chList.clear();
        cgList.clear();
        ddList.clear();
        dd2List.clear();
        dlList.clear();
        gaList.clear();
        gjList.clear();
        hrList.clear();
        hpList.clear();
        jkList.clear();
        jhList.clear();
        kaList.clear();
        klList.clear();
        laList.clear();
        ldList.clear();
        mpList.clear();
        mhList.clear();
        mnList.clear();
        mlList.clear();
        mzList.clear();
        nlList.clear();
        odList.clear();
        pyList.clear();
        pbList.clear();
        rjList.clear();
        skList.clear();
        tnList.clear();
        tsList.clear();
        trList.clear();
        ukList.clear();
        upList.clear();
        wbList.clear();


        for (int i = 0; i < loadListToCompare.size(); i++) {
            if (loadListToCompare.get(i).getPick_state().equals("AN")) {
                anList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("AP")) {
                apList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("AR")) {
                arList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("AS")) {
                asList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("BR")) {
                brList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("CH/PB")) {
                chList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("CG")) {
                cgList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("DD")) {
                ddList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("DD2")) {
                dd2List.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("DL")) {
                dlList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("GA")) {
                gaList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("GJ")) {
                gjList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("HR")) {
                hrList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("HP")) {
                hpList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("JK")) {
                jkList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("JH")) {
                jhList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("KA")) {
                kaList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("KL")) {
                klList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("LA")) {
                laList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("LD")) {
                ldList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MP")) {
                mpList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MH")) {
                mhList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MN")) {
                mnList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("ML")) {
                mlList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MZ")) {
                mzList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("NL")) {
                nlList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("OD")) {
                odList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("PY")) {
                pyList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("PB")) {
                pbList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("RJ")) {
                rjList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("SK")) {
                skList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("TN")) {
                tnList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("TS")) {
                tsList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("TR")) {
                trList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("UK")) {
                ukList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("UP")) {
                upList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("WB")) {
                wbList.add(loadListToCompare.get(i));
            }
        }
    }
}