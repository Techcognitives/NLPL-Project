package com.nlpl.ui.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nlpl.R;
import com.nlpl.model.MapsModel.LocationModel;
import com.nlpl.model.Responses.RatingResponse;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.ui.activities.FindTrucksActivity;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.GetUserDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapTextInfoAdapter implements GoogleMap.InfoWindowAdapter {
    FindTrucksActivity activity;
    ArrayList<UserResponse.UserList> userList;

    public GoogleMapTextInfoAdapter(FindTrucksActivity findTrucksActivity, ArrayList<UserResponse.UserList> userList) {
        activity = findTrucksActivity;
        this.userList = userList;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        View view = ((Activity) activity).getLayoutInflater().inflate(R.layout.bids_responses_list, null);

        TextView city = view.findViewById(R.id.bids_responses_sp_name);
        city.setLayoutParams(new TextSwitcher.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ImageView imageView = view.findViewById(R.id.bids_responses_sp_profilePhto);
        imageView.setVisibility(View.INVISIBLE);
        TextView nego = view.findViewById(R.id.bids_responses_nego);
        nego.setVisibility(View.GONE);
        TextView budget = view.findViewById(R.id.bids_responses_budget_sp);
        budget.setVisibility(View.GONE);
        TextView button = view.findViewById(R.id.bids_responses_view_accept_bids);
        button.setVisibility(View.GONE);
        TextView ratings = view.findViewById(R.id.bids_responses_sp_rating_number);
        RatingBar ratingBar = view.findViewById(R.id.bids_responses_star_rating);
        ratingBar.setVisibility(View.GONE);

        String URLString = "https://findyourtruck.in/images/logo.png";

        Picasso.get()
                .load(URLString)
                .error(R.drawable.delete_icon)
                .into(imageView, new MarkerCallBack(marker));

        for (int i = 0; i < userList.size(); i++) {
            if (marker.getTitle().equals(userList.get(i).getUser_id())) {
                city.setText(userList.get(i).getName());
                ratings.setText(userList.get(i).getUser_type());
            }
        }

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    private class MarkerCallBack implements com.squareup.picasso.Callback {
        Marker marker = null;

        public MarkerCallBack(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }

        @Override
        public void onError(Exception e) {
            Log.e(getClass().getSimpleName(), "Error Loading thumbnail");
        }
    }
}
