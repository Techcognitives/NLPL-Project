package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private ArrayList<BidsResponsesModel> bidResponsesList = new ArrayList<>();
    BidsResponsesAdapter bidsResponsesAdapter;
    private RequestQueue mQueue;

    public BidsReceivedAdapter(CustomerDashboardActivity activity, ArrayList<BidsReceivedModel> loadList) {
        this.loadList = loadList;
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
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

//        String bidsResponses = String.valueOf(bidResponsesList.size());
//        holder.bidsReceived.setText(bidsResponses + " Responses Received");

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(activity);
        linearLayoutManagerBank.setReverseLayout(true);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.VERTICAL);
        holder.bidsResponsesRecyclerView.setLayoutManager(linearLayoutManagerBank);
        holder.bidsResponsesRecyclerView.setHasFixedSize(true);

        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView);

//        holder.showRecyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bidResponsesList.clear();
//
//                String url1 = activity.getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
//                Log.i("URL: ", url1);
//
//                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray bidResponsesLists = response.getJSONArray("data");
//                            for (int i = 0; i < bidResponsesLists.length(); i++) {
//                                JSONObject obj = bidResponsesLists.getJSONObject(i);
//                                BidsResponsesModel bidsResponsesModel = new BidsResponsesModel();
//                                bidsResponsesModel.setSp_bid_id(obj.getString("sp_bid_id"));
//                                bidsResponsesModel.setUser_id(obj.getString("user_id"));
//                                bidsResponsesModel.setIdpost_load(obj.getString("idpost_load"));
//                                bidsResponsesModel.setSp_quote(obj.getString("sp_quote"));
//                                bidsResponsesModel.setIs_negatiable(obj.getString("is_negatiable"));
//                                bidsResponsesModel.setAssigned_truck_id(obj.getString("assigned_truck_id"));
//                                bidsResponsesModel.setAssigned_driver_id(obj.getString("assigned_driver_id"));
//                                bidsResponsesModel.setVehicle_model(obj.getString("vehicle_model"));
//                                bidsResponsesModel.setFeet(obj.getString("feet"));
//                                bidsResponsesModel.setCapacity(obj.getString("capacity"));
//                                bidsResponsesModel.setBody_type(obj.getString("body_type"));
//                                bidsResponsesModel.setNotes(obj.getString("notes"));
//                                bidsResponsesModel.setBid_status(obj.getString("bid_status"));
//                                bidsResponsesModel.setIs_bid_accpted_by_sp(obj.getString("is_bid_accpted_by_sp"));
//                                bidResponsesList.add(bidsResponsesModel);
//                            }
////                    if (bidResponsesList.size() > 0) {
////                        bidsResponsesAdapter.updateData(bidResponsesList);
////                    } else {
////                    }
//                            for (int i = 0; i < bidResponsesList.size(); i++) {
//                                if (obj.getIdpost_load().equals(bidResponsesList.get(i).getIdpost_load())) {
//                                    bidsResponsesAdapter = new BidsResponsesAdapter(activity, bidResponsesList);
//                                    holder.bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
//                                    bidsResponsesAdapter.updateData(bidResponsesList);
//                                    activity.showRecyclerView(holder.showRecyclerView, obj, holder.bidsResponsesRecyclerView);
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//                mQueue.add(request);
//                //-------------------------------------------------------------------------------------------
//            }
//        });


//        for (int i = 0; i < bidResponsesListNumber.size(); i++) {
//            if (obj.getIdpost_load().equals(bidResponsesList.get(i).getIdpost_load())) {
//                getBidResponses(obj.getIdpost_load());
//                bidsResponsesAdapter = new BidsResponsesAdapter(activity, bidResponsesList);
//                holder.bidsResponsesRecyclerView.setAdapter(bidsResponsesAdapter);
//                bidsResponsesAdapter.updateData(bidResponsesList);
//            }else{
//
//            }
//        }

        holder.editLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickEditLoadPost(obj);
            }
        });

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
        RecyclerView bidsResponsesRecyclerView;
        ConstraintLayout showRecyclerView;

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
            bidsReceived = itemView.findViewById(R.id.bids_responses_no_of_responses);
            bidsResponsesRecyclerView = itemView.findViewById(R.id.bids_received_recycler_view);
            showRecyclerView = itemView.findViewById(R.id.bids_received_show_recycler_view_constrain);

        }

    }

//    public void getBidResponses(String loadId) {
//
//        String url1 = activity.getString(R.string.baseURL) + "/spbid/getBidDtByPostId/"+ obj.getIdpost_load();
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

}