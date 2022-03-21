package com.nlpl.ui.ui.activities;

import static com.nlpl.R.drawable.blue_profile_small;
import static com.nlpl.R.drawable.driver;
import static com.nlpl.R.drawable.find;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
import android.app.PendingIntent;
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
import android.os.Message;
import android.provider.ContactsContract;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidSubmittedModel;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.model.UpdateMethods.UpdateBidDetails;
import com.nlpl.model.UpdateMethods.UpdateDriverDetails;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.model.UpdateMethods.UpdateTruckDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.services.AddTruckService;
import com.nlpl.ui.ui.adapters.DriversListAdapter;
import com.nlpl.ui.ui.adapters.DriversListAdapterBid;
import com.nlpl.ui.ui.adapters.LoadNotificationAdapter;
import com.nlpl.ui.ui.adapters.LoadSubmittedAdapter;
import com.nlpl.ui.ui.adapters.TrucksListAdapter;
import com.nlpl.ui.ui.adapters.TrucksListAdapterBid;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.FooThread;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

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

public class ServiceProviderDashboardActivity extends AppCompatActivity {

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

    View actionBar, loadNotificationUnderline, bidSubmittedUnderline;
    TextView customerNumber, customerNumberHeading, customerName, customerNameHeading, customerFirstBudget, customerSecondBudget, cancel2, cancel, acceptAndBid, spQuote, selectDriver, selectTruck, selectedTruckModel, selectedTruckCapacity, actionBarTitle;
    EditText notesSp;
    CheckBox declaration;
    RadioButton negotiable_yes, negotiable_no;
    Boolean isLoadNotificationSelected, loadNotificationSelected, profileAdded, isTruckSelectedToBid = false, negotiable = null, isNegotiableSelected = false, fromAdapter = false;
    ImageView actionBarBackButton, actionBarMenuButton, profilePic;

    Dialog menuDialog, previewDialogProfile;
    ConstraintLayout drawerLayout;
    TextView timeLeft00, timeLeftTextview, partitionTextview, menuUserNameTextView, mobileText, personalDetailsButton, bankDetailsTextView, addTrucksTextView;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView, truckDetailsLogoImageView, driverDetailsLogoImageView, arrowImage, actionBarWhatsApp;

    ConstraintLayout loadNotificationConstrain, bidsSubmittedConstrain;
    TextView loadNotificationTextView, bidsSubmittedTextView, currentLocationText;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String loadId, selectedDriverId, selectedDriverName, userId, userIdAPI, phone, mobileNoAPI, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayDriverMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

    String mobile, name, address, pinCode, city, role, emailIdAPI;

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

        loadNotificationConstrain = (ConstraintLayout) findViewById(R.id.dashboard_load_notification_constrain);
        bidsSubmittedConstrain = (ConstraintLayout) findViewById(R.id.dashboard_bids_submitted_constrain);
        loadNotificationTextView = (TextView) findViewById(R.id.dashboard_load_notification_button);
        bidsSubmittedTextView = (TextView) findViewById(R.id.dashboard_bids_submitted_button);
        loadNotificationUnderline = (View) findViewById(R.id.dashboard_load_notification_view);
        bidSubmittedUnderline = (View) findViewById(R.id.dashboard_bids_submitted_view);

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
        currentLocationText = (TextView) findViewById(R.id.dashboard_current_location_text_view);

