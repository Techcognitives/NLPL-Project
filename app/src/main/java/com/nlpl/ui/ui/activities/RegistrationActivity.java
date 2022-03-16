package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nlpl.R;
import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.CreateUser;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.GetStateCityUsingPINCode;
import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    final String NOTIFICATION_CHANNEL_ID = "100012";
    final String default_notification_channel_id = "defaults";

    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    View action_bar;
    TextView actionBarTitle, selectStateText, selectDistrictText, english, marathi, hindi, actionBarSkip;
    ImageView actionBarBackButton, actionBarMenuButton;

    String selectedState, role;
    String mobile, stateByPinCode, distByPinCode;

    Dialog language;

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
        roleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(roleDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        //------------------------------------------------------------------------------------------
        language = new Dialog(RegistrationActivity.this);
        language.setContentView(R.layout.dialog_language);
        language.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_grey)));

        getCurrentLocation = new GetCurrentLocation();

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(language.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        language.show();
        language.setCancelable(true);
        language.getWindow().setAttributes(lp2);

        english = language.findViewById(R.id.english);
        marathi = language.findViewById(R.id.marathi);
        hindi = language.findViewById(R.id.hindi);

        mQueue = Volley.newRequestQueue(RegistrationActivity.this);

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language.dismiss();
                roleDialog.show();
                roleDialog.getWindow().setAttributes(lp);
                roleDialog.setCancelable(false);
            }
        });

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language.dismiss();
                roleDialog.show();
                roleDialog.getWindow().setAttributes(lp);
                roleDialog.setCancelable(false);
            }
        });

        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language.dismiss();
                roleDialog.show();
                roleDialog.getWindow().setAttributes(lp);
                roleDialog.setCancelable(false);
            }
        });

        action_bar = (View) findViewById(R.id.registration_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) action_bar.findViewById(R.id.action_bar_menu);
        actionBarSkip = (TextView) action_bar.findViewById(R.id.action_bar_skip);

        actionBarSkip.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);
        if (role.equals("Customer")){
            actionBarTitle.setText("Registration as " + "Load Poster");
        }else{
            actionBarTitle.setText("Registration as " + role);
        }
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

            alertTitle.setText("Registration Successful");
            alertMessage.setText("Welcome to " + getString(R.string.app_name) + "\n\nPlease update your profile and explore platform benefits.");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
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