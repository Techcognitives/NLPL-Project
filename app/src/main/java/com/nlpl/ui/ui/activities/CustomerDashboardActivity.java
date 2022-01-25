package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
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
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.UpdateBidStatusAccepted;
import com.nlpl.model.UpdateBidStatusFinalAccepted;
import com.nlpl.model.UpdateCustomerBudget;
import com.nlpl.model.UpdateLoadStatusSubmitted;
import com.nlpl.services.BidLoadService;
import com.nlpl.services.PostLoadService;
import com.nlpl.ui.ui.adapters.BidsAcceptedAdapter;
import com.nlpl.ui.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.ui.adapters.BidsResponsesAdapter;
import com.nlpl.ui.ui.adapters.LoadNotificationAdapter;
import com.nlpl.ui.ui.adapters.LoadSubmittedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerDashboardActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mQueue;

    private ArrayList<BidsReceivedModel> bidsList = new ArrayList<>();
    private BidsReceivedAdapter bidsListAdapter;
    private RecyclerView bidsListRecyclerView;

    private ArrayList<BidsAcceptedModel> acceptedList;
    private BidsAcceptedAdapter bidsAcceptedAdapter;
    private RecyclerView bidsAcceptedRecyclerView;

    private BidsResponsesAdapter bidsResponsesAdapter;
    private BidLoadService bidService;

    Dialog menuDialog;
    TextView userNameTextViewMenu, mobileTextViewMenu;
    ImageView personalDetailsLogoImageView, bankDetailsLogoImageView;

    String isPersonalDetailsDone, isBankDetailsDone;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    Dialog previewDialogAcceptANdBid, setBudget, acceptFinalBid, viewConsignmentCustomer;
    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    ConstraintLayout loadAcceptedConstrain, bidsReceivedConstrain;
    TextView timeLeftTextview, timeLeft00, loadAcceptedTextView, bidsReceivedTextView, customerQuote, submitResponseBtn, cancleBtn;
    RadioButton negotiable_yes, negotiable_no;
    EditText notesCustomer;
    String userId, phone;
    String spQuoteByApi, bid_idByAPI, noteByApi;
    private PostLoadService postLoadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
        }
        mQueue = Volley.newRequestQueue(CustomerDashboardActivity.this);

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

        userNameTextViewMenu = (TextView) menuDialog.findViewById(R.id.customer_menu_name_text);
        mobileTextViewMenu = (TextView) menuDialog.findViewById(R.id.customer_menu_mobile);
        personalDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.customer_menu_personal_details_logo_image_view);
        bankDetailsLogoImageView = (ImageView) menuDialog.findViewById(R.id.customer_menu_bank_details_logo_image_view);
        //------------------------------------------------------------------------------------------
        //------------------------------- bottom nav -----------------------------------------------
        bottomNav = (View) findViewById(R.id.customer_dashboard_bottom_nav_bar);
        bottomNav.setVisibility(View.GONE);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));

        loadAcceptedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_loads_accepted_constrain);
        bidsReceivedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_bids_received_constrain);
        loadAcceptedTextView = (TextView) findViewById(R.id.customer_dashboard_loads_accepted_button);
        bidsReceivedTextView = (TextView) findViewById(R.id.customer_dashboard_bids_received_button);

        acceptedList = new ArrayList<>();

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        bidService = retrofit.create(BidLoadService.class);
        postLoadService = retrofit.create(PostLoadService.class);

    }

    private void getUserId(String userMobileNumber) {
        ArrayList<String> arrayUserId = new ArrayList<>(), arrayMobileNo = new ArrayList<>(), arrayCustomerName = new ArrayList<>(), isPersonalD = new ArrayList<>(), isBankD = new ArrayList<>();
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
                        String isBank = data.getString("isBankDetails_given");
                        isBankD.add(isBank);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(userMobileNumber)) {
                            userId = arrayUserId.get(j);
                            String customerNameAPI = arrayCustomerName.get(j);
                            userNameTextViewMenu.setText(customerNameAPI);
                            String customerNumberAPI = arrayMobileNo.get(j);
                            mobileTextViewMenu.setText("+" + customerNumberAPI);

                            isPersonalDetailsDone = isPersonalD.get(j);
                            isBankDetailsDone = isBankD.get(j);

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
        bidsListAdapter = new BidsReceivedAdapter(CustomerDashboardActivity.this, bidsList);
        bidsListRecyclerView.setAdapter(bidsListAdapter);
        bidsAcceptedAdapter = new BidsAcceptedAdapter(CustomerDashboardActivity.this, acceptedList);
        bidsAcceptedRecyclerView.setAdapter(bidsAcceptedAdapter);
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

                        if (obj.getString("bid_status").equals("submitted")) {
                            acceptedList.add(bidsAcceptedModel);
                        }
                    }

                    TextView noAcceptedLoads = (TextView) findViewById(R.id.customer_dashboard_no_load_accepted_text);
