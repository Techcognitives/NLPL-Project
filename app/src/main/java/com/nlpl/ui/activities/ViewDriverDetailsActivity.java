package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
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
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.model.UpdateMethods.UpdateDriverDetails;
import com.nlpl.services.AddDriverService;
import com.nlpl.ui.adapters.DriversAdapter;
import com.nlpl.ui.adapters.TrucksListAdapter;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewDriverDetailsActivity extends AppCompat {

    private ArrayList<DriverModel> driverList = new ArrayList<>();
    private DriversAdapter driverListAdapter;
    private RecyclerView driverListRecyclerView;
    private RequestQueue mQueue;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<TruckModel> truckList = new ArrayList<>();
    private TrucksListAdapter truckListAdapter;
    private RecyclerView truckListRecyclerView;

    private AddDriverService addDriverService;

    ArrayList<String> arrayUserDriverId, arrayDriverMobileNo;
    Dialog previewDialogDriverDetails, previewDialogAssignedTruck;
    TextView previewDriverDetailsDriverBankName, previewDriverDetailsLabelAddDriverBank, previewDriverDetailsAddDriverBank, previewDriverDetailsDriverBankAccountNumber, previewDriverDetailsDriverBankIFSICode, previewDriverDetailsDriverBankDetailsTitle;
    String userIdTruckDetails, truckIdTruckDetails, vehicleNumberTruckDetails, modelTruckDetails, capacityTruckDetails, rcBookTruckDetails, insuranceTruckDetails, driverIdTruckDetails;

    String mobileNoDriverAPI, userDriverIdAPI, driverUserIdGet;

    Dialog previewDialogRcBook, previewDialogInsurance, previewDialogSpinner;
    ImageView previewRcBook, previewInsurance;

    Dialog previewDialogDL, previewDialogSelfie;
    TextView previewAssignedTruckTitle, previewAssignedTruckNumber, previewAssignedTruckModel, previewAssignedTruckCapacity, previewAssignedTruckRcBook, previewAssignedTruckInsurance, previewAssignedTruckReAssign, previewAssignedTruckOkButton, previewAssignedTruckMessage;
    ImageView previewDriverLicense, previewDriverSelfie;

    String phone, userId, driverId;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;
    EditText searchDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Driver", phone);
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(ViewDriverDetailsActivity.this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.234.163.179:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        addDriverService = retrofit.create(AddDriverService.class);

        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.view_driver_details_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.My_Drivers));
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
                JumpTo.goToServiceProviderDashboard(ViewDriverDetailsActivity.this, phone, true);
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.view_driver_details_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));
        //------------------------------------------------------------------------------------------

        arrayUserDriverId = new ArrayList<>();
        arrayDriverMobileNo = new ArrayList<>();

        previewDialogDriverDetails = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_details);
        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverDetailsDriverBankName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_bank_name);
        previewDriverDetailsDriverBankAccountNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_account_number);
        previewDriverDetailsDriverBankIFSICode = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_ifsc_code);
        previewDriverDetailsDriverBankDetailsTitle = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_title);
        previewDriverDetailsAddDriverBank = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_bank_details);
        previewDriverDetailsLabelAddDriverBank = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_label_add_driver_bank);

        previewDialogAssignedTruck = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogAssignedTruck.setContentView(R.layout.dialog_preview_driver_truck_details);
        previewDialogAssignedTruck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewAssignedTruckTitle = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_details_title);
        previewAssignedTruckNumber = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_vehicle_number);
        previewAssignedTruckModel = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_vehicle_model);
        previewAssignedTruckCapacity = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_capacity);
        previewAssignedTruckRcBook = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_rc_book_preview);
        previewAssignedTruckInsurance = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_insurance_preview);
        previewAssignedTruckReAssign = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_reassign_button);
        previewAssignedTruckOkButton = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_ok_button);
        previewAssignedTruckMessage = previewDialogAssignedTruck.findViewById(R.id.dialog_driver_truck_details_label_add_driver_bank);

        previewDialogRcBook = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogRcBook.setContentView(R.layout.dialog_preview_images);
        previewDialogRcBook.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewRcBook = (ImageView) previewDialogRcBook.findViewById(R.id.dialog_preview_image_view);

        previewDialogInsurance = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogInsurance.setContentView(R.layout.dialog_preview_images);
        previewDialogInsurance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewInsurance = (ImageView) previewDialogInsurance.findViewById(R.id.dialog_preview_image_view);

        previewDialogDL = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogDL.setContentView(R.layout.dialog_preview_images);
        previewDialogDL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverLicense = (ImageView) previewDialogDL.findViewById(R.id.dialog_preview_image_view);

        previewDialogSelfie = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogSelfie.setContentView(R.layout.dialog_preview_images);
        previewDialogSelfie.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverSelfie = (ImageView) previewDialogSelfie.findViewById(R.id.dialog_preview_image_view);

        previewDialogSpinner = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogSpinner.setContentView(R.layout.dialog_spinner_bind);
        previewDialogSpinner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView previewSpinnerTitle = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_title);
        TextView previewSpinnerAddTruck = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView previewSpinnerOkButton = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_cancel);

        previewSpinnerTitle.setText(getString(R.string.selectTruck));
        previewSpinnerAddTruck.setText(getString(R.string.Add_Truck));

        //---------------------------- Get Driver Details -------------------------------------------
        driverListRecyclerView = (RecyclerView) findViewById(R.id.driver_list_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversAdapter(ViewDriverDetailsActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);
        getDriverDetailsList();
        //------------------------------------------------------------------------------------------

        //---------------------------- Get Truck Details -------------------------------------------
        truckListRecyclerView = previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        truckListAdapter = new TrucksListAdapter(ViewDriverDetailsActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);
        getTruckList();
        //------------------------------------------------------------------------------------------
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_driver_details_refresh_constrain);
        searchDriver = (EditText) findViewById(R.id.view_driver_details_search_truck_edit_text);
        searchDriver.addTextChangedListener(searchDriverWatcher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });
    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
        JumpTo.goToViewDriverDetailsActivity(ViewDriverDetailsActivity.this, userId, phone, true);
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
                        modelDriver.setAlternate_ph_no(obj.getString("alternate_ph_no"));
                        driverList.add(modelDriver);
                    }
                    if (driverList.size() > 0) {
                        driverListAdapter.updateData(driverList);
                    } else {

                    }
