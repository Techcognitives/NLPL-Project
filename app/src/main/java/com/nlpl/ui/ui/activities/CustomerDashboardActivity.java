package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.ui.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.ui.adapters.BidsResponsesAdapter;
import com.nlpl.ui.ui.adapters.LoadNotificationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomerDashboardActivity extends AppCompatActivity {

    private RequestQueue mQueue;

    private ArrayList<BidsReceivedModel> bidsList = new ArrayList<>();
    private BidsReceivedAdapter bidsListAdapter;
    private RecyclerView bidsListRecyclerView;
//    private ArrayList<BidsResponsesModel> bidResponsesList = new ArrayList<>();
    private BidsResponsesAdapter bidsResponsesAdapter;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    ConstraintLayout loadAcceptedConstrain, bidsReceivedConstrain;
    TextView loadAcceptedTextView, bidsReceivedTextView;

    String userId, phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
        }
        mQueue = Volley.newRequestQueue(CustomerDashboardActivity.this);
//        getBidResponsesByUserId();
        actionBar = findViewById(R.id.customer_dashboard_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Load Poster Dashboard");
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.customer_dashboard_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));

        loadAcceptedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_loads_accepted_constrain);
        bidsReceivedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_bids_received_constrain);
        loadAcceptedTextView = (TextView) findViewById(R.id.customer_dashboard_loads_accepted_button);
        bidsReceivedTextView = (TextView) findViewById(R.id.customer_dashboard_bids_received_button);

        //---------------------------- Get Load Details -------------------------------------------
        bidsListRecyclerView = (RecyclerView) findViewById(R.id.customer_dashboard_load_notification_recycler_view);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(true);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.VERTICAL);
        bidsListRecyclerView.setLayoutManager(linearLayoutManagerBank);
        bidsListRecyclerView.setHasFixedSize(true);

        bidsListAdapter = new BidsReceivedAdapter(CustomerDashboardActivity.this, bidsList);
        bidsListRecyclerView.setAdapter(bidsListAdapter);
        getBidsReceived();
        //------------------------------------------------------------------------------------------
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

    public void onClickBottomNavigation(View view){
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

        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/"+userId;
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
                        bidsList.add(bidsReceivedModel);
                    }
                    if (bidsList.size() > 0) {
                        bidsListAdapter.updateData(bidsList);
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

//    public void getBidResponsesByUserId() {
//
//        String url1 = getString(R.string.baseURL) + "/spbid/getBidDtByUserId/"+ userId;
//        Log.i("URL: ", url1);
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray bidResponsesLists = response.getJSONArray("data");
//                    for (int i = 0; i < bidResponsesLists.length(); i++) {
//                        JSONObject obj = bidResponsesLists.getJSONObject(i);
//                        BidsResponsesModel bidsResponsesModel = new BidsResponsesModel();
//                        bidsResponsesModel.setSp_bid_id(obj.getString("sp_bid_id"));
//                        bidsResponsesModel.setUser_id(obj.getString("user_id"));
//                        bidsResponsesModel.setIdpost_load(obj.getString("idpost_load"));
//                        bidsResponsesModel.setSp_quote(obj.getString("sp_quote"));
//                        bidsResponsesModel.setIs_negatiable(obj.getString("is_negatiable"));
//                        bidsResponsesModel.setAssigned_truck_id(obj.getString("assigned_truck_id"));
//                        bidsResponsesModel.setAssigned_driver_id(obj.getString("assigned_driver_id"));
//                        bidsResponsesModel.setVehicle_model(obj.getString("vehicle_model"));
//                        bidsResponsesModel.setFeet(obj.getString("feet"));
//                        bidsResponsesModel.setCapacity(obj.getString("capacity"));
//                        bidsResponsesModel.setBody_type(obj.getString("body_type"));
//                        bidsResponsesModel.setNotes(obj.getString("notes"));
//                        bidsResponsesModel.setBid_status(obj.getString("bid_status"));
//                        bidsResponsesModel.setIs_bid_accpted_by_sp(obj.getString("is_bid_accpted_by_sp"));
//                        bidResponsesList.add(bidsResponsesModel);
//                    }
////                    if (bidResponsesList.size() > 0) {
////                        bidsResponsesAdapter.updateData(bidResponsesList);
////                    } else {
////                    }
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

    public void onClickViewAndAcceptBid(BidsResponsesModel obj) {
    }

    public void onClickEditLoadPost(BidsReceivedModel obj) {
    }

    public void showRecyclerView(ConstraintLayout showRecyclerView, BidsReceivedModel obj, RecyclerView bidsResponsesRecyclerView) {
//        String isVisible = String.valueOf(bidsResponsesRecyclerView.getVisibility());
//        bidsResponsesRecyclerView.setVisibility(View.GONE);
//        Log.i("isVisible", isVisible);
//        if (isVisible.equals("8")){
//            bidsResponsesRecyclerView.setVisibility(View.VISIBLE);
//        }else{
//            bidsResponsesRecyclerView.setVisibility(View.GONE);
//        }
    }

    public void getBidsResponsesList(BidsReceivedModel obj, RecyclerView bidsResponsesRecyclerView) {
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
//                    if (bidResponsesList.size() > 0) {
//                        bidsResponsesAdapter.updateData(bidResponsesList);
//                    } else {
//                    }

                    for (int i = 0; i < bidResponsesList.size(); i++) {
                        if (obj.getIdpost_load().equals(bidResponsesList.get(i).getIdpost_load())) {
                            bidsResponsesAdapter = new BidsResponsesAdapter(CustomerDashboardActivity.this, bidResponsesList);
                            bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
                            bidsResponsesAdapter.updateData(bidResponsesList);
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
}