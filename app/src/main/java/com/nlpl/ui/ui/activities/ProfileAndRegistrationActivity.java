package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import org.w3c.dom.Text;

import java.io.InputStream;
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

    private boolean isPersonalExpanded = false, isBankExpanded = false, isTruckExpanded = false, isDriverExpanded = false;
    String isPersonalDetailsDone, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone, isFirmDetailsDone;

    View action_bar;
    TextView accHolderName, addDriver, addTruck, addBankDetails, accNoDone, editPersonalDetails, actionBarTitle, addCompany, phoneDone, nameDone, firmName, addressDone;
    ImageView actionBarBackButton, previewPersonalDetails;

    View bottomNav;
    TextView truckLoadText;

    String userId, userIdAPI, phone, mobileNoAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;

    Button personalDetails, bankDetails, addTrucks, addDrivers;
    String mobile, name, address, pinCode, city, role, emailIdAPI;
    TextView nameTitle, mobileText, emailIdTextView, officeAddressTextView;
    ConstraintLayout personal_done, bankDone, vehicleDone, driverDone;

    String companyName, companyAddress, companyCity, companyZip;

    TextView dialogPersonalDetailsName, dialogPersonalDetailsPhone, dialogPersonalDetailsEmail, dialogPersonalDetailsAddress, dialogPersonalDetailsFirmName, dialogPersonalDetailsFirmAddress;
    TextView dialogBankDetailsBankName, dialogBankDetailsBankAccountNumber, dialogBankDetailsBankIFSICode;
    Dialog previewDialogPersonalDetails, previewDialogBankDetails, previewDialogTruckDetails, previewDialogDriverDetails;
    TextView previewTruckDetailsVehicleNumber, previewTruckDetailsTruckType, previewTruckDetailsVehicleType, previewTruckDetailsTruckFeet, previewTruckDetailsVehicleCapacity;
    TextView previewDriverDetailsDriverName, previewDriverDetailsDriverNumber, previewDriverDetailsEmailId;
    ImageView previewPanImage, previewAadharImage, previewCancelledCheque, previewRcBook, previewInsurance, previewDrivingLicense, previewDriverSelfie;

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
        previewPersonalDetails = (ImageView) findViewById(R.id.profile_and_registration_preview_personal_details);

        previewDialogPersonalDetails = new Dialog(ProfileAndRegistrationActivity.this);
        previewDialogPersonalDetails.setContentView(R.layout.dialog_preview_personal_details);
        previewDialogPersonalDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogPersonalDetailsName = (TextView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_name_text_view);
        dialogPersonalDetailsPhone = (TextView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_phone_number_text_view);
        dialogPersonalDetailsEmail = (TextView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_email_id_text_view);
        dialogPersonalDetailsAddress = (TextView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_address_text_view);
        dialogPersonalDetailsFirmName = (TextView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_firm_name_text_view);
        dialogPersonalDetailsFirmAddress = (TextView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_firm_address_text_view);
        previewPanImage = (ImageView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_pan_image_view);
        previewAadharImage = (ImageView) previewDialogPersonalDetails.findViewById(R.id.dialog_personal_details_aadhar_image_view);

        previewDialogBankDetails = new Dialog(ProfileAndRegistrationActivity.this);
        previewDialogBankDetails.setContentView(R.layout.dialog_preview_bank_details);
        previewDialogBankDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBankDetailsBankName = (TextView) previewDialogBankDetails.findViewById(R.id.dialog_bank_details_name_text_view);
        dialogBankDetailsBankAccountNumber = (TextView) previewDialogBankDetails.findViewById(R.id.dialog_bank_details_account_number_text_view);
        dialogBankDetailsBankIFSICode = (TextView) previewDialogBankDetails.findViewById(R.id.dialog_bank_details_ifsi_code_text_view);
        previewCancelledCheque = (ImageView) previewDialogBankDetails.findViewById(R.id.dialog_bank_details_cheque_image_view);

        previewDialogTruckDetails = new Dialog(ProfileAndRegistrationActivity.this);
        previewDialogTruckDetails.setContentView(R.layout.dialog_preview_truck_details);
        previewDialogTruckDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewTruckDetailsVehicleNumber = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_vehicle_number_text_view);
        previewTruckDetailsTruckType = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_truck_type_text_view);
        previewTruckDetailsVehicleType = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_vehicle_type_text_view);
        previewTruckDetailsTruckFeet = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_truck_ft_text_view);
        previewTruckDetailsVehicleCapacity = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_capacity_text_view);
        previewRcBook = (ImageView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_rc_image_view);
        previewInsurance = (ImageView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_insurance_image_view);

        previewDialogDriverDetails = new Dialog(ProfileAndRegistrationActivity.this);
        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_details);
        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverDetailsDriverName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_name_text_view);
        previewDriverDetailsDriverNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_phone_number_text_view);
        previewDriverDetailsEmailId = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_email_id_text_view);
        previewDrivingLicense = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driving_license_image_view);
        previewDriverSelfie = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_selfie_image_view);

        mQueue = Volley.newRequestQueue(ProfileAndRegistrationActivity.this);
        getImageURL();

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
                        dialogPersonalDetailsName.setText(" Name: " + name);
                        nameDone.setText(" " + name);

                        String s1 = mobile.substring(2, 12);
                        mobileText.setText("+91 " + s1);

                        phoneDone.setText(" Phone: +91 " + s1);
                        dialogPersonalDetailsPhone.setText(" Phone: +91 " + s1);

                        emailIdTextView.setText(" Email: " + emailIdAPI);
                        dialogPersonalDetailsEmail.setText(" Email: " + emailIdAPI);

                        addressDone.setText(" Address: " + address + ", " + city + " " + pinCode);
                        dialogPersonalDetailsAddress.setText(" Address: " + address + ", " + city + " " + pinCode);

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
                        model.setTruck_type(obj.getString("truck_type"));
                        model.setVehicle_type(obj.getString("vehicle_type"));
                        model.setTruck_ft(obj.getString("truck_ft"));
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
//                        modelDriver.setUpload_lc(obj.getString("upload_lc"));
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

    public void getBankDetailsList() {
        //---------------------------- Get Bank Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/bank/getBkByUserId/" + userId;
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
                        modelBank.setBank_id(obj.getString("bank_id"));
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
                    dialogPersonalDetailsFirmName.setText(" Firm Name: " + companyName);

                    officeAddressTextView.setText(" Office Address: " + companyAddress + ", " + " " + companyCity + ", " + companyZip);
                    dialogPersonalDetailsFirmAddress.setText(" Office Address: " + companyAddress + ", " + " " + companyCity + ", " + companyZip);

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

    public void onClickPreviewBankDetails(BankModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogBankDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogBankDetails.show();
        previewDialogBankDetails.getWindow().setAttributes(lp);

        dialogBankDetailsBankName.setText(" Bank Name: " + obj.getAccountholder_name());
        dialogBankDetailsBankAccountNumber.setText(" Account Number: " + obj.getAccount_number());
        dialogBankDetailsBankIFSICode.setText(" IFSI Code: " + obj.getIFSI_CODE());
    }

    public void getOnClickPreviewTruckDetails(TruckModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogTruckDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogTruckDetails.show();
        previewDialogTruckDetails.getWindow().setAttributes(lp);

        previewTruckDetailsVehicleNumber.setText(" Vehicle Number: " + obj.getVehicle_no());
        previewTruckDetailsTruckType.setText(" Truck Model: " + obj.getTruck_type());
        previewTruckDetailsVehicleType.setText(" Vehicle Type: " + obj.getVehicle_type());
        previewTruckDetailsTruckFeet.setText(" Truck ft.: " + obj.getTruck_ft());
        previewTruckDetailsVehicleCapacity.setText(" Truck Capacity: " + obj.getTruck_carrying_capacity());
    }

    public void onClickPreviewDriverDetails(DriverModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogDriverDetails.show();
        previewDialogDriverDetails.getWindow().setAttributes(lp);

        previewDriverDetailsDriverName.setText(" Driver Name: " + obj.getDriver_name());
        previewDriverDetailsDriverNumber.setText(" Mobile Number: +" + obj.getDriver_number());
        previewDriverDetailsEmailId.setText(" Email Id: " + obj.getDriver_emailId());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void getImageURL() {
        String url = getString(R.string.baseURL) + "/imgbucket/Images/4";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");

                        String panImageURL, aadharImageURL, cancelledChequeURL, rcBookURL, insuranceURL, drivingLicenseURL, selfieURL;

                        if (imageType.equals("adhar")) {
                            aadharImageURL = obj.getString("image_url");
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewAadharImage).execute(aadharImageURL);
                            Log.i("IMAGE AADHAR URL", aadharImageURL);
                        }

                        if (imageType.equals("pan")) {
                            panImageURL = obj.getString("image_url");
                            Log.i("IMAGE PAN URL", panImageURL);
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewPanImage).execute(panImageURL);
                        }

                        if (imageType.equals("cheque")) {
                            cancelledChequeURL = obj.getString("image_url");
                            Log.i("IMAGE CHEQUE URL", cancelledChequeURL);
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewCancelledCheque).execute(cancelledChequeURL);
                        }

                        if (imageType.equals("rc")) {
                            rcBookURL = obj.getString("image_url");
                            Log.i("IMAGE RC URL", rcBookURL);
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewRcBook).execute(rcBookURL);
                        }

                        if (imageType.equals("insurance")) {
                            insuranceURL = obj.getString("image_url");
                            Log.i("IMAGE INSURANCE URL", insuranceURL);
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewInsurance).execute(insuranceURL);
                        }

                        if (imageType.equals("dl")) {
                            drivingLicenseURL = obj.getString("image_url");
                            Log.i("IMAGE DL URL", drivingLicenseURL);
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewDrivingLicense).execute(drivingLicenseURL);
                        }

                        if (imageType.equals("selfie")) {
                            selfieURL = obj.getString("image_url");
                            Log.i("IMAGE DL URL", selfieURL);
                            new ProfileAndRegistrationActivity.DownloadImageTask(previewDriverSelfie).execute(selfieURL);
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

    public void getTruckDetails(TruckModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("truckId", obj.getTruck_id());
        intent.putExtra("mobile", phone);

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
                    if (isPersonalExpanded == false) {
                        isPersonalExpanded = true;
                        personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_down_personal, 0);
                        personal_done.setVisibility(View.VISIBLE);

                        if (isBankExpanded || isTruckExpanded || isDriverExpanded) {

//                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);

                            isBankExpanded = false;
                            isTruckExpanded = false;
                            isDriverExpanded = false;
                            bankDone.setVisibility(View.GONE);
                            addBankDetails.setVisibility(View.GONE);
                            vehicleDone.setVisibility(View.GONE);
                            driverDone.setVisibility(View.GONE);
                            addTruck.setVisibility(View.GONE);
                            addDriver.setVisibility(View.GONE);
                        }

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
                        addCompany.setVisibility(View.GONE);
                        personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        isPersonalExpanded = false;
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
                    if (isBankExpanded == false) {
                        isBankExpanded = true;

                        bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_down_personal, 0);
                        bankDone.setVisibility(View.VISIBLE);
                        addBankDetails.setVisibility(View.VISIBLE);

                        if (isPersonalExpanded || isDriverExpanded || isTruckExpanded) {

                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
//                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);

                            isPersonalExpanded = false;
                            isTruckExpanded = false;
                            isDriverExpanded = false;
                            personal_done.setVisibility(View.GONE);
                            vehicleDone.setVisibility(View.GONE);
                            driverDone.setVisibility(View.GONE);
                            addCompany.setVisibility(View.GONE);
                            addTruck.setVisibility(View.GONE);
                            addDriver.setVisibility(View.GONE);
                        }

                    } else {
                        bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        isBankExpanded = false;
                        bankDone.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                    intent.putExtra("isEdit", false);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                }
                break;

            case R.id.profile_registration_truck_details:
                if (isTruckDetailsDone.equals("1")) {
                    getTruckList();
                    if (isTruckExpanded == false) {
                        isTruckExpanded = true;

                        addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_down_personal, 0);
                        vehicleDone.setVisibility(View.VISIBLE);
                        addTruck.setVisibility(View.VISIBLE);

                        if (isPersonalExpanded || isBankExpanded || isTruckExpanded) {

                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
//                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);

                            isPersonalExpanded = false;
                            isBankExpanded = false;
                            isDriverExpanded = false;
                            bankDone.setVisibility(View.GONE);
                            addBankDetails.setVisibility(View.GONE);
                            personal_done.setVisibility(View.GONE);
                            driverDone.setVisibility(View.GONE);
                            addCompany.setVisibility(View.GONE);
                            addDriver.setVisibility(View.GONE);
                        }

                    } else {

                        addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        isTruckExpanded = false;
                        vehicleDone.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                    }

                } else {
                    Intent intent2 = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                    intent2.putExtra("userId", userId);
                    intent2.putExtra("isEdit", false);
                    intent2.putExtra("mobile", phone);
                    startActivity(intent2);
                }

                break;

            case R.id.profile_registration_driver_details:
                getDriverDetailsList();
                if (isDriverDetailsDone.equals("1")) {
                    if (isDriverExpanded == false) {
                        isDriverExpanded = true;
                        addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_down_personal, 0);
                        driverDone.setVisibility(View.VISIBLE);
                        addDriver.setVisibility(View.VISIBLE);

                        if (isPersonalExpanded || isTruckExpanded || isBankExpanded) {

                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
//                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);

                            isBankExpanded = false;
                            isTruckExpanded = false;
                            isPersonalExpanded = false;
                            bankDone.setVisibility(View.GONE);
                            addBankDetails.setVisibility(View.GONE);
                            personal_done.setVisibility(View.GONE);
                            vehicleDone.setVisibility(View.GONE);
                            addCompany.setVisibility(View.GONE);
                            addTruck.setVisibility(View.GONE);
                        }
                    } else {
                        isDriverExpanded = false;
                        addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
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
                intent2.putExtra("isEdit", false);
                intent2.putExtra("mobile", phone);

                startActivity(intent2);
                break;

            case R.id.addTruck:
                Intent intent3 = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                intent3.putExtra("userId", userId);
                intent3.putExtra("isEdit", false);
                intent3.putExtra("mobile", phone);
                startActivity(intent3);
                break;

            case R.id.addDriverDone:
                Intent intent4 = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                intent4.putExtra("userId", userId);
                intent4.putExtra("isEdit", false);
                intent4.putExtra("mobile", phone);
                startActivity(intent4);
                break;
        }
    }

    public void onClickPreviewPersonalDetails(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogPersonalDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogPersonalDetails.show();
        previewDialogPersonalDetails.getWindow().setAttributes(lp);
    }
}