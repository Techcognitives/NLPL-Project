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

    View action_bar;
    TextView addDriver, addTruck, addBankDetails, addBankDone, bankNameDone, accNoDone, editPersonalDetails, actionBarTitle, language, addCompany, phoneDone, nameDone, firmDone, firmName, addressDone;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    View bottomNav;
    TextView truckLoadText;

    String userId;

    Button personalDetails, bankDetails, addTrucks, addDrivers;
    String driverName, mobile, name, address, pinCode, city, bankName, accNo, vehicleNo, role;
    TextView nameTitle, mobileText;
    ConstraintLayout personal_done, bankDone, vehicleDone, driverDone;

    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, getIsPersonalDetailsDoneVisible = false, getIsBankDetailsDoneVisible = false, getIsAddTrucksDoneVisible = false, getIsAddDriversDoneVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            address = bundle.getString("address");
            pinCode = bundle.getString("pinCode");
            city = bundle.getString("city");
            userId = bundle.getString("userId");
            bankName = bundle.getString("bankName");
            accNo = bundle.getString("accNo");
            vehicleNo = bundle.getString("vehicleNo");
            driverName = bundle.getString("driverName");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            role = bundle.getString("role");
            Log.i("Role", role);
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

        if (role.equals("Customer")) {
            truckLoadText.setText("Post a Load");
        } else {
            truckLoadText.setText("Post a Trip");
        }

        personalDetails = findViewById(R.id.profile_registration_personal_details_button);
        bankDetails = findViewById(R.id.profile_registration_bank_details_button);
        addTrucks = findViewById(R.id.profile_registration_truck_details);
        addDrivers = findViewById(R.id.profile_registration_driver_details);
        personal_done = findViewById(R.id.personal_done);
        addCompany = findViewById(R.id.add_company);
        phoneDone = findViewById(R.id.phone_done);
        nameDone = findViewById(R.id.name_done);
        firmDone = findViewById(R.id.firm_done);
        firmName = findViewById(R.id.firm_name_done);
        addressDone = findViewById(R.id.address_done);
        editPersonalDetails = findViewById(R.id.editPersonalDetails);
        bankNameDone = findViewById(R.id.bankNameDone);
        accNoDone = findViewById(R.id.accNoDone);
        bankDone = findViewById(R.id.bankDetailsDoneLayout);
        addBankDone = findViewById(R.id.addBankDone);
        addBankDetails = findViewById(R.id.addBankDone);
        vehicleDone = findViewById(R.id.addTrucksDone);
        addTruck = findViewById(R.id.addTruck);
        driverDone = findViewById(R.id.driverDone);
        addDriver = findViewById(R.id.addDriverDone);

        nameTitle = (TextView) findViewById(R.id.profile_registration_name_text);
        mobileText = (TextView) findViewById(R.id.profile_registration_mobile_text);


        mQueue = Volley.newRequestQueue(ProfileAndRegistrationActivity.this);
        getUserDetails();


        if (isPersonalDetailsDone) {

            bankDone.setVisibility(View.GONE);
            addBankDone.setVisibility(View.GONE);
            if (isBankDetailsDone) {
                bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
            } else {
                bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
            }

            vehicleDone.setVisibility(View.GONE);
            addTruck.setVisibility(View.GONE);
            if (isAddTrucksDone) {
                addTrucks.setText(" My Trucks");
                addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
            } else {
                addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
            }
            driverDone.setVisibility(View.GONE);
            addDriver.setVisibility(View.GONE);
            if (isAddDriversDone) {
                addDrivers.setText(" My Drivers");
                addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
            } else {
                addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
            }

            getIsPersonalDetailsDoneVisible = true;
            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_down_personal, 0);
            personal_done.setVisibility(View.VISIBLE);
            addCompany.setVisibility(View.VISIBLE);
            nameDone.setText(name);
            addressDone.setText(address + ", " + city + " " + pinCode);

        }

        if (isBankDetailsDone) {

            personal_done.setVisibility(View.GONE);
            addCompany.setVisibility(View.GONE);
            if (isPersonalDetailsDone) {
                personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
            } else {
                personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
            }

            vehicleDone.setVisibility(View.GONE);
            addTruck.setVisibility(View.GONE);
            if (isAddTrucksDone) {
                addTrucks.setText(" My Trucks");
                addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
            } else {
                addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
            }
            driverDone.setVisibility(View.GONE);
            addDriver.setVisibility(View.GONE);
            if (isAddDriversDone) {
                addDrivers.setText(" My Drivers");
                addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
            } else {
                addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
            }

            getIsBankDetailsDoneVisible = true;
            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_down_personal, 0);

        }

        if (isAddTrucksDone) {

            addTrucks.setText(" My Trucks");

            bankDone.setVisibility(View.GONE);
            addBankDone.setVisibility(View.GONE);
            if (isBankDetailsDone) {
                bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
            } else {
                bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
            }

            personal_done.setVisibility(View.GONE);
            addCompany.setVisibility(View.GONE);
            if (isPersonalDetailsDone) {
                personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
            } else {
                personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
            }

            driverDone.setVisibility(View.GONE);
            addDriver.setVisibility(View.GONE);
            if (isAddDriversDone) {
                addDrivers.setText(" My Drivers");
                addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
            } else {
                addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver, 0, R.drawable.ic_right, 0);
            }

            getIsAddTrucksDoneVisible = true;
            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_down_personal, 0);
            vehicleDone.setVisibility(View.VISIBLE);
            addTruck.setVisibility(View.VISIBLE);
        }

        if (isAddDriversDone) {

            addDrivers.setText(" My Drivers");

            bankDone.setVisibility(View.GONE);
            addBankDone.setVisibility(View.GONE);
            if (isBankDetailsDone) {
                bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
            } else {
                bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank, 0, R.drawable.ic_right, 0);
            }

            vehicleDone.setVisibility(View.GONE);
            addTruck.setVisibility(View.GONE);
            if (isAddTrucksDone) {
                addTrucks.setText(" My Trucks");
                addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
            } else {
                addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck, 0, R.drawable.ic_right, 0);
            }

            personal_done.setVisibility(View.GONE);
            addCompany.setVisibility(View.GONE);
            if (isPersonalDetailsDone) {
                personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
            } else {
                personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal, 0, R.drawable.ic_right, 0);
            }

            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_down_personal, 0);
            getIsAddDriversDoneVisible = true;
            driverDone.setVisibility(View.VISIBLE);
            addDriver.setVisibility(View.VISIBLE);
        }

        if (isPersonalDetailsDone && isBankDetailsDone && isAddTrucksDone && isAddDriversDone) {
        }

        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, CompanyDetailsActivity.class);
                startActivity(intent);
            }
        });

        editPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsAndIdProofActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("userId", userId);
                intent.putExtra("bankName", bankName);
                intent.putExtra("accNo", accNo);
                intent.putExtra("vehicleNo", vehicleNo);
                intent.putExtra("driverName", driverName);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver", isAddDriversDone);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        addBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("userId", userId);
                intent.putExtra("bankName", bankName);
                intent.putExtra("accNo", accNo);
                intent.putExtra("vehicleNo", vehicleNo);
                intent.putExtra("driverName", driverName);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver", isAddDriversDone);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        addTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("userId", userId);
                intent.putExtra("bankName", bankName);
                intent.putExtra("accNo", accNo);
                intent.putExtra("vehicleNo", vehicleNo);
                intent.putExtra("driverName", driverName);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver", isAddDriversDone);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("userId", userId);
                intent.putExtra("bankName", bankName);
                intent.putExtra("accNo", accNo);
                intent.putExtra("vehicleNo", vehicleNo);
                intent.putExtra("driverName", driverName);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver", isAddDriversDone);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });


        personalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPersonalDetailsDone) {
                    if (getIsPersonalDetailsDoneVisible) {
                        personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_right, 0);
                        personal_done.setVisibility(View.GONE);
                        addCompany.setVisibility(View.GONE);
                        getIsPersonalDetailsDoneVisible = false;
                    } else {
                        personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, R.drawable.ic_down_personal, 0);
                        personal_done.setVisibility(View.VISIBLE);
                        addCompany.setVisibility(View.VISIBLE);
                        getIsPersonalDetailsDoneVisible = true;
                        nameDone.setText(name);
                        addressDone.setText(address + ", " + city + " " + pinCode);
                    }

                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("mobile3", mobile);
                    intent.putExtra("name3", name);
                    intent.putExtra("address", address);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("city", city);
                    intent.putExtra("userId", userId);
                    intent.putExtra("bankName", bankName);
                    intent.putExtra("accNo", accNo);
                    intent.putExtra("vehicleNo", vehicleNo);
                    intent.putExtra("driverName", driverName);
                    intent.putExtra("isPersonal", isPersonalDetailsDone);
                    intent.putExtra("isBank", isBankDetailsDone);
                    intent.putExtra("isTrucks", isAddTrucksDone);
                    intent.putExtra("isDriver", isAddDriversDone);
                    intent.putExtra("role", role);
                    startActivity(intent);
