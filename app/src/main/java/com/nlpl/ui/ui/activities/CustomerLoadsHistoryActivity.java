package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.ui.ui.adapters.BidsReceivedAdapter;
import com.nlpl.ui.ui.adapters.LoadsExpiredAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CustomerLoadsHistoryActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<BidsReceivedModel> expiredLoadList = new ArrayList<>();
    private LoadsExpiredAdapter loadsExpiredAdapter;
    private RecyclerView expiredLoadsRecyclerView;
    private RequestQueue mQueue;
    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    String phone, userId;
    TextView loadsCompleted, loadsExpired;
    ConstraintLayout loadExpiredConstrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_loads_history);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(CustomerLoadsHistoryActivity.this);
        getExpiredLoads();
        //----------------------------Action Bar----------------------------------------------------
        actionBar = findViewById(R.id.customer_dashboard_load_history_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Loads History");
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLoadsHistoryActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        //------------------------------------------------------------------------------------------

//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_load_history);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//                RearrangeItems();
//            }
//        });

        loadsExpired = findViewById(R.id.customer_dashboard_loads_expired_button);
        loadsCompleted = findViewById(R.id.customer_dashboard_load_completed_button);
        loadExpiredConstrain = findViewById(R.id.customer_dashboard_loads_expired_constrain);
        expiredLoadsRecyclerView = findViewById(R.id.customer_dashboard_loads_expired_recycler_view);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(false);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.VERTICAL);
        expiredLoadsRecyclerView.setLayoutManager(linearLayoutManagerBank);
        expiredLoadsRecyclerView.setHasFixedSize(true);

        loadsExpiredAdapter = new LoadsExpiredAdapter(CustomerLoadsHistoryActivity.this, expiredLoadList);
        expiredLoadsRecyclerView.setAdapter(loadsExpiredAdapter);
    }

    private void RearrangeItems() {
        Intent intent = new Intent(CustomerLoadsHistoryActivity.this, CustomerLoadsHistoryActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

    public void getExpiredLoads() {

        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    expiredLoadList = new ArrayList<>();
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
                        bidsReceivedModel.setBid_ends_at(obj.getString("bid_ends_at"));

                        if (obj.getString("bid_status").equals("loadExpired")) {
                            expiredLoadList.add(bidsReceivedModel);
                        }
                    }

                    Collections.reverse(expiredLoadList);
                    TextView noLoadsExpiredTextView = (TextView) findViewById(R.id.customer_dashboard_no_load_expired_text);

                    if (expiredLoadList.size() > 0) {
                        noLoadsExpiredTextView.setVisibility(View.GONE);
                        loadsExpiredAdapter.updateData(expiredLoadList);
                    } else {
                        noLoadsExpiredTextView.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i8 = new Intent(CustomerLoadsHistoryActivity.this, CustomerDashboardActivity.class);
        i8.putExtra("mobile", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);
    }

    public void onClickLoadsCompleted(View view) {
        loadExpiredConstrain.setVisibility(View.INVISIBLE);
        loadsCompleted.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
        loadsExpired.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
    }

    public void onClickLoadsExpired(View view) {
        loadExpiredConstrain.setVisibility(View.VISIBLE);
        loadsExpired.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
        loadsCompleted.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
    }

    public void reActivateLoad(BidsReceivedModel obj) {

        //----------------------- Alert Dialog -------------------------------------------------
        Dialog reActivateLoad = new Dialog(CustomerLoadsHistoryActivity.this);
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
                Intent intent = new Intent(CustomerLoadsHistoryActivity.this, PostALoadActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                intent.putExtra("reActivate", true);
                intent.putExtra("isEdit", false);
                intent.putExtra("loadId", obj.getIdpost_load());
                startActivity(intent);
            }
        });

    }
}