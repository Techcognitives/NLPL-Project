package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;
import com.nlpl.ui.ui.activities.CustomerDashboardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BidsResponsesAdapter extends RecyclerView.Adapter<BidsResponsesAdapter.BidsResponsesViewHolder> {

    private ArrayList<BidsResponsesModel> bidsResponsesList;
    private CustomerDashboardActivity activity;


    public BidsResponsesAdapter(CustomerDashboardActivity activity, ArrayList<BidsResponsesModel> bidsResponsesList) {
        this.bidsResponsesList = bidsResponsesList;
        this.activity = activity;
    }

    @Override
    public BidsResponsesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_responses_list, parent, false);
        return new BidsResponsesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BidsResponsesViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsResponsesModel obj = bidsResponsesList.get(position);

        String pickUpCity = obj.getPick_city();
        holder.spName.setText("  "+pickUpCity);
    }

    @Override
    public int getItemCount() {
        return bidsResponsesList.size();
    }

    public void updateData(ArrayList<BidsResponsesModel> bidsResponsesList) {
        this.bidsResponsesList = bidsResponsesList;
        notifyDataSetChanged();
    }

    public class BidsResponsesViewHolder extends RecyclerView.ViewHolder {
        private TextView spName, ratingFloat, negotiable, budget, acceptViewBidButton;
        private RatingBar starRatings;

        public BidsResponsesViewHolder(@NonNull View itemView) {
            super(itemView);

            spName = itemView.findViewById(R.id.bids_responses_sp_name);
            ratingFloat = itemView.findViewById(R.id.bids_responses_sp_rating_number);
            negotiable = itemView.findViewById(R.id.bids_responses_nego);
            budget = itemView.findViewById(R.id.bids_responses_budget_sp);
            acceptViewBidButton = itemView.findViewById(R.id.bids_responses_view_accept_bids);
            starRatings = itemView.findViewById(R.id.bids_responses_star_rating);

        }

    }
//--------------------------------------------------------------------------------------------------
}