//                finish();
                }
            }
        });

        bankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isBankDetailsDone) {
                    if (getIsBankDetailsDoneVisible) {
                        getIsBankDetailsDoneVisible = false;
                        bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_right, 0);
                        bankDone.setVisibility(View.GONE);
                        addBankDone.setVisibility(View.GONE);

                    } else {
                        getIsBankDetailsDoneVisible = true;
                        bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, R.drawable.ic_down_personal, 0);
                        bankDone.setVisibility(View.VISIBLE);
                        addBankDone.setVisibility(View.VISIBLE);
                        bankNameDone.setText(bankName);
                        accNoDone.setText(accNo);
                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                    intent.putExtra("mobile3", mobile);
                    intent.putExtra("name3", name);
                    intent.putExtra("address", address);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("city", city);
                    intent.putExtra("userId", userId);
                    intent.putExtra("bankName", bankName);
                    intent.putExtra("accNo", accNo);
                    intent.putExtra("vehicleNo", vehicleNo);
                    intent.putExtra("driverName", driverName);
                    intent.putExtra("isPersonal", isPersonalDetailsDone);
                    intent.putExtra("isBank", isBankDetailsDone);
                    intent.putExtra("isTrucks", isAddTrucksDone);
                    intent.putExtra("isDriver", isAddDriversDone);
                    intent.putExtra("role", role);
                    startActivity(intent);
