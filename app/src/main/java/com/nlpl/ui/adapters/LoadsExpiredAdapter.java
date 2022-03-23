package com.nlpl.ui.adapters;

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
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.ui.activities.CustomerLoadsHistoryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadsExpiredAdapter extends RecyclerView.Adapter<LoadsExpiredAdapter.LoadExpiredViewHolder> {
    private ArrayList<BidsReceivedModel> expiredLoadList;
    private CustomerLoadsHistoryActivity activity;
    private RequestQueue mQueue;

    public LoadsExpiredAdapter(CustomerLoadsHistoryActivity activity, ArrayList<BidsReceivedModel> expiredLoadList) {
        this.expiredLoadList = expiredLoadList;
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public LoadsExpiredAdapter.LoadExpiredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_list, parent, false);
        return new LoadsExpiredAdapter.LoadExpiredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadsExpiredAdapter.LoadExpiredViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsReceivedModel obj = expiredLoadList.get(position);

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String date = obj.getPick_up_date();
        holder.date.setText(activity.getString(R.string.pick_up_date_colon) + date);

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

        holder.bidNowButton.setText(activity.getString(R.string.ReActivate_Load));
        holder.bidNowButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.dark_grey));
        holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.reActivateLoad(obj);
            }
        });

    }

    @Override
    public int getItemCount() {
        return expiredLoadList.size();
    }

    public void updateData(ArrayList<BidsReceivedModel> loadList) {
        this.expiredLoadList = loadList;
        notifyDataSetChanged();
    }

    public class LoadExpiredViewHolder extends RecyclerView.ViewHolder {
        private TextView timeLeft, destinationStart, destinationEnd, budget, date, time, distance, capacity, body, pickUpLocation, bidNowButton;

        public LoadExpiredViewHolder(@NonNull View itemView) {
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
}
