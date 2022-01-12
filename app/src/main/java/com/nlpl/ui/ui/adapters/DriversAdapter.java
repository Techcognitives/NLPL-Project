package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.ui.ui.activities.DashboardActivity;

import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.DriverViewHolder> {

    private ArrayList<DriverModel> driverList;
    private DashboardActivity activity;

    public DriversAdapter(DashboardActivity activity, ArrayList<DriverModel> driverList) {
        this.driverList = driverList;
        this.activity = activity;
    }

    @Override
    public DriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_driver_list, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DriverViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DriverModel obj = driverList.get(position);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = obj.getDriver_name();
        Log.i("File Name:", name1);

        holder.list_title.setText(" " + name1);

        holder.list_driver_number.setText("+"+obj.getDriver_number());

        holder.list_driver_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+obj.getDriver_number()));
                activity.startActivity(i2);
            }
        });
//--------------------------------------------------------------------------------------------------
        holder.list_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getDriverDetails(obj);
            }
        });

        holder.list_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickPreviewDriverDetails(obj);
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
        private TextView list_title, list_edit, list_driver_number;
        private ImageView list_preview;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.my_driver_list_driver_name_text_view);
            list_edit = itemView.findViewById(R.id.my_driver_list_edit_text_view);
            list_preview = itemView.findViewById(R.id.my_driver_list_preview_image_view);
            list_driver_number = itemView.findViewById(R.id.my_driver_list_driver_phone_number);
        }

    }
//--------------------------------------------------------------------------------------------------
}