//                finish();
                }
            }
        });

        addTrucks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isAddTrucksDone) {
                    addTrucks.setText(" My Trucks");
                    if (getIsAddTrucksDoneVisible) {
                        getIsAddTrucksDoneVisible = false;
                        addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_right, 0);
                        vehicleDone.setVisibility(View.GONE);
                        addTruck.setVisibility(View.GONE);

                    } else {
                        getIsAddTrucksDoneVisible = true;
                        addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, R.drawable.ic_down_personal, 0);
                        vehicleDone.setVisibility(View.VISIBLE);
                        addTruck.setVisibility(View.VISIBLE);

                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                    intent.putExtra("mobile3", mobile);
                    intent.putExtra("name3", name);
                    intent.putExtra("address", address);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("city", city);
                    intent.putExtra("userId", userId);
                    intent.putExtra("bankName", bankName);
                    intent.putExtra("accNo", accNo);
                    intent.putExtra("vehicleNo", vehicleNo);
                    intent.putExtra("driverName", driverName);
                    intent.putExtra("isPersonal", isPersonalDetailsDone);
                    intent.putExtra("isBank", isBankDetailsDone);
                    intent.putExtra("isTrucks", isAddTrucksDone);
                    intent.putExtra("isDriver", isAddDriversDone);
                    intent.putExtra("role", role);
                    startActivity(intent);
//                finish();
                }
            }
        });

        addDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isAddDriversDone) {
                    addDrivers.setText(" My Drivers");
                    if (getIsAddDriversDoneVisible) {
                        addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_right, 0);
                        getIsAddDriversDoneVisible = false;
                        driverDone.setVisibility(View.GONE);
                        addDriver.setVisibility(View.GONE);
                    } else {
                        addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, R.drawable.ic_down_personal, 0);
                        getIsAddDriversDoneVisible = true;
                        driverDone.setVisibility(View.VISIBLE);
                        addDriver.setVisibility(View.VISIBLE);
                    }
                } else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                    intent.putExtra("mobile3", mobile);
                    intent.putExtra("name3", name);
                    intent.putExtra("address", address);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("city", city);
                    intent.putExtra("userId", userId);
                    intent.putExtra("bankName", bankName);
                    intent.putExtra("accNo", accNo);
                    intent.putExtra("vehicleNo", vehicleNo);
                    intent.putExtra("driverName", driverName);
                    intent.putExtra("isPersonal", isPersonalDetailsDone);
                    intent.putExtra("isBank", isBankDetailsDone);
                    intent.putExtra("isTrucks", isAddTrucksDone);
                    intent.putExtra("isDriver", isAddDriversDone);
                    intent.putExtra("role", role);
                    startActivity(intent);
