package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.ui.activities.ViewDriverDetailsActivity;

import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.DriverViewHolder> {

    private ArrayList<DriverModel> driverList;
    private ViewDriverDetailsActivity activity;

    public DriversAdapter(ViewDriverDetailsActivity activity, ArrayList<DriverModel> driverList) {
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

        holder.list_title.setText(" " + obj.getDriver_name());

        String s1 = obj.getDriver_number().substring(2, 12);
        holder.list_driver_number.setText("+91 " + s1);

        holder.list_driver_email_id.setText(" " + obj.getDriver_emailId());

        try{
            if (obj.getAlternate_ph_no().equals("null")||obj.getAlternate_ph_no()==null){
                holder.alternateDriverNumber.setVisibility(View.GONE);
            }else {
                String s2 = obj.getAlternate_ph_no().substring(2, 12);
                holder.alternateDriverNumber.setText("+91 "+ s2);
            }
        }catch (Exception e){
            e.printStackTrace();
            holder.alternateDriverNumber.setVisibility(View.GONE);
        }

        holder.list_driver_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + obj.getDriver_number()));
                activity.startActivity(i2);
            }
        });

        holder.alternateDriverNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + obj.getAlternate_ph_no()));
                activity.startActivity(i2);
            }
        });

        holder.list_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getDriverDetails(obj);
            }
        });

        holder.list_preview_driver_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickPreviewDriverLicense(obj);
            }
        });

        holder.list_preview_driver_selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickPreviewDriverSelfie(obj);
            }
        });

        holder.list_preview_driver_bank_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickPreviewDriverBankDetails(obj);
            }
        });

        holder.list_preview_truck_assigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickPreviewAssignedTruckDetails(obj);
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
        private TextView list_title, list_edit, list_driver_number, alternateDriverNumber, list_driver_email_id, list_preview_driver_license, list_preview_driver_selfie, list_preview_driver_bank_details, list_preview_truck_assigned;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.my_driver_list_driver_name_text_view);
            list_edit = itemView.findViewById(R.id.my_driver_list_edit_text_view);
            list_preview_driver_license = itemView.findViewById(R.id.my_driver_list_preview_driver_license);
            list_driver_number = itemView.findViewById(R.id.my_driver_list_driver_phone_number);
            list_driver_email_id = itemView.findViewById(R.id.my_driver_list_driver_email_id);
            list_preview_driver_selfie = itemView.findViewById(R.id.my_driver_list_driver_selfie);
            list_preview_driver_bank_details = itemView.findViewById(R.id.my_driver_list_driver_bank_details);
            list_preview_truck_assigned = itemView.findViewById(R.id.my_driver_list_truck_details);
            alternateDriverNumber = itemView.findViewById(R.id.my_driver_list_driver_alternate_mobile_number);
        }

    }
//--------------------------------------------------------------------------------------------------
}