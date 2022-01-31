package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
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
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.ui.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.ui.adapters.FindLoadAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindLoadsActivity extends AppCompatActivity {

    private RequestQueue mQueue;

    private ArrayList<FindLoadsModel> bidsList = new ArrayList<>();
    private FindLoadAdapter bidsListAdapter;
    private RecyclerView bidsListRecyclerView;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String phone, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_loads);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }
        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.find_loads_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Load Notifications");
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindLoadsActivity.this.finish();
            }
        });

        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.find_loads_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText("Find Loads");
        profileImageView.setImageDrawable(getDrawable(R.drawable.find_small));
        //---------------------------- Get Bank Details --------------------------------------------

        mQueue = Volley.newRequestQueue(FindLoadsActivity.this);
        bidsListRecyclerView = (RecyclerView) findViewById(R.id.find_loads_recycler_view);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(false);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.HORIZONTAL);
        bidsListRecyclerView.setLayoutManager(linearLayoutManagerBank);
        bidsListRecyclerView.setHasFixedSize(true);

        bidsListAdapter = new FindLoadAdapter(FindLoadsActivity.this, bidsList);
        bidsListRecyclerView.setAdapter(bidsListAdapter);
        getBidsReceived();
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                Intent intent = new Intent(FindLoadsActivity.this, DashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                break;

            case R.id.bottom_nav_customer_dashboard:
                break;
        }
    }

    public void getBidsReceived() {

        String url1 = getString(R.string.baseURL) + "/loadpost/getAllPosts";
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bidsList = new ArrayList<>();
                    JSONArray bidsLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsLists.length(); i++) {
                        JSONObject obj = bidsLists.getJSONObject(i);
                        FindLoadsModel findLoadsModel = new FindLoadsModel();
                        findLoadsModel.setIdpost_load(obj.getString("idpost_load"));
                        findLoadsModel.setUser_id(obj.getString("user_id"));
                        findLoadsModel.setPick_up_date(obj.getString("pick_up_date"));
                        findLoadsModel.setPick_up_time(obj.getString("pick_up_time"));
                        findLoadsModel.setBudget(obj.getString("budget"));
                        findLoadsModel.setBid_status(obj.getString("bid_status"));
                        findLoadsModel.setVehicle_model(obj.getString("vehicle_model"));
                        findLoadsModel.setFeet(obj.getString("feet"));
                        findLoadsModel.setCapacity(obj.getString("capacity"));
                        findLoadsModel.setBody_type(obj.getString("body_type"));
                        findLoadsModel.setPick_add(obj.getString("pick_add"));
                        findLoadsModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        findLoadsModel.setPick_city(obj.getString("pick_city"));
                        findLoadsModel.setPick_state(obj.getString("pick_state"));
                        findLoadsModel.setPick_country(obj.getString("pick_country"));
                        findLoadsModel.setDrop_add(obj.getString("drop_add"));
                        findLoadsModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        findLoadsModel.setDrop_city(obj.getString("drop_city"));
                        findLoadsModel.setDrop_state(obj.getString("drop_state"));
                        findLoadsModel.setDrop_country(obj.getString("drop_country"));
                        findLoadsModel.setKm_approx(obj.getString("km_approx"));
                        findLoadsModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidsList.add(findLoadsModel);
                    }

                    for (int i = 0; i < bidsList.size(); i++) {
                        if (bidsList.get(i).getBid_status().equals("loadPosted")) {
                            if (bidsList.size() == 0) {
                                bidsListRecyclerView.setVisibility(View.GONE);
                            } else if (bidsList.size() == 1) {
                                ArrayList<FindLoadsModel> newList = new ArrayList<>(bidsList.subList(bidsList.size() - 1, bidsList.size()));
                                bidsListAdapter.updateData(newList);
                            } else if (bidsList.size() == 2) {
                                ArrayList<FindLoadsModel> newList = new ArrayList<>(bidsList.subList(bidsList.size() - 2, bidsList.size()));
                                bidsListAdapter.updateData(newList);
                            } else if (bidsList.size() >= 3) {
                                ArrayList<FindLoadsModel> newList = new ArrayList<>(bidsList.subList(bidsList.size() - 3, bidsList.size()));
                                bidsListAdapter.updateData(newList);
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
        mQueue.add(request);
        //-------------------------------------------------------------------------------------------
    }

    public void onClickShiftRecyclerviewToLeft(View view) {
        bidsListRecyclerView.setAdapter(bidsListAdapter);
    }

    public void onClickShiftRecyclerviewToRight(View view) {
        bidsListRecyclerView.scrollToPosition(bidsListAdapter.getItemCount() - 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i8 = new Intent(FindLoadsActivity.this, DashboardActivity.class);
        i8.putExtra("mobile2", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);

    }
}