        actionBar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);
        actionBarWhatsApp = (ImageView) actionBar.findViewById(R.id.action_bar_whats_app);
        actionBarWhatsApp.setVisibility(View.VISIBLE);

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
        profileImageView.setImageDrawable(getDrawable(R.drawable.bottom_nav_search_small));

        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayDriverMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();

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

        dialogSelectTruckTitle.setText("Select Truck");
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

        previewSpinnerTitle.setText("Select Driver");
        previewSpinnerAddTruck.setVisibility(View.GONE);
        previewSpinnerOkButton.setVisibility(View.GONE);

        driverListRecyclerView = dialogSelectDriver.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversListAdapterBid(ServiceProviderDashboardActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);

        //------------------------------------------------------------------------------------------
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

            alertTitle.setText("Please Register");
            alertMessage.setText("You cannot bid without Registration");
            alertPositiveButton.setText("Register Now");
            alertNegativeButton.setText("Cancel");

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

        alertTitle.setText("Log Out");
        alertMessage.setText("Are you sure you want to logout?");
        alertPositiveButton.setText("Yes");
        alertNegativeButton.setText("No");

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
                break;

            case R.id.dashboard_bids_submitted_button:
                loadNotificationSelected = false;
                loadNotificationConstrain.setVisibility(View.INVISIBLE);
                bidsSubmittedConstrain.setVisibility(View.VISIBLE);
                loadNotificationUnderline.setVisibility(View.INVISIBLE);
                bidSubmittedUnderline.setVisibility(View.VISIBLE);
                loadNotificationTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                bidsSubmittedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));

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

                    alertTitle.setText("Please Register");
                    alertMessage.setText("You cannot bid without Registration");
                    alertPositiveButton.setText("Register Now");
                    alertNegativeButton.setText("Cancel");

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
                    JumpTo.goToFindLoadsActivity(ServiceProviderDashboardActivity.this, userId, phone);
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

            alertTitle.setText("Please Register");
            alertMessage.setText("You cannot bid without Registration");
            alertPositiveButton.setText("Register Now");
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
                JumpTo.goToRegistrationActivity(ServiceProviderDashboardActivity.this, phone, true);
            });
            //------------------------------------------------------------------------------------------
        } else {
            if (!isTruckDetailsDone.equals("1")){
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
            }else if (!isDriverDetailsDone.equals("1")){
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
            }else {
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
                loadIdHeading.setText("Load Details: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
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

                            alertTitle.setText("Post Bid");
                            alertMessage.setText("Bid Posted Successfully");
                            alertPositiveButton.setVisibility(View.GONE);
                            alertNegativeButton.setText("OK");
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

                        if (!obj.getString("bid_status").equals("delete") && !obj.getString("bid_status").equals("loadExpired")) {
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

                                    alertTitle.setText("Your Bidget is High");
                                    alertMessage.setText("Your Budget is Higher than the Load Poster");
                                    alertPositiveButton.setVisibility(View.GONE);
                                    alertNegativeButton.setText("OK");
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

                                    alertTitle.setText("Your Bidget is Low");
                                    alertMessage.setText("Your Budget is Lower than the Load Poster");
                                    alertPositiveButton.setVisibility(View.GONE);
                                    alertNegativeButton.setText("OK");
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
                    ShowAlert.showAlert(ServiceProviderDashboardActivity.this, "Enter Appropriate Quote", "You cannot bid less than 50% of customer Budget", true, false, "Ok", "null");
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
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText("Load Details: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
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

                alertTitle.setText("Bid Revised and Responded");
                alertMessage.setText("Bid Revised and Responded Successfully");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
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
        loadIdHeading.setText("Load Details: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_select_driver_textview);
        selectedTruckModel = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckCapacity = (TextView) dialogViewConsignment.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
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
        cancel2.setBackgroundTintList(getResources().getColorStateList(R.color.light_black));

        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RearrangeItems();
            }
        });

        notesSp.setVisibility(View.GONE);
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

        getBidDetailsByBidId(obj.getBidId(), false);
        getCustomerNameAndNumber(obj.getUser_id());

        cancel.setText("Withdraw");
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

                alertTitle.setText("Withdraw Bid");
                alertMessage.setText("Do you really want to withdraw bid.");
                alertPositiveButton.setVisibility(View.VISIBLE);
                alertNegativeButton.setText("Cancel");
                alertPositiveButton.setText("Withdraw");
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

                        alertTitle.setText("Withdrawn Bid");
                        alertMessage.setText("Bid is withdrawn successfully. Customer will no longer see your Bid.");
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText("Ok");
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

        acceptAndBid.setText("Start Trip");
        acceptAndBid.setEnabled(false);
        acceptAndBid.setBackgroundResource((R.drawable.button_de_active));

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                alertTitle.setText("Trip Started Successfully");
                alertMessage.setText("You can track your trip in track section");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
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
        if (obj.getTruck_type().toLowerCase().equals(required_truck_body.toLowerCase()) && obj.getTruck_carrying_capacity().toLowerCase().equals(required_capacity.toLowerCase())){
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
        }else{
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

            alertTitle.setText("Truck doesn't match");
            alertMessage.setText("The Truck assigned doesn't match Load Poster requirements."
                    +"\n\nI hereby declare, will provide appropriate Truck matching Load Poster requirement.");
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

                            currentLocationText.setText(addressCurrent);
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

        alertTitle.setText("Profile Picture");
        alertMessage.setText("Profile Picture added successfully");
        alertPositiveButton.setVisibility(View.GONE);
        alertNegativeButton.setText("OK");
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

            alertTitle.setText("Personal Details");
            alertMessage.setText("Profile Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
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

                alertTitle.setText("Personal Details");
                alertMessage.setText("Profile Uploaded Successfully");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
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

                alertTitle.setText("Personal Details");
                alertMessage.setText("Profile not Uploaded, please try again");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
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
        galleryText.setText("Call");

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
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"+91"+mobileNumber + "&text="+message));
                    startActivity(intent);
                }catch (Exception e){
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
}