package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.ui.ui.activities.CustomerDashboardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.util.ArrayList;

public class BidsReceivedAdapter extends RecyclerView.Adapter<BidsReceivedAdapter.BidsReceivedViewHolder> {

    private ArrayList<BidsReceivedModel> loadList;
    private CustomerDashboardActivity activity;

    private RequestQueue mQueue;
    private ArrayList<BidsResponsesModel> bidsResponseList = new ArrayList<>();
    private BidsResponsesAdapter bidsResponsesAdapter;

    public BidsReceivedAdapter(CustomerDashboardActivity activity, ArrayList<BidsReceivedModel> loadList) {
        this.loadList = loadList;
        this.activity = activity;
    }

    @Override
    public BidsReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_received_list, parent, false);
        return new BidsReceivedAdapter.BidsReceivedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BidsReceivedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsReceivedModel obj = loadList.get(position);

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String budget = obj.getBudget();
        holder.budget.setText("₹ " + budget);

        String date = obj.getPick_up_date();
        holder.date.setText("Date: " + date);

        String time = obj.getPick_up_time();
        holder.time.setText("Time: " + time);

        String approxKms = obj.getKm_approx();
        holder.distance.setText("Distance: " + approxKms);

        String model = obj.getVehicle_model();
        holder.model.setText("Model: " + model);

        String feet = obj.getFeet();
        holder.feet.setText("Feet: " + feet);

        String capacity = obj.getCapacity();
        holder.capacity.setText("Capacity: " + capacity);

        String bodyType = obj.getBody_type();
        holder.body.setText("Body: " + bodyType);

        String bidsResponses = String.valueOf(loadList.size());
        holder.bidsReceived.setText(bidsResponses + " Responses Received");

        mQueue = Volley.newRequestQueue(activity);
        getBidsResponses(obj.getUser_id());

        bidsResponsesAdapter = new BidsResponsesAdapter(activity, bidsResponseList);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(activity);
        linearLayoutManagerBank.setReverseLayout(true);
        holder.bidsReceivedRecyclerView.setLayoutManager(linearLayoutManagerBank);
        holder.bidsReceivedRecyclerView.setHasFixedSize(true);
        holder.bidsReceivedRecyclerView.setAdapter(bidsResponsesAdapter);

//        activity.getBidsResponses(obj, holder.bidsReceivedRecyclerView);
    }

    @Override
    public int getItemCount() {
        return loadList.size();
    }

    public void updateData(ArrayList<BidsReceivedModel> loadList) {
        this.loadList = loadList;
        notifyDataSetChanged();
    }

    public class BidsReceivedViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationStart, destinationEnd, budget, date, time, distance, model, feet, capacity, body, editLoadButton, bidsReceived;
        RecyclerView bidsReceivedRecyclerView;

        public BidsReceivedViewHolder(@NonNull View itemView) {
            super(itemView);

            destinationStart = itemView.findViewById(R.id.bids_received_pick_up);
            destinationEnd = itemView.findViewById(R.id.bids_responses_drop);
            budget = itemView.findViewById(R.id.bids_responses_budget);
            date = itemView.findViewById(R.id.bids_responses_pick_up_date);
            time = itemView.findViewById(R.id.bids_responses_pick_up_time);
            distance = itemView.findViewById(R.id.bids_responses_kms_approx);
            model = itemView.findViewById(R.id.bids_responses_model);
            feet = itemView.findViewById(R.id.bids_responses_feet);
            capacity = itemView.findViewById(R.id.bids_responses_capacity);
            body = itemView.findViewById(R.id.bids_responses_body);
            editLoadButton = itemView.findViewById(R.id.bids_responses_edit_load_button);
            bidsReceivedRecyclerView = itemView.findViewById(R.id.bids_received_recycler_view);
            bidsReceived = itemView.findViewById(R.id.bids_responses_no_of_responses);

        }

    }
//--------------------------------------------------------------------------------------------------

    public void getBidsResponses(String userId) {

        String url1 = activity.getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/"+userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bidsResponseList = new ArrayList<>();
                    JSONArray bidsResponsesLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsResponsesLists.length(); i++) {
                        JSONObject obj = bidsResponsesLists.getJSONObject(i);
                        BidsResponsesModel bidsResponsesModel = new BidsResponsesModel();
                        bidsResponsesModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsResponsesModel.setUser_id(obj.getString("user_id"));
                        bidsResponsesModel.setPick_up_date(obj.getString("pick_up_date"));
                        bidsResponsesModel.setPick_up_time(obj.getString("pick_up_time"));
                        bidsResponsesModel.setBudget(obj.getString("budget"));
                        bidsResponsesModel.setBid_status(obj.getString("bid_status"));
                        bidsResponsesModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidsResponsesModel.setFeet(obj.getString("feet"));
                        bidsResponsesModel.setCapacity(obj.getString("capacity"));
                        bidsResponsesModel.setBody_type(obj.getString("body_type"));
                        bidsResponsesModel.setPick_add(obj.getString("pick_add"));
                        bidsResponsesModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        bidsResponsesModel.setPick_city(obj.getString("pick_city"));
                        bidsResponsesModel.setPick_state(obj.getString("pick_state"));
                        bidsResponsesModel.setPick_country(obj.getString("pick_country"));
                        bidsResponsesModel.setDrop_add(obj.getString("drop_add"));
                        bidsResponsesModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        bidsResponsesModel.setDrop_city(obj.getString("drop_city"));
                        bidsResponsesModel.setDrop_state(obj.getString("drop_state"));
                        bidsResponsesModel.setDrop_country(obj.getString("drop_country"));
                        bidsResponsesModel.setKm_approx(obj.getString("km_approx"));
                        bidsResponsesModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidsResponseList.add(bidsResponsesModel);
                    }
                    if (bidsResponseList.size() > 0) {
                        bidsResponsesAdapter.updateData(bidsResponseList);
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
}