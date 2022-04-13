package com.nlpl.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidSubmittedModel;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;

import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.model.Requests.BidLoadRequest;
import com.nlpl.model.Responses.BidLadResponse;

import com.nlpl.model.Responses.TripResponse;

import com.nlpl.ui.adapters.TripListAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindLoadsActivity extends AppCompat {

    ConstraintLayout tripConstrain, truckConstrain;
    View tripUnderline, truckUnderline;
    TextView tripText, truckText;

    View actionBar;
    TextView actionBarTitle, noTrips;
    ImageView actionBarBackButton, actionBarMenuButton;

    String phone, userId;
    SwipeRefreshLayout swipeRefreshLayout;

    //----------------------------------------------------------------------------------------------
    ArrayList<TripResponse.TripList> tripList = new ArrayList<>();
    TripListAdapter tripListAdapter;
    RecyclerView tripListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_loads);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
            if (userId != null) {
                Log.i("userId find loads", userId);
            }
        }

        //-------------------------- Initialization ------------------------------------------------
        tripText = findViewById(R.id.find_loads_find_trip_text);
        truckText = findViewById(R.id.find_loads_find_truck_text);
        tripUnderline = findViewById(R.id.find_loads_find_trip_view);
        truckUnderline = findViewById(R.id.find_loads_find_truck_view);
        tripConstrain = findViewById(R.id.load_trip_constrain);
        truckConstrain = findViewById(R.id.find_load_constrain);
        noTrips = findViewById(R.id.find_trips_no_trips);

        truckText.setVisibility(View.GONE);

        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.find_loads_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.Trips));
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, true, true);
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        View bottomNav = findViewById(R.id.find_loads_bottom_nav_bar);
        ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        ConstraintLayout customerDashboard = bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        customerDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        TextView profileText = bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText(getString(R.string.Trips));
        profileImageView.setImageDrawable(getDrawable(R.drawable.black_truck_small));
        View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
        spView.setVisibility(View.INVISIBLE);
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
        customerView.setVisibility(View.VISIBLE);
        ConstraintLayout truck = findViewById(R.id.bottom_nav_trip);
        truck.setVisibility(View.GONE);

        //---------------------------- Get Bank Details --------------------------------------------
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.find_loads_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });

        //------------------------------------------------------------------------------------------
        tripListRecyclerView = (RecyclerView) findViewById(R.id.find_trips_recycler_view);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        tripListRecyclerView.setLayoutManager(linearLayoutManager1);
        tripListRecyclerView.setHasFixedSize(true);

        tripListAdapter = new TripListAdapter(FindLoadsActivity.this, tripList);
        tripListRecyclerView.setAdapter(tripListAdapter);
        checkTrip();
    }

    private void checkTrip() {
        Call<TripResponse> tripModelClass = ApiClient.getPostTripService().getTripDetailsByUserId(userId);
        tripModelClass.enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                TripResponse tripModelClass1 = response.body();
                TripResponse.TripList list = tripModelClass1.getData().get(0);
                if (response.isSuccessful()) tripList.addAll(tripModelClass1.getData());
                tripListAdapter.updateData(tripList);
                if (tripList.size() == 0) noTrips.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {

            }
        });
    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(FindLoadsActivity.this);
        JumpTo.goToFindLoadsActivity(FindLoadsActivity.this, userId, phone, true);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(FindLoadsActivity.this);
                JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, true, true);
                break;

            case R.id.bottom_nav_customer_dashboard:
                ShowAlert.loadingDialog(FindLoadsActivity.this);
                RearrangeItems();
                break;

            case R.id.bottom_nav_track:
                ShowAlert.loadingDialog(FindLoadsActivity.this);
                JumpTo.goToSPTrackActivity(FindLoadsActivity.this, phone, false);
                break;

            case R.id.bottom_nav_profile_image:
                ShowAlert.loadingDialog(FindLoadsActivity.this);
                JumpTo.goToViewPersonalDetailsActivity(FindLoadsActivity.this, userId, phone, true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, true, true);
    }

    public void onClickTripLoads(View view) {
        switch (view.getId()) {
            case R.id.find_loads_find_trip_text:

                truckConstrain.setVisibility(View.INVISIBLE);
                tripConstrain.setVisibility(View.VISIBLE);
                tripText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                truckText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                tripUnderline.setVisibility(View.VISIBLE);
                truckUnderline.setVisibility(View.INVISIBLE);
                break;

            case R.id.find_loads_find_truck_text:

                truckConstrain.setVisibility(View.VISIBLE);
                tripConstrain.setVisibility(View.INVISIBLE);
                tripText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                truckText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                tripUnderline.setVisibility(View.INVISIBLE);
                truckUnderline.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onClickEditTrip(TripResponse.TripList obj) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(FindLoadsActivity.this);
        alert.setContentView(R.layout.dialog_alert);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(false);

        TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText(getString(R.string.Edit_Trip));
        alertMessage.setText(getString(R.string.Would_you_like_to_edit_your_Trip));
        alertPositiveButton.setText(getString(R.string.edit));
        alertNegativeButton.setText(getString(R.string.cancel));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alertPositiveButton.setOnClickListener(view1 -> {
            alert.dismiss();
            ShowAlert.loadingDialog(FindLoadsActivity.this);
            JumpTo.goToPostATrip(FindLoadsActivity.this, "" + phone, "" + userId, true, "" + obj.getTrip_id(), false);
        });
        //------------------------------------------------------------------------------------------
    }
}