//                    if (driverList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        driverListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        driverListRecyclerView.setLayoutParams(params);
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

    private void getUserDriverId(String getMobile) {
        String receivedMobile = getMobile;
        Log.i("received Mobile", receivedMobile);
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userDriverIdAPI = data.getString("user_id");
                        mobileNoDriverAPI = data.getString("phone_number");
                        arrayUserDriverId.add(userDriverIdAPI);
                        arrayDriverMobileNo.add(mobileNoDriverAPI);
                    }

                    for (int j = 0; j < arrayDriverMobileNo.size(); j++) {
                        if (arrayDriverMobileNo.get(j).equals(receivedMobile)) {
                            driverUserIdGet = arrayUserDriverId.get(j);
                            Log.i("DriverUserId", driverUserIdGet);
                            getUserDriverBankDetails(driverUserIdGet);
                        }
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }

    private void getUserDriverBankDetails(String driverUserId) {

        String url = getString(R.string.baseURL) + "/bank/getBkByUserId/" + driverUserId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String bankName = obj.getString("bank_name");
                        String bankAccountNumber = obj.getString("account_number");
                        String ifsiCode = obj.getString("IFSI_CODE");

                        if (bankName.length() == 0) {
                            previewDriverDetailsLabelAddDriverBank.setVisibility(View.VISIBLE);
                            previewDriverDetailsAddDriverBank.setVisibility(View.VISIBLE);
                            previewDriverDetailsDriverBankName.setVisibility(View.INVISIBLE);
                            previewDriverDetailsDriverBankAccountNumber.setVisibility(View.INVISIBLE);
                            previewDriverDetailsDriverBankIFSICode.setVisibility(View.INVISIBLE);
                        } else {
                            previewDriverDetailsDriverBankName.setVisibility(View.VISIBLE);
                            previewDriverDetailsDriverBankAccountNumber.setVisibility(View.VISIBLE);
                            previewDriverDetailsDriverBankIFSICode.setVisibility(View.VISIBLE);
                            previewDriverDetailsDriverBankName.setText(getString(R.string.Bank_Name) + bankName);
                            previewDriverDetailsDriverBankAccountNumber.setText(getString(R.string.Account_No) + bankAccountNumber);
                            previewDriverDetailsDriverBankIFSICode.setText(getString(R.string.IFSI_Code) + ifsiCode);
                            previewDriverDetailsLabelAddDriverBank.setVisibility(View.GONE);
                            previewDriverDetailsAddDriverBank.setVisibility(View.GONE);
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
    }

    public void onClickPreviewDriverLicense(DriverModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogDL.show();
        previewDialogDL.getWindow().setAttributes(lp);

        String drivingLicenseURL = obj.getUpload_lc();
        Log.i("IMAGE DL URL", drivingLicenseURL);
        new DownloadImageTask(previewDriverLicense).execute(drivingLicenseURL);
    }

    public void onClickPreviewDriverSelfie(DriverModel obj) {
        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(previewDialogSelfie.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.gravity = Gravity.CENTER;

        previewDialogSelfie.show();
        previewDialogSelfie.getWindow().setAttributes(lp2);

        String selfieURL = obj.getDriver_selfie();
        Log.i("IMAGE Selfie URL", selfieURL);
        new DownloadImageTask(previewDriverSelfie).execute(selfieURL);
    }

    public void onClickAddDriverDetails(View view) {
        ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
        JumpTo.goToDriverDetailsActivity(ViewDriverDetailsActivity.this, userId, phone, false, false, false, null, null);
    }

    public void onClickAddDriverBankDetails(View view) {
        ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
        JumpTo.goToBankDetailsActivity(ViewDriverDetailsActivity.this, driverUserIdGet, phone, false, true, null);
    }

    public void onClickCloseDialogDriverBankDetails(View view) {
        previewDialogAssignedTruck.dismiss();
        previewDialogDriverDetails.dismiss();
        RearrangeItems();
    }

    public void onClickPreviewDriverBankDetails(DriverModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogDriverDetails.show();
        previewDialogDriverDetails.setCancelable(false);
        previewDialogDriverDetails.getWindow().setAttributes(lp);

        getUserDriverId(obj.getDriver_number());
    }

    public void getDriverDetails(DriverModel obj) {
        ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
        JumpTo.goToDriverDetailsActivity(ViewDriverDetailsActivity.this, userId, phone, true, false, false, null, obj.getDriver_id());
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
                JumpTo.goToServiceProviderDashboard(ViewDriverDetailsActivity.this, phone, true);
                break;

            case R.id.bottom_nav_customer_dashboard:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
        JumpTo.goToServiceProviderDashboard(ViewDriverDetailsActivity.this, phone, true);
    }

    public void onClickPreviewAssignedTruckDetails(DriverModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogAssignedTruck.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogAssignedTruck.show();
        previewDialogAssignedTruck.setCancelable(false);
        previewDialogAssignedTruck.getWindow().setAttributes(lp);

        driverId = obj.getDriver_id();
        getTruckDetails(obj.getTruck_id());
    }

    public void getTruckDetails(String truckId) {
        //---------------------------- Get Truck Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/truck/" + truckId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        userIdTruckDetails = obj.getString("user_id");
                        truckIdTruckDetails = obj.getString("truck_id");
                        vehicleNumberTruckDetails = obj.getString("vehicle_no");
                        modelTruckDetails = obj.getString("truck_type");
                        capacityTruckDetails = obj.getString("truck_carrying_capacity");
                        rcBookTruckDetails = obj.getString("rc_book");
                        insuranceTruckDetails = obj.getString("vehicle_insurance");
                        driverIdTruckDetails = obj.getString("driver_id");
                    }

                    previewAssignedTruckNumber.setText(vehicleNumberTruckDetails);
                    previewAssignedTruckModel.setText(getString(R.string.model) + modelTruckDetails);
                    previewAssignedTruckCapacity.setText(getString(R.string.Capacity) + capacityTruckDetails);

                    if (vehicleNumberTruckDetails == null) {
                        previewAssignedTruckNumber.setVisibility(View.INVISIBLE);
                        previewAssignedTruckModel.setVisibility(View.INVISIBLE);
                        previewAssignedTruckCapacity.setVisibility(View.INVISIBLE);
                        previewAssignedTruckRcBook.setVisibility(View.INVISIBLE);
                        previewAssignedTruckInsurance.setVisibility(View.INVISIBLE);
                        previewAssignedTruckMessage.setVisibility(View.VISIBLE);
                        previewAssignedTruckReAssign.setText(getString(R.string.Assign_Truck));
                    } else {
                        previewAssignedTruckNumber.setVisibility(View.VISIBLE);
                        previewAssignedTruckModel.setVisibility(View.VISIBLE);
                        previewAssignedTruckCapacity.setVisibility(View.VISIBLE);
                        previewAssignedTruckRcBook.setVisibility(View.VISIBLE);
                        previewAssignedTruckInsurance.setVisibility(View.VISIBLE);
                        previewAssignedTruckMessage.setVisibility(View.INVISIBLE);
                        previewAssignedTruckReAssign.setText(getString(R.string.ReAssign_Truck));
                    }

                    new DownloadImageTask(previewRcBook).execute(rcBookTruckDetails);
                    new DownloadImageTask(previewInsurance).execute(insuranceTruckDetails);

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
        lp.copyFrom(previewDialogRcBook.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogRcBook.show();
        previewDialogRcBook.getWindow().setAttributes(lp);
    }

    public void onClickPreviewInsuranceAssigned(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogInsurance.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogInsurance.show();
        previewDialogInsurance.getWindow().setAttributes(lp);
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

    public void onClickCancelSelectBind(View view) {
        previewDialogSpinner.dismiss();
        previewDialogAssignedTruck.dismiss();
        RearrangeItems();
    }

    public void onClickAddDriverDetailsAssigned(View view) {
        previewDialogSpinner.dismiss();
        previewDialogAssignedTruck.dismiss();
        ShowAlert.loadingDialog(ViewDriverDetailsActivity.this);
        JumpTo.goToVehicleDetailsActivity(ViewDriverDetailsActivity.this, userId, phone, false, false, true, false, driverId, null);
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
                        model.setTruck_carrying_capacity(obj.getString("truck_carrying_capacity"));
                        model.setRc_book(obj.getString("rc_book"));
                        model.setVehicle_insurance(obj.getString("vehicle_insurance"));
                        model.setTruck_id(obj.getString("truck_id"));
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

    public void onClickAssignTruckFromList(TruckModel obj) {
        Log.i("TruckId", obj.getTruck_id());
        UpdateDriverDetails.updateDriverTruckId(driverId, obj.getTruck_id());
        previewDialogAssignedTruck.dismiss();
        previewDialogSpinner.dismiss();
        RearrangeItems();
    }

    private TextWatcher searchDriverWatcher = new TextWatcher() {
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
        ArrayList<DriverModel> searchVehicleList = new ArrayList<>();

        for (DriverModel item : driverList) {
            if (item.getDriver_name().toLowerCase().contains(text.toLowerCase())) {
                searchVehicleList.add(item);
            }
            if (item.getDriver_number().toLowerCase().contains(text.toLowerCase())) {
                searchVehicleList.add(item);
            }
        }
        driverListAdapter.updateData(searchVehicleList);
    }

}