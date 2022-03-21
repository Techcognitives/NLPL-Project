package com.nlpl.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.nlpl.R;
import com.nlpl.utils.CreateUser;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.GetStateCityUsingPINCode;
import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.LanguageManager;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

public class RegistrationActivity extends AppCompatActivity {

    final String NOTIFICATION_CHANNEL_ID = "100012";
    final String default_notification_channel_id = "defaults";

    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    View action_bar;
    TextView actionBarTitle, selectStateText, selectDistrictText, actionBarSkip;
    ImageView actionBarBackButton, actionBarMenuButton;

    String selectedState, role;
    String mobile, stateByPinCode, distByPinCode;

    EditText name, pinCode, address, mobileNoEdit, email_id, alternateMobile;
    TextView series, setCurrentLocation;
    Button okButton;
    View personalAndAddress;
    private RequestQueue mQueue;
    int PLACE_PICKER_REQUEST = 1;
    GetCurrentLocation getCurrentLocation;
    ConstraintLayout roleConstrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile1");
            Log.i("Mobile No Registration", mobile);
        }

        Dialog roleDialog = new Dialog(this);
        roleDialog.setContentView(R.layout.dialog_role);
        roleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(roleDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        roleDialog.show();
        roleDialog.getWindow().setAttributes(lp);
        roleDialog.setCancelable(false);

        //------------------------------------------------------------------------------------------

        getCurrentLocation = new GetCurrentLocation();

        mQueue = Volley.newRequestQueue(RegistrationActivity.this);

        action_bar = (View) findViewById(R.id.registration_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) action_bar.findViewById(R.id.action_bar_menu);
        actionBarSkip = (TextView) action_bar.findViewById(R.id.action_bar_skip);

        actionBarSkip.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setVisibility(View.GONE);

        //------------------------------------------------------------------------------------------
        personalAndAddress = (View) findViewById(R.id.registration_personal_and_address);
        name = (EditText) personalAndAddress.findViewById(R.id.registration_edit_name);
        pinCode = (EditText) personalAndAddress.findViewById(R.id.registration_pin_code_edit);
        address = (EditText) personalAndAddress.findViewById(R.id.registration_address_edit);
        mobileNoEdit = (EditText) personalAndAddress.findViewById(R.id.registration_mobile_no_edit);
        alternateMobile = (EditText) personalAndAddress.findViewById(R.id.registration_mobile_no_edit_alternate);
        series = (TextView) personalAndAddress.findViewById(R.id.registration_prefix);
        selectStateText = (TextView) personalAndAddress.findViewById(R.id.registration_select_state);
        selectDistrictText = (TextView) personalAndAddress.findViewById(R.id.registration_select_city);
        okButton = (Button) findViewById(R.id.registration_ok);
        email_id = personalAndAddress.findViewById(R.id.registration_email_id_edit);
        setCurrentLocation = (TextView) personalAndAddress.findViewById(R.id.personal_and_address_get_current_location);
        setCurrentLocation.setVisibility(View.VISIBLE);
        roleConstrain = personalAndAddress.findViewById(R.id.personal_registration_sp_constrain);
        roleConstrain.setVisibility(View.GONE);

        name.addTextChangedListener(registrationWatcher);
        selectStateText.addTextChangedListener(registrationWatcher);
        selectDistrictText.addTextChangedListener(registrationWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(registrationWatcher);

        email_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String email = email_id.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.matches(emailPattern) && s.length() > 0) {
                    email_id.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                } else {
                    email_id.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                }

            }
        });

        mobileNoEdit.setVisibility(View.VISIBLE);
        String s1 = mobile.substring(2, 12);
        mobileNoEdit.setText(s1);
        mobileNoEdit.setEnabled(false);
        series.setVisibility(View.VISIBLE);

        name.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        name.setFilters(new InputFilter[]{filter});
        address.setFilters(new InputFilter[]{filter});

        ownerButton = (RadioButton) roleDialog.findViewById(R.id.role_dialog_truck_owner);
        driverButton = (RadioButton) roleDialog.findViewById(R.id.role_dialog_driver);
        brokerButton = (RadioButton) roleDialog.findViewById(R.id.role_dialog_broker);
        customerButton = (RadioButton) roleDialog.findViewById(R.id.role_dialog_customer);
        TextView okRole = (TextView) roleDialog.findViewById(R.id.role_dialog_ok_button);
        okRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roleDialog.dismiss();
                Log.i("Role Selected", role);
                if (role!=null) {
                    if (role.equals("Customer")) {
                        actionBarTitle.setText(getString(R.string.Registration_as) + getString(R.string.Load_Poster));
                    } else {
                        actionBarTitle.setText(getString(R.string.Registration_as) + role);
                    }
                }
            }
        });

        name.setOnClickListener(view -> name.setCursorVisible(true));

        pinCode.setOnClickListener(view -> pinCode.setCursorVisible(true));

        address.setOnClickListener(view -> address.setCursorVisible(true));

        selectStateText.setOnClickListener(view -> SelectState.selectState(RegistrationActivity.this, selectStateText, selectDistrictText));

        selectDistrictText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);
                if (!selectStateText.getText().toString().isEmpty()) {
                    selectedState = selectStateText.getText().toString();

                    SelectCity.selectCity(RegistrationActivity.this, selectedState, selectDistrictText);
                }
            }
        });
    }

    public void onRadioClick(View view) {

        name.setCursorVisible(false);
        pinCode.setCursorVisible(false);
        address.setCursorVisible(false);

        switch (view.getId()) {
            case R.id.role_dialog_truck_owner:
                ownerButton.setChecked(true);
                driverButton.setChecked(false);
                brokerButton.setChecked(false);
                customerButton.setChecked(false);
                role = "Owner";

                break;

            case R.id.role_dialog_driver:
                ownerButton.setChecked(false);
                driverButton.setChecked(true);
                brokerButton.setChecked(false);
                customerButton.setChecked(false);
                role = "Driver";

                break;

            case R.id.role_dialog_broker:
                ownerButton.setChecked(false);
                driverButton.setChecked(false);
                brokerButton.setChecked(true);
                customerButton.setChecked(false);
                role = "Broker";

                break;

            case R.id.role_dialog_customer:
                ownerButton.setChecked(false);
                driverButton.setChecked(false);
                brokerButton.setChecked(false);
                customerButton.setChecked(true);
                role = "Customer";
                break;
        }
    }

    public void onClickSkip(View view) {
        ShowAlert.loadingDialog(RegistrationActivity.this);
        JumpTo.goToServiceProviderDashboard(RegistrationActivity.this, mobile, true);
    }

    public void onClickGetCurrentLocation(View view) {
//        GetCurrentLocation.getCurrentLocation(RegistrationActivity.this, address, pinCode, selectStateText, selectDistrictText);
        getCurrentLocation.getCurrentLocationMaps(RegistrationActivity.this, address, pinCode);
    }

    public void onClickRegistration(View view) {
        if (name.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        }else if (address.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show();
        }else if (pinCode.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Enter PIN Code", Toast.LENGTH_SHORT).show();
        }else if (selectStateText.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Select State", Toast.LENGTH_SHORT).show();
        }else if (selectDistrictText.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Select City", Toast.LENGTH_SHORT).show();
        }else{
            CreateUser.saveUser(CreateUser.createUser(name.getText().toString(), mobile, "91" + alternateMobile.getText().toString(), address.getText().toString(), role, email_id.getText().toString(), pinCode.getText().toString(), selectDistrictText.getText().toString(), selectStateText.getText().toString()));
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(RegistrationActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(false);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Registration_Successful));
            alertMessage.setText(getString(R.string.Welcome_to) + getString(R.string.app_name) + getString(R.string.Please_update_your_profile));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    ShowAlert.loadingDialog(RegistrationActivity.this);
                    if (role.equals("Customer")) {
                        JumpTo.goToCustomerDashboard(RegistrationActivity.this, mobile, true);
                    } else {
                        JumpTo.goToServiceProviderDashboard(RegistrationActivity.this, mobile, true);
                    }
                }
            });
            //------------------------------------------------------------------------------------------
        }
    }

    private TextWatcher registrationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            for (int i = s.length() - 1; i >= 0; i--) {
                if (s.charAt(i) == '\n') {
                    s.delete(i, i + 1);
                    return;
                }
            }
        }
    };

    private TextWatcher pinCodeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String pinCodeWatcher = pinCode.getText().toString().trim();

            if (pinCodeWatcher.length() != 6) {
                selectStateText.setText("");
                selectDistrictText.setText("");
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                selectStateText.setEnabled(true);
                selectDistrictText.setEnabled(true);
            } else {
                String enteredPinCode = pinCode.getText().toString().trim();
                GetStateCityUsingPINCode.getStateAndDistrictForPickUp(RegistrationActivity.this, enteredPinCode, selectStateText, selectDistrictText);
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                selectStateText.setEnabled(false);
                selectDistrictText.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private String blockCharacterSet = ".,[]`~#^|$%&*!+@₹_-()':;?/={}";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        InAppNotification.SendNotificationJumpToRegistrationActivity(RegistrationActivity.this, "Hello!!", "Please Complete your Registration", mobile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCurrentLocation.setAddressAndPin(RegistrationActivity.this, data, address, pinCode);
    }

    public void onClickExploreNow(View view) {
        ShowAlert.loadingDialog(RegistrationActivity.this);
        JumpTo.goToSliderActivity(RegistrationActivity.this, mobile);
    }
}