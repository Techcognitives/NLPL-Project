package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsAcceptedModel;
import com.nlpl.ui.ui.activities.CustomerDashboardActivity;
import com.nlpl.ui.ui.activities.DashboardActivity;

import java.util.ArrayList;

public class BidsAcceptedAdapter extends RecyclerView.Adapter<BidsAcceptedAdapter.BidsAcceptedViewHolder> {

    private ArrayList<BidsAcceptedModel> acceptedList;
    private CustomerDashboardActivity activity;

    public BidsAcceptedAdapter(CustomerDashboardActivity activity, ArrayList<BidsAcceptedModel> acceptedList) {
        this.acceptedList = acceptedList;
        this.activity = activity;
    }

    @Override
    public BidsAcceptedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_list, parent, false);
        return new BidsAcceptedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BidsAcceptedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsAcceptedModel obj = acceptedList.get(position);

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String budget = obj.getBudget();
        holder.budget.setText("₹" + budget);

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
        holder.pickUpLocation.setText(" "+pickUpLocation);


        holder.bidNowButton.setText("View Consignment");
        holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickViewConsignment(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return acceptedList.size();
    }

    public void updateData(ArrayList<BidsAcceptedModel> acceptedList) {
        this.acceptedList = acceptedList;
        notifyDataSetChanged();
    }

    public class BidsAcceptedViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationStart, destinationEnd, budget, date, time, distance, model, feet, capacity, body, pickUpLocation, bidNowButton;

        public BidsAcceptedViewHolder(@NonNull View itemView) {
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