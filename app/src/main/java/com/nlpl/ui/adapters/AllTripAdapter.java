package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.model.Responses.TripResponse;
import com.nlpl.ui.activities.FindTripLPActivity;
import com.nlpl.ui.activities.FindTrucksActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllTripAdapter extends RecyclerView.Adapter<AllTripAdapter.SearchLoadsViewHolder> {

    private FindTripLPActivity activity;
    ArrayList<TripResponse.TripList> array_indian_states;

    public AllTripAdapter(FindTripLPActivity activity, ArrayList<TripResponse.TripList> searchList) {
        this.activity = activity;
        this.array_indian_states = searchList;
    }

    @Override
    public SearchLoadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list, parent, false);
        return new SearchLoadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchLoadsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TripResponse.TripList obj = array_indian_states.get(position);
        holder.startCity.setText(obj.getPick_city());
        holder.endCity.setText(obj.getDrop_city());
        holder.date.setText(activity.getString(R.string.Trip_Date_colon) + obj.getTrip_date());
        holder.time.setText(activity.getString(R.string.Trip_Time_colon) + obj.getTrip_start_time());
        holder.budget.setText("₹ " + obj.getTrip_budget());
        holder.body.setText(activity.getString(R.string.bodyType) + obj.getVehicle_model());
        holder.capacity.setText(activity.getString(R.string.Load_Type) + obj.getCapacity());
        holder.note.setText(activity.getString(R.string.Notes_colon) + obj.getNotes_meterial_des());
    }

    @Override
    public int getItemCount() {
        return array_indian_states.size();
    }

    public void updateData(ArrayList<TripResponse.TripList> searchList) {
        array_indian_states = searchList;
        notifyDataSetChanged();
    }

    public class SearchLoadsViewHolder extends RecyclerView.ViewHolder {
        private TextView startCity, endCity, date, time, capacity, body, budget, note;
        private ConstraintLayout tripConstrain;

        public SearchLoadsViewHolder(@NonNull View itemView) {
            super(itemView);

            startCity = itemView.findViewById(R.id.trip_list_pick_up);
            endCity = itemView.findViewById(R.id.trip_list_drop);
            date = itemView.findViewById(R.id.trip_list_pick_up_date);
            time = itemView.findViewById(R.id.load_list_pick_up_time);
            capacity = itemView.findViewById(R.id.trip_list_capacity);
            body = itemView.findViewById(R.id.trip_list_body);
            budget = itemView.findViewById(R.id.trip_list_budget);
            note = itemView.findViewById(R.id.trip_list_notes);
            tripConstrain = itemView.findViewById(R.id.trip_list_constrain);

        }

    }
//--------------------------------------------------------------------------------------------------
}