package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.ui.ui.adapters.FindLoadAdapter;
import com.nlpl.ui.ui.adapters.SearchLoadAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class FindLoadsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<FindLoadsModel> bidsList = new ArrayList<>();
    private ArrayList<FindLoadsModel> anList, apList, arList, asList, brList, chList, cgList, ddList,
            dd2List, dlList, gaList, gjList, hrList, hpList, jkList, jhList, kaList, klList, laList,
            ldList, mpList, mhList, mnList, mlList, mzList, nlList, odList, pyList, pbList, rjList,
            skList, tnList, tsList, trList, ukList, upList, wbList;
    private FindLoadAdapter bidsListAdapter;
    private RecyclerView bidsListRecyclerView;

    private ArrayList<SearchLoadModel> searchLoadModels = new ArrayList<>();
    ArrayList<String> searchList;
    private SearchLoadAdapter searchLoadAdapter;
    private RecyclerView searchListRecyclerView;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    String phone, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_loads);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
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
        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.find_loads_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Load Notifications");
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
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                Intent intent = new Intent(FindLoadsActivity.this, ServiceProviderDashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                break;

            case R.id.bottom_nav_customer_dashboard:
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

                        if (obj.getString("bid_status").equals("loadPosted") || obj.getString("bid_status").equals("loadReactivated")) {
                            bidsList.add(findLoadsModel);
                        }
                    }

                    for (int i = 0; i < bidsList.size(); i++) {
                        if (bidsList.size() == 0) {
                            bidsListRecyclerView.setVisibility(View.GONE);
                        } else if (bidsList.size() == 1) {
                            ArrayList<FindLoadsModel> newList = new ArrayList<>(bidsList.subList(bidsList.size() - 1, bidsList.size()));
                            bidsListAdapter.updateData(newList);
                        } else if (bidsList.size() == 2) {
                            ArrayList<FindLoadsModel> newList = new ArrayList<>(bidsList.subList(bidsList.size() - 2, bidsList.size()));
                            bidsListAdapter.updateData(newList);
                        } else if (bidsList.size() >= 3) {
                            ArrayList<FindLoadsModel> newList = new ArrayList<>(bidsList.subList(bidsList.size() - 3, bidsList.size()));
                            bidsListAdapter.updateData(newList);
                        }

                        if (bidsList.get(i).getPick_state().equals("AN")) {
                            anList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("AP")) {
                            apList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("AR")) {
                            arList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("AS")) {
                            asList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("BR")) {
                            brList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("CH/PB")) {
                            chList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("CG")) {
                            cgList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("DD")) {
                            ddList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("DD2")) {
                            dd2List.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("DL")) {
                            dlList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("GA")) {
                            gaList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("GJ")) {
                            gjList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("HR")) {
                            hrList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("HP")) {
                            hpList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("JK")) {
                            jkList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("JH")) {
                            jhList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("KA")) {
                            kaList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("KL")) {
                            klList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("LA")) {
                            laList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("LD")) {
                            ldList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("MP")) {
                            mpList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("MH")) {
                            mhList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("MN")) {
                            mnList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("ML")) {
                            mlList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("MZ")) {
                            mzList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("NL")) {
                            nlList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("OD")) {
                            odList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("PY")) {
                            pyList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("PB")) {
                            pbList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("RJ")) {
                            rjList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("SK")) {
                            skList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("TN")) {
                            tnList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("TS")) {
                            tsList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("TR")) {
                            trList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("UK")) {
                            ukList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("UP")) {
                            upList.add(bidsList.get(i));
                        }
                        if (bidsList.get(i).getPick_state().equals("WB")) {
                            wbList.add(bidsList.get(i));
                        }
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

        Intent i8 = new Intent(FindLoadsActivity.this, ServiceProviderDashboardActivity.class);
        i8.putExtra("mobile2", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}