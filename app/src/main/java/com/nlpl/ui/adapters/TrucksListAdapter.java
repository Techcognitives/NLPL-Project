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
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.activities.ViewDriverDetailsActivity;
import com.nlpl.ui.activities.ViewTruckDetailsActivity;

import java.util.ArrayList;

public class TrucksListAdapter extends RecyclerView.Adapter<TrucksListAdapter.TruckListViewHolder> {

    private ArrayList<TruckModel> truckList;
    private ViewDriverDetailsActivity activity;

    public TrucksListAdapter(ViewDriverDetailsActivity activity, ArrayList<TruckModel> truckList) {
        this.truckList = truckList;
        this.activity = activity;
    }

    @Override
    public TruckListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_single, parent, false);
        return new TruckListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TruckListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TruckModel obj = truckList.get(position);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = obj.getVehicle_no();
        Log.i("File Name:", name1);

        holder.list_title.setText(" " + name1);
        holder.list_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickAssignTruckFromList(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return truckList.size();
    }

    public void updateData(ArrayList<TruckModel> truckList) {
        this.truckList = truckList;
        notifyDataSetChanged();
    }

    public class TruckListViewHolder extends RecyclerView.ViewHolder {
        private TextView list_title;

        public TruckListViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.text1);

        }

    }
//--------------------------------------------------------------------------------------------------
}