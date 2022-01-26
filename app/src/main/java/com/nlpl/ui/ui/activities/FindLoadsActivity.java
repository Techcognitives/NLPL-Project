package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

                    for (int i=0; i< bidsList.size(); i++){
                        if (bidsList.get(i).getBid_status().equals("pending")){
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
}