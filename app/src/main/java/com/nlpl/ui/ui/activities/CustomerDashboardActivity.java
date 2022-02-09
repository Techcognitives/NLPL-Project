package com.nlpl.ui.ui.activities;

import static com.nlpl.R.drawable.blue_profile_small;

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
import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.nlpl.model.UpdateModel.Models.UpdateBids.UpdateBudgetCustomerForSP;
import com.nlpl.ui.ui.adapters.BidsAcceptedAdapter;
import com.nlpl.ui.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.ui.adapters.BidsResponsesAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.MakePayment;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.razorpay.PaymentResultListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDashboardActivity extends AppCompatActivity implements PaymentResultListener  {

    SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mQueue;

    private ArrayList<BidsReceivedModel> bidsList = new ArrayList<>();
    private BidsReceivedAdapter bidsListAdapter;
    private RecyclerView bidsListRecyclerView;

    private ArrayList<BidsAcceptedModel> acceptedList;
    private BidsAcceptedAdapter bidsAcceptedAdapter;
    private RecyclerView bidsAcceptedRecyclerView;

    private BidsResponsesAdapter bidsResponsesAdapter;
    boolean isBackPressed = false, bidsReceivedSelected = true, isbidsReceivedSelected;

    private int CAMERA_PIC_REQUEST2 = 4;
    private int GET_FROM_GALLERY2 = 5;

    Dialog menuDialog;
    TextView userNameTextViewMenu, mobileTextViewMenu, spNumber, driverNumber;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView;
    Dialog previewDialogProfile, previewDialogProfileOfSp;
    ImageView profilePic;

    String isPersonalDetailsDone, isBankDetailsDone, isProfileAdded;
    int count = 0;
    String img_type;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    Dialog previewDialogAcceptANdBid, setBudget, acceptFinalBid, viewConsignmentCustomer;
    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    ConstraintLayout loadAcceptedConstrain, bidsReceivedConstrain;
    TextView quoteBySp1, timeLeftTextview, timeLeft00, loadAcceptedTextView, bidsReceivedTextView, customerQuote, submitResponseBtn, cancleBtn;
    RadioButton negotiable_yes, negotiable_no;
    EditText notesCustomer;
    String userId, phone;
    String spQuoteByApi, bid_idByAPI, noteByApi;

    ArrayList<String> arrayAssignedDriverId, arrayBidId, arrayUserId, arrayBidStatus, arrayNotesFromSP;
    String fianlBidId, noteBySPToCustomer, assignedDriverId, assignedDriverIdAPI, assignedUserId, assignedUserIdAPI, bidStatusAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        Checkout.preload(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
        }
        isbidsReceivedSelected = bundle.getBoolean("bidsReveived");
        mQueue = Volley.newRequestQueue(CustomerDashboardActivity.this);

        loadAcceptedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_loads_accepted_constrain);
        bidsReceivedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_bids_received_constrain);
        loadAcceptedTextView = (TextView) findViewById(R.id.customer_dashboard_loads_accepted_button);
        bidsReceivedTextView = (TextView) findViewById(R.id.customer_dashboard_bids_received_button);

        if (isbidsReceivedSelected) {
            bidsReceivedSelected = true;
            loadAcceptedConstrain.setVisibility(View.INVISIBLE);
            bidsReceivedConstrain.setVisibility(View.VISIBLE);
            bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
            loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));

        } else {
            bidsReceivedSelected = false;
            loadAcceptedConstrain.setVisibility(View.VISIBLE);
            bidsReceivedConstrain.setVisibility(View.INVISIBLE);
            loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
            bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));

        }

        getUserId(phone);

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

        actionBarTitle.setText("Load Poster Dashboard");
        actionBarBackButton.setVisibility(View.GONE);
        actionBarMenuButton.setVisibility(View.VISIBLE);
        //------------------------------------------------------------------------------------------
        //------------------------------- menu button ----------------------------------------------
        menuDialog = new Dialog(CustomerDashboardActivity.this);
        menuDialog.setContentView(R.layout.dialog_customer_menu);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogProfile = new Dialog(CustomerDashboardActivity.this);
        previewDialogProfile.setContentView(R.layout.dialog_preview_profile);
        previewDialogProfile.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        previewDialogProfileOfSp = new Dialog(CustomerDashboardActivity.this);
        previewDialogProfileOfSp.setContentView(R.layout.dialog_preview_images);
        previewDialogProfileOfSp.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));


        userNameTextViewMenu = (TextView) menuDialog.findViewById(R.id.customer_menu_name_text);
        profilePic = (ImageView) menuDialog.findViewById(R.id.profile_picture_on_customer_menu);
        mobileTextViewMenu = (TextView) menuDialog.findViewById(R.id.customer_menu_mobile);
        personalDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.customer_menu_personal_details_logo_image_view);
        bankDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.customer_menu_bank_details_logo_image_view);
        //------------------------------------------------------------------------------------------
        //------------------------------- bottom nav -----------------------------------------------
        bottomNav = (View) findViewById(R.id.customer_dashboard_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);

        acceptedList = new ArrayList<>();
        arrayAssignedDriverId = new ArrayList<>();
        arrayUserId = new ArrayList<>();
        arrayBidId = new ArrayList<>();
        arrayBidStatus = new ArrayList<>();
        arrayNotesFromSP = new ArrayList<>();

        //---------------------------- Get Load Details -------------------------------------------
        bidsListRecyclerView = (RecyclerView) findViewById(R.id.customer_dashboard_load_notification_recycler_view);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(false);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.VERTICAL);
        bidsListRecyclerView.setLayoutManager(linearLayoutManagerBank);
        bidsListRecyclerView.setHasFixedSize(true);

        bidsListAdapter = new BidsReceivedAdapter(CustomerDashboardActivity.this, bidsList);
        bidsListRecyclerView.setAdapter(bidsListAdapter);
        //------------------------------------------------------------------------------------------

        //---------------------------- Get Accepted Load Details -----------------------------------
        bidsAcceptedRecyclerView = (RecyclerView) findViewById(R.id.customer_dashboard_loads_accepted_recycler_view);

        LinearLayoutManager linearLayoutManagerAccepted = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerAccepted.setReverseLayout(false);
        linearLayoutManagerAccepted.setOrientation(LinearLayoutManager.VERTICAL);
        bidsAcceptedRecyclerView.setLayoutManager(linearLayoutManagerAccepted);
        bidsAcceptedRecyclerView.setHasFixedSize(true);

        bidsAcceptedAdapter = new BidsAcceptedAdapter(CustomerDashboardActivity.this, acceptedList);
        bidsAcceptedRecyclerView.setAdapter(bidsAcceptedAdapter);
        //------------------------------------------------------------------------------------------

        previewDialogAcceptANdBid = new Dialog(CustomerDashboardActivity.this);
        previewDialogAcceptANdBid.setContentView(R.layout.dialog_acept_bid_customer);
        previewDialogAcceptANdBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        acceptFinalBid = new Dialog(CustomerDashboardActivity.this);
        acceptFinalBid.setContentView(R.layout.dialog_acept_bid_customer);
        acceptFinalBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewConsignmentCustomer = new Dialog(CustomerDashboardActivity.this);
        viewConsignmentCustomer.setContentView(R.layout.dialog_acept_bid_customer);
        viewConsignmentCustomer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void getUserId(String userMobileNumber) {
        ArrayList<String> arrayUserId = new ArrayList<>(), arrayMobileNo = new ArrayList<>(), arrayCustomerName = new ArrayList<>(), isPersonalD = new ArrayList<>(), isProfileArray = new ArrayList<>(), isBankD = new ArrayList<>();
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

                        String isPer = data.getString("isPersonal_dt_added");
                        isPersonalD.add(isPer);
                        isProfileArray.add(data.getString("isProfile_pic_added"));
                        String isBank = data.getString("isBankDetails_given");
                        isBankD.add(isBank);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(userMobileNumber)) {
                            userId = arrayUserId.get(j);
                            String customerNameAPI = arrayCustomerName.get(j);
                            userNameTextViewMenu.setText(customerNameAPI);
                            String customerNumberAPI = arrayMobileNo.get(j);
                            String s1 = customerNumberAPI.substring(2, 12);
                            mobileTextViewMenu.setText("+91 " + s1);

                            isPersonalDetailsDone = isPersonalD.get(j);
                            isProfileAdded = isProfileArray.get(j);
                            isBankDetailsDone = isBankD.get(j);

                            if (isProfileAdded.equals("1")) {
                                getProfilePic();
                            } else {
                                profilePic.setImageDrawable(getResources().getDrawable(blue_profile_small));
                            }

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
                        }
                    }
                    getBidsReceived();
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
        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        intent.putExtra("bidsReveived", bidsReceivedSelected);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
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

                        if (obj.getString("bid_status").equals("loadSubmitted")) {
                            acceptedList.add(bidsAcceptedModel);
                        }
                    }

                    TextView noAcceptedLoads = (TextView) findViewById(R.id.customer_dashboard_no_load_accepted_text);
