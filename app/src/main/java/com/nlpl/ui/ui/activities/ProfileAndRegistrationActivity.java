package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

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
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    Button personalDetails, bankDetails, addTrucks, addDrivers, okBtn;
    String mobile, name;
    TextView nameTitle, mobileText;

    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile2");
            name = bundle.getString("name2");
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

            }
        });

        actionBarTitle.setText("Registration");
        actionBarBackButton.setVisibility(View.GONE);

        personalDetails = findViewById(R.id.persnolDetails);
        bankDetails = findViewById(R.id.bankDetails);
        addTrucks = findViewById(R.id.addTrurks);
        addDrivers = findViewById(R.id.adddrivers);
        okBtn = findViewById(R.id.ok);

        nameTitle = (TextView) findViewById(R.id.backText);
        mobileText = (TextView) findViewById(R.id.profile_registration_mobile_text);

        String hello = getString(R.string.hello);
        nameTitle.setText(hello+" "+name);
        mobileText.setText(mobile);

        if (isPersonalDetailsDone){
            personalDetails.setBackground(getResources().getDrawable(R.drawable.button_active));
        }
        if (isBankDetailsDone){
            bankDetails.setBackground(getResources().getDrawable(R.drawable.button_active));
        }
        if (isAddTrucksDone){
            addTrucks.setBackground(getResources().getDrawable(R.drawable.button_active));
        }
        if (isAddDriversDone){
            addDrivers.setBackground(getResources().getDrawable(R.drawable.button_active));
        }
        if (isPersonalDetailsDone && isBankDetailsDone && isAddTrucksDone && isAddDriversDone){
            okBtn.setBackground(getResources().getDrawable(R.drawable.button_active));
        }

        personalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileAndRegistrationActivity.this, PersonalDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
                intent.putExtra("isPersonal", isPersonalDetailsDone);
                intent.putExtra("isBank", isBankDetailsDone);
                intent.putExtra("isTrucks", isAddTrucksDone);
                intent.putExtra("isDriver",isAddDriversDone);
                startActivity(intent);
//                finish();
            }
        });

        bankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileAndRegistrationActivity.this, BankDetailsActivity.class);
                intent.putExtra("mobile3", mobile);
                intent.putExtra("name3", name);
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