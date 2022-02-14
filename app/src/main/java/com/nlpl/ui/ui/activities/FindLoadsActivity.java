package com.nlpl.ui.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.nlpl.ui.ui.adapters.FindLoadAdapter;

import com.nlpl.ui.ui.adapters.SearchLoadAdapter;
import com.nlpl.ui.ui.adapters.StateLoadAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.JumpTo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class FindLoadsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<FindLoadsModel> bidsList = new ArrayList<>();
    private ArrayList<FindLoadsModel> anList, apList, arList, asList, brList, chList, cgList, ddList,
            dd2List, dlList, gaList, gjList, hrList, hpList, jkList, jhList, kaList, klList, laList,
            ldList, mpList, mhList, mnList, mlList, mzList, nlList, odList, pyList, pbList, rjList,
            skList, tnList, tsList, trList, ukList, upList, wbList;
    private FindLoadAdapter bidsListAdapter;
    private RecyclerView bidsListRecyclerView;
    ConstraintLayout stateConstrain;

    private ArrayList<SearchLoadModel> searchLoadModels = new ArrayList<>();
    ArrayList<String> searchList;
    private SearchLoadAdapter searchLoadAdapter;
    private RecyclerView searchListRecyclerView;

    private ArrayList<BidSubmittedModel> loadSubmittedList = new ArrayList<>();
    private ArrayList<BidSubmittedModel> updatedLoadSubmittedList = new ArrayList<>();

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    EditText searchLoad;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String phone, userId;

    String loadId, bidStatus, vehicle_no, truckId, selectedDriverId, updateAssignedTruckId, updateAssignedDriverId, selectedDriverName;
    Dialog previewDialogBidNow, selectTruckDialog, setBudget;
    TextView cancel, customerFirstBudget, acceptAndBid, spQuote, addDriver, selectDriver, addTruck, selectTruck, selectedTruckModel, selectedTruckFeet, selectedTruckCapacity, selectedTruckBodyType;
    EditText notesSp;
    CheckBox declaration;
    RadioButton negotiable_yes, negotiable_no;
    Boolean isNegotiableSelected = false, isTruckSelectedToBid = false, negotiable = null;
    ArrayList<String> arrayTruckId, arrayTruckList, arrayDriverId, arrayDriverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_loads);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
            Log.i("userId find loads", userId);
        }
        //------------------------------------------------------------------------------------------
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

        arrayTruckId = new ArrayList<>();
        arrayTruckList = new ArrayList<>();
        arrayDriverId = new ArrayList<>();
        arrayDriverName = new ArrayList<>();
        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.find_loads_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Find Loads");
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindLoadsActivity.this.finish();
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.find_loads_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText("Find Loads");
        profileImageView.setImageDrawable(getDrawable(R.drawable.find_small));

        //---------------------------- Get Bank Details --------------------------------------------
        mQueue = Volley.newRequestQueue(FindLoadsActivity.this);
        bidsListRecyclerView = (RecyclerView) findViewById(R.id.find_loads_recycler_view);
        searchListRecyclerView = (RecyclerView) findViewById(R.id.find_loads_search_recycler_view);
        searchLoad = (EditText) findViewById(R.id.find_loads_search_load);
        stateConstrain = (ConstraintLayout) findViewById(R.id.find_loads_state_constrain);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(false);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.HORIZONTAL);
        bidsListRecyclerView.setLayoutManager(linearLayoutManagerBank);
        bidsListRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchListRecyclerView.setLayoutManager(linearLayoutManager);
        searchListRecyclerView.setHasFixedSize(true);

        bidsListAdapter = new FindLoadAdapter(FindLoadsActivity.this, bidsList);
        bidsListRecyclerView.setAdapter(bidsListAdapter);


        searchList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_indian_states)));
        for (int i = 0; i < searchList.size(); i++) {
            SearchLoadModel searchLoadModel = new SearchLoadModel();
            searchLoadModel.setSearchList(searchList.get(i));
            searchLoadModels.add(searchLoadModel);
        }

        searchLoadAdapter = new SearchLoadAdapter(FindLoadsActivity.this, searchLoadModels);
        searchListRecyclerView.setAdapter(searchLoadAdapter);
        getBidsReceived();

        searchLoad.addTextChangedListener(searchTextWatcher);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            filter(editable.toString());
        }
    };

    private void filter(String text) {
        ArrayList<SearchLoadModel> searchLoadList = new ArrayList<>();

        for (SearchLoadModel item : searchLoadModels) {
            if (item.getSearchList().toLowerCase().contains(text.toLowerCase())) {
                searchLoadList.add(item);
            }
        }
        searchLoadAdapter.updateData(searchLoadList);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, true);
                break;

            case R.id.bottom_nav_customer_dashboard:
                stateConstrain.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void getBidsReceived() {

        String url1 = getString(R.string.baseURL) + "/loadpost/getAllPosts";
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidsLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsLists.length(); i++) {
                        JSONObject obj = bidsLists.getJSONObject(i);
                        FindLoadsModel findLoadsModel = new FindLoadsModel();
                        findLoadsModel.setIdpost_load(obj.getString("idpost_load"));
                        findLoadsModel.setUser_id(obj.getString("user_id"));
                        findLoadsModel.setPick_up_date(obj.getString("pick_up_date"));
                        findLoadsModel.setPick_up_time(obj.getString("pick_up_time"));
                        findLoadsModel.setBudget(obj.getString("budget"));
                        findLoadsModel.setBid_status(obj.getString("bid_status"));
                        findLoadsModel.setVehicle_model(obj.getString("vehicle_model"));
                        findLoadsModel.setFeet(obj.getString("feet"));
                        findLoadsModel.setCapacity(obj.getString("capacity"));
                        findLoadsModel.setBody_type(obj.getString("body_type"));
                        findLoadsModel.setPick_add(obj.getString("pick_add"));
                        findLoadsModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        findLoadsModel.setPick_city(obj.getString("pick_city"));
                        findLoadsModel.setPick_state(obj.getString("pick_state"));
                        findLoadsModel.setPick_country(obj.getString("pick_country"));
                        findLoadsModel.setDrop_add(obj.getString("drop_add"));
                        findLoadsModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        findLoadsModel.setDrop_city(obj.getString("drop_city"));
                        findLoadsModel.setDrop_state(obj.getString("drop_state"));
                        findLoadsModel.setDrop_country(obj.getString("drop_country"));
                        findLoadsModel.setKm_approx(obj.getString("km_approx"));
                        findLoadsModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        findLoadsModel.setBid_ends_at(obj.getString("bid_ends_at"));
                        if (obj.getString("bid_status").equals("loadPosted") || obj.getString("bid_status").equals("loadReactivated")) {
                            bidsList.add(findLoadsModel);
                        }
                    }

                    getBidListByUserId(bidsList);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
        //-------------------------------------------------------------------------------------------
    }

    public void onClickShiftRecyclerviewToLeft(View view) {
        bidsListRecyclerView.setAdapter(bidsListAdapter);
    }

    public void onClickShiftRecyclerviewToRight(View view) {
        bidsListRecyclerView.scrollToPosition(bidsListAdapter.getItemCount() - 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String visibility = String.valueOf(stateConstrain.getVisibility());
        Log.i("visibility", visibility); //visible = 0
        if (visibility.equals("0")) {
            JumpTo.goToFindLoadsActivity(FindLoadsActivity.this, userId, phone);
        } else {
            JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, true);
        }
    }

    public void setLoadCount(SearchLoadModel obj, TextView numberOfLoads) {
        try {
            if (obj.getSearchList().equals(searchList.get(0))) {
                numberOfLoads.setText(anList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(1))) {
                numberOfLoads.setText(apList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(2))) {
                numberOfLoads.setText(arList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(3))) {
                numberOfLoads.setText(asList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(4))) {
                numberOfLoads.setText(brList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(5))) {
                numberOfLoads.setText(chList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(6))) {
                numberOfLoads.setText(cgList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(7))) {
                numberOfLoads.setText(ddList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(8))) {
                numberOfLoads.setText(dd2List.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(9))) {
                numberOfLoads.setText(dlList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(10))) {
                numberOfLoads.setText(gaList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(11))) {
                numberOfLoads.setText(gjList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(12))) {
                numberOfLoads.setText(hrList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(13))) {
                numberOfLoads.setText(hpList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(14))) {
                numberOfLoads.setText(jkList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(15))) {
                numberOfLoads.setText(jhList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(16))) {
                numberOfLoads.setText(kaList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(17))) {
                numberOfLoads.setText(klList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(18))) {
                numberOfLoads.setText(laList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(19))) {
                numberOfLoads.setText(ldList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(20))) {
                numberOfLoads.setText(mpList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(21))) {
                numberOfLoads.setText(mhList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(22))) {
                numberOfLoads.setText(mnList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(23))) {
                numberOfLoads.setText(mlList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(24))) {
                numberOfLoads.setText(mzList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(25))) {
                numberOfLoads.setText(nlList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(26))) {
                numberOfLoads.setText(odList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(27))) {
                numberOfLoads.setText(pyList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(28))) {
                numberOfLoads.setText(pbList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(29))) {
                numberOfLoads.setText(rjList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(30))) {
                numberOfLoads.setText(skList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(31))) {
                numberOfLoads.setText(tnList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(32))) {
                numberOfLoads.setText(tsList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(33))) {
                numberOfLoads.setText(trList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(34))) {
                numberOfLoads.setText(ukList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(35))) {
                numberOfLoads.setText(upList.size() + " Loads");
            }
            if (obj.getSearchList().equals(searchList.get(36))) {
                numberOfLoads.setText(wbList.size() + " Loads");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickFindLoadListItem(SearchLoadModel obj, TextView holder) {
        if (holder.getText().equals("0 Loads")) {
            stateConstrain.setVisibility(View.INVISIBLE);
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Load Notifications");
            alertMessage.setText("No loads available for the state\n" + obj.getSearchList());
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------
        } else {
            stateConstrain.setVisibility(View.VISIBLE);

            //-------------------------------- Action Bar for state ----------------------------------------------
            View actionBarState = findViewById(R.id.find_loads_action_bar_for_state);
            TextView actionBarTitleState = (TextView) actionBarState.findViewById(R.id.action_bar_title);
            ImageView actionBarBackButtonState = (ImageView) actionBarState.findViewById(R.id.action_bar_back_button);
            ImageView actionBarMenuButtonState = (ImageView) actionBarState.findViewById(R.id.action_bar_menu);

            actionBarTitleState.setText("Loads for " + obj.getSearchList());
            actionBarMenuButtonState.setVisibility(View.GONE);
            actionBarBackButtonState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stateConstrain.setVisibility(View.INVISIBLE);
                }
            });
            //--------------------------------------------------------------------------------------
            RecyclerView stateLoadRecyclerView = (RecyclerView) findViewById(R.id.find_loads_state_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setReverseLayout(false);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            stateLoadRecyclerView.setLayoutManager(linearLayoutManager);
            stateLoadRecyclerView.setHasFixedSize(true);

            try {
                if (obj.getSearchList().equals(searchList.get(0))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, anList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(1))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, apList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(2))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, arList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(3))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, asList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(4))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, brList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(5))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, chList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(6))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, cgList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(7))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, ddList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(8))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, dd2List);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(9))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, dlList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(10))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, gaList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(11))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, gjList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(12))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, hrList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(13))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, hpList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(14))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, jkList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(15))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, jhList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(16))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, kaList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(17))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, klList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(18))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, laList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(19))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, ldList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(20))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, mpList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(21))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, mhList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(22))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, mnList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(23))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, mlList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(24))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, mzList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(25))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, nlList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(26))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, odList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(27))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, pyList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(28))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, pbList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(29))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, rjList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(30))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, skList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(31))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, tnList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(32))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, tsList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(33))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, trList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(34))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, ukList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(35))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, upList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
                if (obj.getSearchList().equals(searchList.get(36))) {
                    StateLoadAdapter stateLoadAdapter = new StateLoadAdapter(FindLoadsActivity.this, wbList);
                    stateLoadRecyclerView.setAdapter(stateLoadAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickBidNow(FindLoadsModel obj) {
        previewDialogBidNow = new Dialog(FindLoadsActivity.this);
        previewDialogBidNow.setContentView(R.layout.dialog_bid_now);
        previewDialogBidNow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadId = obj.getIdpost_load();
        bidStatus = obj.getBid_status();
        String pick_up_date = obj.getPick_up_date();
        String pick_up_time = obj.getPick_up_time();
        String required_budget = obj.getBudget();
        String distance = obj.getKm_approx();
        String required_model = obj.getVehicle_model();
        String required_feet = obj.getFeet();
        String required_capacity = obj.getCapacity();
        String required_truck_body = obj.getBody_type();
        String pick_up_location = obj.getPick_add() + " " + obj.getPick_city() + " " + obj.getPick_state() + " " + obj.getPick_pin_code();
        String drop_location = obj.getDrop_add() + " " + obj.getDrop_city() + " " + obj.getDrop_state() + " " + obj.getDrop_pin_code();
        String received_notes_description = obj.getNotes_meterial_des();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogBidNow.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        previewDialogBidNow.show();
        previewDialogBidNow.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView pickUpDate = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_date_textview);
        TextView pickUpTime = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_time_textview);
        customerFirstBudget = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_budget_textview);
        TextView approxDistance = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_distance_textview);
        TextView reqModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_model_textview);
        TextView reqFeet = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_feet_textview);
        TextView reqCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_capacity_textview);
        TextView reqBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_req_bodyType_textview);
        TextView pickUpLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_pick_up_location_textview);
        TextView dropLocation = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_drop_location_textview);
        TextView receivedNotes = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_received_notes_textview);
        TextView loadIdHeading = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_loadId_heading);

        pickUpDate.setText(pick_up_date);
        pickUpTime.setText(pick_up_time);
        customerFirstBudget.setText(required_budget);
        approxDistance.setText(distance);
        reqModel.setText(required_model);
        reqFeet.setText(required_feet);
        reqCapacity.setText(required_capacity);
        reqBodyType.setText(required_truck_body);
        pickUpLocation.setText(pick_up_location);
        dropLocation.setText(drop_location);
        receivedNotes.setText(received_notes_description);
        loadIdHeading.setText("Load ID: " + obj.getPick_city() + "-" + obj.getDrop_city() + "-000");
        //----------------------------------------------------------------------------------------------------------------

        //-------------------------------------------------Accept Load and Bid now-----------------------------------------
        spQuote = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_sp_quote_textview);
        selectTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_truck_textview);
        selectDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_select_driver_textview);
        addTruck = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_add_truck_textview);
        addDriver = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_add_driver_textview);
        selectedTruckModel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_model_textview);
        selectedTruckFeet = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_feet_textview);
        selectedTruckCapacity = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_capacity_textview);
        selectedTruckBodyType = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_truck_body_type_textview);
        notesSp = (EditText) previewDialogBidNow.findViewById(R.id.dialog_bid_now_notes_editText);
        declaration = (CheckBox) previewDialogBidNow.findViewById(R.id.dialog_bid_now_declaration);
        acceptAndBid = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_accept_and_bid_btn);
        cancel = (TextView) previewDialogBidNow.findViewById(R.id.dialog_bid_now_cancel_btn);
        negotiable_yes = previewDialogBidNow.findViewById(R.id.dialog_bid_now_radio_btn_yes);
        negotiable_no = previewDialogBidNow.findViewById(R.id.dialog_bid_now_radio_btn_no);

        acceptAndBid.setEnabled(false);
        cancel.setEnabled(true);
        cancel.setBackgroundResource((R.drawable.button_active));

        negotiable_no.setChecked(false);
        negotiable_yes.setChecked(false);
        isNegotiableSelected = false;


        if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
            acceptAndBid.setEnabled(true);
            acceptAndBid.setBackgroundResource((R.drawable.button_active));
        } else {
            acceptAndBid.setEnabled(false);
            acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
        }

        declaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, false);
            }
        });

        acceptAndBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {

                    if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString()) || !negotiable) {
                        isNegotiableSelected = true;
                        saveBid(createBidRequest("RespondedBySP", spQuote.getText().toString()));
                    } else {
                        saveBid(createBidRequest("submitted", ""));
                    }

                    Log.i("loadId bidded", obj.getIdpost_load());
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

                    TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText("Post Bid");
                    alertMessage.setText("Bid Posted Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            JumpTo.goToServiceProviderDashboard(FindLoadsActivity.this, phone, false);
                            previewDialogBidNow.dismiss();
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        });

        negotiable_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNegotiableSelected = true;
                negotiable = true;

                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }

                negotiable_yes.setChecked(true);
                negotiable_no.setChecked(false);
            }
        });

        negotiable_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNegotiableSelected = true;
                negotiable = false;

                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }

                negotiable_yes.setChecked(false);
                negotiable_no.setChecked(true);
            }
        });

        spQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetSet(spQuote.getText().toString());

            }
        });

        selectTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayTruckId.clear();
                getTrucksByUserId();
                arrayTruckList.clear();
            }
        });

        selectDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTruckSelectedToBid) {
                    arrayDriverId.clear();
                    getDriversByUserId();
                    arrayDriverName.clear();
                }
            }
        });

        addTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToVehicleDetailsActivity(FindLoadsActivity.this, userId, phone, false, true, false, false, null, null);
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToDriverDetailsActivity(FindLoadsActivity.this, userId, phone, false, true, false, null, null);
            }
        });

    }

    public BidLoadRequest createBidRequest(String status, String spFinal) {
        BidLoadRequest bidLoadRequest = new BidLoadRequest();
        bidLoadRequest.setUser_id(userId);
        bidLoadRequest.setAssigned_truck_id(truckId);
        bidLoadRequest.setAssigned_driver_id(selectedDriverId);
        bidLoadRequest.setIdpost_load(loadId);
        bidLoadRequest.setBid_status(status);
        bidLoadRequest.setBody_type(selectedTruckBodyType.getText().toString());
        bidLoadRequest.setVehicle_model(selectedTruckModel.getText().toString());
        bidLoadRequest.setFeet(selectedTruckFeet.getText().toString());
        bidLoadRequest.setCapacity(selectedTruckCapacity.getText().toString());
        bidLoadRequest.setNotes(notesSp.getText().toString());
        bidLoadRequest.setIs_negatiable(negotiable);
        bidLoadRequest.setSp_quote(spQuote.getText().toString());
        bidLoadRequest.setIs_bid_accpted_by_sp(spFinal);
        return bidLoadRequest;
    }

    public void saveBid(BidLoadRequest bidLoadRequest) {
        Call<BidLadResponse> bidLadResponseCall = ApiClient.getBidLoadService().saveBid(bidLoadRequest);
        bidLadResponseCall.enqueue(new Callback<BidLadResponse>() {
            @Override
            public void onResponse(Call<BidLadResponse> call, retrofit2.Response<BidLadResponse> response) {
            }

            @Override
            public void onFailure(Call<BidLadResponse> call, Throwable t) {
            }
        });
    }

    private void budgetSet(String previousBudget) {

        setBudget = new Dialog(FindLoadsActivity.this);
        setBudget.setContentView(R.layout.dialog_budget);

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(setBudget.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;

        setBudget.show();
        setBudget.setCancelable(true);
        setBudget.getWindow().setAttributes(lp2);

        EditText budget = setBudget.findViewById(R.id.dialog_budget_edit);
        Button okBudget = setBudget.findViewById(R.id.dialog_budget_ok_btn);
        budget.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String newPreviousBudget = previousBudget.replaceAll(",", "");
        budget.setText(newPreviousBudget);

        if (!previousBudget.isEmpty()) {
            okBudget.setEnabled(true);
            okBudget.setBackgroundResource((R.drawable.button_active));
        } else {
            okBudget.setEnabled(false);
            okBudget.setBackgroundResource((R.drawable.button_de_active));
        }

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {

                    String finalBudget, lastThree = "";
                    String budget1 = budget.getText().toString();
                    if (budget1.length() > 3) {
                        lastThree = budget1.substring(budget1.length() - 3);
                    }
                    if (budget1.length() == 1) {
                        finalBudget = budget1;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 2) {
                        finalBudget = budget1;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 3) {
                        finalBudget = budget1;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 4) {
                        Character fourth = budget1.charAt(0);
                        finalBudget = fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 5) {
                        Character fifth = budget1.charAt(0);
                        Character fourth = budget1.charAt(1);
                        finalBudget = fifth + "" + fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 6) {
                        Character fifth = budget1.charAt(1);
                        Character fourth = budget1.charAt(2);
                        Character sixth = budget1.charAt(0);
                        finalBudget = sixth + "," + fifth + "" + fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    } else if (budget1.length() == 7) {
                        Character seventh = budget1.charAt(0);
                        Character sixth = budget1.charAt(1);
                        Character fifth = budget1.charAt(2);
                        Character fourth = budget1.charAt(3);
                        finalBudget = seventh + "" + sixth + "," + fifth + "" + fourth + "," + lastThree;
                        spQuote.setText(finalBudget);
                    }

                    if (spQuote.getText().toString().equals(customerFirstBudget.getText().toString())) {
                        spQuote.setTextColor(getResources().getColor(R.color.green));
                        negotiable_no.setChecked(true);
                        negotiable_yes.setChecked(false);
                        negotiable_yes.setEnabled(false);
                        isNegotiableSelected = true;
                    } else {
                        spQuote.setTextColor(getResources().getColor(R.color.redDark));
                    }
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                } else {
                    okBudget.setEnabled(false);
                    okBudget.setBackgroundResource((R.drawable.button_de_active));
                }

                TextView amountInWords = setBudget.findViewById(R.id.dialog_budget_amount_in_words);
                if (budgetEditText.length() > 0) {
                    String return_val_in_english = EnglishNumberToWords.convert(Long.parseLong(budgetEditText));
                    amountInWords.setText(return_val_in_english);
                } else {
                    amountInWords.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        okBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }
                setBudget.dismiss();
            }
        });
    }

    private void selectTruckToBid(ArrayList<String> arrayTruckId) {

        selectTruckDialog = new Dialog(FindLoadsActivity.this);
        selectTruckDialog.setContentView(R.layout.dialog_spinner);
        selectTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTruckDialog.show();
        selectTruckDialog.setCancelable(true);
        TextView model_title = selectTruckDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText("Select Truck to Bid");

        ListView modelList = (ListView) selectTruckDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayTruckList);
        modelList.setAdapter(adapter1);


        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isTruckSelectedToBid = true;

                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }

                getTruckDetailsByTruckId(arrayTruckId.get(i));
                selectTruckDialog.dismiss();
                arrayTruckList.clear();
            }
        });
    }

    private void getTrucksByUserId() {

        String url = getString(R.string.baseURL) + "/truck/truckbyuserID/" + userId;
        Log.i("url for truckByUserId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_no = obj.getString("vehicle_no");
                        truckId = obj.getString("truck_id");
                        arrayTruckList.add(vehicle_no);
                        arrayTruckId.add(truckId);
                    }
                    if (arrayTruckId.size() == 0) {
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
                        alert.setCancelable(true);

                        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                        alertTitle.setText("Add a Truck");
                        alertMessage.setText("Please add a Truck to submit your response");
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText("OK");
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                JumpTo.goToVehicleDetailsActivity(FindLoadsActivity.this, userId, phone, false, true, false, false, null, null);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    } else {
                        selectTruckToBid(arrayTruckId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    private void getTruckDetailsByTruckId(String truckIdSelected) {

        updateAssignedTruckId = truckIdSelected;


        Log.i("truckId selected", truckIdSelected);
        truckId = truckIdSelected;
        String url = getString(R.string.baseURL) + "/truck/" + truckIdSelected;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String truckModel = obj.getString("truck_type");
                        String truckFeet = obj.getString("truck_ft");
                        String truckCapacity = obj.getString("truck_carrying_capacity");
                        String bodyType = obj.getString("vehicle_type");
                        String vehicleNo = obj.getString("vehicle_no");
                        selectedDriverId = obj.getString("driver_id");

                        selectTruck.setText(vehicleNo);
                        selectedTruckModel.setText(truckModel);
                        selectedTruckFeet.setText(truckFeet);
                        selectedTruckBodyType.setText(bodyType);
                        selectedTruckCapacity.setText(truckCapacity);
                    }

                    if (selectedDriverId.equals("null")) {
                        selectDriver.setText("");
                        Log.i("driverId null", "There is no driver Id for this truck");
                    } else {
                        getDriverDetailsByDriverId(selectedDriverId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void getDriverDetailsByDriverId(String driverIdSelected) {

        updateAssignedDriverId = driverIdSelected;

        Log.i("Driver selected", driverIdSelected);
        String url = getString(R.string.baseURL) + "/driver/driverId/" + driverIdSelected;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        selectedDriverName = obj.getString("driver_name");
                    }

                    selectDriver.setText(selectedDriverName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void getDriversByUserId() {

        String url = getString(R.string.baseURL) + "/driver/userId/" + userId;
        Log.i("url for driverByUserId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        selectedDriverId = obj.getString("driver_id");
                        selectedDriverName = obj.getString("driver_name");
                        arrayDriverId.add(selectedDriverId);
                        arrayDriverName.add(selectedDriverName);
                    }
                    if (arrayDriverName.size() == 0) {
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
                        alert.setCancelable(true);

                        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                        alertTitle.setText("Add a Driver");
                        alertMessage.setText("Please add a Driver to submit your response");
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText("OK");
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                JumpTo.goToDriverDetailsActivity(FindLoadsActivity.this, userId, phone, false, true, false, null, null);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    } else {
                        selectDriverToBid(arrayDriverId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void selectDriverToBid(ArrayList<String> arrayDriverId) {

        selectTruckDialog = new Dialog(FindLoadsActivity.this);
        selectTruckDialog.setContentView(R.layout.dialog_spinner);
        selectTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectTruckDialog.show();
        selectTruckDialog.setCancelable(true);
        TextView model_title = selectTruckDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText("Select Driver to Bid");

        ListView modelList = (ListView) selectTruckDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayDriverName);
        modelList.setAdapter(adapter1);

        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectDriver.setText(adapter1.getItem(i));
                getDriverDetailsByDriverId(arrayDriverId.get(i));
                if (isNegotiableSelected && isTruckSelectedToBid && !spQuote.getText().toString().isEmpty() && !selectDriver.getText().toString().isEmpty() && declaration.isChecked()) {
                    acceptAndBid.setEnabled(true);
                    acceptAndBid.setBackgroundResource((R.drawable.button_active));
                } else {
                    acceptAndBid.setEnabled(false);
                    acceptAndBid.setBackgroundResource((R.drawable.button_de_active));
                }
                selectTruckDialog.dismiss();
                arrayDriverName.clear();
            }
        });
    }

    //--------------------------- Compare ----------------------------------------------------------
    private void getBidListByUserId(ArrayList<FindLoadsModel> loadListToCompare) {

        String url = getString(R.string.baseURL) + "/spbid/getBidDtByUserId/" + userId;
        Log.i("url betBidByUserID", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String postId = obj.getString("idpost_load");
                        String bidId = obj.getString("sp_bid_id");
                        getBidSubmittedList(postId, bidId, loadListToCompare);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    public void getBidSubmittedList(String loadIdReceived, String bidId, ArrayList<FindLoadsModel> loadListToCompare) {
        //---------------------------- Get Bank Details ------------------------------------------
        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByPostId/" + loadIdReceived;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    loadSubmittedList = new ArrayList<>();
                    loadSubmittedList.clear();

                    JSONArray loadLists = response.getJSONArray("data");
                    for (int i = 0; i < loadLists.length(); i++) {
                        JSONObject obj = loadLists.getJSONObject(i);
                        BidSubmittedModel bidSubmittedModel = new BidSubmittedModel();
                        bidSubmittedModel.setIdpost_load(obj.getString("idpost_load"));
                        bidSubmittedModel.setUser_id(obj.getString("user_id"));
                        bidSubmittedModel.setPick_up_date(obj.getString("pick_up_date"));
                        bidSubmittedModel.setPick_up_time(obj.getString("pick_up_time"));
                        bidSubmittedModel.setBudget(obj.getString("budget"));
                        bidSubmittedModel.setBid_status(obj.getString("bid_status"));
                        bidSubmittedModel.setVehicle_model(obj.getString("vehicle_model"));
                        bidSubmittedModel.setFeet(obj.getString("feet"));
                        bidSubmittedModel.setCapacity(obj.getString("capacity"));
                        bidSubmittedModel.setBody_type(obj.getString("body_type"));
                        bidSubmittedModel.setPick_add(obj.getString("pick_add"));
                        bidSubmittedModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        bidSubmittedModel.setPick_city(obj.getString("pick_city"));
                        bidSubmittedModel.setPick_state(obj.getString("pick_state"));
                        bidSubmittedModel.setPick_country(obj.getString("pick_country"));
                        bidSubmittedModel.setDrop_add(obj.getString("drop_add"));
                        bidSubmittedModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        bidSubmittedModel.setDrop_city(obj.getString("drop_city"));
                        bidSubmittedModel.setDrop_state(obj.getString("drop_state"));
                        bidSubmittedModel.setDrop_country(obj.getString("drop_country"));
                        bidSubmittedModel.setKm_approx(obj.getString("km_approx"));
                        bidSubmittedModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidSubmittedModel.setBid_ends_at(obj.getString("bid_ends_at"));
                        bidSubmittedModel.setBidId(bidId);

                        if (!obj.getString("bid_status").equals("delete") && !obj.getString("bid_status").equals("loadExpired")) {
                            loadSubmittedList.add(bidSubmittedModel);
                        }
                    }

                    if (loadSubmittedList.size() > 0) {
                        updatedLoadSubmittedList.addAll(loadSubmittedList);
                        compareAndRemove(loadListToCompare);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void compareAndRemove(ArrayList<FindLoadsModel> loadListToCompare) {

        Log.i("Load list", String.valueOf(loadListToCompare.size()));

        for (int i = 0; i < loadListToCompare.size(); i++) {
            for (int j = 0; j < updatedLoadSubmittedList.size(); j++) {
                if (loadListToCompare.get(i).getIdpost_load().equals(updatedLoadSubmittedList.get(j).getIdpost_load())) {
                    loadListToCompare.remove(i);
                }
            }
        }

//        Collections.reverse(loadListToCompare);

        bidsListAdapter = new FindLoadAdapter(FindLoadsActivity.this, loadListToCompare);
        bidsListRecyclerView.setAdapter(bidsListAdapter);

        if (loadListToCompare.size() > 0) {
            bidsListAdapter.updateData(loadListToCompare);

            for (int i = 0; i < loadListToCompare.size(); i++) {
                if (loadListToCompare.size() == 0) {
                    bidsListRecyclerView.setVisibility(View.GONE);
                } else if (loadListToCompare.size() == 1) {
                    ArrayList<FindLoadsModel> newList = new ArrayList<>(loadListToCompare.subList(loadListToCompare.size() - 1, loadListToCompare.size()));
                    bidsListAdapter.updateData(newList);
                } else if (loadListToCompare.size() == 2) {
                    ArrayList<FindLoadsModel> newList = new ArrayList<>(loadListToCompare.subList(loadListToCompare.size() - 2, loadListToCompare.size()));
                    bidsListAdapter.updateData(newList);
                } else if (loadListToCompare.size() >= 3) {
                    ArrayList<FindLoadsModel> newList = new ArrayList<>(loadListToCompare.subList(loadListToCompare.size() - 3, loadListToCompare.size()));
                    bidsListAdapter.updateData(newList);
                }
            }
            getStateBids(loadListToCompare);
        }

    }

    private void getStateBids(ArrayList<FindLoadsModel> loadListToCompare) {
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
    //----------------------------------------------------------------------------------------------
}