//                    for (int i=0; i< acceptedList.size(); i++){
//                        if (acceptedList.get(i).getBid_status().equals("FinalAccepted")){
                    if (acceptedList.size() > 0) {
//                        bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                        noAcceptedLoads.setVisibility(View.GONE);
                        bidsAcceptedAdapter.updateData(acceptedList);
                    } else {
//                        bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                        noAcceptedLoads.setVisibility(View.VISIBLE);
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

    public void onClickBidsAndLoads(View view) {
        switch (view.getId()) {
            case R.id.customer_dashboard_bids_received_button:
                bidsReceivedSelected = true;
                loadAcceptedConstrain.setVisibility(View.INVISIBLE);
                bidsReceivedConstrain.setVisibility(View.VISIBLE);
                bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));

                break;

            case R.id.customer_dashboard_loads_accepted_button:
                bidsReceivedSelected = false;
                loadAcceptedConstrain.setVisibility(View.VISIBLE);
                bidsReceivedConstrain.setVisibility(View.INVISIBLE);
                bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                break;
        }
    }

    public void onClickPostALoad(View view) {
        Intent intent = new Intent(CustomerDashboardActivity.this, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        intent.putExtra("reActivate", false);
        intent.putExtra("isEdit", false);
        startActivity(intent);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:

                break;

            case R.id.bottom_nav_customer_dashboard:

                break;
        }
    }

    public void getBidsReceived() {

        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bidsList = new ArrayList<>();
                    JSONArray bidsLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsLists.length(); i++) {
                        JSONObject obj = bidsLists.getJSONObject(i);
                        BidsReceivedModel bidsReceivedModel = new BidsReceivedModel();
                        bidsReceivedModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsReceivedModel.setUser_id(obj.getString("user_id"));
                        bidsReceivedModel.setPick_up_date(obj.getString("pick_up_date"));
                        bidsReceivedModel.setPick_up_time(obj.getString("pick_up_time"));
                        bidsReceivedModel.setBudget(obj.getString("budget"));
                        bidsReceivedModel.setBid_status(obj.getString("bid_status"));
                        bidsReceivedModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidsReceivedModel.setFeet(obj.getString("feet"));
                        bidsReceivedModel.setCapacity(obj.getString("capacity"));
                        bidsReceivedModel.setBody_type(obj.getString("body_type"));
                        bidsReceivedModel.setPick_add(obj.getString("pick_add"));
                        bidsReceivedModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        bidsReceivedModel.setPick_city(obj.getString("pick_city"));
                        bidsReceivedModel.setPick_state(obj.getString("pick_state"));
                        bidsReceivedModel.setPick_country(obj.getString("pick_country"));
                        bidsReceivedModel.setDrop_add(obj.getString("drop_add"));
                        bidsReceivedModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        bidsReceivedModel.setDrop_city(obj.getString("drop_city"));
                        bidsReceivedModel.setDrop_state(obj.getString("drop_state"));
                        bidsReceivedModel.setSp_count(obj.getInt("sp_count"));
                        bidsReceivedModel.setDrop_country(obj.getString("drop_country"));
                        bidsReceivedModel.setKm_approx(obj.getString("km_approx"));
                        bidsReceivedModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidsReceivedModel.setBid_ends_at(obj.getString("bid_ends_at"));

                        if (!obj.getString("bid_status").equals("loadSubmitted") && !obj.getString("bid_status").equals("delete") && !obj.getString("bid_status").equals("loadExpired")) {
                            bidsList.add(bidsReceivedModel);
                        }
                    }

                    Collections.reverse(bidsList);
                    TextView noLoadTextView = (TextView) findViewById(R.id.customer_dashboard_no_load_text);

                    if (bidsList.size() > 0) {
                        noLoadTextView.setVisibility(View.GONE);
                        bidsListAdapter.updateData(bidsList);
                    } else {
                        noLoadTextView.setVisibility(View.VISIBLE);
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

    public void onClickViewAndAcceptBid(BidsResponsesModel obj) {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogAcceptANdBid.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        previewDialogAcceptANdBid.show();
        previewDialogAcceptANdBid.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView nameSP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bit_service_provider_name);
        TextView modelBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_model_textview);
        TextView feetBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_feet_textview);
        TextView capacityBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_capacity_textview);
        TextView bodyTypeBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_body_type_textview);
        quoteBySp1 = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_bidder_quote_textview);
        TextView negotiableBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_negotiable_textview);
        TextView notesBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_received_notes_textview);

        //----------------------------------------------------------
        String url = getString(R.string.baseURL) + "/user/" + obj.getUser_id();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String spName = obj.getString("name");
                        nameSP.setText(spName);
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

        Log.i("Bid-id", obj.getSp_bid_id());
        Log.i("Load-id", obj.getIdpost_load());

        quoteBySp1.setText(obj.getSp_quote());
        modelBySP.setText(obj.getVehicle_model());
        feetBySP.setText(obj.getFeet());
        capacityBySP.setText(obj.getCapacity());
        bodyTypeBySP.setText(obj.getBody_type());
        notesBySP.setText(obj.getNotes());
        String isNegotiableBySP = obj.getIs_negatiable();
        if (isNegotiableBySP.equals("1")) {
            negotiableBySP.setText("Yes");
        } else {
            negotiableBySP.setText("No");
        }
        //----------------------------------------------------------------------------------------------------------------
        customerQuote = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_customer_final_quote_textview);
        negotiable_yes = previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_radio_btn_yes);
        negotiable_no = previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_radio_btn_no);
        notesCustomer = (EditText) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_notes_editText);
        submitResponseBtn = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_submit_response_btn);
        cancleBtn = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_cancel_btn);

        negotiable_yes.setChecked(false);
        negotiable_yes.setEnabled(false);
        negotiable_no.setChecked(true);

        if (!customerQuote.getText().toString().isEmpty()) {
            submitResponseBtn.setEnabled(true);
            submitResponseBtn.setBackgroundResource((R.drawable.button_active));
        } else {
            submitResponseBtn.setEnabled(false);
            submitResponseBtn.setBackgroundResource((R.drawable.button_de_active));
        }

        customerQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetSet(customerQuote.getText().toString());
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                intent.putExtra("bidsReveived", bidsReceivedSelected);
                startActivity(intent);
                finish();
                previewDialogAcceptANdBid.dismiss();
            }
        });

        getLoadDetails(obj.getIdpost_load());

        submitResponseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdatePostLoadDetails.updateNotes(obj.getIdpost_load(), notesCustomer.getText().toString());
                UpdateBidDetails.updateBidStatus(obj.getSp_bid_id(), "Accepted");
                UpdateBidDetails.updateCustomerBudgetForSP(obj.getSp_bid_id(), customerQuote.getText().toString());
                UpdatePostLoadDetails.updateBudget(obj.getIdpost_load(), customerQuote.getText().toString());
                UpdatePostLoadDetails.updateCount(obj.getIdpost_load(), count + 1);

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

                alertTitle.setText("Load Response");
                alertMessage.setText("Response submitted Successfully");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("mobile", phone);
                        intent.putExtra("bidsReveived", bidsReceivedSelected);
                        startActivity(intent);
                        finish();
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------

    private void getLoadDetails(String loadId) {
        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByPostId/" + loadId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bidsList = new ArrayList<>();
                    JSONArray bidsLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsLists.length(); i++) {
                        JSONObject obj = bidsLists.getJSONObject(i);
                        count = obj.getInt("sp_count");
                    }

                    Collections.reverse(bidsList);
                    TextView noLoadTextView = (TextView) findViewById(R.id.customer_dashboard_no_load_text);

                    if (bidsList.size() > 0) {
                        noLoadTextView.setVisibility(View.GONE);
                        bidsListAdapter.updateData(bidsList);
                    } else {
                        noLoadTextView.setVisibility(View.VISIBLE);
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

    private void budgetSet(String previousBudget) {

        setBudget = new Dialog(CustomerDashboardActivity.this);
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
                        customerQuote.setText(finalBudget);
                    } else if (budget1.length() == 2) {
                        finalBudget = budget1;
                        customerQuote.setText(finalBudget);
                    } else if (budget1.length() == 3) {
                        finalBudget = budget1;
                        customerQuote.setText(finalBudget);
                    } else if (budget1.length() == 4) {
                        Character fourth = budget1.charAt(0);
                        finalBudget = fourth + "," + lastThree;
                        customerQuote.setText(finalBudget);
                    } else if (budget1.length() == 5) {
                        Character fifth = budget1.charAt(0);
                        Character fourth = budget1.charAt(1);
                        finalBudget = fifth + "" + fourth + "," + lastThree;
                        customerQuote.setText(finalBudget);
                    } else if (budget1.length() == 6) {
                        Character fifth = budget1.charAt(1);
                        Character fourth = budget1.charAt(2);
                        Character sixth = budget1.charAt(0);
                        finalBudget = sixth + "," + fifth + "" + fourth + "," + lastThree;
                        customerQuote.setText(finalBudget);
                    } else if (budget1.length() == 7) {
                        Character seventh = budget1.charAt(0);
                        Character sixth = budget1.charAt(1);
                        Character fifth = budget1.charAt(2);
                        Character fourth = budget1.charAt(3);
                        finalBudget = seventh + "" + sixth + "," + fifth + "" + fourth + "," + lastThree;
                        customerQuote.setText(finalBudget);
                    }

                    if (quoteBySp1.getText().toString().equals(customerQuote.getText().toString())) {
                        customerQuote.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        customerQuote.setTextColor(getResources().getColor(R.color.redDark));
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
                if (!customerQuote.getText().toString().isEmpty()) {
                    submitResponseBtn.setEnabled(true);
                    submitResponseBtn.setBackgroundResource((R.drawable.button_active));
                } else {
                    submitResponseBtn.setEnabled(false);
                    submitResponseBtn.setBackgroundResource((R.drawable.button_de_active));
                }
                setBudget.dismiss();
            }
        });
    }

    public void onClickEditLoadPost(BidsReceivedModel obj) {
        Intent intent = new Intent(CustomerDashboardActivity.this, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        intent.putExtra("reActivate", false);
        intent.putExtra("isEdit", true);
        intent.putExtra("loadId", obj.getIdpost_load());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    public void getBidsResponsesList(BidsReceivedModel obj1, RecyclerView bidsResponsesRecyclerView, TextView bidsReceived, ConstraintLayout showRecyclerView) {
        ArrayList<BidsResponsesModel> bidResponsesList = new ArrayList<>();
        bidResponsesList.clear();

        String url1 = getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj1.getIdpost_load();
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidResponsesLists = response.getJSONArray("data");
                    for (int i = 0; i < bidResponsesLists.length(); i++) {
                        JSONObject obj = bidResponsesLists.getJSONObject(i);
                        BidsResponsesModel bidsResponsesModel = new BidsResponsesModel();
                        bidsResponsesModel.setSp_bid_id(obj.getString("sp_bid_id"));
                        bidsResponsesModel.setUser_id(obj.getString("user_id"));
                        bidsResponsesModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsResponsesModel.setSp_quote(obj.getString("sp_quote"));
                        bidsResponsesModel.setIs_negatiable(obj.getString("is_negatiable"));
                        bidsResponsesModel.setAssigned_truck_id(obj.getString("assigned_truck_id"));
                        bidsResponsesModel.setAssigned_driver_id(obj.getString("assigned_driver_id"));
                        bidsResponsesModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidsResponsesModel.setFeet(obj.getString("feet"));
                        bidsResponsesModel.setCapacity(obj.getString("capacity"));
                        bidsResponsesModel.setBody_type(obj.getString("body_type"));
                        bidsResponsesModel.setNotes(obj.getString("notes"));
                        bidsResponsesModel.setBid_status(obj.getString("bid_status"));
                        bidsResponsesModel.setIs_bid_accpted_by_sp(obj.getString("is_bid_accpted_by_sp"));

                        if (!obj.getString("bid_status").equals("withdrawnByLp")) {
//                            if (obj1.getSp_count()<=3) {
                                bidResponsesList.add(bidsResponsesModel);
//                            }
                        }
                    }

                    for (int i = 0; i < bidResponsesList.size(); i++) {
                        if (obj1.getIdpost_load().equals(bidResponsesList.get(i).getIdpost_load())) {

                            String bidsResponses = String.valueOf(bidResponsesList.size());
                            Log.i("bid size", String.valueOf(bidResponsesList.size()));
                            bidsReceived.setText(bidsResponses + " Responses Received");
                            Log.i("Res", bidsReceived.getText().toString());

                            if (bidResponsesList.get(i).getBid_status().equals("submitted")) {
                                bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                                bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                                bidsResponsesAdapter.updateData(bidResponsesList);
                            }
                            if (bidResponsesList.get(i).getBid_status().equals("Accepted")) {
                                int itemPos = bidResponsesList.indexOf(bidResponsesList.get(i));
                                bidResponsesList.add(0, bidResponsesList.get(i));
                                bidResponsesList.remove(itemPos + 1);

                                bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                                bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                                bidsResponsesAdapter.updateData(bidResponsesList);
                            }
                            if (bidResponsesList.get(i).getBid_status().equals("RespondedBySP")) {
                                bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                                bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                                bidsResponsesAdapter.updateData(bidResponsesList);
                            }

                        }
                    }

                    if (bidsReceived.getText().toString().equals("0 Responses Received")) {
                        showRecyclerView.setVisibility(View.GONE);
                        bidsResponsesRecyclerView.setVisibility(View.GONE);
                    } else {
                        if (obj1.getBid_status().equals("loadExpired")) {
                            showRecyclerView.setVisibility(View.GONE);
                            bidsResponsesRecyclerView.setVisibility(View.GONE);
                        } else {
                            showRecyclerView.setVisibility(View.VISIBLE);
                            bidsResponsesRecyclerView.setVisibility(View.VISIBLE);
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
        //-------------------------------------------------------------------------------------------
    }

    public void acceptFinalOffer(BidsResponsesModel obj) {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(acceptFinalBid.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        acceptFinalBid.show();
        acceptFinalBid.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView nameSP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bit_service_provider_name);
        TextView modelBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_model_textview);
        TextView feetBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_feet_textview);
        TextView capacityBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_capacity_textview);
        TextView bodyTypeBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_body_type_textview);
        TextView quoteBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_bidder_quote_textview);
        TextView negotiableBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_negotiable_textview);
        TextView notesBySP = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_received_notes_textview);

        //----------------------------------------------------------
        String url = getString(R.string.baseURL) + "/user/" + obj.getUser_id();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String spName = obj.getString("name");
                        nameSP.setText(spName);
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

        Log.i("Bid-id", obj.getSp_bid_id());
        Log.i("Load-id", obj.getIdpost_load());

        quoteBySP.setText(obj.getSp_quote());
        modelBySP.setText(obj.getVehicle_model());
        feetBySP.setText(obj.getFeet());
        capacityBySP.setText(obj.getCapacity());
        bodyTypeBySP.setText(obj.getBody_type());
        notesBySP.setText(obj.getNotes());
        negotiableBySP.setText("No");
        //----------------------------------------------------------------------------------------------------------------

        customerQuote = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_customer_final_quote_textview);
        negotiable_yes = acceptFinalBid.findViewById(R.id.dialog_accept_bid_radio_btn_yes);
        negotiable_no = acceptFinalBid.findViewById(R.id.dialog_accept_bid_radio_btn_no);
        notesCustomer = (EditText) acceptFinalBid.findViewById(R.id.dialog_accept_bid_notes_editText);
        submitResponseBtn = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_submit_response_btn);
        cancleBtn = (TextView) acceptFinalBid.findViewById(R.id.dialog_accept_bid_cancel_btn);

        negotiable_yes.setChecked(false);
        negotiable_yes.setEnabled(false);
        negotiable_no.setChecked(true);
        customerQuote.setText(obj.getSp_quote());
        submitResponseBtn.setText("Accept Final Offer");

        submitResponseBtn.setEnabled(true);
        submitResponseBtn.setBackgroundResource((R.drawable.button_active));

        submitResponseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //----------------------------------------------------------------------------------
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

                alertTitle.setText("Pay and Accept Final Offer");
                alertMessage.setText("Payment Amount: "+obj.getSp_quote());
                alertPositiveButton.setText("Pay & Accept Bid");
                alertNegativeButton.setText("Cancel");

                alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MakePayment.makePayment(CustomerDashboardActivity.this);
                    }
                });

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("mobile", phone);
                        intent.putExtra("bidsReveived", bidsReceivedSelected);
                        startActivity(intent);
                        finish();
                        acceptFinalBid.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------

                //----------------------------------------------------------------------------------

