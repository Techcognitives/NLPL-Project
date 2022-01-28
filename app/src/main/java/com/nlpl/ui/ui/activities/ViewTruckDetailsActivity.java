package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.ui.adapters.TrucksAdapter;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewTruckDetailsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<TruckModel> truckList = new ArrayList<>();
    private TrucksAdapter truckListAdapter;
    private RecyclerView truckListRecyclerView;

    Dialog previewDialogRcBook, previewDialogInsurance;
    String phone, userId;
    ImageView previewRcBook, previewInsurance;

    ArrayList<String> arrayuserAllDrivers;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_truck_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(ViewTruckDetailsActivity.this);
        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.view_truck_details_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("My Trucks");
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTruckDetailsActivity.this, DashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.view_truck_details_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));

        //---------------------------- Get Truck Details -------------------------------------------
        truckListRecyclerView = (RecyclerView) findViewById(R.id.trucks_list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        arrayuserAllDrivers = new ArrayList<>();

        truckListAdapter = new TrucksAdapter(ViewTruckDetailsActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);

        getTruckList();
        //------------------------------------------------------------------------------------------

        previewDialogRcBook = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogRcBook.setContentView(R.layout.dialog_preview_images);
        previewDialogRcBook.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewRcBook = (ImageView) previewDialogRcBook.findViewById(R.id.dialog_preview_image_view);

        previewDialogInsurance = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogInsurance.setContentView(R.layout.dialog_preview_images);
        previewDialogInsurance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewInsurance = (ImageView) previewDialogInsurance.findViewById(R.id.dialog_preview_image_view);

    }

    public void getTruckList() {
        //---------------------------- Get Truck Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/truck/truckbyuserID/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    truckList = new ArrayList<>();
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        TruckModel model = new TruckModel();
                        model.setUser_id(obj.getString("user_id"));
                        model.setVehicle_no(obj.getString("vehicle_no"));
                        model.setTruck_type(obj.getString("truck_type"));
                        model.setVehicle_type(obj.getString("vehicle_type"));
                        model.setTruck_ft(obj.getString("truck_ft"));
                        model.setTruck_carrying_capacity(obj.getString("truck_carrying_capacity"));
                        model.setRc_book(obj.getString("rc_book"));
                        model.setVehicle_insurance(obj.getString("vehicle_insurance"));
                        model.setTruck_id(obj.getString("truck_id"));
                        model.setDriver_id(obj.getString("driver_id"));
                        truckList.add(model);
                    }
                    if (truckList.size() > 0) {
                        truckListAdapter.updateData(truckList);
                    } else {
                    }

