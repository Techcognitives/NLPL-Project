package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BankModel;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.ui.adapters.BanksAdapter;
import com.nlpl.ui.ui.adapters.DriversAdapter;
import com.nlpl.ui.ui.adapters.TrucksAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileAndRegistrationActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<TruckModel> truckList = new ArrayList<>();
    private TrucksAdapter truckListAdapter;
    private RecyclerView truckListRecyclerView;

    private ArrayList<DriverModel> driverList = new ArrayList<>();
    private DriversAdapter driverListAdapter;
    private RecyclerView driverListRecyclerView;

    private ArrayList<BankModel> bankList = new ArrayList<>();
    private BanksAdapter bankListAdapter;
    private RecyclerView bankListRecyclerView;

    private boolean isRecExpanded = true;
    String isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    View action_bar;
    TextView accHolderName, addDriver, addTruck, addBankDetails, accNoDone, editPersonalDetails, actionBarTitle, addCompany, phoneDone, nameDone, firmName, addressDone;
    ImageView actionBarBackButton;

    View bottomNav;
    TextView truckLoadText;

    String userId, userIdAPI, phone, mobileNoAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

    Button personalDetails, bankDetails, addTrucks, addDrivers;
    String mobile, name, address, pinCode, city, role, emailIdAPI;
    TextView nameTitle, mobileText, emailIdTextView, officeAddressTextView;
    ConstraintLayout personal_done, bankDone, vehicleDone, driverDone;

    String companyName, companyAddress, companyCity, companyZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile2");
            Log.i("Mobile No Registration", phone);
        }

        action_bar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);

        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();

        actionBarTitle.setText("My Profile");
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.profile_registration_bottom_nav_bar);
        truckLoadText = (TextView) bottomNav.findViewById(R.id.dhuejsfcb);

        personalDetails = findViewById(R.id.profile_registration_personal_details_button);
        bankDetails = findViewById(R.id.profile_registration_bank_details_button);
        addTrucks = findViewById(R.id.profile_registration_truck_details);
        addDrivers = findViewById(R.id.profile_registration_driver_details);
        personal_done = findViewById(R.id.personal_done);
        addCompany = findViewById(R.id.add_company);
        phoneDone = findViewById(R.id.phone_done);
        nameDone = findViewById(R.id.name_done);
        firmName = findViewById(R.id.firm_name_done);
        addressDone = findViewById(R.id.address_done);
        editPersonalDetails = findViewById(R.id.editPersonalDetails);
        accNoDone = findViewById(R.id.bank_list_account_number_text);
        bankDone = findViewById(R.id.bankDetailsDoneLayout);
        addBankDetails = findViewById(R.id.addBankDone);
        vehicleDone = findViewById(R.id.addTrucksDone);
        addTruck = findViewById(R.id.addTruck);
        driverDone = findViewById(R.id.driverDone);
        addDriver = findViewById(R.id.addDriverDone);

        nameTitle = (TextView) findViewById(R.id.profile_registration_name_text);
        mobileText = (TextView) findViewById(R.id.profile_registration_mobile_text);
        emailIdTextView = (TextView) findViewById(R.id.profile_and_registration_email_id_text);
        officeAddressTextView = (TextView) findViewById(R.id.profile_and_registration_office_address_text);

        mQueue = Volley.newRequestQueue(ProfileAndRegistrationActivity.this);

        personal_done.setVisibility(View.GONE);
        bankDone.setVisibility(View.GONE);
        vehicleDone.setVisibility(View.GONE);
        driverDone.setVisibility(View.GONE);
        addCompany.setVisibility(View.GONE);

        //---------------------------- Get Truck Details -------------------------------------------
        truckListRecyclerView = (RecyclerView) findViewById(R.id.trucks_list_view);

        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userIdAPI = data.getString("user_id");
                        arrayUserId.add(userIdAPI);
                        mobileNoAPI = data.getString("phone_number");
                        arrayMobileNo.add(mobileNoAPI);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(phone)) {
                            userId = arrayUserId.get(j);
                            Log.i("userIDAPI:", userId);
                        }
                    }

                    getUserDetails();

                    //---------------------------- Get Truck Details -------------------------------------------
                    truckListRecyclerView = (RecyclerView) findViewById(R.id.trucks_list_view);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setReverseLayout(true);
                    truckListRecyclerView.setLayoutManager(linearLayoutManager);
                    truckListRecyclerView.setHasFixedSize(true);

                    truckListAdapter = new TrucksAdapter(ProfileAndRegistrationActivity.this, truckList);
                    truckListRecyclerView.setAdapter(truckListAdapter);

                    //------------------------------------------------------------------------------------------

                    //---------------------------- Get Driver Details -------------------------------------------
                    driverListRecyclerView = (RecyclerView) findViewById(R.id.driver_list_view);

                    LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManagerDriver.setReverseLayout(true);
                    driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
                    driverListRecyclerView.setHasFixedSize(true);

                    driverListAdapter = new DriversAdapter(ProfileAndRegistrationActivity.this, driverList);
                    driverListRecyclerView.setAdapter(driverListAdapter);

                    //------------------------------------------------------------------------------------------

                    //---------------------------- Get Bank Details -------------------------------------------
                    bankListRecyclerView = (RecyclerView) findViewById(R.id.bank_list_view);

                    LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManagerBank.setReverseLayout(true);
                    bankListRecyclerView.setLayoutManager(linearLayoutManagerBank);
                    bankListRecyclerView.setHasFixedSize(true);

                    bankListAdapter = new BanksAdapter(ProfileAndRegistrationActivity.this, bankList);
                    bankListRecyclerView.setAdapter(bankListAdapter);

                    //------------------------------------------------------------------------------------------

