package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.nlpl.R;

public class ProfileAndRegistrationActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language, addCompany, phoneDone, nameDone, firmDone, firmName, addressDone;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    Button personalDetails, bankDetails, addTrucks, addDrivers;
    String mobile, name, address, pinCode, city;
    TextView nameTitle, mobileText;
    ConstraintLayout personal_done;


    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, getIsPersonalDetailsDoneVisible=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile2");
            name = bundle.getString("name2");
            address = bundle.getString("address");
            pinCode = bundle.getString("pinCode");
            city = bundle.getString("city");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            Log.i("Mobile No", mobile);
            Log.i("Name", name);
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

        nameTitle = (TextView) findViewById(R.id.profile_registration_name_text);
        mobileText = (TextView) findViewById(R.id.profile_registration_mobile_text);

        String hello = getString(R.string.hello);
        nameTitle.setText(hello+" "+name+"!");
        String s = mobile.substring(3,13);
        mobileText.setText("+91 "+s);

        if (isPersonalDetailsDone){
            getIsPersonalDetailsDoneVisible = true;
            personalDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.personal_success, 0, 0, 0);
            personal_done.setVisibility(View.VISIBLE);
            addCompany.setVisibility(View.VISIBLE);
            phoneDone.setText("+91 "+s);
            nameDone.setText(name);
            addressDone.setText(address+", "+city+" "+pinCode);

        }
        if (isBankDetailsDone){
            bankDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bank_success, 0, 0, 0);
        }
        if (isAddTrucksDone){
            addTrucks.setCompoundDrawablesWithIntrinsicBounds(R.drawable.truck_success, 0, 0, 0);
        }
        if (isAddDriversDone){
            addDrivers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_success, 0, 0, 0);
        }
        if (isPersonalDetailsDone && isBankDetailsDone && isAddTrucksDone && isAddDriversDone){
        }

        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, CompanyDetailsActivity.class);
                startActivity(intent);
            }
        });

        personalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPersonalDetailsDone){
                    if (getIsPersonalDetailsDoneVisible){
                        personal_done.setVisibility(View.GONE);
                        addCompany.setVisibility(View.GONE);
                        phoneDone.setText("+91 "+s);
                        nameDone.setText(name);
                        addressDone.setText(address+", "+city+" "+pinCode);
                        getIsPersonalDetailsDoneVisible= false;
                    }else {
                        personal_done.setVisibility(View.VISIBLE);
                        addCompany.setVisibility(View.VISIBLE);
                        phoneDone.setText("+91 " + s);
                        nameDone.setText(name);
                        addressDone.setText(address+", "+city+" "+pinCode);
                        getIsPersonalDetailsDoneVisible = true;
                    }

                }else {
                    Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("mobile3", mobile);
                    intent.putExtra("name3", name);
                    intent.putExtra("address", address);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("city", city);
                    intent.putExtra("isPersonal", isPersonalDetailsDone);
                    intent.putExtra("isBank", isBankDetailsDone);
                    intent.putExtra("isTrucks", isAddTrucksDone);
                    intent.putExtra("isDriver", isAddDriversDone);
                    startActivity(intent);
//                finish();
                }
            }
        });

        bankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver",isAddDriversDone);
                startActivity(intent);
//                finish();
            }
        });

        addTrucks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, VehicleDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver",isAddDriversDone);
                startActivity(intent);
//                finish();
            }
        });

        addDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, DriverDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("city", city);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver",isAddDriversDone);
                startActivity(intent);
//                finish();
            }
        });
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