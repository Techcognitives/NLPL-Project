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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.BankModel;
import com.nlpl.model.DriverModel;
import com.nlpl.model.TruckModel;
import com.nlpl.model.UserUpdate;
import com.nlpl.services.UserService;
import com.nlpl.ui.ui.adapters.BanksAdapter;
import com.nlpl.ui.ui.adapters.DriversAdapter;
import com.nlpl.ui.ui.adapters.TrucksAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    private boolean isRecExpanded = false;
    boolean isPersonalDetailsDone = true, isBankDetailsDone = true, isTruckDetailsDone = true, isDriverDetailsDone = true;

    private static String BASE_URL = "http://65.2.3.41:8080";
    private UserService userService;

    View action_bar;
    TextView addDriver, addTruck, addBankDetails, accNoDone, editPersonalDetails, actionBarTitle, language, addCompany, phoneDone, nameDone, firmName, addressDone;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    View bottomNav;
    TextView truckLoadText;

    String userId;

    Button personalDetails, bankDetails, addTrucks, addDrivers;
    String mobile, name, address, pinCode, city, role;
    TextView nameTitle, mobileText, emailIdTextView, officeAddressTextView;
    ConstraintLayout personal_done, bankDone, vehicleDone, driverDone;

    String companyName, companyAddress, companyCity, companyZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            Log.i("UserId PandR", userId);
        }

        action_bar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setText(getString(R.string.english));
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(ProfileAndRegistrationActivity.this);
                languageDialog.setContentView(R.layout.dialog_language);
                languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(languageDialog.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp2.gravity = Gravity.BOTTOM;

                languageDialog.show();
                languageDialog.getWindow().setAttributes(lp2);

                TextView english = languageDialog.findViewById(R.id.english);
                TextView marathi = languageDialog.findViewById(R.id.marathi);
                TextView hindi = languageDialog.findViewById(R.id.hindi);

                english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.english));
                    }
                });

                marathi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.marathi));
                    }
                });

                hindi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.hindi));
                    }
                });

            }
        });

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
        getUserDetails();
        getCompanyDetails();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);

        //---------------------------- Get Truck Details -------------------------------------------
        truckListRecyclerView = (RecyclerView) findViewById(R.id.trucks_list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        truckListAdapter = new TrucksAdapter(ProfileAndRegistrationActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);

        getTruckList();
        //------------------------------------------------------------------------------------------

        //---------------------------- Get Driver Details -------------------------------------------
        driverListRecyclerView = (RecyclerView) findViewById(R.id.driver_list_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversAdapter(ProfileAndRegistrationActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);

        getDriverDetailsList();
        //------------------------------------------------------------------------------------------

        //---------------------------- Get Bank Details -------------------------------------------
        bankListRecyclerView = (RecyclerView) findViewById(R.id.bank_list_view);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(true);
        bankListRecyclerView.setLayoutManager(linearLayoutManagerBank);
        bankListRecyclerView.setHasFixedSize(true);

        bankListAdapter = new BanksAdapter(ProfileAndRegistrationActivity.this, bankList);
        bankListRecyclerView.setAdapter(bankListAdapter);

        getBankDetailsList();

        //------------------------------------------------------------------------------------------
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

//                        Boolean isPersonalDetailsDone = obj.getBoolean("isPersonal_dt_Added");
//                        Boolean isFirmDetailsDone = obj.getBoolean("isCompany_added");
//                        Boolean isBankDetailsDone = obj.getBoolean("isBankDetails_Given");
//                        Boolean isTruckDetailsDone = obj.getBoolean("isTruck_added");
//                        Boolean isDriverDetailsDone = obj.getBoolean("isDriver_added");

                        if (isPersonalDetailsDone) {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        } else {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
                        }

                        if (isBankDetailsDone) {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        } else {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
                        }

                        if (isTruckDetailsDone) {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
                        }

                        if (isDriverDetailsDone) {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
                        }

                        String hello = getString(R.string.hello);
                        nameTitle.setText(hello + " " + name + "!");
                        String s1 = mobile.substring(2, 12);
                        mobileText.setText("+91 " + s1);

                        nameDone.setText(" " + name);
                        phoneDone.setText(" Phone: +91 " + s1);
                        emailIdTextView.setText(" Email Id");
                        addressDone.setText(" Address: " + address + ", " + city + " " + pinCode);

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
        String url1 = getString(R.string.baseURL) + "/truck/truckbyuserID/"+userId;
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
                        model.setVehicle_body_type(obj.getString("vehicle_body_type"));
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
        String url1 = getString(R.string.baseURL) + "/company/41e69305-7260-4b01-8892-a0f4f7daec71";
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

                    if (companyName == null){
                        firmName.setVisibility(View.GONE);
                        officeAddressTextView.setVisibility(View.GONE);
                        addCompany.setVisibility(View.VISIBLE);
                    }else{
                        firmName.setVisibility(View.VISIBLE);
                        officeAddressTextView.setVisibility(View.VISIBLE);
                        addCompany.setVisibility(View.GONE);
                        firmName.setText(" Firm Name: "+companyName);
                        officeAddressTextView.setText(" Office Address: "+companyAddress+", "+companyCity+" "+companyZip);
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

    public void getTruckDetails(TruckModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit",true);
        intent.putExtra("truckId", obj.getTruck_id());

        startActivity(intent);
    }

    public void getDriverDetails(DriverModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit",true);
        intent.putExtra("driverId", obj.getDriver_id());

        startActivity(intent);
    }

    public void getBankDetails(BankModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit",true);
        intent.putExtra("bankDetailsID", obj.getBank_id());

        startActivity(intent);
    }

    //-------------------------------- Update User Details -----------------------------------------
    private void updateUserDetails() {

//------------------------------------- Update Type ------------------------------------------------
        UserUpdate userUpdate = new UserUpdate("" + userId, null, null, null, "paid", null, null, null, null, null, null, null, null, null, null, null, null);

        Call<UserUpdate> call = userService.updateUserDetails("" + userId, userUpdate);

        call.enqueue(new Callback<UserUpdate>() {
            @Override
            public void onResponse(Call<UserUpdate> call, retrofit2.Response<UserUpdate> response) {

            }

            @Override
            public void onFailure(Call<UserUpdate> call, Throwable t) {

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    public void onClickProfileAndRegister(View view) {
        switch (view.getId()) {
            case R.id.profile_registration_personal_details_button:
                if (isPersonalDetailsDone) {
                    if (isRecExpanded) {
                        personal_done.setVisibility(View.GONE);
//                        addCompany.setVisibility(View.GONE);
                        personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        isRecExpanded = false;
                    } else {
                        personal_done.setVisibility(View.VISIBLE);
//                        addCompany.setVisibility(View.VISIBLE);
                        bankDone.setVisibility(View.GONE);
                        vehicleDone.setVisibility(View.GONE);
                        driverDone.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                        personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_down_personal, 0);

                        if (isBankDetailsDone) {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        } else {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
                        }
                        if (isTruckDetailsDone) {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
                        }

                        if (isDriverDetailsDone) {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
                        }

                        isRecExpanded = true;
                    }
                } else {
                    personal_done.setVisibility(View.GONE);
//                    addCompany.setVisibility(View.GONE);
                    personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);

                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("userId", userId);

                    startActivity(intent);
                }
                break;

            case R.id.profile_registration_bank_details_button:
                if (isBankDetailsDone) {
                    if (isRecExpanded) {
                        bankDone.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                        bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        isRecExpanded = false;
                    } else {
                        bankDone.setVisibility(View.VISIBLE);
                        addBankDetails.setVisibility(View.VISIBLE);
                        personal_done.setVisibility(View.GONE);
                        vehicleDone.setVisibility(View.GONE);
                        driverDone.setVisibility(View.GONE);
//                        addCompany.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                        bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_down_personal, 0);

                        if (isPersonalDetailsDone) {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        } else {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
                        }

                        if (isTruckDetailsDone) {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
                        }

                        if (isDriverDetailsDone) {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
                        }

                        isRecExpanded = true;
                    }
                } else {
                    bankDone.setVisibility(View.GONE);
                    addBankDetails.setVisibility(View.GONE);
                    bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);

                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                    intent.putExtra("isEdit",false);
                    intent.putExtra("userId", userId);

                    startActivity(intent);
                }
                break;

            case R.id.profile_registration_truck_details:
                if (isTruckDetailsDone) {
                    if (isRecExpanded) {
                        vehicleDone.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                        addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        isRecExpanded = false;
                    } else {
                        vehicleDone.setVisibility(View.VISIBLE);
                        addTruck.setVisibility(View.VISIBLE);
                        personal_done.setVisibility(View.GONE);
                        bankDone.setVisibility(View.GONE);
                        driverDone.setVisibility(View.GONE);
//                        addCompany.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                        addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_down_personal, 0);

                        if (isPersonalDetailsDone) {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        } else {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
                        }

                        if (isBankDetailsDone) {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        } else {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
                        }

                        if (isDriverDetailsDone) {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
                        }

                        isRecExpanded = true;
                    }
                } else {
                    Intent intent2 = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                    intent2.putExtra("userId", userId);
                    intent2.putExtra("isEdit",false);
                    startActivity(intent2);
                }

                break;

            case R.id.profile_registration_driver_details:
                if (isDriverDetailsDone) {

                    if (isRecExpanded) {
                        driverDone.setVisibility(View.GONE);
                        addDriver.setVisibility(View.GONE);
                        addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        isRecExpanded = false;
                    } else {
                        driverDone.setVisibility(View.VISIBLE);
                        addDriver.setVisibility(View.VISIBLE);
                        personal_done.setVisibility(View.GONE);
                        bankDone.setVisibility(View.GONE);
                        vehicleDone.setVisibility(View.GONE);
//                        addCompany.setVisibility(View.GONE);
                        addBankDetails.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);
                        addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_down_personal, 0);

                        if (isPersonalDetailsDone) {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        } else {
                            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
                        }

                        if (isBankDetailsDone) {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        } else {
                            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
                        }

                        if (isTruckDetailsDone) {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        } else {
                            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
                        }
                        isRecExpanded = true;
                    }
                } else {

                    driverDone.setVisibility(View.GONE);
                    addDriver.setVisibility(View.GONE);
                    addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);

                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("isEdit", false);
                    startActivity(intent);
                }
                break;

            case R.id.editPersonalDetails:

                Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsAndIdProofActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;

            case R.id.add_company:
                Intent intent1 = new Intent(ProfileAndRegistrationActivity.this, CompanyDetailsActivity.class);
                intent1.putExtra("userId", userId);
                startActivity(intent1);
                break;

            case R.id.addBankDone:
                Intent intent2 = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                intent2.putExtra("userId", userId);
                intent2.putExtra("isEdit",false);
                startActivity(intent2);
                break;

            case R.id.addTruck:
                Intent intent3 = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                intent3.putExtra("userId", userId);
                intent3.putExtra("isEdit",false);
                startActivity(intent3);
                break;

            case R.id.addDriverDone:
                Intent intent4 = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                intent4.putExtra("userId", userId);
                intent4.putExtra("isEdit",false);
                startActivity(intent4);
                break;
        }
    }
}