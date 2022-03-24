package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;
import com.nlpl.ui.activities.CustomerDashboardActivity;
import com.nlpl.ui.activities.TrackForLoadPosterActivity;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrackLPBidsResponsesAdapter extends RecyclerView.Adapter<TrackLPBidsResponsesAdapter.TrackLPBidsViewHolder> {

    private ArrayList<BidsResponsesModel> bidsResponsesList;
    private TrackForLoadPosterActivity activity;
    private RequestQueue mQueue;

    String name;

    public TrackLPBidsResponsesAdapter(TrackForLoadPosterActivity activity, ArrayList<BidsResponsesModel> bidsResponsesList) {
        this.bidsResponsesList = bidsResponsesList;
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public TrackLPBidsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_responses_list, parent, false);
        return new TrackLPBidsViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(TrackLPBidsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsResponsesModel obj = bidsResponsesList.get(position);
        //----------------------------------------------------------
        String url = activity.getString(R.string.baseURL) + "/user/" + obj.getUser_id();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        name = obj.getString("name");
                        String spName = name;
                        holder.spName.setText(spName);
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

        String url1 = activity.getString(R.string.baseURL) + "/imgbucket/Images/" + obj.getUser_id();
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");

                        String profileImgUrl = "";
                        if (imageType.equals("profile")) {
                            profileImgUrl = obj.getString("image_url");
                            new DownloadImageTask(holder.profilePictureSP).execute(profileImgUrl);
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
        mQueue.add(request1);

        holder.profilePictureSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.ViewProfileOfSPToCustomer(obj);
            }
        });

        String isNegotiable = obj.getIs_negatiable();
        if (isNegotiable.equals("1")) {
            holder.negotiable.setText(activity.getString(R.string.negotiable));
        } else {
            holder.negotiable.setText(activity.getString(R.string.Non_negotiable));
        }

        if (obj.getBid_status().equals("start")) {
            holder.negotiable.setText(activity.getString(R.string.Non_negotiable));
            holder.acceptViewBidButton.setText(activity.getString(R.string.Accept_Final_Offer_newLine));
            holder.acceptViewBidButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.redDark));

            holder.acceptViewBidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        String budget = obj.getSp_quote();
        holder.budget.setText("₹ " + budget);

    }

    @Override
    public int getItemCount() {
        return bidsResponsesList.size();
    }

    public void updateData(ArrayList<BidsResponsesModel> bidsResponsesList) {
        this.bidsResponsesList = bidsResponsesList;
        notifyDataSetChanged();
    }

    public class TrackLPBidsViewHolder extends RecyclerView.ViewHolder {
        private TextView spName, ratingFloat, negotiable, budget, acceptViewBidButton;
        private ImageView profilePictureSP;
        private RatingBar starRatings;

        public TrackLPBidsViewHolder(@NonNull View itemView) {
            super(itemView);

            spName = itemView.findViewById(R.id.bids_responses_sp_name);
            ratingFloat = itemView.findViewById(R.id.bids_responses_sp_rating_number);
            negotiable = itemView.findViewById(R.id.bids_responses_nego);
            budget = itemView.findViewById(R.id.bids_responses_budget_sp);
            acceptViewBidButton = itemView.findViewById(R.id.bids_responses_view_accept_bids);
            starRatings = itemView.findViewById(R.id.bids_responses_star_rating);
            profilePictureSP = itemView.findViewById(R.id.bids_responses_sp_profilePhto);

        }

    }
//--------------------------------------------------------------------------------------------------
}