//                finish();
                }
            }
        });

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

    private void getUserDetails(){

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        name =  obj.getString("name");
                        mobile = obj.getString("phone_number");

                        String hello = getString(R.string.hello);
                        nameTitle.setText(hello + " " + name + "!");
                        String s1 = mobile.substring(2, 12);
                        mobileText.setText("+91 " + s1);
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

    public void getTruckList(){
        //---------------------------- Get Truck Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/truck/" + userId;
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
                    }else{

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

    public void getDriverDetailsList(){
        //---------------------------- Get Driver Details ------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/" + userId;
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
                    }else{

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

    public void getBankDetailsList(){
        //---------------------------- Get Truck Details -------------------------------------------
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
                    }else{
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
        intent.putExtra("mobile3", mobile);
        intent.putExtra("name3", name);
        intent.putExtra("address", address);
        intent.putExtra("pinCode", pinCode);
        intent.putExtra("city", city);
        intent.putExtra("userId", userId);
        intent.putExtra("bankName", bankName);
        intent.putExtra("accNo", accNo);
        intent.putExtra("vehicleNo", vehicleNo);
        intent.putExtra("driverName", driverName);
        intent.putExtra("isPersonal", isPersonalDetailsDone);
        intent.putExtra("isBank", isBankDetailsDone);
        intent.putExtra("isTrucks", isAddTrucksDone);
        intent.putExtra("isDriver", isAddDriversDone);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    public void getDriverDetails(DriverModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
        intent.putExtra("mobile3", mobile);
        intent.putExtra("name3", name);
        intent.putExtra("address", address);
        intent.putExtra("pinCode", pinCode);
        intent.putExtra("city", city);
        intent.putExtra("userId", userId);
        intent.putExtra("bankName", bankName);
        intent.putExtra("accNo", accNo);
        intent.putExtra("vehicleNo", vehicleNo);
        intent.putExtra("driverName", driverName);
        intent.putExtra("isPersonal", isPersonalDetailsDone);
        intent.putExtra("isBank", isBankDetailsDone);
        intent.putExtra("isTrucks", isAddTrucksDone);
        intent.putExtra("isDriver", isAddDriversDone);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    public void getBankDetails(BankModel obj) {
        Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
        intent.putExtra("mobile3", mobile);
        intent.putExtra("name3", name);
        intent.putExtra("address", address);
        intent.putExtra("pinCode", pinCode);
        intent.putExtra("city", city);
        intent.putExtra("userId", userId);
        intent.putExtra("bankName", bankName);
        intent.putExtra("accNo", accNo);
        intent.putExtra("vehicleNo", vehicleNo);
        intent.putExtra("driverName", driverName);
        intent.putExtra("isPersonal", isPersonalDetailsDone);
        intent.putExtra("isBank", isBankDetailsDone);
        intent.putExtra("isTrucks", isAddTrucksDone);
        intent.putExtra("isDriver", isAddDriversDone);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    public void onClickProfileAndRegister(View view) {
        Log.i("Status Personal", String.valueOf(isPersonalDetailsDone));
        Log.i("Status Bank", String.valueOf(isBankDetailsDone));
        Log.i("Status Truck", String.valueOf(isAddTrucksDone));
        Log.i("Status Driver", String.valueOf(isAddDriversDone));
        if (isPersonalDetailsDone && isBankDetailsDone && isAddTrucksDone && isAddDriversDone) {
            Intent i8 = new Intent(ProfileAndRegistrationActivity.this, RazorPayActivity.class);
            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i8);
            overridePendingTransition(0, 0);
//        ProfileAndRegistrationActivity.this.finish();
        }
    }
}