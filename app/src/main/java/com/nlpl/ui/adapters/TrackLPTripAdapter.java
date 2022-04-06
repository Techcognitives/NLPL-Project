package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
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
import com.nlpl.model.ModelForRecyclerView.BidsAcceptedModel;
import com.nlpl.ui.activities.CustomerDashboardActivity;
import com.nlpl.ui.activities.TrackForLoadPosterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class TrackLPTripAdapter extends RecyclerView.Adapter<TrackLPTripAdapter.TrackLPTripViewHolder> {

    private ArrayList<BidsAcceptedModel> acceptedList;
    private TrackForLoadPosterActivity activity;
    ArrayList<String> arrayBidId, arrayBidStatus;
    String fianlBidId, bidEndsAt, currentTimeToCompare, bidEndsAtStringTime, finalBidEndsAt, finalDate;
    int timeLeftToExpire, timeInMillisec, minLeftToExpire, months;
    private RequestQueue mQueue;

    public TrackLPTripAdapter(TrackForLoadPosterActivity activity, ArrayList<BidsAcceptedModel> acceptedList) {
        this.acceptedList = acceptedList;
        this.activity = activity;
    }

    @Override
    public TrackLPTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mQueue = Volley.newRequestQueue(activity);
        arrayBidId = new ArrayList<>();
        arrayBidStatus = new ArrayList<>();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_list, parent, false);
        return new TrackLPTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackLPTripViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsAcceptedModel obj = acceptedList.get(position);

        if (obj.getBid_ends_at().equals("null")) {
            bidEndsAt = "2022-02-01 12:05:11.598";
        } else {
            bidEndsAt = obj.getBid_ends_at();
        }

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String date = obj.getPick_up_date();
        holder.date.setText(activity.getString(R.string.pickUpDateWithColon) + date);

        String time = obj.getPick_up_time();
        holder.time.setText(activity.getString(R.string.timeOnly) + time);

        String approxKms = obj.getKm_approx();
        holder.distance.setText(activity.getString(R.string.distanceWithColon) + approxKms);

        String capacity = obj.getCapacity();
        holder.capacity.setText(activity.getString(R.string.Load_Type) + capacity);

        String bodyType = obj.getBody_type();
        holder.body.setText(activity.getString(R.string.bodyType) + bodyType);

        String pickUpLocation = obj.getPick_add();
        holder.pickUpLocation.setText(" " + pickUpLocation);

//        holder.budget.setText("₹" + obj.getBudget());

        String url = activity.getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
        Log.i("URL: ", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidResponsesLists = response.getJSONArray("data");
                    for (int i = 0; i < bidResponsesLists.length(); i++) {
                        JSONObject obj = bidResponsesLists.getJSONObject(i);
                        arrayBidId.add(obj.getString("sp_bid_id"));
                        arrayBidStatus.add(obj.getString("bid_status"));
                    }

                    for (int j = 0; j < arrayBidStatus.size(); j++) {

                        if (arrayBidStatus.get(j).equals("start")) {
                            fianlBidId = arrayBidId.get(j);
                            holder.timeLeft.setVisibility(View.INVISIBLE);
                            //----------------------------------------------------------
                            String url = activity.getString(R.string.baseURL) + "/spbid/bidDtByBidId/" + fianlBidId;
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray truckLists = response.getJSONArray("data");
                                        for (int i = 0; i < truckLists.length(); i++) {
                                            JSONObject obj1 = truckLists.getJSONObject(i);
                                            String bid_status = obj1.getString("bid_status");

                                            if (bid_status.equals("start")) {
//                                                holder.budget.setText("₹" + obj1.getString("is_bid_accpted_by_sp"));
                                                holder.bidNowButton.setText(activity.getString(R.string.View_Trip_Details));
                                                holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.green));
                                                holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        activity.onClickViewConsignment(obj);
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

        //----------------------------------------------------------
        String url1 = activity.getString(R.string.baseURL) + "/loadpost/getLoadDtByPostId/" + obj.getIdpost_load();
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        holder.budget.setText("₹" + obj.getString("budget"));
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

    @Override
    public int getItemCount() {
        return acceptedList.size();
    }

    public void updateData(ArrayList<BidsAcceptedModel> acceptedList) {
        this.acceptedList = acceptedList;
        notifyDataSetChanged();
    }

    public class TrackLPTripViewHolder extends RecyclerView.ViewHolder {
        private TextView timeLeft, destinationStart, destinationEnd, budget, date, time, distance, capacity, body, pickUpLocation, bidNowButton;

        public TrackLPTripViewHolder(@NonNull View itemView) {
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