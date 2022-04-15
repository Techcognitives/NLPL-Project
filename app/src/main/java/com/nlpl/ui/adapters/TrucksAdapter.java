package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.MainResponse;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.activities.ViewTruckDetailsActivity;

import java.util.ArrayList;

public class TrucksAdapter extends RecyclerView.Adapter<TrucksAdapter.TruckViewHolder> {

    private ArrayList<MainResponse.Data.TruckDetails> truckList;
    private ViewTruckDetailsActivity activity;

    public TrucksAdapter(ViewTruckDetailsActivity activity, ArrayList<MainResponse.Data.TruckDetails> truckList) {
        this.truckList = truckList;
        this.activity = activity;
    }

    @Override
    public TruckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_truck_list, parent, false);
        return new TruckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TruckViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MainResponse.Data.TruckDetails obj = truckList.get(position);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = obj.getVehicle_no();
        Log.i("File Name:", name1);

        holder.list_title.setText(" " + name1);
//--------------------------------------------------------------------------------------------------
        holder.list_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getTruckDetails(obj);
            }
        });

        holder.list_preview_rc_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getOnClickPreviewTruckRcBook(obj);
            }
        });

        holder.list_preview_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getOnClickPreviewTruckInsurance(obj);
            }
        });

        holder.list_view_driver_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getDriverDetailsOnTruckActivity(obj);
            }
        });

        holder.list_truck_type.setText(activity.getString(R.string.bodyType) + obj.getTruck_type());
        holder.list_capacity.setText(activity.getString(R.string.Load_Type) + obj.getTruck_carrying_capacity());

        holder.list_delete.setOnClickListener(view -> activity.deleteTruckDetails(obj));
    }

    @Override
    public int getItemCount() {
        return truckList.size();
    }

    public void updateData(ArrayList<MainResponse.Data.TruckDetails> truckList) {
        this.truckList = truckList;
        notifyDataSetChanged();
    }

    public class TruckViewHolder extends RecyclerView.ViewHolder {
        private TextView list_view_driver_details,  list_title, list_edit, list_truck_type, list_capacity, list_preview_rc_book, list_preview_insurance, list_delete;

        public TruckViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.my_truck_list_vehicle_number_text_view);
            list_edit = itemView.findViewById(R.id.my_truck_list_edit_text_view);
            list_preview_rc_book = itemView.findViewById(R.id.my_truck_list_preview_rc_book_text_view);
            list_preview_insurance = itemView.findViewById(R.id.my_truck_list_preview_insurance_text_view);
            list_truck_type = itemView.findViewById(R.id.my_truck_list_truck_type);
            list_capacity = itemView.findViewById(R.id.my_truck_list_capacity);
            list_view_driver_details = itemView.findViewById(R.id.my_truck_list_driver_details);
            list_delete = itemView.findViewById(R.id.my_truck_list_delete_text_view);

        }

    }
//--------------------------------------------------------------------------------------------------
}