package com.nlpl.ui.activities;


import static com.nlpl.R.drawable.blue_profile_small;
import static java.lang.Float.parseFloat;


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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.firebase.auth.FirebaseAuth;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsAcceptedModel;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;

import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateBidDetails;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.ui.adapters.BidsAcceptedAdapter;
import com.nlpl.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.adapters.BidsResponsesAdapter;
import com.nlpl.ui.adapters.TrackLPBidsResponsesAdapter;
import com.nlpl.ui.adapters.TrackLPTripAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.CreateUser;
import com.nlpl.utils.DisplayTrack;
import com.nlpl.utils.DownloadImageTask;

import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.FooThread;
import com.nlpl.utils.GetUserDetails;

import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;
import com.razorpay.Checkout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class TrackForLoadPosterActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mQueue;

    private ArrayList<BidsAcceptedModel> acceptedList;
    private TrackLPTripAdapter bidsAcceptedAdapter;
    private RecyclerView bidsAcceptedRecyclerView;

    private TrackLPBidsResponsesAdapter bidsResponsesAdapter;
    boolean fabVisible = true, isBackPressed = false;

    private int CAMERA_PIC_REQUEST2 = 4;
    private int GET_FROM_GALLERY2 = 5;

    Dialog loadingDialog;
    TextView spNumber, driverNumber;
    ImageView actionBarWhatsApp;
    Dialog previewDialogProfileOfSp;
    Boolean checkedReasonOne = false, checkedReasonTwo = false, checkedReasonThree = false, checkedReasonFour = false, checkedReasonFive = false, checkedReasonSix = false, checkedReasonSeven = false;

    String profileImgUrlForRating, reasonForLowRate = "";
    float ratingGiven = 0;
    int count = 0;
    String img_type, paymentTypeFromAPI, numberOfBids, paymentMethod = "", paymentPercentage = "threePercent";

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    Dialog previewDialogAcceptANdBid, setBudget, acceptFinalBid, viewConsignmentCustomer;
    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    TextView quoteBySp1, timeLeftTextview, timeLeft00, customerQuote, submitResponseBtn, cancleBtn, noTrips;
    RadioButton negotiable_yes, negotiable_no;
    EditText notesCustomer;
    String userId, phone, s1, customerEmail;
    String spQuoteByApi, bid_idByAPI, noteByApi, vehicleModelByApi, vehicleFeetByApi, VehicleCapacityByApi, VehicleTypeByApi;

    ArrayList<String> arrayAssignedDriverId, arrayBidId, arrayUserId, arrayBidStatus, arrayNotesFromSP;
    String fianlBidId, noteBySPToCustomer, assignedDriverId, assignedDriverIdAPI, assignedUserId, assignedUserIdAPI, bidStatusAPI, customerNameAPI;
    String loadIdForUpdate, spBidIdForUpdate, noteForUpdate, quoteForUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_for_load_poster);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
        }

        mQueue = Volley.newRequestQueue(TrackForLoadPosterActivity.this);
        getUserId(phone);

        loadingDialog = new Dialog(TrackForLoadPosterActivity.this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;

        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);
        noTrips = findViewById(R.id.find_trips_no_trips);