//                UpdatePostLoadDetails.updateNotes(obj.getIdpost_load(), notesCustomer.getText().toString());
//                UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "loadSubmitted");
//                UpdateBidDetails.updateBidStatus(obj.getSp_bid_id(), "FinalAccepted");
//                UpdateBidDetails.updateCustomerBudgetForSP(obj.getSp_bid_id(), obj.getSp_quote());
//                UpdatePostLoadDetails.updateBudget(obj.getIdpost_load(), obj.getSp_quote());

            }
        });

        cancleBtn.setEnabled(true);
        cancleBtn.setBackgroundResource((R.drawable.button_active));

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                intent.putExtra("bidsReveived", bidsReceivedSelected);
                startActivity(intent);
                finish();
                acceptFinalBid.dismiss();
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment ID");
        builder.setMessage(s);
        builder.show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void onClickViewConsignment(BidsAcceptedModel obj) {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(viewConsignmentCustomer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        viewConsignmentCustomer.show();
        viewConsignmentCustomer.setCancelable(false);
        viewConsignmentCustomer.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView nameSP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bit_service_provider_name);
        TextView modelBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_model_textview);
        TextView feetBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_feet_textview);
        TextView capacityBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_capacity_textview);
        TextView bodyTypeBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_body_type_textview);
        TextView quoteBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_bidder_quote_textview);
        TextView negotiableBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_negotiable_textview);
        TextView notesBySP = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_received_notes_textview);
        spNumber = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_sp_number);
        TextView companyNameHeading = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_company_name_heading);
        TextView companyName = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_company_name);
        TextView driverNameHeading = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_driver_name_heading);
        TextView driverName = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_driver_name);
        driverNumber = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_driver_number);

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
                        spQuoteByApi = obj.getString("sp_quote");
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
                        if (arrayBidStatus.get(j).equals("FinalAccepted")) {
                            fianlBidId = arrayBidId.get(j);
                            assignedUserId = arrayUserId.get(j);
                            assignedDriverId = arrayAssignedDriverId.get(j);
                            noteBySPToCustomer = arrayNotesFromSP.get(j);
                        }

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
                                        spNumber.setText(obj.getString("phone_number"));

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
                                        driverNumber.setText(obj.getString("driver_number"));
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

                    quoteBySP.setText(spQuoteByApi);
                    modelBySP.setText(obj.getVehicle_model());
                    feetBySP.setText(obj.getFeet());
                    capacityBySP.setText(obj.getCapacity());
                    bodyTypeBySP.setText(obj.getBody_type());
                    negotiableBySP.setText("No");
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

                    timeLeftTextview.setText("CONSIGNMENT");
                    timeLeft00.setVisibility(View.GONE);
                    timeLeftTextview.setTextColor(getResources().getColorStateList(R.color.black));
                    timeLeftTextview.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                    noteHeading.setVisibility(View.GONE);
                    notesCustomer.setVisibility(View.GONE);
                    negotiable_yes.setChecked(false);
                    negotiable_yes.setEnabled(false);
                    negotiable_no.setChecked(true);
                    customerQuote.setText(spQuoteByApi);

                    submitResponseBtn.setText("Withdraw");
                    submitResponseBtn.setBackgroundResource((R.drawable.button_active));
                    submitResponseBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey));
                    submitResponseBtn.setEnabled(true);

                    submitResponseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //----------------------- Alert Dialog -------------------------------------------------
                            Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

                            alertTitle.setText("Withdrawn Load");
                            alertMessage.setText("Do you want withdraw fully or continue with other Service Provider");
                            alertPositiveButton.setVisibility(View.VISIBLE);
                            alertPositiveButton.setText("Withdraw Fully");
                            alertNegativeButton.setText("Withdraw Partially");
                            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                            alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alert.dismiss();
