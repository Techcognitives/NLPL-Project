package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.Responses.PreferedLocationResponse;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.ui.activities.CustomerSettingsAndPreferences;
import com.nlpl.ui.activities.FindLoadsActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class PreferredLocationAdapter extends RecyclerView.Adapter<PreferredLocationAdapter.PreferredLocationViewHolder> {

    private ArrayList<PreferedLocationResponse.UserList> loadList;
    private CustomerSettingsAndPreferences activity;

    public PreferredLocationAdapter(CustomerSettingsAndPreferences activity, ArrayList<PreferedLocationResponse.UserList> loadList) {
        this.loadList = loadList;
        this.activity = activity;
    }

    @Override
    public PreferredLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list, parent, false);
        return new PreferredLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PreferredLocationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PreferedLocationResponse.UserList obj = loadList.get(position);
        holder.location.setText(obj.getPref_state() + ", " +obj.getPref_city());
        holder.deleteLocation.setOnClickListener(view -> activity.deleteLocation(obj));
    }

    @Override
    public int getItemCount() {
        return loadList.size();
    }

    public void refreshData(ArrayList<PreferedLocationResponse.UserList> loadList) {
        this.loadList = loadList;
        notifyDataSetChanged();
    }

    public class PreferredLocationViewHolder extends RecyclerView.ViewHolder {
        TextView location;
        ImageView deleteLocation;

        public PreferredLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.tvBody);
            deleteLocation = itemView.findViewById(R.id.delete_location);
        }

    }
//--------------------------------------------------------------------------------------------------
}