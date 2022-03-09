package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.model.UpdateMethods.UpdateTruckDetails;
import com.nlpl.services.AddTruckService;
import com.nlpl.ui.ui.adapters.DriversListAdapter;
import com.nlpl.ui.ui.adapters.TrucksAdapter;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewTruckDetailsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<TruckModel> truckList = new ArrayList<>();
    private TrucksAdapter truckListAdapter;
    private RecyclerView truckListRecyclerView;

    private ArrayList<DriverModel> driverList = new ArrayList<>();
    private DriversListAdapter driverListAdapter;
    private RecyclerView driverListRecyclerView;

    SwipeRefreshLayout swipeRefreshLayout;

    Dialog previewDialogRcBook, previewDialogInsurance;
    String phone, userId, truckIdPass;
    ImageView previewRcBook, previewInsurance;

    Dialog previewDialogDL, previewDialogSelfie, previewDialogSpinner;
    ImageView previewDL, previewSelfie;

    ArrayList<String> arrayuserAllDrivers;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;
    EditText searchVehicle;

    Dialog previewDialogDriverDetails;
    TextView previewDriverDetailsTitle, previewDriverDetailsDriverName, previewDriverDetailsDriverNumber, textviewGone, textviewGone2, previewDriverDetailsDriverEmails, previewDriverDetailsDriverLicence, previewDriverDetailsDriverSelfie, previewDriverDetailsAssignDriverButton, previewDriverDetailsOKButton, previewDriverDetailsMessage;

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
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
                JumpTo.goToServiceProviderDashboard(ViewTruckDetailsActivity.this, phone, true);
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
        previewDialogDL = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogDL.setContentView(R.layout.dialog_preview_images);
        previewDialogDL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDL = (ImageView) previewDialogDL.findViewById(R.id.dialog_preview_image_view);

        previewDialogSelfie = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogSelfie.setContentView(R.layout.dialog_preview_images);
        previewDialogSelfie.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewSelfie = (ImageView) previewDialogSelfie.findViewById(R.id.dialog_preview_image_view);
        //----------------------- Alert Dialog -------------------------------------------------
        previewDialogDriverDetails = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_truck_details);
        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverDetailsTitle = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_title);
        previewDriverDetailsDriverName = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_vehicle_number);
        previewDriverDetailsDriverNumber = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_vehicle_model);
        textviewGone = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_title_feet);
        textviewGone2 = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_type);
        previewDriverDetailsDriverEmails = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_capacity);
        previewDriverDetailsDriverLicence = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_rc_book_preview);
        previewDriverDetailsDriverSelfie = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_insurance_preview);
        previewDriverDetailsAssignDriverButton = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_reassign_button);
        previewDriverDetailsOKButton = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_ok_button);
        previewDriverDetailsMessage = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_label_add_driver_bank);
        //------------------------------------------------------------------------------------------
        previewDialogSpinner = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogSpinner.setContentView(R.layout.dialog_spinner_bind);
        previewDialogSpinner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView previewSpinnerTitle = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_title);
        TextView previewSpinnerAddTruck = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView previewSpinnerOkButton = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_cancel);
        //------------------------------------------------------------------------------------------

        //---------------------------- Get Driver Details ------------------------------------------
        driverListRecyclerView = previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversListAdapter(ViewTruckDetailsActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);
        getDriverDetailsList();
        //------------------------------------------------------------------------------------------

        previewDialogRcBook = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogRcBook.setContentView(R.layout.dialog_preview_images);
        previewDialogRcBook.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewRcBook = (ImageView) previewDialogRcBook.findViewById(R.id.dialog_preview_image_view);

        previewDialogInsurance = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogInsurance.setContentView(R.layout.dialog_preview_images);
        previewDialogInsurance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewInsurance = (ImageView) previewDialogInsurance.findViewById(R.id.dialog_preview_image_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_truck_details_refresh_constrain);
        searchVehicle = (EditText) findViewById(R.id.view_truck_details_search_truck_edit_text);
        searchVehicle.addTextChangedListener(searchVehicleWatcher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });

    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToViewVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
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
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true, false, false, false,null, obj.getTruck_id());
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
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, false, false, false, false, null, null);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
                JumpTo.goToServiceProviderDashboard(ViewTruckDetailsActivity.this, phone, true);
                break;

            case R.id.bottom_nav_customer_dashboard:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToServiceProviderDashboard(ViewTruckDetailsActivity.this, phone, true);
    }

    public void getDriverDetailsOnTruckActivity(TruckModel obj) {
        truckIdPass = obj.getTruck_id();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogDriverDetails.show();
        previewDialogDriverDetails.getWindow().setAttributes(lp);
        previewDialogDriverDetails.setCancelable(true);

        previewDriverDetailsTitle.setText("Driver Details");
        textviewGone.setVisibility(View.GONE);
        textviewGone2.setVisibility(View.GONE);
        previewDriverDetailsDriverLicence.setText("Driver Licence");
        previewDriverDetailsDriverSelfie.setText("Driver Selfie");
        previewDriverDetailsMessage.setText("Please add a Driver");
        previewDriverDetailsMessage.setVisibility(View.INVISIBLE);

        if (obj.getDriver_id().equals("null")) {
            previewDriverDetailsMessage.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverLicence.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverSelfie.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverName.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverNumber.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverEmails.setVisibility(View.INVISIBLE);
            previewDriverDetailsAssignDriverButton.setText("Assign Driver");
        } else {
            previewDriverDetailsAssignDriverButton.setText("Re-Assign Driver");
            previewDriverDetailsMessage.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverLicence.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverSelfie.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverName.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverNumber.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverEmails.setVisibility(View.VISIBLE);
            getDriverDetailsAssigned(obj.getDriver_id());
        }
    }

    public void getDriverDetailsAssigned(String driverId){
        //---------------------------- Get Driver Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/driverId/" + driverId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    truckList = new ArrayList<>();
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        previewDriverDetailsDriverName.setText(obj.getString("driver_name"));
                        previewDriverDetailsDriverNumber.setText(obj.getString("driver_number"));
                        String driverEmail = obj.getString("driver_emailId");
                        String driverDlURL = obj.getString("upload_dl");
                        String driverSelfieURL = obj.getString("driver_selfie");

                        new DownloadImageTask(previewDL).execute(driverDlURL);
                        new DownloadImageTask(previewSelfie).execute(driverSelfieURL);

                        if (driverEmail == null){
                            previewDriverDetailsDriverEmails.setVisibility(View.INVISIBLE);
                        }else{
                            previewDriverDetailsDriverEmails.setVisibility(View.VISIBLE);
                            previewDriverDetailsDriverEmails.setText(driverEmail);
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

    public void onClickPreviewRcBookAssigned(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogDL.show();
        previewDialogDL.getWindow().setAttributes(lp);
    }

    public void onClickPreviewInsuranceAssigned(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogSelfie.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogSelfie.show();
        previewDialogSelfie.getWindow().setAttributes(lp);
    }

    public void onClickCloseDialogDriverBankDetails(View view) {
        previewDialogDriverDetails.dismiss();
        RearrangeItems();
    }

    public void onClickReAssignTruck(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogSpinner.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogSpinner.show();
        previewDialogSpinner.getWindow().setAttributes(lp);
    }

    public void onClickAddDriverDetailsAssigned(View view) {
        previewDialogSpinner.dismiss();
        previewDialogDriverDetails.dismiss();
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToDriverDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, false, false, true, truckIdPass, null);
    }

    public void getDriverDetailsList() {
        //---------------------------- Get Driver Details ------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/userId/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    driverList = new ArrayList<>();
                    JSONArray driverLists = response.getJSONArray("data");
                    for (int i = 0; i < driverLists.length(); i++) {
                        JSONObject obj = driverLists.getJSONObject(i);
                        DriverModel modelDriver = new DriverModel();
                        modelDriver.setUser_id(obj.getString("user_id"));
                        modelDriver.setTruck_id(obj.getString("truck_id"));
                        modelDriver.setDriver_id(obj.getString("driver_id"));
                        modelDriver.setDriver_name(obj.getString("driver_name"));
                        modelDriver.setUpload_lc(obj.getString("upload_dl"));
                        modelDriver.setDriver_selfie(obj.getString("driver_selfie"));
                        modelDriver.setDriver_number(obj.getString("driver_number"));
                        modelDriver.setDriver_emailId(obj.getString("driver_emailId"));
                        driverList.add(modelDriver);
                    }
                    if (driverList.size() > 0) {
                        driverListAdapter.updateData(driverList);
                    } else {

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

    public void onClickCancelSelectBind(View view) {
        previewDialogSpinner.dismiss();
        previewDialogDriverDetails.dismiss();
        RearrangeItems();
    }

    public void onClickReAssignDriver(DriverModel obj) {
        UpdateTruckDetails.updateTruckDriverId(truckIdPass, obj.getDriver_id());
        RearrangeItems();
    }

    private TextWatcher searchVehicleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length()==0){
                RearrangeItems();
            }
            filter(editable.toString());
        }
    };

    private void filter(String text) {
        ArrayList<TruckModel> searchVehicleList = new ArrayList<>();

        for (TruckModel item : truckList) {
            if (item.getVehicle_no().toLowerCase().contains(text.toLowerCase())) {
                searchVehicleList.add(item);
            }
        }
        truckListAdapter.updateData(searchVehicleList);
    }
}