//                                    viewConsignmentCustomer.dismiss();
                                    UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "delete");

                                    //----------------------- Alert Dialog -------------------------------------------------
                                    Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

                                    alertTitle.setText("Withdraw Load");
                                    alertMessage.setText("Load withdrawn successfully and no longer visible for anyone");
                                    alertPositiveButton.setVisibility(View.GONE);
                                    alertNegativeButton.setText("Ok");
                                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alert.dismiss();
                                            viewConsignmentCustomer.dismiss();
                                            Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                                            intent.putExtra("userId", userId);
                                            intent.putExtra("mobile", phone);
                                            intent.putExtra("bidsReveived", bidsReceivedSelected);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(0, 0);
                                        }
                                    });
                                    //------------------------------------------------------------------------------------------
                                }
                            });
                            //------------------------------------------------------------------------------------------

                            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alert.dismiss();
                                    viewConsignmentCustomer.dismiss();
                                    UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "loadPosted");
                                    UpdateBidDetails.updateBidStatus(fianlBidId, "withdrawnByLp");

                                    //----------------------- Alert Dialog -------------------------------------------------
                                    Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

                                    alertTitle.setText("Withdraw from current Service Provider");
                                    alertMessage.setText("Load withdrawn from current Service Provider and visible for other Service Provider");
                                    alertPositiveButton.setVisibility(View.GONE);
                                    alertNegativeButton.setText("Ok");
                                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alert.dismiss();
                                            viewConsignmentCustomer.dismiss();
                                            Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                                            intent.putExtra("userId", userId);
                                            intent.putExtra("mobile", phone);
                                            intent.putExtra("bidsReveived", bidsReceivedSelected);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(0, 0);
                                        }
                                    });
                                    //------------------------------------------------------------------------------------------

                                }
                            });
                        }
                    });

                    cancleBtn.setEnabled(true);
                    cancleBtn.setBackgroundResource((R.drawable.button_active));
                    cancleBtn.setText("Back");

                    cancleBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("mobile", phone);
                            intent.putExtra("bidsReveived", bidsReceivedSelected);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
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

    }

    //--------------------------------- menu -------------------------------------------------------
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

    public void onClickDismissDialog(View view) {
        menuDialog.dismiss();
    }

    public void onClickLogOutCustomer(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(CustomerDashboardActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onClickProfileAndRegisterCustomer(View view) {
        switch (view.getId()) {
            case R.id.customer_menu_personal_details_button:
                Intent i8 = new Intent(CustomerDashboardActivity.this, ViewPersonalDetailsActivity.class);
                i8.putExtra("userId", userId);
                i8.putExtra("mobile", phone);
                startActivity(i8);
                break;

            case R.id.customer_menu_bank_details_button:
                if (isBankDetailsDone.equals("1")) {
                    Intent intent = new Intent(CustomerDashboardActivity.this, ViewBankDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerDashboardActivity.this, BankDetailsActivity.class);
                    intent.putExtra("isEdit", false);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;
        }
    }
    //----------------------------------------------------------------------------------------------

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

    public void ViewCustomerProfile(View view) {
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
                                if (profileImgUrl.equals("null")) {

                                } else {
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
            uploadProfileDialogChoose();
        }
    }

    private void uploadProfileDialogChoose() {
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "profile";

        Dialog chooseDialog;
        chooseDialog = new Dialog(CustomerDashboardActivity.this);
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

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(CustomerDashboardActivity.this);
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
            Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

        Dialog alert = new Dialog(CustomerDashboardActivity.this);
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
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                intent.putExtra("bidsReveived", bidsReceivedSelected);
                startActivity(intent);
                finish();
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
        if (ContextCompat.checkSelfPermission(CustomerDashboardActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerDashboardActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(CustomerDashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerDashboardActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(CustomerDashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerDashboardActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    public void reActivateLoad(BidsReceivedModel obj) {

        //----------------------- Alert Dialog -------------------------------------------------
        Dialog reActivateLoad = new Dialog(CustomerDashboardActivity.this);
        reActivateLoad.setContentView(R.layout.dialog_alert);
        reActivateLoad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reActivateLoad.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        reActivateLoad.show();
        reActivateLoad.getWindow().setAttributes(lp);
        reActivateLoad.setCancelable(true);

        TextView alertTitle = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText("Re-activate Load");
        alertMessage.setText("Do you want to re-activate load");
        alertNegativeButton.setText("Re-activate Load");
        alertPositiveButton.setVisibility(View.GONE);
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reActivateLoad.dismiss();
                Intent intent = new Intent(CustomerDashboardActivity.this, PostALoadActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                intent.putExtra("reActivate", true);
                intent.putExtra("isEdit", true);
                intent.putExtra("loadId", obj.getIdpost_load());
                startActivity(intent);

            }
        });
        //------------------------------------------------------------------------------------------
    }

    public void CustomerLoadHistory(View view) {
        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerLoadsHistoryActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        startActivity(intent);
    }

    public void continueWithOtherSp(BidsAcceptedModel obj) {

        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

        alertTitle.setText("Continue with other Service Provider");
        alertMessage.setText("Current Bidder have withdrawn Bid, do you still want to continue with other Service Provider?");
        alertPositiveButton.setVisibility(View.VISIBLE);
        alertPositiveButton.setText("Continue");
        alertNegativeButton.setText("Cancel");
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                viewConsignmentCustomer.dismiss();
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                intent.putExtra("bidsReveived", bidsReceivedSelected);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        //------------------------------------------------------------------------------------------

        alertPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                viewConsignmentCustomer.dismiss();
                UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "loadPosted");

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(CustomerDashboardActivity.this);
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

                alertTitle.setText("Continue with other Service Provider");
                alertMessage.setText("Load withdrawn from current Service Provider and visible for other Service Provider");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("Ok");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        viewConsignmentCustomer.dismiss();
                        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("mobile", phone);
                        intent.putExtra("bidsReveived", bidsReceivedSelected);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });
    }

    public void sortByPriceHighToLow(BidsReceivedModel obj) {

        ArrayList<BidsResponsesModel> highToLowLIst = new ArrayList<>();
        ArrayList<String> arraySpQuote = new ArrayList<>();
        highToLowLIst.clear();

        String url1 = getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray highToLow = response.getJSONArray("data");
                    for (int i = 0; i < highToLow.length(); i++) {
                        JSONObject obj = highToLow.getJSONObject(i);
                        BidsResponsesModel bidsResponsesModel = new BidsResponsesModel();
                        bidsResponsesModel.setSp_bid_id(obj.getString("sp_bid_id"));
                        bidsResponsesModel.setUser_id(obj.getString("user_id"));
                        bidsResponsesModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsResponsesModel.setSp_quote(obj.getString("sp_quote"));
                        bidsResponsesModel.setIs_negatiable(obj.getString("is_negatiable"));
                        bidsResponsesModel.setAssigned_truck_id(obj.getString("assigned_truck_id"));
                        bidsResponsesModel.setAssigned_driver_id(obj.getString("assigned_driver_id"));
                        bidsResponsesModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidsResponsesModel.setFeet(obj.getString("feet"));
                        bidsResponsesModel.setCapacity(obj.getString("capacity"));
                        bidsResponsesModel.setBody_type(obj.getString("body_type"));
                        bidsResponsesModel.setNotes(obj.getString("notes"));
                        bidsResponsesModel.setBid_status(obj.getString("bid_status"));
                        bidsResponsesModel.setIs_bid_accpted_by_sp(obj.getString("is_bid_accpted_by_sp"));

                        if (!obj.getString("bid_status").equals("withdrawnByLp")) {
                            arraySpQuote.add(obj.getString("sp_quote"));
                            highToLowLIst.add(bidsResponsesModel);
                        }
                    }

                    for (int i=0; i<highToLowLIst.size();i++){

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
}