//                    if (truckList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        truckListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        truckListRecyclerView.setLayoutParams(params);
//                    }

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

    public void getTruckDetails(TruckModel obj) {
        Intent intent = new Intent(ViewTruckDetailsActivity.this, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("fromBidNow", false);
        intent.putExtra("truckId", obj.getTruck_id());
        intent.putExtra("mobile", phone);

        startActivity(intent);
    }

    public void getOnClickPreviewTruckRcBook(TruckModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogRcBook.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogRcBook.show();
        previewDialogRcBook.getWindow().setAttributes(lp);

        String rcBookURL = obj.getRc_book();
        Log.i("IMAGE RC URL", rcBookURL);
        new DownloadImageTask(previewRcBook).execute(rcBookURL);
    }

    public void getOnClickPreviewTruckInsurance(TruckModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogInsurance.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogInsurance.show();
        previewDialogInsurance.getWindow().setAttributes(lp);

        String insuranceURL = obj.getVehicle_insurance();
        Log.i("IMAGE INSURANCE URL", insuranceURL);
        new DownloadImageTask(previewInsurance).execute(insuranceURL);
    }

    public void onClickAddTruckDetails(View view) {
        Intent intent3 = new Intent(ViewTruckDetailsActivity.this, VehicleDetailsActivity.class);
        intent3.putExtra("userId", userId);
        intent3.putExtra("fromBidNow", false);
        intent3.putExtra("isEdit", false);
        intent3.putExtra("mobile", phone);

        startActivity(intent3);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                Intent intent = new Intent(ViewTruckDetailsActivity.this, DashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                break;

            case R.id.bottom_nav_customer_dashboard:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i8 = new Intent(ViewTruckDetailsActivity.this, DashboardActivity.class);
        i8.putExtra("mobile2", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);

    }

    public void getDriverDetailsOnTruckActivity(TruckModel obj) {

        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(ViewTruckDetailsActivity.this);
        alert.setContentView(R.layout.dialog_preview_driver_truck_details);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(true);

        TextView driverDetailsHeading = alert.findViewById(R.id.dialog_driver_truck_details_title);
        TextView driverName = alert.findViewById(R.id.dialog_driver_truck_details_vehicle_number);
        TextView driverNumber = alert.findViewById(R.id.dialog_driver_truck_details_vehicle_model);
        TextView textviewGone = alert.findViewById(R.id.dialog_driver_truck_details_title_feet);
        TextView textviewGone2 = alert.findViewById(R.id.dialog_driver_truck_details_type);
        TextView textviewGone3 = alert.findViewById(R.id.dialog_driver_truck_details_capacity);
        TextView driverLicence = alert.findViewById(R.id.dialog_driver_truck_details_rc_book_preview);
        TextView driverSelfie = alert.findViewById(R.id.dialog_driver_truck_details_insurance_preview);
        TextView assignDriver = alert.findViewById(R.id.dialog_driver_truck_details_reassign_button);
        TextView ok = alert.findViewById(R.id.dialog_driver_truck_details_ok_button);
        TextView pleaseAddDriver = alert.findViewById(R.id.dialog_driver_truck_details_label_add_driver_bank);

        driverDetailsHeading.setText("Driver Details");
        textviewGone.setVisibility(View.GONE);
        textviewGone2.setVisibility(View.GONE);
        textviewGone3.setVisibility(View.GONE);
        driverLicence.setText("Driver Licence");
        driverSelfie.setText("Driver Selfie");
        pleaseAddDriver.setText("Please add a Driver");
        pleaseAddDriver.setVisibility(View.INVISIBLE);


        if (obj.getDriver_id().equals("null")) {
            pleaseAddDriver.setVisibility(View.VISIBLE);
            driverLicence.setVisibility(View.INVISIBLE);
            driverSelfie.setVisibility(View.INVISIBLE);
            driverName.setVisibility(View.INVISIBLE);
            driverNumber.setVisibility(View.INVISIBLE);
            assignDriver.setText("Assign Driver");

            assignDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDriverListDialog(obj.getUser_id());
                }
            });

        } else {

            assignDriver.setText("Reassign Driver");
            pleaseAddDriver.setVisibility(View.INVISIBLE);
            driverLicence.setVisibility(View.VISIBLE);
            driverSelfie.setVisibility(View.VISIBLE);
            driverName.setVisibility(View.VISIBLE);
            driverNumber.setVisibility(View.VISIBLE);
            //---------------------------- Get Driver Details -------------------------------------------
            String url1 = getString(R.string.baseURL) + "/driver/driverId/" + obj.getDriver_id();
            Log.i("URL: ", url1);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        truckList = new ArrayList<>();
                        JSONArray truckLists = response.getJSONArray("data");
                        for (int i = 0; i < truckLists.length(); i++) {
                            JSONObject obj = truckLists.getJSONObject(i);
                            driverName.setText(obj.getString("driver_name"));
                            driverNumber.setText(obj.getString("driver_number"));
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
    }

    private void getDriverListDialog(String userId) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(ViewTruckDetailsActivity.this);
        alert.setContentView(R.layout.dialog_spinner_bind);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(true);

        TextView addDriver = alert.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView cancel = alert.findViewById(R.id.dialog_spinner_bind_cancel);

        RecyclerView driverNames = alert.findViewById(R.id.dialog_spinner_bind_recycler_view);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ViewTruckDetailsActivity.this, R.layout.custom_list_row, arrayuserAllDrivers);
//        driverNames.setAdapter(adapter1);

    }
}