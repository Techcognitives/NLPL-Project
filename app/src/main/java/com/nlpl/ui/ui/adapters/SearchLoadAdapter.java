package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.ui.ui.activities.FindLoadsActivity;

import java.util.ArrayList;

public class SearchLoadAdapter extends RecyclerView.Adapter<SearchLoadAdapter.SearchLoadsViewHolder> {

    private FindLoadsActivity activity;
    ArrayList<SearchLoadModel> array_indian_states;

    public SearchLoadAdapter(FindLoadsActivity activity, ArrayList<SearchLoadModel> searchList) {
        this.activity = activity;
        this.array_indian_states = searchList;
    }

    @Override
    public SearchLoadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_load_list, parent, false);
        return new SearchLoadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchLoadsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SearchLoadModel obj = array_indian_states.get(position);
        String state = obj.getSearchList();
        holder.stateName.setText("  " + state);

        activity.setLoadCount(obj, holder.numberOfLoads);

        holder.findConstrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickFindLoadListItem(obj, holder.numberOfLoads);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array_indian_states.size();
    }

    public void updateData(ArrayList<SearchLoadModel> searchList) {
        array_indian_states = searchList;
        notifyDataSetChanged();
    }

    public class SearchLoadsViewHolder extends RecyclerView.ViewHolder {
        private TextView stateName, numberOfLoads;
        private ConstraintLayout findConstrain;

        public SearchLoadsViewHolder(@NonNull View itemView) {
            super(itemView);

            stateName = itemView.findViewById(R.id.find_load_list_state_name);
            numberOfLoads = itemView.findViewById(R.id.find_load_list_load_count);
            findConstrain = itemView.findViewById(R.id.find_load_list_constrain);

        }

    }
//--------------------------------------------------------------------------------------------------
}