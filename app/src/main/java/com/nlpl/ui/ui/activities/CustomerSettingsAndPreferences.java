package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

public class CustomerSettingsAndPreferences extends AppCompatActivity {

    String phone, userId;
    View actionBar;
    TextView actionBarTitle, addressDialogState, addressDialogCity, addressDialogOkButton, addressDialogTitle, addressDialogGetCurrentLocation;
    ImageView actionBarBackButton, actionBarMenuButton;
    Dialog addressDialog;
    EditText addressDialogAddress, addressDialogPinCode;

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

        actionBarTitle.setText("Settings and Preferences");
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.INVISIBLE);

        actionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerSettingsAndPreferences.this.finish();
            }
        });
        //------------------------------------------------------------------------------------------

        addressDialog = new Dialog(CustomerSettingsAndPreferences.this);
        addressDialog.setContentView(R.layout.dialog_address);
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addressDialogState = (TextView) addressDialog.findViewById(R.id.dialog_address_state_text_view);
        addressDialogCity = (TextView) addressDialog.findViewById(R.id.dialog_address_city_text_view);
        addressDialogAddress = (EditText) addressDialog.findViewById(R.id.dialog_address_address_edit_text);
        addressDialogPinCode = (EditText) addressDialog.findViewById(R.id.dialog_address_pin_code_edit_text);
        addressDialogOkButton = (TextView) addressDialog.findViewById(R.id.dialog_address_ok_button);
        addressDialogTitle = (TextView) addressDialog.findViewById(R.id.dialog_address_title);
        addressDialogGetCurrentLocation = (TextView) addressDialog.findViewById(R.id.dialog_address_current_location);

        addressDialogGetCurrentLocation.setVisibility(View.GONE);

    }

    public void getPreferredLocation(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addressDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        addressDialog.show();
        addressDialog.getWindow().setAttributes(lp);
    }
}