//        loadingDialog.show();
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(TrackForLoadPosterActivity.this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });
//--------------------------- action bar -----------------------------------------------------------
        actionBar = findViewById(R.id.customer_dashboard_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);
        actionBarWhatsApp = (ImageView) actionBar.findViewById(R.id.action_bar_whats_app);
        actionBarWhatsApp.setVisibility(View.VISIBLE);

        actionBarTitle.setText(getString(R.string.Trip_Details));
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(view -> this.finish());
        //------------------------------------------------------------------------------------------

        previewDialogProfileOfSp = new Dialog(TrackForLoadPosterActivity.this);
        previewDialogProfileOfSp.setContentView(R.layout.dialog_preview_images);
        previewDialogProfileOfSp.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        //------------------------------------------------------------------------------------------
        //------------------------------- bottom nav -----------------------------------------------
        bottomNav = (View) findViewById(R.id.customer_dashboard_bottom_nav_bar);
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        profileText.setText(getString(R.string.Trucks));
        View dashboardView = findViewById(R.id.bottom_nav_bar_dashboard_underline);
        dashboardView.setVisibility(View.INVISIBLE);
        ConstraintLayout dashboardConstrain = findViewById(R.id.bottom_nav_sp_dashboard);
        dashboardConstrain.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        ConstraintLayout trackConstrain = findViewById(R.id.bottom_nav_track);
        trackConstrain.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        View trackView = findViewById(R.id.bottom_nav_bar_track_underline);
        trackView.setVisibility(View.VISIBLE);

        acceptedList = new ArrayList<>();
        arrayAssignedDriverId = new ArrayList<>();
        arrayUserId = new ArrayList<>();
        arrayBidId = new ArrayList<>();
        arrayBidStatus = new ArrayList<>();
        arrayNotesFromSP = new ArrayList<>();

        //---------------------------- Get Accepted Load Details -----------------------------------
        bidsAcceptedRecyclerView = (RecyclerView) findViewById(R.id.customer_dashboard_loads_accepted_recycler_view);

        LinearLayoutManager linearLayoutManagerAccepted = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerAccepted.setReverseLayout(false);
        linearLayoutManagerAccepted.setOrientation(LinearLayoutManager.VERTICAL);
        bidsAcceptedRecyclerView.setLayoutManager(linearLayoutManagerAccepted);
        bidsAcceptedRecyclerView.setHasFixedSize(true);

        bidsAcceptedAdapter = new TrackLPTripAdapter(TrackForLoadPosterActivity.this, acceptedList);
        bidsAcceptedRecyclerView.setAdapter(bidsAcceptedAdapter);
        //------------------------------------------------------------------------------------------

        previewDialogAcceptANdBid = new Dialog(TrackForLoadPosterActivity.this);
        previewDialogAcceptANdBid.setContentView(R.layout.dialog_acept_bid_customer);
        previewDialogAcceptANdBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        acceptFinalBid = new Dialog(TrackForLoadPosterActivity.this);
        acceptFinalBid.setContentView(R.layout.dialog_acept_bid_customer);
        acceptFinalBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewConsignmentCustomer = new Dialog(TrackForLoadPosterActivity.this);
        viewConsignmentCustomer.setContentView(R.layout.dialog_acept_bid_customer);
        viewConsignmentCustomer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void getUserId(String userMobileNumber) {
        ArrayList<String> arrayUserId = new ArrayList<>(), arrayMobileNo = new ArrayList<>(), arrayCustomerName = new ArrayList<>(), arrayCustomerEmail = new ArrayList<>(), isPersonalD = new ArrayList<>(), isProfileArray = new ArrayList<>(), isBankD = new ArrayList<>();
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
                        String userIdAPI = data.getString("user_id");
                        arrayUserId.add(userIdAPI);
                        String mobileNoAPI = data.getString("phone_number");
                        arrayMobileNo.add(mobileNoAPI);
                        String userName = data.getString("name");
                        arrayCustomerName.add(userName);
                        String emailAPI = data.getString("email_id");
                        arrayCustomerEmail.add(emailAPI);

                        String isPer = data.getString("isPersonal_dt_added");
                        isPersonalD.add(isPer);
                        isProfileArray.add(data.getString("isProfile_pic_added"));
                        String isBank = data.getString("isBankDetails_given");
                        isBankD.add(isBank);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(userMobileNumber)) {
                            userId = arrayUserId.get(j);
                            customerNameAPI = arrayCustomerName.get(j);

                            String customerNumberAPI = arrayMobileNo.get(j);
                            s1 = customerNumberAPI.substring(2, 12);

                            customerEmail = arrayCustomerEmail.get(j);

                        }
                    }

                    getBidsAccepted();

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

    public void RearrangeItems() {
        ShowAlert.loadingDialog(TrackForLoadPosterActivity.this);
        JumpTo.goToLPTrackActivity(TrackForLoadPosterActivity.this, phone, true);
    }

    public void getBidsAccepted() {
        //---------------------------- Get Bank Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray acceptedLoadList = response.getJSONArray("data");
                    for (int i = 0; i < acceptedLoadList.length(); i++) {

                        JSONObject obj = acceptedLoadList.getJSONObject(i);
                        BidsAcceptedModel bidsAcceptedModel = new BidsAcceptedModel();
                        bidsAcceptedModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsAcceptedModel.setUser_id(obj.getString("user_id"));
                        bidsAcceptedModel.setPick_up_date(obj.getString("pick_up_date"));
                        bidsAcceptedModel.setPick_up_time(obj.getString("pick_up_time"));
                        bidsAcceptedModel.setBudget(obj.getString("budget"));
                        bidsAcceptedModel.setBid_status(obj.getString("bid_status"));
                        bidsAcceptedModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidsAcceptedModel.setFeet(obj.getString("feet"));
                        bidsAcceptedModel.setCapacity(obj.getString("capacity"));
                        bidsAcceptedModel.setBody_type(obj.getString("body_type"));
                        bidsAcceptedModel.setPick_add(obj.getString("pick_add"));
                        bidsAcceptedModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        bidsAcceptedModel.setPick_city(obj.getString("pick_city"));
                        bidsAcceptedModel.setPick_state(obj.getString("pick_state"));
                        bidsAcceptedModel.setPick_country(obj.getString("pick_country"));
                        bidsAcceptedModel.setDrop_add(obj.getString("drop_add"));
                        bidsAcceptedModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        bidsAcceptedModel.setDrop_city(obj.getString("drop_city"));
                        bidsAcceptedModel.setDrop_state(obj.getString("drop_state"));
                        bidsAcceptedModel.setDrop_country(obj.getString("drop_country"));
                        bidsAcceptedModel.setKm_approx(obj.getString("km_approx"));
                        bidsAcceptedModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidsAcceptedModel.setBid_ends_at(obj.getString("bid_ends_at"));

                        if (obj.getString("bid_status").equals("start")) {
                            acceptedList.add(bidsAcceptedModel);
                        }
                    }

                    FooThread fooThread = new FooThread(handler);
                    fooThread.start();


//                    for (int i=0; i< acceptedList.size(); i++){
//                        if (acceptedList.get(i).getBid_status().equals("FinalAccepted")){
                    if (acceptedList.size() > 0) {
//                        bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                        bidsAcceptedAdapter.updateData(acceptedList);
                    } else {
                        noTrips.setVisibility(View.VISIBLE);
                    }
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

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(TrackForLoadPosterActivity.this);
                JumpTo.goToCustomerDashboard(TrackForLoadPosterActivity.this, phone, true);
                break;

            case R.id.bottom_nav_customer_dashboard:
                ShowAlert.loadingDialog(TrackForLoadPosterActivity.this);
                JumpTo.goToFindTrucksActivity(TrackForLoadPosterActivity.this, userId, phone);
                break;

            case R.id.bottom_nav_track:
                RearrangeItems();
                break;

            case R.id.bottom_nav_trip:
                ShowAlert.loadingDialog(TrackForLoadPosterActivity.this);
                JumpTo.goToFindTripLPActivity(TrackForLoadPosterActivity.this, userId, phone, true);
                break;
        }
    }


    public void onClickViewConsignment(BidsAcceptedModel obj) {
        Log.i("Message Object", obj.getIdpost_load());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(viewConsignmentCustomer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        viewConsignmentCustomer.show();
        viewConsignmentCustomer.setCancelable(true);
        viewConsignmentCustomer.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView nameSP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bit_service_provider_name);
        TextView capacityBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_capacity_textview);
        TextView bodyTypeBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_body_type_textview);
        TextView quoteBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_bidder_quote_textview);
        TextView negotiableBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_negotiable_textview);
        TextView notesBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_received_notes_textview);
        RatingBar spRatings = (RatingBar) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_customer_sp_rate);
        TextView spRatingInInt = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        spNumber = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_sp_number);
        TextView companyNameHeading = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_company_name_heading);
        TextView companyName = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_company_name);
        TextView driverNameHeading = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_driver_name_heading);
        TextView driverName = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_driver_name);
        driverNumber = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_driver_number);
        TextView tripToFrom = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_bid_now_loadId_heading);
        tripToFrom.setText(getString(R.string.Load_Details) + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");

        TextView rateSp = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_rate_sp);
        TextView rateDriver = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_rate_driver);

        spRatings.setVisibility(View.VISIBLE);
        spRatingInInt.setVisibility(View.VISIBLE);
        nameSP.setVisibility(View.VISIBLE);
        rateSp.setVisibility(View.VISIBLE);
        rateDriver.setVisibility(View.GONE);

        spNumber.setVisibility(View.VISIBLE);
        driverName.setVisibility(View.VISIBLE);
        driverNumber.setVisibility(View.VISIBLE);
        driverNameHeading.setVisibility(View.VISIBLE);

        String url1 = getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
        Log.i("URL: ", url1);

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidResponsesLists = response.getJSONArray("data");
                    for (int i = 0; i < bidResponsesLists.length(); i++) {
                        JSONObject obj = bidResponsesLists.getJSONObject(i);
                        noteByApi = obj.getString("notes");
                        bid_idByAPI = obj.getString("sp_bid_id");
                        assignedDriverIdAPI = obj.getString("assigned_driver_id");
                        assignedUserIdAPI = obj.getString("user_id");
                        bidStatusAPI = obj.getString("bid_status");

                        arrayBidId.add(bid_idByAPI);
                        arrayUserId.add(assignedUserIdAPI);
                        arrayAssignedDriverId.add(assignedDriverIdAPI);
                        arrayBidStatus.add(bidStatusAPI);
                        arrayNotesFromSP.add(noteByApi);
                    }

                    for (int j = 0; j < arrayBidStatus.size(); j++) {
                        if (arrayBidStatus.get(j).equals("start")) {
                            fianlBidId = arrayBidId.get(j);
                            assignedUserId = arrayUserId.get(j);
                            assignedDriverId = arrayAssignedDriverId.get(j);
                            noteBySPToCustomer = arrayNotesFromSP.get(j);
                        }

                        //--------------------------------------------------------------------------
                        String url4 = getString(R.string.baseURL) + "/spbid/bidDtByBidId/" + fianlBidId;
                        JsonObjectRequest request4 = new JsonObjectRequest(Request.Method.GET, url4, null, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray truckLists = response.getJSONArray("data");
                                    for (int i = 0; i < truckLists.length(); i++) {
                                        JSONObject obj = truckLists.getJSONObject(i);
                                        spQuoteByApi = obj.getString("sp_quote");
                                        customerQuote.setText(spQuoteByApi);
                                        quoteBySP.setText(spQuoteByApi);
                                        capacityBySP.setText(obj.getString("capacity"));
                                        bodyTypeBySP.setText(obj.getString("body_type"));
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
                        mQueue.add(request4);
                        //--------------------------------------------------------------------------
                        GetUserDetails.getRatings(assignedUserId, spRatingInInt);
                        //----------------------------------------------------------
                        String url = getString(R.string.baseURL) + "/user/" + assignedUserId;
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray truckLists = response.getJSONArray("data");
                                    for (int i = 0; i < truckLists.length(); i++) {
                                        JSONObject obj = truckLists.getJSONObject(i);
                                        nameSP.setText(obj.getString("name"));
                                        String spNumberAPI = obj.getString("phone_number").substring(2, 12);
                                        spNumber.setText("+91 " + spNumberAPI);

                                        int isCompAded = obj.getInt("isCompany_added");

                                        if (isCompAded == 1) {
                                            companyName.setVisibility(View.VISIBLE);
                                            companyNameHeading.setVisibility(View.VISIBLE);
                                            //----------------------------------------------------------
                                            String url2 = getString(R.string.baseURL) + "/company/get/" + obj.getString("user_id");
                                            JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null, new com.android.volley.Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        JSONArray truckLists = response.getJSONArray("data");
                                                        for (int i = 0; i < truckLists.length(); i++) {
                                                            JSONObject obj = truckLists.getJSONObject(i);
                                                            companyName.setText(obj.getString("company_name"));
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
                                            mQueue.add(request2);
                                            //----------------------------------------------------------
                                        } else {
                                            companyName.setVisibility(View.GONE);
                                            companyNameHeading.setVisibility(View.GONE);
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

                        //----------------------------------------------------------
                        String url1 = getString(R.string.baseURL) + "/driver/driverId/" + assignedDriverId;
                        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray truckLists = response.getJSONArray("data");
                                    for (int i = 0; i < truckLists.length(); i++) {
                                        JSONObject obj = truckLists.getJSONObject(i);
                                        driverName.setText(obj.getString("driver_name"));
                                        String driverNumberAPI = obj.getString("driver_number").substring(2, 12);
                                        driverNumber.setText("+91 " + driverNumberAPI);
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
                        //----------------------------------------------------------
                    }
                    negotiableBySP.setText(getString(R.string.no));
                    notesBySP.setText(noteBySPToCustomer);
                    //----------------------------------------------------------------------------------------------------------------

                    customerQuote = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_customer_final_quote_textview);
                    negotiable_yes = viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_radio_btn_yes);
                    negotiable_no = viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_radio_btn_no);
                    notesCustomer = (EditText) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_notes_editText);
                    submitResponseBtn = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_submit_response_btn);
                    cancleBtn = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_cancel_btn);
                    timeLeftTextview = viewConsignmentCustomer.findViewById(R.id.accept_bid_time_left_textview);
                    timeLeft00 = viewConsignmentCustomer.findViewById(R.id.accept_bid_time_left_00_textview);
                    TextView noteHeading = viewConsignmentCustomer.findViewById(R.id.notes_text_heading_view_consignment_customer);

                    timeLeftTextview.setText(getString(R.string.CONSIGNMENT));
                    timeLeft00.setVisibility(View.GONE);
                    timeLeftTextview.setTextColor(getResources().getColorStateList(R.color.black));
                    timeLeftTextview.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                    noteHeading.setVisibility(View.GONE);
                    notesCustomer.setVisibility(View.GONE);
                    negotiable_yes.setChecked(false);
                    negotiable_yes.setEnabled(false);
                    negotiable_no.setChecked(true);


                    submitResponseBtn.setText(getString(R.string.Locate_Truck));
                    submitResponseBtn.setBackgroundResource((R.drawable.button_active));
                    submitResponseBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_black));
                    submitResponseBtn.setEnabled(true);

                    submitResponseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DisplayTrack.DisplayTrack(TrackForLoadPosterActivity.this, obj.getPick_add() + " " + obj.getPick_city() + " " + obj.getPick_pin_code(), obj.getDrop_add() + " " + obj.getDrop_city() + " " + obj.getDrop_pin_code());
                        }
                    });

                    cancleBtn.setEnabled(true);
                    cancleBtn.setBackgroundResource((R.drawable.button_active));
                    cancleBtn.setText(getString(R.string.Back));

                    cancleBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RearrangeItems();
                            viewConsignmentCustomer.dismiss();
                        }
                    });
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
        //-------------------------------------------------------------------------------------------
        rateSp.setOnClickListener(view -> rate(assignedUserId, nameSP.getText().toString(), tripToFrom.getText().toString()));
