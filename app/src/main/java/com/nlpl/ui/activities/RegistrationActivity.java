package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.nlpl.R;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.CreateUser;
import com.nlpl.utils.GetLocationPickUp;
import com.nlpl.utils.GetStateCityUsingPINCode;
import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompat {

    final String NOTIFICATION_CHANNEL_ID = "100012";
    final String default_notification_channel_id = "defaults";

    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    View action_bar;
    TextView actionBarTitle, selectStateText, selectDistrictText, actionBarSkip;
    ImageView actionBarBackButton, actionBarMenuButton;

    String selectedState, role, userId;
    String mobile, stateByPinCode, distByPinCode, deviceId;

    EditText name, pinCode, address, mobileNoEdit, email_id, alternateMobile;
    TextView series, setCurrentLocation;
    Button okButton;
    View personalAndAddress;

    int PLACE_PICKER_REQUEST = 1;
    ConstraintLayout roleConstrain;
    String latForAddress, longForAddress;
    Boolean isEdit;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile1");
            Log.i("Mobile No Registration", mobile);
            isEdit = bundle.getBoolean("isEdit");
            userId = bundle.getString("userId");
        }

        //------------------------------------------------------------------------------------------
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);
        //------------------------------------------------------------------------------------------

        checkLocationService();
        Dialog roleDialog = new Dialog(this);
        roleDialog.setContentView(R.layout.dialog_role);
        roleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(roleDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        if (!isEdit) {
            roleDialog.show();
        } else {
            getUserDetails(userId);
        }
        roleDialog.getWindow().setAttributes(lp);
        roleDialog.setCancelable(false);

        //------------------------------------------------------------------------------------------

        action_bar = (View) findViewById(R.id.registration_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) action_bar.findViewById(R.id.action_bar_menu);
        actionBarSkip = (TextView) action_bar.findViewById(R.id.action_bar_skip);

        if (!isEdit) {
            actionBarSkip.setVisibility(View.VISIBLE);
            actionBarBackButton.setVisibility(View.GONE);
        } else {
            actionBarSkip.setVisibility(View.GONE);
            actionBarTitle.setText("Edit Personal Details");
            actionBarBackButton.setVisibility(View.VISIBLE);
        }
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setOnClickListener(view -> JumpTo.goToViewPersonalDetailsActivity(RegistrationActivity.this, userId, mobile, true));

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
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

        pinCode.setVisibility(View.GONE);
        selectStateText.setVisibility(View.GONE);
        selectDistrictText.setVisibility(View.GONE);

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
        if (!isEdit) {
            mobileNoEdit.setEnabled(false);
        }
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
                if (role != null) {
                    if (role.equals("Customer")) {
                        actionBarTitle.setText(getString(R.string.Registration_as) + getString(R.string.Load_Poster));
                    } else if (role.equals("Owner")) {
                        actionBarTitle.setText(getString(R.string.Registration_as) + getString(R.string.Truck_Owner));
                    } else if (role.equals("Driver")) {
                        actionBarTitle.setText(getString(R.string.Registration_as) + getString(R.string.Driver));
                    } else {
                        actionBarTitle.setText(getString(R.string.Registration_as) + getString(R.string.Broker));
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
        checkLocationService();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (role.equals("Customer")) {
                JumpTo.goToFindTrucksActivity(RegistrationActivity.this, null, mobile);
            } else {
                JumpTo.goToServiceProviderDashboard(RegistrationActivity.this, mobile, true, false);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Dialog alert = new Dialog(this);
            alert.setContentView(R.layout.dialog_alert);
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

            alertTitle.setText(getString(R.string.Provide_Access));
            alertMessage.setText(getString(R.string.Please_provide_location_access));

            alertPositiveButton.setText(getString(R.string.Go_to_settings));
            alertPositiveButton.setVisibility(View.VISIBLE);

            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setVisibility(View.VISIBLE);

            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

            alertNegativeButton.setOnClickListener(view1 -> {
                alert.dismiss();

            });
            alertPositiveButton.setOnClickListener(View2 -> {
                alert.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            });
        }
    }

    public void checkLocationService() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                Dialog alert = new Dialog(this);
                alert.setContentView(R.layout.dialog_alert);
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

                alertTitle.setText(getString(R.string.Provide_Access));
                alertMessage.setText(getString(R.string.Please_provide_location_access));

                alertPositiveButton.setText(getString(R.string.Go_to_settings));
                alertPositiveButton.setVisibility(View.VISIBLE);

                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setVisibility(View.VISIBLE);

                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(view1 -> {
                    alert.dismiss();

                });
                alertPositiveButton.setOnClickListener(View2 -> {
                    alert.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                });
            }
            //------------------------------------------------------------------------------------------
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps_enabled && !network_enabled) {
                Dialog alert = new Dialog(this);
                alert.setContentView(R.layout.dialog_alert);
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

                alertTitle.setText(getString(R.string.Provide_Access));
                alertMessage.setText(getString(R.string.Please_provide_location_access));

                alertPositiveButton.setText(getString(R.string.Go_to_settings));
                alertPositiveButton.setVisibility(View.VISIBLE);

                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setVisibility(View.VISIBLE);

                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(view1 -> {
                    alert.dismiss();

                });
                alertPositiveButton.setOnClickListener(View2 -> {
                    alert.dismiss();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
            }
            //------------------------------------------------------------------------------------------
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickOpenMaps(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Places.initialize(getApplicationContext(), "AIzaSyDQqGwhvhMOwbcbuhkZVZ5fp9lETjWno_Q");
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, 100);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void onClickGetCurrentLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(this, address, pinCode);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void getCurrentLocation(Activity activity, EditText address, EditText pinCode) {
        Dialog loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.show();
        loadingDialog.setCancelable(false);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);


        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                        try {
                            String latitudeCurrent, longitudeCurrent, countryCurrent, stateCurrent, cityCurrent, subCityCurrent, addressCurrent, pinCodeCurrent;
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLatitude()));
                            longitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLongitude()));
                            countryCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getCountryName()));
                            stateCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getAdminArea()));
                            cityCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLocality()));
                            subCityCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getSubLocality()));
                            addressCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getAddressLine(0)));
                            pinCodeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getPostalCode()));

                            address.setText(addressCurrent);
                            pinCode.setText(pinCodeCurrent);

                            latForAddress = latitudeCurrent;
                            longForAddress = longitudeCurrent;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                    }

                }
            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void onClickRegistration(View view) {
        if (latForAddress == null || latForAddress.isEmpty() && longForAddress == null || longForAddress.isEmpty()){
            getCurrentLocation(RegistrationActivity.this, address, pinCode);
            checkLocationService();
        }else{
            if (name.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
            } else if (address.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show();
            } else if (pinCode.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter PIN Code", Toast.LENGTH_SHORT).show();
            } else if (selectStateText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Select State", Toast.LENGTH_SHORT).show();
            } else if (selectDistrictText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Select City", Toast.LENGTH_SHORT).show();
            } else {
                if (isEdit) {
                    if (name.getText().toString() != null) {
                        UpdateUserDetails.updateUserName(userId, name.getText().toString());
                    }

                    if (alternateMobile.getText().toString() != null) {
                        UpdateUserDetails.updateUserAlternatePhoneNumber(userId, "91" + alternateMobile.getText().toString());
                    }

                    if (email_id.getText().toString() != null) {
                        UpdateUserDetails.updateUserEmailId(userId, email_id.getText().toString());
                    }

                    if (address.getText().toString() != null) {
                        UpdateUserDetails.updateUserAddress(userId, address.getText().toString());
                        UpdateUserDetails.updateUserLatLong(userId, latForAddress, longForAddress);
                    }

                    if (pinCode.getText().toString() != null) {
                        UpdateUserDetails.updateUserPinCode(userId, pinCode.getText().toString());
                        UpdateUserDetails.updateUserLatLong(userId, latForAddress, longForAddress);
                    }

                    if (selectStateText.getText().toString() != null) {
                        UpdateUserDetails.updateUserState(userId, selectStateText.getText().toString());
                    }

                    if (selectDistrictText.getText().toString() != null) {
                        UpdateUserDetails.updateUserCity(userId, selectDistrictText.getText().toString());
                    }

//                if (role != null) {
//                    UpdateUserDetails.updateUserType(userId, role);
//                }

                    if (mobile.equals("91" + mobileNoEdit.getText().toString()) || mobileNoEdit.getText().toString().isEmpty()) {
                        JumpTo.goToViewPersonalDetailsActivity(RegistrationActivity.this, userId, mobile, true);
                    } else {
                        //----------------------- Alert Dialog -------------------------------------------------
                        Dialog alert = new Dialog(RegistrationActivity.this);
                        alert.setContentView(R.layout.dialog_alert);
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

                        alertTitle.setText(getString(R.string.Personal_Details));
                        alertMessage.setText(getString(R.string.update_your_phone_number));
                        alertPositiveButton.setText(getString(R.string.yes));
                        alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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

                                alertTitle.setText(getString(R.string.OTP_sent_successfully));
                                alertMessage.setText(getString(R.string.OTP_sent_to) + "+91" + mobileNoEdit.getText().toString());
                                alertPositiveButton.setVisibility(View.GONE);
                                alertNegativeButton.setText(getString(R.string.ok));
                                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alert.dismiss();
                                        JumpTo.goToOTPActivity(RegistrationActivity.this, "+91" + mobileNoEdit.getText().toString(), true, userId);
                                    }
                                });
                            }
                        });

                        alertNegativeButton.setText(getString(R.string.no));
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();

                            }
                        });
                        //------------------------------------------------------------------------------------------
                    }
                } else {
                    CreateUser.saveUser(CreateUser.createUser(name.getText().toString(), mobile, "91" + alternateMobile.getText().toString(), address.getText().toString(), role, email_id.getText().toString(), pinCode.getText().toString(), selectDistrictText.getText().toString(), selectStateText.getText().toString(), deviceId, latForAddress, longForAddress));
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
                    alertMessage.setText(getString(R.string.Welcome_to) + " FindYourTruck" + getString(R.string.Please_update_your_profile));
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            if (role.equals("Customer")) {
                                JumpTo.goToCustomerDashboard(RegistrationActivity.this, mobile, true);
                            } else {
                                JumpTo.goToServiceProviderDashboard(RegistrationActivity.this, mobile, true, true);
                            }
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        }
    }

    private TextWatcher registrationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String addressText = address.getText().toString().trim();

            if (addressText.length() == 0) {
                pinCode.setVisibility(View.GONE);
                selectStateText.setVisibility(View.GONE);
                selectDistrictText.setVisibility(View.GONE);
            } else {
                pinCode.setVisibility(View.VISIBLE);
                selectStateText.setVisibility(View.VISIBLE);
                selectDistrictText.setVisibility(View.VISIBLE);
            }

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

                GetLocationPickUp geoLocation = new GetLocationPickUp();
                String addressFull = address.getText().toString() + " " + pinCode.getText().toString();
                geoLocation.geLatLongPickUp(addressFull, getApplicationContext(), new GeoHandlerLatitude());
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
        try {
            Place place = Autocomplete.getPlaceFromIntent(data);
//            String[] addressField = place.getAddress().split(",");
//            address.setText(addressField[0]);

            List<Address> addresses;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                latForAddress = String.valueOf(addresses.get(0).getLatitude());
                longForAddress = String.valueOf(addresses.get(0).getLongitude());

                Log.i("Register Maps Latitude", latForAddress);
                Log.i("Register Maps Longitude", longForAddress);

//                Log.e("Address1: ", "" + address1);
//                Log.e("Address2: ", "" + address2);
//                Log.e("AddressCity: ", "" + city);
//                Log.e("AddressState: ", "" + state);
//                Log.e("AddressCountry: ", "" + country);
//                Log.e("AddressPostal: ", "" + postalCode);
//                Log.e("AddressLatitude: ", "" + place.getLatLng().latitude);
//                Log.e("AddressLongitude: ", "" + place.getLatLng().longitude);

                char[] chars = address1.toCharArray();
                StringBuilder sb = new StringBuilder();
                for (char c : chars) {
                    if (!Character.isDigit(c)) {
                        sb.append(c);
                    }
                }
//                System.out.println("String "+sb);

                String[] addressFieldWithoutCountry = address1.split(country);
                String[] addressFieldWithoutState = addressFieldWithoutCountry[0].split(state);
                String[] addressFiledWithoutCity = addressFieldWithoutState[0].split(city);

                try {
                    address.setText(addressFiledWithoutCity[0]);
                    pinCode.setText(postalCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    address.setText(address1);
                    pinCode.setText(postalCode);
                }

                address.setText(address1);
                pinCode.setText(postalCode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            //setMarker(latLng);
        }

    }

    public void onClickExploreNow(View view) {
        JumpTo.goToSliderActivity(RegistrationActivity.this, mobile);
    }

    private class GeoHandlerLatitude extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String latLong, lat = null, lon = null;
            switch (msg.what) {
                case 1:
                    try {
                        Bundle bundle = msg.getData();
                        latLong = bundle.getString("latLong1");
                        String[] arrSplit = latLong.split(" ");
                        for (int i = 0; i < arrSplit.length; i++) {
                            lat = arrSplit[0];
                            lon = arrSplit[1];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    lat = null;
                    lon = null;
            }
            try {
                latForAddress = lat;
                longForAddress = lon;
                Log.i("Lat and long 1", String.valueOf(latForAddress + " " + longForAddress));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void getUserDetails(String userId) {
        Dialog loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.show();
        loadingDialog.setCancelable(false);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);

        Call<UserResponse> call = ApiClient.getUserService().getUserDetailsParticular(userId);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse nameResponse = response.body();
                UserResponse.UserList listObj = nameResponse.getData().get(0);
                name.setText(listObj.getName());
                address.setText(listObj.getAddress());
                String phoneNumber = listObj.getAlternate_ph_no();
                try {
                    String s1 = phoneNumber.substring(2, 12);
                    alternateMobile.setText(s1);
                } catch (Exception e) {
                    alternateMobile.setText("");
                }
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                pinCode.setText(listObj.getPin_code());
                selectStateText.setText(listObj.getState_code());
                selectDistrictText.setText(listObj.getPreferred_location());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isEdit) {
            JumpTo.goToViewPersonalDetailsActivity(RegistrationActivity.this, userId, mobile, true);
        }
    }

}