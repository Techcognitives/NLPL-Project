package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
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
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Responses.BidLadResponse;
import com.nlpl.model.UpdateBidStatusAccepted;
import com.nlpl.model.UpdateBidStatusRespondedBySP;
import com.nlpl.model.UpdateLoadStatusSubmitted;
import com.nlpl.model.UpdateSPQuoteFinal;
import com.nlpl.services.BidLoadService;
import com.nlpl.services.PostLoadService;
import com.nlpl.ui.ui.adapters.BidsAcceptedAdapter;
import com.nlpl.ui.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.ui.adapters.BidsResponsesAdapter;
import com.nlpl.ui.ui.adapters.LoadNotificationAdapter;
import com.nlpl.ui.ui.adapters.LoadSubmittedAdapter;
import com.nlpl.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    FusedLocationProviderClient fusedLocationProviderClient;

    private RequestQueue mQueue;
    private PostLoadService postLoadService;
    private BidLoadService bidService;

    private ArrayList<LoadNotificationModel> loadList = new ArrayList<>();
    private ArrayList<LoadNotificationModel> loadListToCompare = new ArrayList<>();
    private ArrayList<LoadNotificationModel> bidStatusLoadListToCompare = new ArrayList<>();


    private ArrayList<BidSubmittedModel> loadSubmittedList = new ArrayList<>();
    private ArrayList<BidSubmittedModel> updatedLoadSubmittedList = new ArrayList<>();

    private LoadNotificationAdapter loadListAdapter;
    private LoadSubmittedAdapter loadSubmittedAdapter;
    private RecyclerView loadListRecyclerView, loadSubmittedRecyclerView;

    Dialog setBudget, selectTruckDialog, previewDialogBidNow, dialogAcceptRevisedBid, dialogViewConsignment;

    String spQuoteOnClickBidNow, bidStatusToCompare, bidStatus, vehicle_no, truckId, isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    SwipeListener swipeListener;

    View actionBar;
    TextView customerNumber, customerNumberHeading, customerName, customerNameHeading, customerFirstBudget, customerSecondBudget, cancel, acceptAndBid, spQuote, addDriver, selectDriver, addTruck, selectTruck, selectedTruckModel, selectedTruckFeet, selectedTruckCapacity, selectedTruckBodyType, actionBarTitle;
    EditText notesSp;
    CheckBox declaration;
    RadioButton negotiable_yes, negotiable_no;
    Boolean isTruckSelectedToBid = false, negotiable = null, isNegotiableSelected = false, fromAdapter = false;
    ImageView actionBarBackButton, actionBarMenuButton;

    Dialog menuDialog;
    ConstraintLayout drawerLayout;
    TextView timeLeft00, timeLeftTextview, partitionTextview, menuUserNameTextView, mobileText, personalDetailsButton, bankDetailsTextView, addTrucksTextView;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView, truckDetailsLogoImageView, driverDetailsLogoImageView;

    ConstraintLayout loadNotificationConstrain, bidsSubmittedConstrain;
    TextView loadNotificationTextView, bidsSubmittedTextView, currentLocationText;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String loadId, selectedDriverId, selectedDriverName, userId, userIdAPI, phone, mobileNoAPI;
    ArrayList<String> arrayBidStatus, arrayUserId, arrayTruckId, arrayDriverId, arrayDriverName, arrayTruckList, arrayMobileNo, arrayDriverMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

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
        arrayBidStatus = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bidService = retrofit.create(BidLoadService.class);
        postLoadService = retrofit.create(PostLoadService.class);

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
        loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadList);
        loadListRecyclerView.setAdapter(loadListAdapter);
        loadSubmittedAdapter = new LoadSubmittedAdapter(DashboardActivity.this, updatedLoadSubmittedList);
        loadSubmittedRecyclerView.setAdapter(loadSubmittedAdapter);
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
                    linearLayoutManagerBank.setReverseLayout(false);
                    loadListRecyclerView.setLayoutManager(linearLayoutManagerBank);
                    loadListRecyclerView.setHasFixedSize(true);

                    LinearLayoutManager linearLayoutManagerBank1 = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManagerBank1.setReverseLayout(false);
                    loadSubmittedRecyclerView.setLayoutManager(linearLayoutManagerBank1);
                    loadSubmittedRecyclerView.setHasFixedSize(true);

                    loadSubmittedAdapter = new LoadSubmittedAdapter(DashboardActivity.this, updatedLoadSubmittedList);
                    loadSubmittedRecyclerView.setAdapter(loadSubmittedAdapter);
                    loadSubmittedRecyclerView.scrollToPosition(loadSubmittedAdapter.getItemCount() - 1);

                    //------------------------------------------------------------------------------------------
                    RearrangeItems();

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
                        menuUserNameTextView.setText(name);
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
                    intent2.putExtra("fromBidNow",false);
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
                    intent4.putExtra("fromBidNow",false);
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
                Intent intent = new Intent(DashboardActivity.this, FindLoadsActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                startActivity(intent);
                break;
        }
    }

    private void compareAndRemove(ArrayList<LoadNotificationModel> loadListToCompare, ArrayList<String> arrayBidStatus) {

        Log.i("Load list", String.valueOf(loadListToCompare.size()));
        Log.i("array bidStatus", String.valueOf(arrayBidStatus.size()));

//        for (int i = 0; i < loadListToCompare.size(); i++) {
//            for (int j = 0; j < updatedLoadSubmittedList.size(); j++) {
//                if (loadListToCompare.get(i).getIdpost_load().equals(updatedLoadSubmittedList.get(j).getIdpost_load())) {
//                    loadListToCompare.remove(i);
//                    arrayBidStatus.remove(j);
//                }
//            }
//        }

        loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadListToCompare);
        loadListRecyclerView.setAdapter(loadListAdapter);
        loadListRecyclerView.scrollToPosition(loadListAdapter.getItemCount() - 1);

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

                        bidStatusToCompare = obj.getString("bid_status");
                        arrayBidStatus.add(bidStatusToCompare);
                        loadList.add(modelLoadNotification);
                    }
                    TextView noLoadAvailable = (TextView) findViewById(R.id.dashboard_load_here_text);
                    if (loadList.size() > 0) {
                        noLoadAvailable.setVisibility(View.GONE);

//                        for (int i=0; i<loadList.size(); i++){
//                            for (int j=0; j<arrayBidStatus.size(); j++){
//                                if (arrayBidStatus.get(j).equals("submitted")){
//                                    loadList.remove(i);
//                                    arrayBidStatus.remove(j);
//                                }
//                            }
//                        }
//                        loadListAdapter.updateData(loadList);
                    }else{
                        noLoadAvailable.setVisibility(View.VISIBLE);
                    }

                    getBidListByUserId(loadList, arrayBidStatus);

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
                overridePendingTransition(0, 0);
                finish();
            }
        });

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {

                    if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString())){
                        Log.i("status send as", "RespondedBySP");
                        saveBid(createBidRequest("RespondedBySP",spQuote.getText().toString()));
                    } else {
                        Log.i("status send as", "submitted");
                        saveBid(createBidRequest("submitted",""));
                    }

                    Log.i("loadId bidded", obj.getIdpost_load());

                    AlertDialog.Builder my_alert = new AlertDialog.Builder(DashboardActivity.this).setCancelable(false);
                    my_alert.setTitle("Bid Posted Successfully");
                    my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                            i8.putExtra("mobile2", phone);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            finish();

                            dialogInterface.dismiss();
                            previewDialogBidNow.dismiss();
                        }
                    });
                    my_alert.show();

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
                intent3.putExtra("fromBidNow",true);
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
                i8.putExtra("fromBidNow",true);
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
                    if (arrayDriverName.size()==0){
                         AlertDialog.Builder my_alert = new AlertDialog.Builder(DashboardActivity.this);
                        my_alert.setTitle("Add a Driver");
                        my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent i8 = new Intent(DashboardActivity.this, DriverDetailsActivity.class);
                                i8.putExtra("userId", userId);
                                i8.putExtra("isEdit", false);
                                i8.putExtra("fromBidNow",true);
                                i8.putExtra("mobile", mobile);
                                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i8);
                                overridePendingTransition(0, 0);
                            }
                        });
                        my_alert.show();
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

                        getDriverDetailsByDriverId(selectedDriverId);

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

    private void getBidListByUserId(ArrayList<LoadNotificationModel> loadListToCompare, ArrayList<String> arrayBidStatus) {

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
                        getBidSubmittedList(postId, bidId, loadListToCompare, arrayBidStatus);
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

    public void getBidSubmittedList(String loadIdReceived, String bidId, ArrayList<LoadNotificationModel> loadListToCompare, ArrayList<String> arrayBidStatus) {
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
                        bidSubmittedModel.setBidId(bidId);

                        loadSubmittedList.add(bidSubmittedModel);
                    }

                    TextView noBidsSubmittedTextView = (TextView) findViewById(R.id.dashboard_no_bids_submitted_text);
                    if (loadSubmittedList.size() > 0) {
                        updatedLoadSubmittedList.addAll(loadSubmittedList);
                        loadSubmittedAdapter.updateData(updatedLoadSubmittedList);
                        noBidsSubmittedTextView.setVisibility(View.GONE);
                        compareAndRemove(loadListToCompare, arrayBidStatus);
                    }else{
                        loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadList);
                        loadListRecyclerView.setAdapter(loadListAdapter);
                        loadListRecyclerView.scrollToPosition(loadListAdapter.getItemCount() - 1);

                        if (loadList.size() > 0) {
                            loadListAdapter.updateData(loadListToCompare);
                        }

                        noBidsSubmittedTextView.setVisibility(View.VISIBLE);
                    }
