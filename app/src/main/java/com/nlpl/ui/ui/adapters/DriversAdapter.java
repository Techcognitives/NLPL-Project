package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.DriverModel;
import com.nlpl.ui.ui.activities.ProfileAndRegistrationActivity;

import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.DriverViewHolder> {

    private ArrayList<DriverModel> driverList;
    private ProfileAndRegistrationActivity activity;

    public DriversAdapter(ProfileAndRegistrationActivity activity, ArrayList<DriverModel> driverList) {
        this.driverList = driverList;
        this.activity = activity;
    }

    @Override
    public DriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_truck_list, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DriverViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DriverModel obj = driverList.get(position);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = obj.getDriver_name();
        Log.i("File Name:", name1);

        holder.list_title.setText(" " + name1);
//--------------------------------------------------------------------------------------------------
        holder.list_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getDriverDetails(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public void updateData(ArrayList<DriverModel> driverList) {
        this.driverList = driverList;
        notifyDataSetChanged();
    }

    public class DriverViewHolder extends RecyclerView.ViewHolder {
        private TextView list_title, list_edit;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.vehicleTextDone);
            list_edit = itemView.findViewById(R.id.vehicleEditDone);
        }

    }
//--------------------------------------------------------------------------------------------------
}