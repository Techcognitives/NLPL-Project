package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.model.Responses.TripResponse;
import com.nlpl.ui.adapters.AllTripAdapter;
import com.nlpl.ui.adapters.SearchTripAdapter;
import com.nlpl.ui.adapters.SearchTripAdapterDrop;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTripLPActivity extends AppCompatActivity {

    String phone, userId;
    ConstraintLayout tripConstrain, truckConstrain;
    View tripUnderline, truckUnderline;
    TextView tripText, truckText, selectState, selectCity, noTrips;

    ArrayList<TripResponse.TripList> tripList = new ArrayList<>();
    AllTripAdapter allTripAdapter;

    RecyclerView allTripsRecyclerView;
    Spinner findTripSpinner;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog loadingDialog;

    //------------------------------------- State List ---------------------------------------------
    private ArrayList<TripResponse.TripList> anList, apList, arList, asList, brList, chList, cgList, ddList,
            dd2List, dlList, gaList, gjList, hrList, hpList, jkList, jhList, kaList, klList, laList,
            ldList, mpList, mhList, mnList, mlList, mzList, nlList, odList, pyList, pbList, rjList,
            skList, tnList, tsList, trList, ukList, upList, wbList;
    private ArrayList<SearchLoadModel> searchLoadModels = new ArrayList<>();
    ArrayList<SearchLoadModel> searchList;
    private SearchTripAdapter searchLoadAdapter;
    private RecyclerView searchListRecyclerView;

    RecyclerView searchListRecyclerViewDrop;
    private SearchTripAdapterDrop searchLoadAdapterDrop;
    private ArrayList<TripResponse.TripList> anListD, apListD, arListD, asListD, brListD, chListD, cgListD, ddListD,
            dd2ListD, dlListD, gaListD, gjListD, hrListD, hpListD, jkListD, jhListD, kaListD, klListD, laListD,
            ldListD, mpListD, mhListD, mnListD, mlListD, mzListD, nlListD, odListD, pyListD, pbListD, rjListD,
            skListD, tnListD, tsListD, trListD, ukListD, upListD, wbListD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trip_lpactivity);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }

        //------------------------------------------------------------------------------------------
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);
        //------------------------------------------------------------------------------------------

        //-------------------------- Initialization ------------------------------------------------
        tripText = findViewById(R.id.find_trucks_find_trip_text);
        truckText = findViewById(R.id.find_trucks_find_truck_text);
        tripUnderline = findViewById(R.id.find_trucks_find_trip_view);
        truckUnderline = findViewById(R.id.find_trucks_find_truck_view);
        truckConstrain = findViewById(R.id.find_trucks_find_trips_constrain);
        tripConstrain = findViewById(R.id.find_trip_all_trips);
        selectState = findViewById(R.id.find_trip_select_state);
        selectCity = findViewById(R.id.find_trip_select_city);
        noTrips = findViewById(R.id.find_trips_no_trips);
        findTripSpinner = findViewById(R.id.find_trips_spinner);
        findTripSpinner.setOnItemSelectedListener(onPickOrDrop);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.find_trips_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });

        anList = new ArrayList<>();
        apList = new ArrayList<>();
        arList = new ArrayList<>();
        asList = new ArrayList<>();
        brList = new ArrayList<>();
        chList = new ArrayList<>();
        cgList = new ArrayList<>();
        ddList = new ArrayList<>();
        dd2List = new ArrayList<>();
        dlList = new ArrayList<>();
        gaList = new ArrayList<>();
        gjList = new ArrayList<>();
        hrList = new ArrayList<>();
        hpList = new ArrayList<>();
        jkList = new ArrayList<>();
        jhList = new ArrayList<>();
        kaList = new ArrayList<>();
        klList = new ArrayList<>();
        laList = new ArrayList<>();
        ldList = new ArrayList<>();
        mpList = new ArrayList<>();
        mhList = new ArrayList<>();
        mnList = new ArrayList<>();
        mlList = new ArrayList<>();
        mzList = new ArrayList<>();
        nlList = new ArrayList<>();
        odList = new ArrayList<>();
        pyList = new ArrayList<>();
        pbList = new ArrayList<>();
        rjList = new ArrayList<>();
        skList = new ArrayList<>();
        tnList = new ArrayList<>();
        tsList = new ArrayList<>();
        trList = new ArrayList<>();
        ukList = new ArrayList<>();
        upList = new ArrayList<>();
        wbList = new ArrayList<>();

        anListD = new ArrayList<>();
        apListD = new ArrayList<>();
        arListD = new ArrayList<>();
        asListD = new ArrayList<>();
        brListD = new ArrayList<>();
        chListD = new ArrayList<>();
        cgListD = new ArrayList<>();
        ddListD = new ArrayList<>();
        dd2ListD = new ArrayList<>();
        dlListD = new ArrayList<>();
        gaListD = new ArrayList<>();
        gjListD = new ArrayList<>();
        hrListD = new ArrayList<>();
        hpListD = new ArrayList<>();
        jkListD = new ArrayList<>();
        jhListD = new ArrayList<>();
        kaListD = new ArrayList<>();
        klListD = new ArrayList<>();
        laListD = new ArrayList<>();
        ldListD = new ArrayList<>();
        mpListD = new ArrayList<>();
        mhListD = new ArrayList<>();
        mnListD = new ArrayList<>();
        mlListD = new ArrayList<>();
        mzListD = new ArrayList<>();
        nlListD = new ArrayList<>();
        odListD = new ArrayList<>();
        pyListD = new ArrayList<>();
        pbListD = new ArrayList<>();
        rjListD = new ArrayList<>();
        skListD = new ArrayList<>();
        tnListD = new ArrayList<>();
        tsListD = new ArrayList<>();
        trListD = new ArrayList<>();
        ukListD = new ArrayList<>();
        upListD = new ArrayList<>();
        wbListD = new ArrayList<>();

        //--------------------------- action bar ---------------------------------------------------
        View actionBar = findViewById(R.id.find_trips_action_bar);
        TextView actionBarTitle = actionBar.findViewById(R.id.action_bar_title);
        ImageView actionBarBackButton = actionBar.findViewById(R.id.action_bar_back_button);
        ImageView actionBarMenuButton = actionBar.findViewById(R.id.action_bar_menu);
        ImageView actionBarWhatsApp = actionBar.findViewById(R.id.action_bar_whats_app);
        actionBarWhatsApp.setVisibility(View.VISIBLE);

        actionBarTitle.setText(getString(R.string.Find_Trips));
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setOnClickListener(view -> {
            ShowAlert.loadingDialog(FindTripLPActivity.this);
            JumpTo.goToCustomerDashboard(FindTripLPActivity.this, phone, true);
        });


        //------------------------------- Bottom Nav -----------------------------------------------
        View bottomNav = findViewById(R.id.find_trips_bottom_nav);
        ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        ConstraintLayout customerDashboard = bottomNav.findViewById(R.id.bottom_nav_trip);
        customerDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        TextView profileText = bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText(getString(R.string.Trucks));
        profileImageView.setImageDrawable(getDrawable(R.drawable.bottom_nav_search_small));
        View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
        spView.setVisibility(View.INVISIBLE);
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_trip_underline);
        customerView.setVisibility(View.VISIBLE);

        //------------------------------------------------------------------------------------------

        searchListRecyclerView = (RecyclerView) findViewById(R.id.find_trip_state_recycler_view);
        searchListRecyclerViewDrop = findViewById(R.id.find_trip_state_recycler_view_drop);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchListRecyclerView.setLayoutManager(linearLayoutManager);
        searchListRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager2.setReverseLayout(false);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        searchListRecyclerViewDrop.setLayoutManager(linearLayoutManager2);
        searchListRecyclerViewDrop.setHasFixedSize(true);

        searchList = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.array_indian_states)));
        for (int i = 0; i < searchList.size(); i++) {
            SearchLoadModel searchLoadModel = new SearchLoadModel();
            searchLoadModel.setSearchList(String.valueOf(searchList.get(i)));
            searchLoadModels.add(searchLoadModel);
        }

        searchLoadAdapter = new SearchTripAdapter(FindTripLPActivity.this, searchLoadModels);
        searchListRecyclerView.setAdapter(searchLoadAdapter);

        searchLoadAdapterDrop = new SearchTripAdapterDrop(FindTripLPActivity.this, searchLoadModels);
        searchListRecyclerViewDrop.setAdapter(searchLoadAdapterDrop);

        allTripsRecyclerView = findViewById(R.id.find_trip_all_recycler_view);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        allTripsRecyclerView.setLayoutManager(linearLayoutManager1);
        allTripsRecyclerView.setHasFixedSize(true);

        allTripAdapter = new AllTripAdapter(FindTripLPActivity.this, tripList);
        allTripsRecyclerView.setAdapter(allTripAdapter);

        getAllTripDetails();
    }

    public void onClickTripTruck(View view) {
        switch (view.getId()) {
            case R.id.find_trucks_find_trip_text:

                truckConstrain.setVisibility(View.INVISIBLE);
                tripConstrain.setVisibility(View.VISIBLE);
                tripText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                truckText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                tripUnderline.setVisibility(View.VISIBLE);
                truckUnderline.setVisibility(View.INVISIBLE);
                break;

            case R.id.find_trucks_find_truck_text:

                truckConstrain.setVisibility(View.VISIBLE);
                tripConstrain.setVisibility(View.INVISIBLE);
                tripText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                truckText.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                tripUnderline.setVisibility(View.INVISIBLE);
                truckUnderline.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getAllTripDetails() {
        Call<TripResponse> tripModelCall = ApiClient.getPostTripService().getAllTripDetails();
        tripModelCall.enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                TripResponse tripModel = response.body();
                TripResponse.TripList list = tripModel.getData().get(0);

                tripList.addAll(tripModel.getData());
                allTripAdapter.updateData(tripList);

                if (tripList.size() == 0) noTrips.setVisibility(View.VISIBLE);
                if (tripList.size() > 0) {
                    getStateBidsPick(tripList);
                    getStateBidsDrop(tripList);
                }
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {

            }
        });
    }

    public void setLoadCount(SearchLoadModel obj, TextView numberOfLoads, ConstraintLayout findConstrain, ArrayList<SearchLoadModel> array_indian_states) {
        try {
            if (obj.getSearchList().equals(searchList.get(0))) {
                numberOfLoads.setText(anList.size() + " Loads");
                obj.setItemCount(anList.size());
            }
            if (obj.getSearchList().equals(searchList.get(1))) {
                numberOfLoads.setText(apList.size() + " Loads");
                obj.setItemCount(apList.size());
            }
            if (obj.getSearchList().equals(searchList.get(2))) {
                numberOfLoads.setText(arList.size() + " Loads");
                obj.setItemCount(arList.size());
            }
            if (obj.getSearchList().equals(searchList.get(3))) {
                numberOfLoads.setText(asList.size() + " Loads");
                obj.setItemCount(asList.size());
            }
            if (obj.getSearchList().equals(searchList.get(4))) {
                numberOfLoads.setText(brList.size() + " Loads");
                obj.setItemCount(brList.size());
            }
            if (obj.getSearchList().equals(searchList.get(5))) {
                numberOfLoads.setText(chList.size() + " Loads");
                obj.setItemCount(chList.size());
            }
            if (obj.getSearchList().equals(searchList.get(6))) {
                numberOfLoads.setText(cgList.size() + " Loads");
                obj.setItemCount(cgList.size());
            }
            if (obj.getSearchList().equals(searchList.get(7))) {
                numberOfLoads.setText(ddList.size() + " Loads");
                obj.setItemCount(ddList.size());
            }
            if (obj.getSearchList().equals(searchList.get(8))) {
                numberOfLoads.setText(dd2List.size() + " Loads");
                obj.setItemCount(dd2List.size());
            }
            if (obj.getSearchList().equals(searchList.get(9))) {
                numberOfLoads.setText(dlList.size() + " Loads");
                obj.setItemCount(dlList.size());
            }
            if (obj.getSearchList().equals(searchList.get(10))) {
                numberOfLoads.setText(gaList.size() + " Loads");
                obj.setItemCount(gaList.size());
            }
            if (obj.getSearchList().equals(searchList.get(11))) {
                numberOfLoads.setText(gjList.size() + " Loads");
                obj.setItemCount(gjList.size());
            }
            if (obj.getSearchList().equals(searchList.get(12))) {
                numberOfLoads.setText(hrList.size() + " Loads");
                obj.setItemCount(hrList.size());
            }
            if (obj.getSearchList().equals(searchList.get(13))) {
                numberOfLoads.setText(hpList.size() + " Loads");
                obj.setItemCount(hpList.size());
            }
            if (obj.getSearchList().equals(searchList.get(14))) {
                numberOfLoads.setText(jkList.size() + " Loads");
                obj.setItemCount(jkList.size());
            }
            if (obj.getSearchList().equals(searchList.get(15))) {
                numberOfLoads.setText(jhList.size() + " Loads");
                obj.setItemCount(jhList.size());
            }
            if (obj.getSearchList().equals(searchList.get(16))) {
                numberOfLoads.setText(kaList.size() + " Loads");
                obj.setItemCount(kaList.size());
            }
            if (obj.getSearchList().equals(searchList.get(17))) {
                numberOfLoads.setText(klList.size() + " Loads");
                obj.setItemCount(klList.size());
            }
            if (obj.getSearchList().equals(searchList.get(18))) {
                numberOfLoads.setText(laList.size() + " Loads");
                obj.setItemCount(laList.size());
            }
            if (obj.getSearchList().equals(searchList.get(19))) {
                numberOfLoads.setText(ldList.size() + " Loads");
                obj.setItemCount(ldList.size());
            }
            if (obj.getSearchList().equals(searchList.get(20))) {
                numberOfLoads.setText(mpList.size() + " Loads");
                obj.setItemCount(mpList.size());
            }
            if (obj.getSearchList().equals(searchList.get(21))) {
                numberOfLoads.setText(mhList.size() + " Loads");
                obj.setItemCount(mhList.size());
            }
            if (obj.getSearchList().equals(searchList.get(22))) {
                numberOfLoads.setText(mnList.size() + " Loads");
                obj.setItemCount(mnList.size());
            }
            if (obj.getSearchList().equals(searchList.get(23))) {
                numberOfLoads.setText(mlList.size() + " Loads");
                obj.setItemCount(mlList.size());
            }
            if (obj.getSearchList().equals(searchList.get(24))) {
                numberOfLoads.setText(mzList.size() + " Loads");
                obj.setItemCount(mzList.size());
            }
            if (obj.getSearchList().equals(searchList.get(25))) {
                numberOfLoads.setText(nlList.size() + " Loads");
                obj.setItemCount(nlList.size());
            }
            if (obj.getSearchList().equals(searchList.get(26))) {
                numberOfLoads.setText(odList.size() + " Loads");
                obj.setItemCount(odList.size());
            }
            if (obj.getSearchList().equals(searchList.get(27))) {
                numberOfLoads.setText(pyList.size() + " Loads");
                obj.setItemCount(pyList.size());
            }
            if (obj.getSearchList().equals(searchList.get(28))) {
                numberOfLoads.setText(pbList.size() + " Loads");
                obj.setItemCount(pbList.size());
            }
            if (obj.getSearchList().equals(searchList.get(29))) {
                numberOfLoads.setText(rjList.size() + " Loads");
                obj.setItemCount(rjList.size());
            }
            if (obj.getSearchList().equals(searchList.get(30))) {
                numberOfLoads.setText(skList.size() + " Loads");
                obj.setItemCount(skList.size());
            }
            if (obj.getSearchList().equals(searchList.get(31))) {
                numberOfLoads.setText(tnList.size() + " Loads");
                obj.setItemCount(tnList.size());
            }
            if (obj.getSearchList().equals(searchList.get(32))) {
                numberOfLoads.setText(tsList.size() + " Loads");
                obj.setItemCount(tsList.size());
            }
            if (obj.getSearchList().equals(searchList.get(33))) {
                numberOfLoads.setText(trList.size() + " Loads");
                obj.setItemCount(trList.size());
            }
            if (obj.getSearchList().equals(searchList.get(34))) {
                numberOfLoads.setText(ukList.size() + " Loads");
                obj.setItemCount(ukList.size());
            }
            if (obj.getSearchList().equals(searchList.get(35))) {
                numberOfLoads.setText(upList.size() + " Loads");
                obj.setItemCount(upList.size());
            }
            if (obj.getSearchList().equals(searchList.get(36))) {
                numberOfLoads.setText(wbList.size() + " Loads");
                obj.setItemCount(wbList.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStateBidsPick(ArrayList<TripResponse.TripList> loadListToCompare) {
        anList.clear();
        apList.clear();
        arList.clear();
        asList.clear();
        brList.clear();
        chList.clear();
        cgList.clear();
        ddList.clear();
        dd2List.clear();
        dlList.clear();
        gaList.clear();
        gjList.clear();
        hrList.clear();
        hpList.clear();
        jkList.clear();
        jhList.clear();
        kaList.clear();
        klList.clear();
        laList.clear();
        ldList.clear();
        mpList.clear();
        mhList.clear();
        mnList.clear();
        mlList.clear();
        mzList.clear();
        nlList.clear();
        odList.clear();
        pyList.clear();
        pbList.clear();
        rjList.clear();
        skList.clear();
        tnList.clear();
        tsList.clear();
        trList.clear();
        ukList.clear();
        upList.clear();
        wbList.clear();


        for (int i = 0; i < loadListToCompare.size(); i++) {
            if (loadListToCompare.get(i).getPick_state().equals("AN")) {
                anList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("AP")) {
                apList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("AR")) {
                arList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("AS")) {
                asList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("BR")) {
                brList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("CH/PB")) {
                chList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("CG")) {
                cgList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("DD")) {
                ddList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("DD2")) {
                dd2List.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("DL")) {
                dlList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("GA")) {
                gaList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("GJ")) {
                gjList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("HR")) {
                hrList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("HP")) {
                hpList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("JK")) {
                jkList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("JH")) {
                jhList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("KA")) {
                kaList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("KL")) {
                klList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("LA")) {
                laList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("LD")) {
                ldList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MP")) {
                mpList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MH")) {
                mhList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MN")) {
                mnList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("ML")) {
                mlList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("MZ")) {
                mzList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("NL")) {
                nlList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("OD")) {
                odList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("PY")) {
                pyList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("PB")) {
                pbList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("RJ")) {
                rjList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("SK")) {
                skList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("TN")) {
                tnList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("TS")) {
                tsList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("TR")) {
                trList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("UK")) {
                ukList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("UP")) {
                upList.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getPick_state().equals("WB")) {
                wbList.add(loadListToCompare.get(i));
            }
        }
    }

    //----------------------------------- Find Loads -----------------------------------------------
    public void setLoadCountDrop(SearchLoadModel obj, TextView numberOfLoads, ConstraintLayout findConstrain, ArrayList<SearchLoadModel> array_indian_states) {
        try {
            if (obj.getSearchList().equals(searchList.get(0))) {
                numberOfLoads.setText(anListD.size() + " Loads");
                obj.setItemCount(anListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(1))) {
                numberOfLoads.setText(apListD.size() + " Loads");
                obj.setItemCount(apListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(2))) {
                numberOfLoads.setText(arListD.size() + " Loads");
                obj.setItemCount(arListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(3))) {
                numberOfLoads.setText(asListD.size() + " Loads");
                obj.setItemCount(asListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(4))) {
                numberOfLoads.setText(brListD.size() + " Loads");
                obj.setItemCount(brListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(5))) {
                numberOfLoads.setText(chListD.size() + " Loads");
                obj.setItemCount(chListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(6))) {
                numberOfLoads.setText(cgListD.size() + " Loads");
                obj.setItemCount(cgListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(7))) {
                numberOfLoads.setText(ddListD.size() + " Loads");
                obj.setItemCount(ddListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(8))) {
                numberOfLoads.setText(dd2ListD.size() + " Loads");
                obj.setItemCount(dd2ListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(9))) {
                numberOfLoads.setText(dlListD.size() + " Loads");
                obj.setItemCount(dlListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(10))) {
                numberOfLoads.setText(gaListD.size() + " Loads");
                obj.setItemCount(gaListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(11))) {
                numberOfLoads.setText(gjListD.size() + " Loads");
                obj.setItemCount(gjListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(12))) {
                numberOfLoads.setText(hrListD.size() + " Loads");
                obj.setItemCount(hrListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(13))) {
                numberOfLoads.setText(hpListD.size() + " Loads");
                obj.setItemCount(hpListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(14))) {
                numberOfLoads.setText(jkListD.size() + " Loads");
                obj.setItemCount(jkListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(15))) {
                numberOfLoads.setText(jhListD.size() + " Loads");
                obj.setItemCount(jhListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(16))) {
                numberOfLoads.setText(kaListD.size() + " Loads");
                obj.setItemCount(kaListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(17))) {
                numberOfLoads.setText(klListD.size() + " Loads");
                obj.setItemCount(klListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(18))) {
                numberOfLoads.setText(laListD.size() + " Loads");
                obj.setItemCount(laListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(19))) {
                numberOfLoads.setText(ldListD.size() + " Loads");
                obj.setItemCount(ldListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(20))) {
                numberOfLoads.setText(mpListD.size() + " Loads");
                obj.setItemCount(mpListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(21))) {
                numberOfLoads.setText(mhListD.size() + " Loads");
                obj.setItemCount(mhListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(22))) {
                numberOfLoads.setText(mnListD.size() + " Loads");
                obj.setItemCount(mnListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(23))) {
                numberOfLoads.setText(mlListD.size() + " Loads");
                obj.setItemCount(mlListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(24))) {
                numberOfLoads.setText(mzListD.size() + " Loads");
                obj.setItemCount(mzListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(25))) {
                numberOfLoads.setText(nlListD.size() + " Loads");
                obj.setItemCount(nlListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(26))) {
                numberOfLoads.setText(odListD.size() + " Loads");
                obj.setItemCount(odListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(27))) {
                numberOfLoads.setText(pyListD.size() + " Loads");
                obj.setItemCount(pyListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(28))) {
                numberOfLoads.setText(pbListD.size() + " Loads");
                obj.setItemCount(pbListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(29))) {
                numberOfLoads.setText(rjListD.size() + " Loads");
                obj.setItemCount(rjListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(30))) {
                numberOfLoads.setText(skListD.size() + " Loads");
                obj.setItemCount(skListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(31))) {
                numberOfLoads.setText(tnListD.size() + " Loads");
                obj.setItemCount(tnListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(32))) {
                numberOfLoads.setText(tsListD.size() + " Loads");
                obj.setItemCount(tsListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(33))) {
                numberOfLoads.setText(trListD.size() + " Loads");
                obj.setItemCount(trListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(34))) {
                numberOfLoads.setText(ukListD.size() + " Loads");
                obj.setItemCount(ukListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(35))) {
                numberOfLoads.setText(upListD.size() + " Loads");
                obj.setItemCount(upListD.size());
            }
            if (obj.getSearchList().equals(searchList.get(36))) {
                numberOfLoads.setText(wbListD.size() + " Loads");
                obj.setItemCount(wbListD.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getStateBidsDrop(ArrayList<TripResponse.TripList> loadListToCompare) {
        anListD.clear();
        apListD.clear();
        arListD.clear();
        asListD.clear();
        brListD.clear();
        chListD.clear();
        cgListD.clear();
        ddListD.clear();
        dd2ListD.clear();
        dlListD.clear();
        gaListD.clear();
        gjListD.clear();
        hrListD.clear();
        hpListD.clear();
        jkListD.clear();
        jhListD.clear();
        kaListD.clear();
        klListD.clear();
        laListD.clear();
        ldListD.clear();
        mpListD.clear();
        mhListD.clear();
        mnListD.clear();
        mlListD.clear();
        mzListD.clear();
        nlListD.clear();
        odListD.clear();
        pyListD.clear();
        pbListD.clear();
        rjListD.clear();
        skListD.clear();
        tnListD.clear();
        tsListD.clear();
        trListD.clear();
        ukListD.clear();
        upListD.clear();
        wbListD.clear();

        for (int i = 0; i < loadListToCompare.size(); i++) {
            if (loadListToCompare.get(i).getDrop_state().equals("AN")) {
                anListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("AP")) {
                apListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("AR")) {
                arListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("AS")) {
                asListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("BR")) {
                brListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("CH/PB")) {
                chListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("CG")) {
                cgListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("DD")) {
                ddListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("DD2")) {
                dd2ListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("DL")) {
                dlListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("GA")) {
                gaListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("GJ")) {
                gjListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("HR")) {
                hrListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("HP")) {
                hpListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("JK")) {
                jkListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("JH")) {
                jhListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("KA")) {
                kaListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("KL")) {
                klListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("LA")) {
                laListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("LD")) {
                ldListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MP")) {
                mpListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MH")) {
                mhListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MN")) {
                mnListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("ML")) {
                mlListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("MZ")) {
                mzListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("NL")) {
                nlListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("OD")) {
                odListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("PY")) {
                pyListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("PB")) {
                pbListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("RJ")) {
                rjListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("SK")) {
                skListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("TN")) {
                tnListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("TS")) {
                tsListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("TR")) {
                trListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("UK")) {
                ukListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("UP")) {
                upListD.add(loadListToCompare.get(i));
            }
            if (loadListToCompare.get(i).getDrop_state().equals("WB")) {
                wbListD.add(loadListToCompare.get(i));
            }
        }
    }

    AdapterView.OnItemSelectedListener onPickOrDrop = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selected = adapterView.getSelectedItem().toString();

            if (selected.equals("Pick-up Location")) {
                searchListRecyclerViewDrop.setVisibility(View.INVISIBLE);
                searchListRecyclerView.setVisibility(View.VISIBLE);
            } else {
                searchListRecyclerViewDrop.setVisibility(View.VISIBLE);
                searchListRecyclerView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickWhatsApp(View view) {
        Dialog chooseDialog = new Dialog(FindTripLPActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        TextView cameraText = chooseDialog.findViewById(R.id.dialog_camera_text);
        cameraText.setText(getString(R.string.whats_app));
        TextView galleryText = chooseDialog.findViewById(R.id.dialog_photo_library_text);
        galleryText.setText(getString(R.string.call));

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        camera.setImageDrawable(getResources().getDrawable(R.drawable.whats_app_small));
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);
        gallery.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
        gallery.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        camera.setOnClickListener(view1 -> {
            chooseDialog.dismiss();
            String mobileNumber = "8806930081";
            String message = "";
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + mobileNumber + "&text=" + message));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(FindTripLPActivity.this, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
            }
        });

        gallery.setOnClickListener(view12 -> {
            chooseDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + "+918806930081"));
            startActivity(intent);
        });
    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(FindTripLPActivity.this);
        JumpTo.goToFindTripLPActivity(FindTripLPActivity.this, phone, userId, true);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(FindTripLPActivity.this);
                JumpTo.goToCustomerDashboard(FindTripLPActivity.this, phone, true);
                break;

            case R.id.bottom_nav_customer_dashboard:
                ShowAlert.loadingDialog(FindTripLPActivity.this);
                JumpTo.goToFindTrucksActivity(FindTripLPActivity.this, userId, phone);
                break;

            case R.id.bottom_nav_track:
                ShowAlert.loadingDialog(FindTripLPActivity.this);
                JumpTo.goToLPTrackActivity(FindTripLPActivity.this, phone, true);
                break;

            case R.id.bottom_nav_trip:
                RearrangeItems();
                break;

            case R.id.bottom_nav_profile:
                ShowAlert.loadingDialog(FindTripLPActivity.this);
                JumpTo.goToViewPersonalDetailsActivity(FindTripLPActivity.this, userId, phone, true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(FindTripLPActivity.this);
        JumpTo.goToCustomerDashboard(FindTripLPActivity.this, phone, true);
    }

    public void showLoading(){
        loadingDialog.show();
    }

    public void dismissLoading(){
        loadingDialog.dismiss();
    }
}