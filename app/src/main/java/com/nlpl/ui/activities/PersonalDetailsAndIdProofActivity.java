package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.GetLocationPickUp;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalDetailsAndIdProofActivity extends AppCompat {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    View personalAndAddressView;
    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    TextView selectStateText, selectDistrictText, series;
    String isPersonalDetailsDone, stateByPinCode, distByPinCode, selectedDistrict, selectedState, role, img_type, alternateMobileNumberAPI;

    EditText name, pinCode, address, mobileEdit, emailIdEdit, alternateMobileNumber;
    Button okButton;
    //----------------------------------------------------------------------------------------------
    String latForAddress, longForAddress;

    TextView setCurrentLocation;

    String nameAPI, mobileAPI, addressAPI, pinCodeAPI, roleAPI, cityAPI, stateAPI, emailAPI;

    private int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST1 = 5;
    int CAMERA_PIC_REQUEST2 = 15;
    private int GET_FROM_GALLERY2 = 125;
    int CAMERA_PIC_REQUEST3 = 54;

    private RequestQueue mQueue;
    String userId, mobileString, panImageURL, aadharImageURL, profileImgUrl;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details_and_id_proof);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            mobileString = bundle.getString("mobile");
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        action_bar = findViewById(R.id.personal_details_id_proof_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText(getString(R.string.Personal_Details));
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(PersonalDetailsAndIdProofActivity.this);
                JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsAndIdProofActivity.this, userId, mobileAPI, false);
            }
        });
        //------------------------------------------------------------------------------------------
        personalAndAddressView = (View) findViewById(R.id.personal_details_id_proof_personal_and_address_layout);
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        name = (EditText) personalAndAddressView.findViewById(R.id.registration_edit_name);
        emailIdEdit = (EditText) personalAndAddressView.findViewById(R.id.registration_email_id_edit);
        pinCode = (EditText) personalAndAddressView.findViewById(R.id.registration_pin_code_edit);
        address = (EditText) personalAndAddressView.findViewById(R.id.registration_address_edit);
        mobileEdit = (EditText) personalAndAddressView.findViewById(R.id.registration_mobile_no_edit);
        series = (TextView) personalAndAddressView.findViewById(R.id.registration_prefix);
        selectStateText = (TextView) personalAndAddressView.findViewById(R.id.registration_select_state);
        selectDistrictText = (TextView) personalAndAddressView.findViewById(R.id.registration_select_city);
        okButton = (Button) findViewById(R.id.personal_details_id_proof_ok_button);
        setCurrentLocation = (TextView) personalAndAddressView.findViewById(R.id.personal_and_address_get_current_location);
        alternateMobileNumber = (EditText) personalAndAddressView.findViewById(R.id.registration_mobile_no_edit_alternate);

        setCurrentLocation.setVisibility(View.VISIBLE);

        name.addTextChangedListener(proofAndPersonalWatcher);
        selectStateText.addTextChangedListener(proofAndPersonalWatcher);
        selectDistrictText.addTextChangedListener(proofAndPersonalWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(proofAndPersonalWatcher);
        mobileEdit.addTextChangedListener(mobileNumberTextWatcher);
        emailIdEdit.addTextChangedListener(proofAndPersonalWatcher);

        emailIdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String email = emailIdEdit.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.matches(emailPattern) && s.length() > 0) {

                    emailIdEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getDrawable(R.drawable.button_de_active));
                    emailIdEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                }

            }
        });

//        name.requestFocus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        name.setFilters(new InputFilter[]{filter});
        address.setFilters(new InputFilter[]{filter});

        mQueue = Volley.newRequestQueue(PersonalDetailsAndIdProofActivity.this);

        getImageURL();
        getUserDetails();