//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------
    }

    private void getUserDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        name = obj.getString("name");
                        mobile = obj.getString("phone_number");
                        address = obj.getString("address");
                        city = obj.getString("preferred_location");
                        pinCode = obj.getString("pin_code");
                        role = obj.getString("user_type");

                        emailIdAPI = obj.getString("email_id");

                        isPersonalDetailsDone = obj.getString("isPersonal_dt_added");
                        isFirmDetailsDone = obj.getString("isCompany_added");
                        isBankDetailsDone = obj.getString("isBankDetails_given");
                        isTruckDetailsDone = obj.getString("isTruck_added");
                        isDriverDetailsDone = obj.getString("isDriver_added");

                        //-------------------------------------Personal details ---- -------------------------------------
                        String hello = getString(R.string.hello);
                        nameTitle.setText(hello + " " + name + "!");
                        String s1 = mobile.substring(2, 12);
                        mobileText.setText("+91 " + s1);

                        nameDone.setText(" " + name);
                        phoneDone.setText(" Phone: +91 " + s1);
                        emailIdTextView.setText(" Email: " + emailIdAPI);
                        addressDone.setText(" Address: " + address + ", " + city + " " + pinCode);

                        //-----------------------------Check all Done or not-----------------------------------------
                        if (isPersonalDetailsDone.equals("1")) {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        } else {
                            personal_done.setVisibility(View.GONE);
                            addCompany.setVisibility(View.GONE);
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
                        }

                        if (isBankDetailsDone.equals("1")) {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        } else {
                            bankDone.setVisibility(View.GONE);
                            addBankDetails.setVisibility(View.GONE);
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
                        }

                        if (isTruckDetailsDone.equals("1")) {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        } else {
                            vehicleDone.setVisibility(View.GONE);
                            addTruck.setVisibility(View.GONE);
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
                        }

                        if (isDriverDetailsDone.equals("1")) {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        } else {
                            driverDone.setVisibility(View.GONE);
                            addDriver.setVisibility(View.GONE);
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
                        }
                        //--------------------------------------------------------------------------------------------------------

                        if (role.equals("Customer")) {
                            truckLoadText.setText("Post a Load");
                        } else {
                            truckLoadText.setText("Post a Trip");
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
//                        model.setVehicle_body_type(obj.getString("vehicle_body_type"));
                        model.setRc_book(obj.getString("rc_book"));
                        model.setVehicle_insurance(obj.getString("vehicle_insurance"));
                        model.setTruck_id(obj.getString("truck_id"));
                        truckList.add(model);
                    }
                    if (truckList.size() > 0) {
                        truckListAdapter.updateData(truckList);
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
                        modelDriver.setDriver_id(obj.getString("driver_id"));
                        modelDriver.setDriver_name(obj.getString("driver_name"));
                        modelDriver.setUpload_lc(obj.getString("upload_lc"));
                        modelDriver.setDriver_number(obj.getString("driver_number"));
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

    public void getBankDetailsList() {
        //---------------------------- Get Bank Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/bank/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bankList = new ArrayList<>();
                    JSONArray bankLists = response.getJSONArray("data");
                    for (int i = 0; i < bankLists.length(); i++) {
                        JSONObject obj = bankLists.getJSONObject(i);
                        BankModel modelBank = new BankModel();
                        modelBank.setUser_id(obj.getString("user_id"));
                        modelBank.setAccountholder_name(obj.getString("accountholder_name"));
                        modelBank.setAccount_number(obj.getString("account_number"));
                        modelBank.setRe_enter_acc_num(obj.getString("re_enter_acc_num"));
                        modelBank.setIFSI_CODE(obj.getString("IFSI_CODE"));
                        bankList.add(modelBank);
                    }
                    if (bankList.size() > 0) {
                        bankListAdapter.updateData(bankList);
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

    public void getCompanyDetails() {
        //---------------------------- Get Company Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/company/get/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray companyDetails = response.getJSONArray("data");
                    for (int i = 0; i < companyDetails.length(); i++) {
                        JSONObject data = companyDetails.getJSONObject(i);
                        companyName = data.getString("company_name");
                        companyAddress = data.getString("comp_add");
                        companyCity = data.getString("comp_city");
                        companyZip = data.getString("comp_zip");
                    }

                    firmName.setText(" Firm Name: " + companyName);
                    officeAddressTextView.setText(" Office Address: " + companyAddress + ", " + " " + companyCity + ", " + companyZip);


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
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("truckId", obj.getTruck_id());

        startActivity(intent);
    }

    public void getDriverDetails(DriverModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("driverId", obj.getDriver_id());
        intent.putExtra("mobile", phone);

        startActivity(intent);
    }

    public void getBankDetails(BankModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("bankDetailsID", obj.getBank_id());
        intent.putExtra("mobile", phone);

        startActivity(intent);
    }

    public void onClickProfileAndRegister(View view) {
        switch (view.getId()) {
            case R.id.profile_registration_personal_details_button:
                if (isPersonalDetailsDone.equals("1")) {
                    if (isRecExpanded) {
                        isRecExpanded = false;

                        personal_done.setVisibility(View.VISIBLE);

                        bankDone.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                        vehicleDone.setVisibility(View.GONE);
                        driverDone.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                        addDriver.setVisibility(View.GONE);

                        if (isFirmDetailsDone.equals("1")) {
                            getCompanyDetails();
                            firmName.setVisibility(View.VISIBLE);
                            officeAddressTextView.setVisibility(View.VISIBLE);
                            addCompany.setVisibility(View.GONE);
                        } else {
                            firmName.setVisibility(View.GONE);
                            officeAddressTextView.setVisibility(View.GONE);
                            addCompany.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isRecExpanded = true;
                        personal_done.setVisibility(View.GONE);
                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;

            case R.id.profile_registration_bank_details_button:
                if (isBankDetailsDone.equals("1")) {
                    getBankDetailsList();
                    if (isRecExpanded){
                        isRecExpanded=false;

                        bankDone.setVisibility(View.VISIBLE);
                        addBankDetails.setVisibility(View.VISIBLE);

                        personal_done.setVisibility(View.GONE);
                        vehicleDone.setVisibility(View.GONE);
                        driverDone.setVisibility(View.GONE);
                        addCompany.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                        addDriver.setVisibility(View.GONE);
                    } else{
                        isRecExpanded=true;
                        bankDone.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                    intent.putExtra("isEdit",false);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;

//            case R.id.profile_registration_truck_details:
//                if (isTruckDetailsDone.equals("1")){
//                    getTruckList();
//                    if (isRecExpanded){
//                        isRecExpanded=false;
//
//                        vehicleDone.setVisibility(View.VISIBLE);
//                        addTruck.setVisibility(View.VISIBLE);
//
//                        bankDone.setVisibility(View.GONE);
//                        addBankDetails.setVisibility(View.GONE);
//                        personal_done.setVisibility(View.GONE);
//                        driverDone.setVisibility(View.GONE);
//                        addCompany.setVisibility(View.GONE);
//                        addDriver.setVisibility(View.GONE);
//                    } else {
//                        isRecExpanded=true;
//                        vehicleDone.setVisibility(View.GONE);
//                        addTruck.setVisibility(View.GONE);
//                    }
//
//                } else {
//                    Intent intent2 = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
//                    intent2.putExtra("userId", userId);
//                    intent2.putExtra("isEdit",false);
//                    intent2.putExtra("mobile", phone);
//                    startActivity(intent2);
//                }
//
//                break;

            case R.id.profile_registration_driver_details:
                getDriverDetailsList();
                if (isDriverDetailsDone.equals("1")){
                    if (isRecExpanded){
                        isRecExpanded=false;

                        driverDone.setVisibility(View.VISIBLE);
                        addDriver.setVisibility(View.VISIBLE);

                        bankDone.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                        personal_done.setVisibility(View.GONE);
                        vehicleDone.setVisibility(View.GONE);
                        addCompany.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                    } else {
                        isRecExpanded=true;

                        driverDone.setVisibility(View.GONE);
                        addDriver.setVisibility(View.GONE);
                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("isEdit", false);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }

                break;

            case R.id.editPersonalDetails:
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsAndIdProofActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                startActivity(intent);
                break;

            case R.id.add_company:
                Intent intent1 = new Intent(ProfileAndRegistrationActivity.this, CompanyDetailsActivity.class);
                intent1.putExtra("userId", userId);
                intent1.putExtra("mobile", phone);
                startActivity(intent1);
                break;

            case R.id.addBankDone:
                Intent intent2 = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                intent2.putExtra("userId", userId);
                intent2.putExtra("isEdit",false);
                intent2.putExtra("mobile", phone);

                startActivity(intent2);
                break;

//            case R.id.addTruck:
//                Intent intent3 = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
//                intent3.putExtra("userId", userId);
//                intent3.putExtra("isEdit",false);
//                intent3.putExtra("mobile", phone);
//                startActivity(intent3);
//                break;
//
            case R.id.addDriverDone:
                Intent intent4 = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                intent4.putExtra("userId", userId);
                intent4.putExtra("isEdit",false);
                intent4.putExtra("mobile", phone);
                startActivity(intent4);
                break;
        }
    }
}