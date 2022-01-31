package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidSubmittedModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.ui.ui.activities.DashboardActivity;
import com.nlpl.ui.ui.activities.PostALoadActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadSubmittedAdapter extends RecyclerView.Adapter<LoadSubmittedAdapter.LoadSubmittedViewHolder> {

    private ArrayList<BidSubmittedModel> loadSubmittedList;
    private DashboardActivity activity;
    private RequestQueue mQueue;

    public LoadSubmittedAdapter(DashboardActivity activity, ArrayList<BidSubmittedModel> loadSubmittedList) {
        this.loadSubmittedList = loadSubmittedList;
        this.activity = activity;
    }

    @Override
    public LoadSubmittedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mQueue = Volley.newRequestQueue(activity);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_list, parent, false);
        return new LoadSubmittedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadSubmittedViewHolder holder, @SuppressLint("RecyclerView") int position) {

        BidSubmittedModel obj = loadSubmittedList.get(position);

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

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

        String pickUpLocation = obj.getPick_add();
        holder.pickUpLocation.setText(" " + pickUpLocation);

        //----------------------------------------------------------
        String url = activity.getString(R.string.baseURL) + "/spbid/bidDtByBidId/" + obj.getBidId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj1 = truckLists.getJSONObject(i);
                        String bid_status = obj1.getString("bid_status");

                        if (bid_status.equals("submitted")) {
                            holder.budget.setText("₹" + obj.getBudget());
                            holder.bidNowButton.setText("Bid Submitted");
                        }

                        if (bid_status.equals("Accepted")) {
                            holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                            holder.bidNowButton.setText("Accept Revised");
                            holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.red));

                            holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.acceptRevisedBid(obj);
                                }
                            });
                        }

                        if (bid_status.equals("RespondedBySP")) {
                            holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                            holder.bidNowButton.setText("You Responded");
                            holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.button_blue));
                        }

                        if (bid_status.equals("FinalAccepted")) {
                            holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                            holder.bidNowButton.setText("View Consignment");
                            holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.green));
                            holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.viewConsignment(obj);
                                }
                            });
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
    }

    @Override
    public int getItemCount() {
        return loadSubmittedList.size();
    }

    public void updateData(ArrayList<BidSubmittedModel> loadSubmittedList) {
        this.loadSubmittedList = loadSubmittedList;
        notifyDataSetChanged();
    }

    public class LoadSubmittedViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationStart, destinationEnd, budget, date, time, distance, model, feet, capacity, body, pickUpLocation, bidNowButton;

        public LoadSubmittedViewHolder(@NonNull View itemView) {
            super(itemView);

            destinationStart = itemView.findViewById(R.id.load_list_pick_up);
            destinationEnd = itemView.findViewById(R.id.load_list_drop);
            budget = itemView.findViewById(R.id.load_list_budget);
            date = itemView.findViewById(R.id.load_list_pick_up_date);
            time = itemView.findViewById(R.id.load_list_pick_up_time);
            distance = itemView.findViewById(R.id.load_list_kms_approx);
            model = itemView.findViewById(R.id.load_list_model);
            feet = itemView.findViewById(R.id.load_list_feet);
            capacity = itemView.findViewById(R.id.load_list_capacity);
            body = itemView.findViewById(R.id.load_list_body);
            pickUpLocation = itemView.findViewById(R.id.load_list_location);
            bidNowButton = itemView.findViewById(R.id.load_list_bid_now_button);

        }
    }
//--------------------------------------------------------------------------------------------------
}