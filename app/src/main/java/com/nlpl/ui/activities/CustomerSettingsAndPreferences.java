package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

public class CustomerSettingsAndPreferences extends AppCompat {

    String phone, userId;
    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setings_and_preferences);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
        }

        //-------------------------------------action Bar-------------------------------------------
        actionBar = findViewById(R.id.customer_setting_and_preferences_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.Settings_and_Preferences));
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.INVISIBLE);

        actionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToCustomerDashboard(CustomerSettingsAndPreferences.this, phone, true);
            }
        });
        //------------------------------------------------------------------------------------------
    }


    @Override
    public void onBackPressed() {
        ShowAlert.loadingDialog(CustomerSettingsAndPreferences.this);
        JumpTo.goToCustomerDashboard(CustomerSettingsAndPreferences.this, phone, true);
    }
}