//        okButton.setEnabled(true);
//        okButton.setBackground(getDrawable(R.drawable.button_active));

        ownerButton = (RadioButton) personalAndAddressView.findViewById(R.id.registration_truck_owner);
        driverButton = (RadioButton) personalAndAddressView.findViewById(R.id.registration_driver);
        brokerButton = (RadioButton) personalAndAddressView.findViewById(R.id.registration_broker);
        customerButton = (RadioButton) personalAndAddressView.findViewById(R.id.registration_customer);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setCursorVisible(true);
            }
        });

        pinCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinCode.setCursorVisible(true);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address.setCursorVisible(true);
            }
        });

        selectStateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);

                SelectState.selectState(PersonalDetailsAndIdProofActivity.this, selectStateText, selectDistrictText);
            }
        });

        selectDistrictText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);

                if (!selectStateText.getText().toString().isEmpty()) {
                    selectedState = selectStateText.getText().toString();
                    SelectCity.selectCity(PersonalDetailsAndIdProofActivity.this, selectedState, selectDistrictText);
                }

            }
        });
        //------------------------------------------------------------------------------------------
    }

    //----------------------------------------------------------------------------------------------
    public void onRadioClick(View view) {

        name.setCursorVisible(false);
        pinCode.setCursorVisible(false);
        address.setCursorVisible(false);

        switch (view.getId()) {
            case R.id.registration_truck_owner:
                ownerButton.setChecked(true);
                driverButton.setChecked(false);
                brokerButton.setChecked(false);
                customerButton.setChecked(false);
                role = "Owner";

                break;

            case R.id.registration_driver:
                ownerButton.setChecked(false);
                driverButton.setChecked(true);
                brokerButton.setChecked(false);
                customerButton.setChecked(false);
                role = "Driver";


                break;

            case R.id.registration_broker:
                ownerButton.setChecked(false);
                driverButton.setChecked(false);
                brokerButton.setChecked(true);
                customerButton.setChecked(false);
                role = "Broker";

                break;

            case R.id.registration_customer:
                ownerButton.setChecked(false);
                driverButton.setChecked(false);
                brokerButton.setChecked(false);
                customerButton.setChecked(true);
                role = "Customer";
                break;
        }
    }

    public void onClickPersonalProof(View view) {
        if (name.getText().toString() != null) {
            UpdateUserDetails.updateUserName(userId, name.getText().toString());
        }

        if (alternateMobileNumber.getText().toString() != null) {
            UpdateUserDetails.updateUserAlternatePhoneNumber(userId, "91"+alternateMobileNumber.getText().toString());
        }

        if (emailIdEdit.getText().toString() != null) {
            UpdateUserDetails.updateUserEmailId(userId, emailIdEdit.getText().toString());
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

        if (role != null) {
            UpdateUserDetails.updateUserType(userId, role);
        }

        if (mobileString.equals("91" + mobileEdit.getText().toString()) || mobileEdit.getText().toString().isEmpty()) {
            ShowAlert.loadingDialog(PersonalDetailsAndIdProofActivity.this);
            JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsAndIdProofActivity.this, userId, mobileAPI, true);
        } else {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(PersonalDetailsAndIdProofActivity.this);
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
                    Dialog alert = new Dialog(PersonalDetailsAndIdProofActivity.this);
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
                    alertMessage.setText(getString(R.string.OTP_sent_to) + "+91" + mobileEdit.getText().toString());
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            JumpTo.goToOTPActivity(PersonalDetailsAndIdProofActivity.this, "+91" + mobileEdit.getText().toString(), true, userId);
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
    }

    private TextWatcher proofAndPersonalWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nameWatcher = name.getText().toString().trim();
            String stateWatcher = selectStateText.getText().toString().trim();
            String cityWatcher = selectDistrictText.getText().toString().trim();
            String pinCodeWatcher = pinCode.getText().toString().trim();
            String addressWatcher = address.getText().toString().trim();
            String mobileWatcher = mobileEdit.getText().toString().trim();
            String emailIdWatcher = emailIdEdit.getText().toString().trim();
            boolean owner = ownerButton.isChecked();
            boolean driver = driverButton.isChecked();
            boolean broker = brokerButton.isChecked();
            boolean customer = customerButton.isChecked();

            if (!nameWatcher.isEmpty() && !stateWatcher.isEmpty() && !cityWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && !mobileWatcher.isEmpty() && !emailIdWatcher.isEmpty()) {
                okButton.setEnabled(true);
                okButton.setBackground(getDrawable(R.drawable.button_active));
            } else {
                okButton.setEnabled(false);
                okButton.setBackground(getDrawable(R.drawable.button_de_active));
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

    private String blockCharacterSet = "~#^|$%&*!+@₹_-()':;?/={}";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
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
                okButton.setEnabled(false);
                okButton.setBackground(getDrawable(R.drawable.button_de_active));
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                selectStateText.setEnabled(true);
                selectDistrictText.setEnabled(true);
            } else {
                getStateAndDistrict(pinCode.getText().toString());
                okButton.setEnabled(true);
                okButton.setBackground(getDrawable(R.drawable.button_active));
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                selectStateText.setEnabled(false);
                selectDistrictText.setEnabled(false);

                GetLocationPickUp geoLocation = new GetLocationPickUp();
                String addressFull = address.getText().toString()+" "+pinCode.getText().toString();
                geoLocation.geLatLongPickUp(addressFull, getApplicationContext(), new GeoHandlerLatitude());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher mobileNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String mobileNoWatcher = mobileEdit.getText().toString().trim();

            if (mobileNoWatcher.length() == 10) {
                okButton.setEnabled(true);
                okButton.setBackground(getDrawable(R.drawable.button_active));
                mobileEdit.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            } else {
                okButton.setEnabled(false);
                okButton.setBackground(getDrawable(R.drawable.button_de_active));
                mobileEdit.setBackground(getResources().getDrawable(R.drawable.mobile_number_right_red));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left_red));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //-------------------------------upload Image---------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    address.setText(address1);
                    pinCode.setText(postalCode);
                }

                address.setText(address1);
                pinCode.setText(postalCode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //setMarker(latLng);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------

    private void getUserDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        nameAPI = obj.getString("name");
                        mobileAPI = obj.getString("phone_number");
                        addressAPI = obj.getString("address");
                        stateAPI = obj.getString("state_code");
                        cityAPI = obj.getString("preferred_location");
                        pinCodeAPI = obj.getString("pin_code");
                        roleAPI = obj.getString("user_type");
                        emailAPI = obj.getString("email_id");
                        isPersonalDetailsDone = obj.getString("isPersonal_dt_added");
                        alternateMobileNumberAPI = obj.getString("alternate_ph_no");

                        Log.i("EmailId", emailAPI);

                        role = roleAPI;
                        name.setText(nameAPI);

                        String s1 = mobileAPI.substring(2, 12);
                        mobileEdit.setText(s1);

                        if (emailAPI != null) {
                            emailIdEdit.setText(emailAPI);
                        }

                        if (alternateMobileNumberAPI != null || !alternateMobileNumberAPI.equals("null")) {
                            try {
                                String s2 = alternateMobileNumberAPI.substring(2, 12);
                                alternateMobileNumber.setText(s2);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        address.setText(addressAPI);
                        pinCode.setText(pinCodeAPI);
                        selectStateText.setText(stateAPI);
                        selectDistrictText.setText(cityAPI);

                        if (roleAPI.equals("Customer")) {

                            customerButton.setChecked(true);
                            ownerButton.setChecked(false);
                            driverButton.setChecked(false);
                            brokerButton.setChecked(false);

                        } else if (roleAPI.equals("Owner")) {
                            customerButton.setChecked(false);
                            ownerButton.setChecked(true);
                            driverButton.setChecked(false);
                            brokerButton.setChecked(false);

                        } else if (roleAPI.equals("Driver")) {
                            customerButton.setChecked(false);
                            ownerButton.setChecked(false);
                            driverButton.setChecked(true);
                            brokerButton.setChecked(false);

                        } else if (roleAPI.equals("Broker")) {
                            customerButton.setChecked(false);
                            ownerButton.setChecked(false);
                            driverButton.setChecked(false);
                            brokerButton.setChecked(true);
                        } else {

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

    private void getImageURL() {

        String url = getString(R.string.baseURL) + "/imgbucket/Images/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");

                        if (imageType.equals("aadhar")) {
                            aadharImageURL = obj.getString("image_url");
//                            new DownloadImageTask(imgF).execute(aadharImageURL);
                        }

                        if (imageType.equals("pan")) {
                            panImageURL = obj.getString("image_url");
//                            new DownloadImageTask(imgPAN).execute(panImageURL);

                        }

                        if (imageType.equals("profile")) {
                            profileImgUrl = obj.getString("image_url");
//                            new DownloadImageTask(imgProfile).execute(profileImgUrl);

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

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        Log.i("file uri: ", String.valueOf(fileUri));
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("mp3"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    //--------------------------------------create image in API -------------------------------------
    public ImageRequest imageRequest() {
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setUser_id(userId);
        imageRequest.setImage_type(img_type);
        return imageRequest;
    }

    public void saveImage(ImageRequest imageRequest) {
        Call<ImageResponse> imageResponseCall = ApiClient.getImageService().saveImage(imageRequest);
        imageResponseCall.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    private void uploadImage(String picPath) {

        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

        MultipartBody.Part body = prepareFilePart("file", Uri.fromFile(file));

        Call<UploadImageResponse> call = ApiClient.getImageUploadService().uploadImage(userId, img_type, body);
        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                Log.i("successful:", "success");
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("failed:", "failed");
            }
        });
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(PersonalDetailsAndIdProofActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalDetailsAndIdProofActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(PersonalDetailsAndIdProofActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalDetailsAndIdProofActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(PersonalDetailsAndIdProofActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalDetailsAndIdProofActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void getStateAndDistrict(String enteredPin) {

        Log.i("Entered PIN", enteredPin);

        String url = "http://13.234.163.179:3000/user/locationData/"+enteredPin;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject obj = response.getJSONObject("data");
                    String stateByPinCode = obj.getString("stateCode");
                    String  distByPinCode = obj.getString("district");

                    selectStateText.setText(stateByPinCode);
                    selectDistrictText.setText(distByPinCode);

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

    public void onClickGetCurrentLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Dialog chooseDialog = new Dialog(this);
            chooseDialog.setContentView(R.layout.dialog_choose);
            chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
            lp2.copyFrom(chooseDialog.getWindow().getAttributes());
            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp2.gravity = Gravity.BOTTOM;

            chooseDialog.show();
            chooseDialog.getWindow().setAttributes(lp2);

            ImageView currentLocation = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
            currentLocation.setImageResource(R.drawable.google_location_small);
            ImageView searchFromMaps = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);
            searchFromMaps.setImageResource(R.drawable.google_address_small);

            TextView currentText = chooseDialog.findViewById(R.id.dialog_camera_text);
            currentText.setText(getString(R.string.Current_Location));
            TextView fromMapText = chooseDialog.findViewById(R.id.dialog_photo_library_text);
            fromMapText.setText(getString(R.string.Search));

            currentLocation.setOnClickListener(view2 -> {
                chooseDialog.dismiss();
                getCurrentLocation(this, address, pinCode);
            });

            searchFromMaps.setOnClickListener(view3 -> {
                chooseDialog.dismiss();
                Places.initialize(getApplicationContext(), "AIzaSyDAAes8x5HVKYB5YEIGBmdnCdyBrAHUijM");
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
                startActivityForResult(intent, 100);
            });

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void getCurrentLocation(Activity activity, EditText address, EditText pinCode) {
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
                    }

                }
            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(PersonalDetailsAndIdProofActivity.this);
        JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsAndIdProofActivity.this, userId, mobileAPI, false);
    }

    private void uploadProfileDialogChoose(){
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "profile";

        Dialog chooseDialog;
        chooseDialog = new Dialog(PersonalDetailsAndIdProofActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST3);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
                chooseDialog.dismiss();
            }
        });
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
}