//                    for (int i=0; i< acceptedList.size(); i++){
//                        if (acceptedList.get(i).getBid_status().equals("FinalAccepted")){
                    if (acceptedList.size() > 0) {
                        noAcceptedLoads.setVisibility(View.GONE);
                        bidsAcceptedAdapter.updateData(acceptedList);
                    } else {
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
                loadAcceptedConstrain.setVisibility(View.INVISIBLE);
                bidsReceivedConstrain.setVisibility(View.VISIBLE);
                loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                break;

            case R.id.customer_dashboard_loads_accepted_button:
                loadAcceptedConstrain.setVisibility(View.VISIBLE);
                bidsReceivedConstrain.setVisibility(View.INVISIBLE);
                loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                break;
        }
    }

    public void onClickPostALoad(View view) {
        Intent intent = new Intent(CustomerDashboardActivity.this, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        startActivity(intent);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                Intent intent = new Intent(CustomerDashboardActivity.this, DashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);

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
                        bidsReceivedModel.setDrop_country(obj.getString("drop_country"));
                        bidsReceivedModel.setKm_approx(obj.getString("km_approx"));
                        bidsReceivedModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));

                        if (obj.getString("bid_status").equals("pending")) {
                            bidsList.add(bidsReceivedModel);
                        }
                    }
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
        TextView quoteBySP = (TextView) previewDialogAcceptANdBid.findViewById(R.id.dialog_accept_bid_bidder_quote_textview);
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

        quoteBySP.setText(obj.getSp_quote());
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
                startActivity(intent);
                finish();
                previewDialogAcceptANdBid.dismiss();
            }
        });

        submitResponseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBidStatusAsAccepted(obj.getSp_bid_id());
                updateCustomerBudgetOnResponse(obj.getSp_bid_id(), customerQuote.getText().toString());
                AlertDialog.Builder my_alert = new AlertDialog.Builder(CustomerDashboardActivity.this);
                my_alert.setTitle("Response submitted Successfully");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("mobile", phone);
                        startActivity(intent);
                        finish();
                        dialogInterface.dismiss();
                        previewDialogAcceptANdBid.dismiss();
                    }
                });
                my_alert.show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------
    private void updateCustomerBudgetOnResponse(String bidId, String cQuote) {

        UpdateCustomerBudget updateCustomerBudget = new UpdateCustomerBudget(cQuote);

        Call<UpdateCustomerBudget> call = bidService.updateCustomerBudget("" + bidId, updateCustomerBudget);

        call.enqueue(new Callback<UpdateCustomerBudget>() {
            @Override
            public void onResponse(Call<UpdateCustomerBudget> call, Response<UpdateCustomerBudget> response) {

            }

            @Override
            public void onFailure(Call<UpdateCustomerBudget> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------
    private void updateBidStatusFinalAccepted(String bidId) {

        UpdateBidStatusFinalAccepted updateBidStatusFinalAccepted = new UpdateBidStatusFinalAccepted("FinalAccepted");

        Call<UpdateBidStatusFinalAccepted> call = bidService.updateFinalAccepted("" + bidId, updateBidStatusFinalAccepted);

        call.enqueue(new Callback<UpdateBidStatusFinalAccepted>() {
            @Override
            public void onResponse(Call<UpdateBidStatusFinalAccepted> call, Response<UpdateBidStatusFinalAccepted> response) {

            }

            @Override
            public void onFailure(Call<UpdateBidStatusFinalAccepted> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------------------------
    private void updateBidStatusAsAccepted(String bidId) {

        UpdateBidStatusAccepted updateBidStatusAccepted = new UpdateBidStatusAccepted("Accepted");

        Call<UpdateBidStatusAccepted> call = bidService.updateBidStatusAccepted("" + bidId, updateBidStatusAccepted);

        call.enqueue(new Callback<UpdateBidStatusAccepted>() {
            @Override
            public void onResponse(Call<UpdateBidStatusAccepted> call, Response<UpdateBidStatusAccepted> response) {

            }

            @Override
            public void onFailure(Call<UpdateBidStatusAccepted> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------
    private void updateLoadStatusSubmitted(String loadId) {

        UpdateLoadStatusSubmitted updateLoadStatusSubmitted = new UpdateLoadStatusSubmitted("submitted");

        Call<UpdateLoadStatusSubmitted> call = postLoadService.updateBidStatusSubmitted("" + loadId, updateLoadStatusSubmitted);

        call.enqueue(new Callback<UpdateLoadStatusSubmitted>() {
            @Override
            public void onResponse(Call<UpdateLoadStatusSubmitted> call, retrofit2.Response<UpdateLoadStatusSubmitted> response) {

            }

            @Override
            public void onFailure(Call<UpdateLoadStatusSubmitted> call, Throwable t) {

            }
        });
    }
    //--------------------------------------------------------------------------------------------------

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
                    customerQuote.setText(budgetEditText);
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
    }


    public void getBidsResponsesList(BidsReceivedModel obj, RecyclerView bidsResponsesRecyclerView, TextView bidsReceived, ConstraintLayout showRecyclerView, ArrayList<BidsReceivedModel> loadList) {
        ArrayList<BidsResponsesModel> bidResponsesList = new ArrayList<>();
        bidResponsesList.clear();

        String url1 = getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
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

                        bidResponsesList.add(bidsResponsesModel);

                    }


                    for (int i = 0; i < bidResponsesList.size(); i++) {
                        if (obj.getIdpost_load().equals(bidResponsesList.get(i).getIdpost_load())) {

                            String bidsResponses = String.valueOf(bidResponsesList.size());
                            Log.i("bid size", String.valueOf(bidResponsesList.size()));
                            bidsReceived.setText(bidsResponses + " Responses Received");
                            Log.i("Res", bidsReceived.getText().toString());

                            if (bidResponsesList.get(i).getBid_status().equals("submitted")) {
                                bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                                bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                                bidsResponsesAdapter.updateData(bidResponsesList);

                            } else if (bidResponsesList.get(i).getBid_status().equals("Accepted")) {
                                int itemPos = bidResponsesList.indexOf(bidResponsesList.get(i));
                                bidResponsesList.add(0, bidResponsesList.get(i));
                                bidResponsesList.remove(itemPos + 1);

                                bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                                bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                                bidsResponsesAdapter.updateData(bidResponsesList);
                            } else if (bidResponsesList.get(i).getBid_status().equals("RespondedBySP")) {
                                bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                                bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                                bidsResponsesAdapter.updateData(bidResponsesList);
                            } else if (bidResponsesList.get(i).getBid_status().equals("FinalAccepted")) {

                            }

                        }
                    }

                    if (bidsReceived.getText().toString().equals("0 Responses Received")) {
                        showRecyclerView.setVisibility(View.GONE);
                        bidsResponsesRecyclerView.setVisibility(View.GONE);
                    } else {
                        showRecyclerView.setVisibility(View.VISIBLE);
                        bidsResponsesRecyclerView.setVisibility(View.VISIBLE);
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

                updateLoadStatusSubmitted(obj.getIdpost_load());
                updateBidStatusFinalAccepted(obj.getSp_bid_id());
                AlertDialog.Builder my_alert = new AlertDialog.Builder(CustomerDashboardActivity.this);
                my_alert.setTitle("Final Offer accepted Successfully");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("mobile", phone);
                        startActivity(intent);
                        finish();
                        dialogInterface.dismiss();
                        acceptFinalBid.dismiss();
                    }
                });
                my_alert.show();
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
                startActivity(intent);
                finish();
                acceptFinalBid.dismiss();
            }
        });
    }

    public void onClickViewConsignment(BidsAcceptedModel obj) {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(viewConsignmentCustomer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        viewConsignmentCustomer.show();
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
                    }

                    quoteBySP.setText(spQuoteByApi);
                    modelBySP.setText(obj.getVehicle_model());
                    feetBySP.setText(obj.getFeet());
                    capacityBySP.setText(obj.getCapacity());
                    bodyTypeBySP.setText(obj.getBody_type());
                    notesBySP.setText(noteByApi);
                    negotiableBySP.setText("No");
                    //----------------------------------------------------------------------------------------------------------------

                    customerQuote = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_customer_final_quote_textview);
                    negotiable_yes = viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_radio_btn_yes);
                    negotiable_no = viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_radio_btn_no);
                    notesCustomer = (EditText) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_notes_editText);
                    submitResponseBtn = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_submit_response_btn);
                    cancleBtn = (TextView) viewConsignmentCustomer.findViewById(R.id.dialog_accept_bid_cancel_btn);
                    timeLeftTextview = viewConsignmentCustomer.findViewById(R.id.accept_bid_time_left_textview);
                    timeLeft00 = viewConsignmentCustomer.findViewById(R.id.accept_bid_time_left_00_textview);

                    timeLeftTextview.setText("CONSIGNMENT");
                    timeLeft00.setVisibility(View.GONE);
                    timeLeftTextview.setTextColor(getResources().getColorStateList(R.color.black));
                    timeLeftTextview.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                    negotiable_yes.setChecked(false);
                    negotiable_yes.setEnabled(false);
                    negotiable_no.setChecked(true);
                    customerQuote.setText(spQuoteByApi);

                    submitResponseBtn.setText("Withdraw");
                    submitResponseBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey));
                    submitResponseBtn.setEnabled(false);

                    submitResponseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            updateLoadStatusSubmitted(obj.getIdpost_load());
                            updateBidStatusFinalAccepted(bid_idByAPI);
                            AlertDialog.Builder my_alert = new AlertDialog.Builder(CustomerDashboardActivity.this).setCancelable(false);
                            my_alert.setTitle("Withdrawn Successfully");
                            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CustomerDashboardActivity.this, CustomerDashboardActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("mobile", phone);
                                    startActivity(intent);
                                    finish();
                                    dialogInterface.dismiss();
                                    viewConsignmentCustomer.dismiss();
                                }
                            });
                            my_alert.show();
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
                            startActivity(intent);
                            finish();
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
                if (isPersonalDetailsDone.equals("1")) {
                    Intent intent = new Intent(CustomerDashboardActivity.this, ViewPersonalDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerDashboardActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
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
}