//        rateCustomer.setOnClickListener(view -> rate("", ""));

    }

    private void rate(String userIdForRating, String nameForRating, String transactionId) {
        Log.i("UserId For Rating", userIdForRating);
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(TrackForLoadPosterActivity.this);
        alert.setContentView(R.layout.dialog_rating);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(true);

        ImageView profile = (ImageView) alert.findViewById(R.id.dialog_rating_profile_picture);
        TextView name = (TextView) alert.findViewById(R.id.dialog_rating_user_name);
        RatingBar ratingBar = (RatingBar) alert.findViewById(R.id.dialog_rating_star_rating);
        TextView ratingText = (TextView) alert.findViewById(R.id.dialog_rating_in_int);
        TextView submitButton = (TextView) alert.findViewById(R.id.dialog_rating_submit_button);
        TextView skipButton = (TextView) alert.findViewById(R.id.dialog_rating_no_thanks_button);

        TextView issue, issueTwo, reasonOne, reasonTwo, reasonThree, reasonFour, reasonFive, reasonSix, reasonSeven;
        issue = (TextView) alert.findViewById(R.id.dialog_rating_issue_title);
        issueTwo = (TextView) alert.findViewById(R.id.dialog_rating_title_two);
        reasonOne = (TextView) alert.findViewById(R.id.dialog_rating_reason_one);
        reasonTwo = (TextView) alert.findViewById(R.id.dialog_rating_reason_two);
        reasonThree = (TextView) alert.findViewById(R.id.dialog_rating_reason_three);
        reasonFour = (TextView) alert.findViewById(R.id.dialog_rating_reason_four);
        reasonFive = (TextView) alert.findViewById(R.id.dialog_rating_reason_five);
        reasonSix = (TextView) alert.findViewById(R.id.dialog_rating_reason_six);
        reasonSeven = (TextView) alert.findViewById(R.id.dialog_rating_reason_seven);

        issue.setVisibility(View.GONE);
        issueTwo.setVisibility(View.GONE);
        reasonOne.setVisibility(View.GONE);
        reasonTwo.setVisibility(View.GONE);
        reasonThree.setVisibility(View.GONE);
        reasonFour.setVisibility(View.GONE);
        reasonFive.setVisibility(View.GONE);
        reasonSix.setVisibility(View.GONE);
        reasonSeven.setVisibility(View.GONE);

        name.setText(nameForRating);
        skipButton.setOnClickListener(view -> alert.dismiss());

        submitButton.setOnClickListener(view -> {
            if (ratingGiven == 0) {
                Toast.makeText(this, "Please provide star ratings", Toast.LENGTH_SHORT).show();
            } else if (ratingGiven == 5) {
                CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), "5 Stars", userIdForRating, userId));
                alert.dismiss();
            } else {
                ArrayList<String> reasonList = new ArrayList<>();
                if (checkedReasonOne || checkedReasonTwo || checkedReasonThree || checkedReasonFour || checkedReasonFive || checkedReasonSix || checkedReasonSeven) {
                    if (checkedReasonOne) {
                        reasonList.add(reasonOne.getText().toString());
                    }
                    if (checkedReasonTwo) {
                        reasonList.add(reasonTwo.getText().toString());
                    }
                    if (checkedReasonThree) {
                        reasonList.add(reasonThree.getText().toString());
                    }
                    if (checkedReasonFour) {
                        reasonList.add(reasonFour.getText().toString());
                    }
                    if (checkedReasonFive) {
                        reasonList.add(reasonFive.getText().toString());
                    }
                    if (checkedReasonSix) {
                        reasonList.add(reasonSix.getText().toString());
                    }
                    if (checkedReasonSeven) {
                        reasonList.add(reasonSeven.getText().toString());
                    }

                    if (reasonList.size() == 1) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0), userIdForRating, userId));

                    } else if (reasonList.size() == 2) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0) + " & " + reasonList.get(1), userIdForRating, userId));

                    } else if (reasonList.size() == 3) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0) + " & " + reasonList.get(1) + " & " + reasonList.get(2), userIdForRating, userId));

                    } else if (reasonList.size() == 4) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0) + " & " + reasonList.get(1) + " & " + reasonList.get(2) + " & " + reasonList.get(3), userIdForRating, userId));

                    } else if (reasonList.size() == 5) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0) + " & " + reasonList.get(1) + " & " + reasonList.get(2) + " & " + reasonList.get(3) + " & " + reasonList.get(4), userIdForRating, userId));

                    } else if (reasonList.size() == 6) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0) + " & " + reasonList.get(1) + " & " + reasonList.get(2) + " & " + reasonList.get(3) + " & " + reasonList.get(4) + " & " + reasonList.get(5), userIdForRating, userId));

                    } else if (reasonList.size() == 7) {
                        CreateUser.saveRatings(CreateUser.createRatings(transactionId, String.valueOf(ratingBar.getRating()), reasonList.get(0) + " & " + reasonList.get(1) + " & " + reasonList.get(2) + " & " + reasonList.get(3) + " & " + reasonList.get(4) + " & " + reasonList.get(5) + " & " + reasonList.get(6), userIdForRating, userId));
                    }
                    alert.dismiss();
                } else {
                    Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show();
                }
            }
            RearrangeItems();
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingGiven = v;
                ratingText.setText(v + " / 5.0");
                if (v < 5 || v == 0) {
                    issue.setVisibility(View.VISIBLE);
                    issueTwo.setVisibility(View.VISIBLE);
                    reasonOne.setVisibility(View.VISIBLE);
                    reasonTwo.setVisibility(View.VISIBLE);
                    reasonThree.setVisibility(View.VISIBLE);
                    reasonFour.setVisibility(View.VISIBLE);
                    reasonFive.setVisibility(View.VISIBLE);
                    reasonSix.setVisibility(View.VISIBLE);
                    reasonSeven.setVisibility(View.VISIBLE);
                } else {
                    issue.setVisibility(View.GONE);
                    issueTwo.setVisibility(View.GONE);
                    reasonOne.setVisibility(View.GONE);
                    reasonTwo.setVisibility(View.GONE);
                    reasonThree.setVisibility(View.GONE);
                    reasonFour.setVisibility(View.GONE);
                    reasonFive.setVisibility(View.GONE);
                    reasonSix.setVisibility(View.GONE);
                    reasonSeven.setVisibility(View.GONE);
                }
            }
        });

        reasonOne.setOnClickListener(view -> {
            if (!checkedReasonOne) {
                reasonForLowRate = "reason";
                reasonOne.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonOne = true;
            } else {
                reasonOne.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonOne = false;
                reasonForLowRate = "";
            }
        });

        reasonTwo.setOnClickListener(view -> {
            if (!checkedReasonTwo) {
                reasonForLowRate = "reason";
                reasonTwo.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonTwo = true;
            } else {
                reasonTwo.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonTwo = false;
                reasonForLowRate = "";
            }
        });

        reasonThree.setOnClickListener(view -> {
            if (!checkedReasonThree) {
                reasonForLowRate = "reason";
                reasonThree.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonThree = true;
            } else {
                reasonThree.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonThree = false;
                reasonForLowRate = "";
            }
        });

        reasonFour.setOnClickListener(view -> {
            if (!checkedReasonFour) {
                reasonFour.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonFour = true;
            } else {
                reasonFour.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonFour = false;
                reasonForLowRate = "";
            }
        });

        reasonFive.setOnClickListener(view -> {
            if (!checkedReasonFive) {
                reasonForLowRate = "reason";
                reasonFive.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonFive = true;
            } else {
                reasonFive.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonFive = false;
                reasonForLowRate = "";
            }
        });

        reasonSix.setOnClickListener(view -> {
            if (!checkedReasonSix) {
                reasonForLowRate = "reason";
                reasonSix.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonSix = true;
            } else {
                reasonSix.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonSix = false;
                reasonForLowRate = "";
            }
        });

        reasonSeven.setOnClickListener(view -> {
            if (!checkedReasonSeven) {
                reasonForLowRate = "reason";
                reasonSeven.setBackground(getResources().getDrawable(R.drawable.image_view_border_selected));
                checkedReasonSeven = true;
            } else {
                reasonSeven.setBackground(getResources().getDrawable(R.drawable.image_view_border));
                checkedReasonSeven = false;
                reasonForLowRate = "";
            }
        });

        //------------------------------------------------------------------------------------------
        String url1 = getString(R.string.baseURL) + "/imgbucket/Images/" + userIdForRating;
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");

                        if (imageType.equals("profile")) {
                            profileImgUrlForRating = obj.getString("image_url");
                            new DownloadImageTask(profile).execute(profileImgUrlForRating);
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

    @Override
    public void onBackPressed() {
        ShowAlert.loadingDialog(TrackForLoadPosterActivity.this);
        JumpTo.goToCustomerDashboard(TrackForLoadPosterActivity.this, phone, true);
    }

    public void onClickOpenDialer(View view) {
        switch (view.getId()) {
            case R.id.dialog_accept_bid_sp_number:
                String numberOpen = spNumber.getText().toString();
                Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + numberOpen));
                startActivity(i1);
                break;
            case R.id.dialog_accept_bid_driver_number:
                String numberOpened = driverNumber.getText().toString();
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + numberOpened));
                startActivity(i2);
                break;
        }

    }

    public void ViewProfileOfSPToCustomer(BidsResponsesModel obj) {
        String url1 = getString(R.string.baseURL) + "/imgbucket/Images/" + obj.getUser_id();
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
                            if (profileImgUrl.equals("null")) {

                            } else {
                                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                lp2.copyFrom(previewDialogProfileOfSp.getWindow().getAttributes());
                                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                                lp2.gravity = Gravity.CENTER;

                                previewDialogProfileOfSp.show();
                                previewDialogProfileOfSp.getWindow().setAttributes(lp2);
                                new DownloadImageTask((ImageView) previewDialogProfileOfSp.findViewById(R.id.dialog_preview_image_view)).execute(profileImgUrl);
                            }
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

    //-------------------------------------------------------------------------------------------------------------------

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int state = msg.getData().getInt("state");
            if (state == 1) {
                loadingDialog.dismiss();
            }
        }
    };


    public void onClickWhatsApp(View view) {
        Dialog chooseDialog = new Dialog(TrackForLoadPosterActivity.this);
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
        cameraText.setText(getString(R.string.Whats_App));
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
                    Toast.makeText(TrackForLoadPosterActivity.this, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
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