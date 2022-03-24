package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.CountDownTimer;
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
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.ui.activities.ServiceProviderDashboardActivity;
import com.nlpl.ui.activities.TrackForServiceProviderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class TrackSPTripAdapter extends RecyclerView.Adapter<TrackSPTripAdapter.TrackSPTripViewHolder> {

    private ArrayList<BidSubmittedModel> loadSubmittedList;
    private TrackForServiceProviderActivity activity;
    private RequestQueue mQueue;
    String bidEndsAt;

    public TrackSPTripAdapter(TrackForServiceProviderActivity activity, ArrayList<BidSubmittedModel> loadSubmittedList) {
        this.loadSubmittedList = loadSubmittedList;
        this.activity = activity;
    }

    @Override
    public TrackSPTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mQueue = Volley.newRequestQueue(activity);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_list, parent, false);
        return new TrackSPTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackSPTripViewHolder holder, @SuppressLint("RecyclerView") int position) {

        BidSubmittedModel obj = loadSubmittedList.get(position);

        if (obj.getBid_ends_at().equals("null")) {
            bidEndsAt = "2022-02-01 12:05:11.598";
        } else {
            bidEndsAt = obj.getBid_ends_at();
        }
        //------------------------------------------------------------------------------------------

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String date = obj.getPick_up_date();
        holder.date.setText(activity.getString(R.string.pick_up_date_colon) + date);

        String time = obj.getPick_up_time();
        holder.time.setText("" + time);

        String approxKms = obj.getKm_approx();
        holder.distance.setText("" + approxKms);

        String capacity = obj.getCapacity();
        holder.capacity.setText(activity.getString(R.string.Load_Type) + capacity);

        String bodyType = obj.getBody_type();
        holder.body.setText(activity.getString(R.string.bodyType) + bodyType);

        String pickUpLocation = obj.getPick_add();
        holder.pickUpLocation.setText(" " + pickUpLocation);
        holder.pickUpLocation.setOnClickListener(view -> activity.openMaps(obj));

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

                        if (bid_status.equals("start")) {
                            holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                            holder.bidNowButton.setText("View Trip Details");
                            holder.timeLeft.setVisibility(View.INVISIBLE);
                            holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.green));
                            holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.viewConsignment(obj);
                                }
                            });
                        }

                        if (bid_status.equals("withdrawnByLp")) {
                            holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                            holder.bidNowButton.setText(activity.getString(R.string.Customer_Withdrawn));
                            holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.dark_grey));
                        }

                        if (bid_status.equals("withdrawnBySp")) {
                            holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                            holder.bidNowButton.setText(activity.getString(R.string.You_Withdrawn));
                            holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.dark_grey));
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

    public class TrackSPTripViewHolder extends RecyclerView.ViewHolder {
        private TextView timeLeft, destinationStart, destinationEnd, budget, date, time, distance, capacity, body, pickUpLocation, bidNowButton;

        public TrackSPTripViewHolder(@NonNull View itemView) {
            super(itemView);

            timeLeft = itemView.findViewById(R.id.load_list_time_left);
            destinationStart = itemView.findViewById(R.id.load_list_pick_up);
            destinationEnd = itemView.findViewById(R.id.load_list_drop);
            budget = itemView.findViewById(R.id.load_list_budget);
            date = itemView.findViewById(R.id.load_list_pick_up_date);
            time = itemView.findViewById(R.id.load_list_pick_up_time);
            distance = itemView.findViewById(R.id.load_list_kms_approx);
            capacity = itemView.findViewById(R.id.load_list_capacity);
            body = itemView.findViewById(R.id.load_list_body);
            pickUpLocation = itemView.findViewById(R.id.load_list_location);
            bidNowButton = itemView.findViewById(R.id.load_list_bid_now_button);

        }
    }
//--------------------------------------------------------------------------------------------------
}