//
//                    else {
//                        loadListAdapter = new LoadNotificationAdapter(DashboardActivity.this, loadListToCompare);
//                        loadListRecyclerView.setAdapter(loadListAdapter);
//                        loadListRecyclerView.scrollToPosition(loadListAdapter.getItemCount() - 1);
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

        budget.setText(previousBudget);

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

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {
                    spQuote.setText(budgetEditText);

                    if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString())){
                        spQuote.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        spQuote.setTextColor(getResources().getColor(R.color.redDark));
                    }


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
                    if (arrayTruckId.size()==0){
                        AlertDialog.Builder my_alert = new AlertDialog.Builder(DashboardActivity.this);
                        my_alert.setTitle("Add a Truck");
                        my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent3 = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
                                intent3.putExtra("userId", userId);
                                intent3.putExtra("isEdit", false);
                                intent3.putExtra("fromBidNow",true);
                                intent3.putExtra("mobile", phone);
                                startActivity(intent3);
                            }
                        });
                        my_alert.show();

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

        cancel.setEnabled(true);
        cancel.setBackgroundResource((R.drawable.button_active));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                i8.putExtra("mobile2", phone);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
                finish();
                previewDialogBidNow.dismiss();
            }
        });

        acceptAndBid.setEnabled(true);
        acceptAndBid.setBackgroundResource((R.drawable.button_active));

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateBidStatusRespondedBySP(obj.getBidId());
                updateSPQuoteFinal(obj.getBidId(), spQuote.getText().toString());

                AlertDialog.Builder my_alert = new AlertDialog.Builder(DashboardActivity.this);
                my_alert.setTitle("Bid Revised and Responded Successfully");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                        i8.putExtra("mobile2", phone);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        finish();

                        dialogInterface.dismiss();
                        previewDialogBidNow.dismiss();
                    }
                });
                my_alert.show();

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
                intent3.putExtra("fromBidNow",true);
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
                i8.putExtra("fromBidNow",true);
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
                overridePendingTransition(0, 0);
                finish();
                dialogViewConsignment.dismiss();
            }
        });

        acceptAndBid.setText("Start Trip");
        acceptAndBid.setEnabled(false);
        acceptAndBid.setBackgroundResource((R.drawable.button_de_active));

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder my_alert = new AlertDialog.Builder(DashboardActivity.this);
                my_alert.setTitle("Trip Started Successfully");
                my_alert.setMessage("You can track your trip in track section");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent i8 = new Intent(DashboardActivity.this, DashboardActivity.class);
                        i8.putExtra("mobile2", phone);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        finish();

                        dialogInterface.dismiss();
                        dialogViewConsignment.dismiss();
                    }
                });
                my_alert.show();

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

        Call<UpdateBidStatusRespondedBySP> call = bidService.updateBidStatusRespondedBySP("" + bidId, updateBidStatusRespondedBySP);

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
    private void updateSPQuoteFinal(String bidId, String spQuote) {

        UpdateSPQuoteFinal updateSPQuoteFinal = new UpdateSPQuoteFinal(spQuote);

        Call<UpdateSPQuoteFinal> call = bidService.updateSPQuoteFinal("" + bidId, updateSPQuoteFinal);

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

}