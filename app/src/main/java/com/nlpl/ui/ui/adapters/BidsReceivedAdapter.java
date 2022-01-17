package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.ui.ui.activities.CustomerDashboardActivity;

import java.util.ArrayList;

public class BidsReceivedAdapter extends RecyclerView.Adapter<BidsReceivedAdapter.BidsReceivedViewHolder> {

    private ArrayList<BidsReceivedModel> loadList;
    private CustomerDashboardActivity activity;


    public BidsReceivedAdapter(CustomerDashboardActivity activity, ArrayList<BidsReceivedModel> loadList) {
        this.loadList = loadList;
        this.activity = activity;
    }

    @Override
    public BidsReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_received_list, parent, false);
        return new BidsReceivedViewHolder(view);
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

        activity.getBidsResponses(obj, holder.bidsReceivedRecyclerView);
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
}