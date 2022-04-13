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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

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
import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.Responses.DLVerificationResponse;
import com.nlpl.model.Responses.PANVerificationResponse;
import com.nlpl.model.Responses.UploadDriverSelfieResponse;
import com.nlpl.model.Responses.UploadDriverLicenseResponse;
import com.nlpl.model.UpdateMethods.UpdateDriverDetails;
import com.nlpl.model.UpdateMethods.UpdateTruckDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.CreateUser;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.GetLocationPickUp;
import com.nlpl.utils.GetStateCityUsingPINCode;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverDetailsActivity extends AppCompat {

    View action_bar;
    TextView actionBarTitle, actionBarSkipButton;
    ImageView actionBarMenuButton;

    EditText driverName, driverMobile, driverAlternateMobile, driverEmailId;
    ImageView actionBarBackButton;
    Intent data;
    int requestCode;
    int resultCode;
    String isDriverDetailsDoneAPI;
    CheckBox selfCheckBox;

    Button uploadDL, uploadSelfie;
    TextView textDL, editDL, series, textDS, editDS;
    int GET_FROM_GALLERY = 0;
    int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST = 1;
    int CAMERA_PIC_REQUEST1 = 3;
    ImageView driverLicenseImage, driverSelfieImg;

    private RequestQueue mQueue;

    String driverUserId, driverUserIdGet, latForAddress, longForAddress;

    String pathForDL, pathForSelfie, userId, driverId, driverNameAPI, driverNumberAPI, driverEmailAPI, mobile;
    Boolean fromBidNow = false, isDLUploaded = false, isEdit, isSelfieUploded = false, idDLEdited = false;
    ImageView previewDrivingLicense, previewSelfie, previewDLImageView, previewSelfieImageView;
    Dialog previewDialogDL, previewDialogSelfie;
    View personalAndAddress;

    TextView selectStateText, selectDistrictText, setCurrentLocation, mapLocation;
    String selectedState;
    EditText pinCode, address, dlNumber, dDOB;

    String userIdAPI, nameAPI, stateAPI, pinCodeAPI, addressAPI, mobileNoAPI, cityAPI, roleAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayState;
    Boolean alreadyDriver = true, isSelfieEdited = false, dlValid = true;
    String truckIdPass, driverIdPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            driverId = bundle.getString("driverId");
            fromBidNow = bundle.getBoolean("fromBidNow");
            mobile = bundle.getString("mobile");
            truckIdPass = bundle.getString("truckIdPass");
        }
        mQueue = Volley.newRequestQueue(DriverDetailsActivity.this);

        personalAndAddress = (View) findViewById(R.id.driver_details_personal_and_address);
        action_bar = (View) findViewById(R.id.driver_details_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarSkipButton = (TextView) action_bar.findViewById(R.id.action_bar_skip);
        actionBarMenuButton = (ImageView) action_bar.findViewById(R.id.action_bar_menu);
        actionBarMenuButton.setVisibility(View.GONE);

        driverMobile = (EditText) personalAndAddress.findViewById(R.id.registration_mobile_no_edit);
        driverAlternateMobile = (EditText) personalAndAddress.findViewById(R.id.registration_mobile_no_edit_alternate);
        driverName = personalAndAddress.findViewById(R.id.registration_edit_name);
        series = (TextView) personalAndAddress.findViewById(R.id.registration_prefix);
        driverEmailId = (EditText) personalAndAddress.findViewById(R.id.registration_email_id_edit);
        driverEmailId.setVisibility(View.GONE);
        setCurrentLocation = (TextView) personalAndAddress.findViewById(R.id.personal_and_address_get_current_location);
        setCurrentLocation.setVisibility(View.VISIBLE);
        mapLocation = personalAndAddress.findViewById(R.id.personal_map_address);

        previewDLImageView = (ImageView) findViewById(R.id.driver_details_preview_driving_license);
        previewSelfieImageView = (ImageView) findViewById(R.id.driver_details_preview_selfie);
        ConstraintLayout personalConstrain = (ConstraintLayout) personalAndAddress.findViewById(R.id.personal_registration_sp_constrain);
        personalConstrain.setVisibility(View.GONE);

        pinCode = (EditText) personalAndAddress.findViewById(R.id.registration_pin_code_edit);
        address = (EditText) personalAndAddress.findViewById(R.id.registration_address_edit);
        selectStateText = (TextView) personalAndAddress.findViewById(R.id.registration_select_state);
        selectDistrictText = (TextView) personalAndAddress.findViewById(R.id.registration_select_city);

//        dlNumber = findViewById(R.id.driver_details_driver_license_number);
//        dDOB = findViewById(R.id.driver_details_dob);
//        dlNumber.addTextChangedListener(dlverificationWatcher);
//        dDOB.addTextChangedListener(dlverificationWatcher);

        driverName.setHint(getString(R.string.EnterDriverName));
        driverMobile.setHint(getString(R.string.Enter_10_digit_Driver_Number));
        driverEmailId.setHint(getString(R.string.Enter_Driver_Email_Id));

        driverName.addTextChangedListener(driverNameWatcher);
        driverMobile.addTextChangedListener(driverMobileWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(driverNameWatcher);

        pinCode.setVisibility(View.GONE);
        selectStateText.setVisibility(View.GONE);
        selectDistrictText.setVisibility(View.GONE);

        address.setFilters(new InputFilter[]{filter});

        driverEmailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String email = driverEmailId.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.matches(emailPattern)) {
                    driverEmailId.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                } else {
                    driverEmailId.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                }

            }
        });

        driverName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        driverName.setFilters(new InputFilter[]{filter});

        actionBarTitle.setText(getString(R.string.Driver_Details));
        actionBarBackButton.setVisibility(View.GONE);
        actionBarSkipButton.setVisibility(View.VISIBLE);

        if (isEdit) {
            actionBarTitle.setText(getString(R.string.Edit_Driver_Details));
            actionBarBackButton.setVisibility(View.VISIBLE);
            actionBarSkipButton.setVisibility(View.INVISIBLE);
            actionBarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowAlert.loadingDialog(DriverDetailsActivity.this);
                    JumpTo.goToViewDriverDetailsActivity(DriverDetailsActivity.this, userId, mobile, true);
                }
            });

        }

        if (truckIdPass == null) {
            actionBarTitle.setText(getString(R.string.Driver_Details));
            actionBarBackButton.setVisibility(View.VISIBLE);
            actionBarSkipButton.setVisibility(View.INVISIBLE);
            actionBarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowAlert.loadingDialog(DriverDetailsActivity.this);
                    JumpTo.goToViewDriverDetailsActivity(DriverDetailsActivity.this, userId, mobile, true);
                }
            });

        }

        actionBarSkipButton.setOnClickListener(view -> {
            if (isDriverDetailsDoneAPI.equals("1")) {
                ShowAlert.loadingDialog(DriverDetailsActivity.this);
                JumpTo.goToViewDriverDetailsActivity(DriverDetailsActivity.this, userId, mobile, true);
            } else {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DriverDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Please_add_a_Driver));
                alertMessage.setText(getString(R.string.You_cannot_bid_unless_you_have_a_Driver));
                alertPositiveButton.setText(getString(R.string.Add));
                alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });

                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        ShowAlert.loadingDialog(DriverDetailsActivity.this);
                        JumpTo.goToViewVehicleDetailsActivity(DriverDetailsActivity.this, userId, mobile, true);
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });

        previewDialogDL = new Dialog(DriverDetailsActivity.this);
        previewDialogDL.setContentView(R.layout.dialog_preview_images);
        previewDialogDL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogSelfie = new Dialog(DriverDetailsActivity.this);
        previewDialogSelfie.setContentView(R.layout.dialog_preview_images);
        previewDialogSelfie.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        uploadDL = findViewById(R.id.driver_details_upload_driver_license);
        uploadSelfie = findViewById(R.id.upload_driver_selfie);
        editDL = findViewById(R.id.driver_details_edit_driver_license);
        textDL = findViewById(R.id.driver_details_driver_license_text_image);
        driverLicenseImage = (ImageView) findViewById(R.id.driver_details_driver_license_image);
        driverSelfieImg = findViewById(R.id.driver_selfie_img);
        textDS = findViewById(R.id.driver_selfie_text);
        editDS = findViewById(R.id.driver_details_edit_selfie_text);
        selfCheckBox = (CheckBox) findViewById(R.id.driver_details_self_checkbox);
        previewDrivingLicense = (ImageView) previewDialogDL.findViewById(R.id.dialog_preview_image_view);
        previewSelfie = (ImageView) previewDialogSelfie.findViewById(R.id.dialog_preview_image_view);

        if (userId != null) {
            getUserDetails();
        }

        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayState = new ArrayList<>();

        if (isEdit) {
            isDLUploaded = true;
            isSelfieUploded = true;
            uploadDL.setVisibility(View.INVISIBLE);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            getDriverDetails();
        }

        uploadSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChooseForDriverSelfie();
            }
        });

        editDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelfieEdited = true;
                DialogChooseForDriverSelfie();
            }
        });

        uploadDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChoose();
            }
        });

        editDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idDLEdited = true;
                DialogChoose();
            }
        });

        selectStateText.setOnClickListener(view -> SelectState.selectState(DriverDetailsActivity.this, selectStateText, selectDistrictText));

        selectDistrictText.setOnClickListener(view -> {
            if (!selectStateText.getText().toString().isEmpty()) {
                selectedState = selectStateText.getText().toString();
                SelectCity.selectCity(DriverDetailsActivity.this, selectedState, selectDistrictText);
            }
        });

        if (isEdit) {
            if (mobile.equals("91" + driverMobile.getText().toString())) {
                selfCheckBox.setVisibility(View.GONE);
                setCurrentLocation.setVisibility(View.GONE);
                mapLocation.setVisibility(View.GONE);
                driverName.setCursorVisible(false);
                driverName.setEnabled(false);
                driverMobile.setCursorVisible(false);
                driverMobile.setEnabled(false);
                driverAlternateMobile.setCursorVisible(false);
                driverAlternateMobile.setEnabled(false);
                address.setCursorVisible(false);
                address.setEnabled(false);
                pinCode.setCursorVisible(false);
                pinCode.setEnabled(false);
                driverEmailId.setCursorVisible(false);
                driverEmailId.setEnabled(false);
                selectDistrictText.setEnabled(false);
                selectStateText.setEnabled(false);
            } else {
                selfCheckBox.setVisibility(View.VISIBLE);
                setCurrentLocation.setVisibility(View.GONE);
                mapLocation.setVisibility(View.GONE);
                driverName.setCursorVisible(true);
                driverName.setEnabled(true);
                driverMobile.setCursorVisible(true);
                driverMobile.setEnabled(true);
                driverAlternateMobile.setCursorVisible(true);
                driverAlternateMobile.setEnabled(true);
                address.setCursorVisible(true);
                address.setEnabled(true);
                pinCode.setCursorVisible(true);
                pinCode.setEnabled(true);
                driverEmailId.setCursorVisible(true);
                driverEmailId.setEnabled(true);
                selectDistrictText.setEnabled(true);
                selectStateText.setEnabled(true);
            }
        }
    }

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
                String enteredPinCode = pinCode.getText().toString();
                GetStateCityUsingPINCode.getStateAndDistrictForPickUp(DriverDetailsActivity.this, enteredPinCode, selectStateText, selectDistrictText);
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

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.resultCode = resultCode;
        this.requestCode = requestCode;
        this.data = data;

        DLimagePicker();
        DLimagePickerWithoutAlert();
        selfieImagePicker();
        selfieImagePickerWithoutAlert();

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

    private String DLimagePickerWithoutAlert() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            driverLicenseImage.setImageURI(selectedImage);
            previewDrivingLicense.setImageURI(selectedImage);
            pathForDL = picturePath;
            return picturePath;

        } else if (requestCode == CAMERA_PIC_REQUEST1) {

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;

            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                driverLicenseImage.setImageBitmap(BitmapFactory.decodeFile(path));
                previewDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForDL = path;
                isDLUploaded = true;
                return path;
            } catch (Exception e) {
                isDLUploaded = false;
            }

        }
        return "";
    }

    private String selfieImagePickerWithoutAlert() {

        if (requestCode == CAMERA_PIC_REQUEST) {

            isSelfieUploded = true;

            textDS.setText(getString(R.string.Selfie_Uploaded));
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                driverSelfieImg.setImageBitmap(BitmapFactory.decodeFile(path));
                previewSelfie.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForSelfie = path;
                return path;
            } catch (Exception e) {
                isSelfieUploded = false;
            }


        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {

            isSelfieUploded = true;

            textDS.setText(R.string.Selfie_Uploaded);
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            driverSelfieImg.setImageURI(selectedImage);
            previewSelfie.setImageURI(selectedImage);
            pathForSelfie = picturePath;
            return picturePath;


        }
        return "";
    }

    private String DLimagePicker() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(DriverDetailsActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Driver_Details));
            alertMessage.setText(getString(R.string.Driving_License_uploaded_successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------
            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            driverLicenseImage.setImageURI(selectedImage);
            previewDrivingLicense.setImageURI(selectedImage);
            pathForDL = picturePath;
            return picturePath;

        } else if (requestCode == CAMERA_PIC_REQUEST1) {

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                driverLicenseImage.setImageBitmap(BitmapFactory.decodeFile(path));
                previewDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForDL = path;

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DriverDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Driver_Details));
                alertMessage.setText(getString(R.string.Driving_License_uploaded_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
                return path;
            } catch (Exception e) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DriverDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Driver_Details));
                alertMessage.setText(getString(R.string.Driving_License_not_uploaded_please_try_again));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isEdit) {

                        } else {
                            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            uploadDL.setVisibility(View.VISIBLE);
                            editDL.setVisibility(View.INVISIBLE);
                            previewDLImageView.setVisibility(View.INVISIBLE);
                            previewSelfieImageView.setVisibility(View.INVISIBLE);
                            isDLUploaded = false;
                        }
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        }
        return "";
    }


    private String selfieImagePicker() {
        if (requestCode == CAMERA_PIC_REQUEST) {
            isSelfieUploded = true;

            textDS.setText(getString(R.string.Selfie_Uploaded));
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                driverSelfieImg.setImageBitmap(BitmapFactory.decodeFile(path));
                previewSelfie.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForSelfie = path;

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DriverDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Driver_Details));
                alertMessage.setText(getString(R.string.Driver_Selfie_uploaded_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
                return path;
            } catch (Exception e) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(DriverDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Driver_Details));
                alertMessage.setText(getString(R.string.Driver_Selfie_not_uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isEdit) {

                        } else {
                            textDS.setText(getString(R.string.Take_a_Photo));
                            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            uploadSelfie.setVisibility(View.VISIBLE);
                            editDS.setVisibility(View.INVISIBLE);
                            isSelfieUploded = false;
                        }

                    }
                });
                //------------------------------------------------------------------------------------------
            }
        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
//----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(DriverDetailsActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Driver_Details));
            alertMessage.setText(getString(R.string.Driver_Selfie_uploaded_successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------

            isSelfieUploded = true;

            textDS.setText(getString(R.string.Selfie_Uploaded));
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            driverSelfieImg.setImageURI(selectedImage);
            previewSelfie.setImageURI(selectedImage);
            pathForSelfie = picturePath;
            return picturePath;

        }
        return "";
    }

    public void onClickDriverDetailsOk(View view) {
        if (driverName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter driver name", Toast.LENGTH_SHORT).show();
        } else if (driverMobile.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
        } else if (driverMobile.getText().toString().length() != 10) {
            Toast.makeText(this, "Please enter valid 10 digit mobile number", Toast.LENGTH_SHORT).show();
        } else if (address.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter driver Address", Toast.LENGTH_SHORT).show();
        } else if (pinCode.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter PIN Code", Toast.LENGTH_SHORT).show();
        } else if (pinCode.getText().toString().length() != 6) {
            Toast.makeText(this, "Please enter 6 digit PIN Code", Toast.LENGTH_SHORT).show();
        } else if (selectStateText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select State", Toast.LENGTH_SHORT).show();
        } else if (selectDistrictText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select City", Toast.LENGTH_SHORT).show();
        }
//        else if (!isDLUploaded || dlNumber.getText().toString().isEmpty()) {
//            Toast.makeText(this, "Please enter Driving License Number or upload Driving License", Toast.LENGTH_SHORT).show();
//        }
        else if (!isSelfieUploded) {
            Toast.makeText(this, "Please upload Driver Selfie", Toast.LENGTH_SHORT).show();
        } else {

                if (isDLUploaded) {
                    createDriverDetails();
                    if (selfCheckBox.isChecked()){
                        UpdateUserDetails.updateUserIsDriverAddedAlready(userId, "1");
                    }
                } else {
                    Toast.makeText(this, "Please enter valid Driving License Number or upload Driving License", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void createDriverDetails() {
        //update Driver as a user (IsDriverAdded)
        UpdateUserDetails.updateUserIsDriverAdded(userId, "1");
        if (isEdit) {
            if (idDLEdited) {
                uploadDriverLicense(driverId, pathForDL);
            }
            if (isSelfieEdited) {
                uploadDriverSelfie(driverId, pathForSelfie);
            }
            if (driverName.getText().toString() != null) {
                UpdateDriverDetails.updateDriverName(driverId, driverName.getText().toString());
                //update Driver as a user (Name)
                UpdateUserDetails.updateUserName(driverUserIdGet, driverName.getText().toString());
            }
            if (driverEmailId.getText().toString() != null) {
                UpdateDriverDetails.updateDriverEmailId(driverId, driverEmailId.getText().toString());
                //update Driver as a user (Email)
                UpdateUserDetails.updateUserEmailId(driverUserIdGet, driverEmailId.getText().toString());
            }
            if (driverMobile.getText().toString() != null && !driverNumberAPI.equals("91" + driverMobile.getText().toString())) {
                UpdateDriverDetails.updateDriverNumber(driverId, "91" + driverMobile.getText().toString());
                //update Driver as a user (Phone)
                UpdateUserDetails.updateUserPhoneNumber(driverUserIdGet, "91" + driverMobile.getText().toString());
            }
            if (driverAlternateMobile.getText().toString() != null) {
                UpdateDriverDetails.updateDriverAlternateNumber(driverId, "91" + driverAlternateMobile.getText().toString());
                //update Driver as a user (Alternate Phone)
                UpdateUserDetails.updateUserAlternatePhoneNumber(driverUserIdGet, "91" + driverAlternateMobile.getText().toString());
            }
            if (address.getText().toString() != null) {
                //update Driver as a user (Address)
                UpdateUserDetails.updateUserAddress(driverUserIdGet, address.getText().toString());
            }
            if (pinCode.getText().toString() != null) {
                //update Driver as a user (PinCode)
                UpdateUserDetails.updateUserPinCode(driverUserIdGet, pinCode.getText().toString());
            }
            if (selectStateText.getText().toString() != null) {
                //update Driver as a user (PinCode)
                UpdateUserDetails.updateUserState(driverUserIdGet, selectStateText.getText().toString());
            }
            if (selectDistrictText.getText().toString() != null) {
                //update Driver as a user (City)
                UpdateUserDetails.updateUserCity(driverUserIdGet, selectDistrictText.getText().toString());
            }

//            if (dlNumber.getText().toString() != null) {
//                UpdateDriverDetails.updateDriverDlNumber(driverId, dlNumber.getText().toString());
//            }
            ShowAlert.loadingDialog(DriverDetailsActivity.this);
            JumpTo.goToViewDriverDetailsActivity(DriverDetailsActivity.this, userId, mobile, true);

        } else {
            if (alreadyDriver) {
                saveDriver(createDriver());
            } else {
                saveDriver(createDriver());
                CreateUser.saveUser(CreateUser.createUser(driverName.getText().toString(), "91" + driverMobile.getText().toString(), "91" + driverAlternateMobile.getText().toString(), address.getText().toString(), "Driver", driverEmailId.getText().toString(), pinCode.getText().toString(), selectDistrictText.getText().toString(), selectStateText.getText().toString(), "null", latForAddress, longForAddress));
            }
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(DriverDetailsActivity.this);
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

            alertTitle.setText(getString(R.string.Driver_Details_added_successfully));
            alertMessage.setText(getString(R.string.Would_you_like_to_add_Drivers_Bank_Details));

            alertPositiveButton.setText(getString(R.string.Add));
            alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    ShowAlert.loadingDialog(DriverDetailsActivity.this);
                    if (alreadyDriver) {
                        JumpTo.goToBankDetailsActivity(DriverDetailsActivity.this, userId, mobile, false, true, null);
                    } else {
                        JumpTo.goToBankDetailsActivity(DriverDetailsActivity.this, driverUserId, mobile, false, true, null);
                    }
                }
            });

            alertNegativeButton.setText(getString(R.string.Later));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));
            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    ShowAlert.loadingDialog(DriverDetailsActivity.this);
                    if (fromBidNow) {
                        DriverDetailsActivity.this.finish();
                    } else {
                        JumpTo.goToViewDriverDetailsActivity(DriverDetailsActivity.this, userId, mobile, true);
                    }
                }
            });
            //------------------------------------------------------------------------------------------
        }
    }

    //--------------------------------------create Driver Details in API -------------------------------------
    public AddDriverRequest createDriver() {
        AddDriverRequest addDriverRequest = new AddDriverRequest();
        addDriverRequest.setUser_id(userId);
        addDriverRequest.setDriver_name(driverName.getText().toString());
        addDriverRequest.setDriver_number("91" + driverMobile.getText().toString());
        addDriverRequest.setDriver_emailId(driverEmailId.getText().toString());
        addDriverRequest.setTruck_id(truckIdPass);
        addDriverRequest.setAlternate_ph_no("91" + driverAlternateMobile.getText().toString());
        //addDriverRequest.setDl_number(dlNumber.getText().toString());
        return addDriverRequest;
    }

    public void saveDriver(AddDriverRequest addDriverRequest) {
        Call<AddDriverResponse> addDriverResponseCall = ApiClient.addDriverService().saveDriver(addDriverRequest);
        addDriverResponseCall.enqueue(new Callback<AddDriverResponse>() {
            @Override
            public void onResponse(Call<AddDriverResponse> call, Response<AddDriverResponse> response) {
                AddDriverResponse driverResponse = response.body();
                try {
                    driverIdPass = driverResponse.getData().getDriver_id();
                    UpdateTruckDetails.updateTruckDriverId(truckIdPass, driverIdPass);
                    uploadDriverLicense(driverIdPass, pathForDL);
                    uploadDriverSelfie(driverIdPass, pathForSelfie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddDriverResponse> call, Throwable t) {
            }
        });
    }

    private TextWatcher driverNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String addressText  = address.getText().toString().trim();

            if (addressText.length()==0){
                pinCode.setVisibility(View.GONE);
                selectStateText.setVisibility(View.GONE);
                selectDistrictText.setVisibility(View.GONE);
            }else{
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


    //**************************************** delete dlNumber************************************************//
//    private TextWatcher dlverificationWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            String dlNumberWatcher = dlNumber.getText().toString().trim();
//            String dob = dDOB.getText().toString().trim();
//
//            if (dlNumberWatcher.length() != 15) {
//                dDOB.setVisibility(View.GONE);
//            } else {
//                dDOB.setVisibility(View.VISIBLE);
//                if (dob.length() == 10) {
////                    checkDl(dlNumberWatcher, dob);
//                } else if (dob.length() > 1) {
//                    dlNumber.setEnabled(false);
//                } else {
//                    dlNumber.setEnabled(true);
//                }
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//        }
//    };

    //**************************************** delete dlNumber************************************************//

//    public void checkDl(String dlNumbers, String dob) {
//        Call<DLVerificationResponse> responseCall = ApiClient.getVerification().checkDL(userId, dlNumbers, dob);
//        responseCall.enqueue(new Callback<DLVerificationResponse>() {
//            @Override
//            public void onResponse(Call<DLVerificationResponse> call, retrofit2.Response<DLVerificationResponse> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        DLVerificationResponse response1 = response.body();
//                        DLVerificationResponse.UserList list = response1.getData().get(0);
//                        Log.i("Success Message", list.getSuccess());
//                        if (list.getSuccess().equals("1")) {
//                            dlNumber.setEnabled(false);
//                            dlNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
//                            dDOB.setEnabled(false);
//                            dDOB.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
//                            dlValid = true;
//                        } else {
//                            Toast.makeText(DriverDetailsActivity.this, "Please enter a valid DL number", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(DriverDetailsActivity.this, "Please enter a valid DL number", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(DriverDetailsActivity.this, "Please enter a valid DL number", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DLVerificationResponse> call, Throwable t) {
//                Toast.makeText(DriverDetailsActivity.this, "Please enter a valid DL number", Toast.LENGTH_SHORT).show();
//            }
//        });

        //**************************************** delete dlNumber************************************************//

        //****************************************************************************************//
   // }


    private TextWatcher driverMobileWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String mobileNoWatcher = driverMobile.getText().toString().trim();
            String nameWatcher = driverName.getText().toString().trim();

            if (mobileNoWatcher.length() == 10) {
                checkPhoneInAPI("91" + driverMobile.getText().toString());

                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            } else {

                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right_red));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left_red));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
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

    private void getDriverDetails() {
        String url = getString(R.string.baseURL) + "/driver/driverId/" + driverId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        driverNameAPI = obj.getString("driver_name");
                        driverNumberAPI = obj.getString("driver_number");
                        driverEmailAPI = obj.getString("driver_emailId");
                        String driverDlNumberAPI = obj.getString("dl_number");

                        try {
                            dlNumber.setText(driverDlNumberAPI);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        driverName.setText(driverNameAPI);

                        String drivingLicenseURL = obj.getString("upload_dl");
                        new DownloadImageTask(previewDrivingLicense).execute(drivingLicenseURL);
                        new DownloadImageTask(driverLicenseImage).execute(drivingLicenseURL);

                        String selfieURL = obj.getString("driver_selfie");
                        new DownloadImageTask(previewSelfie).execute(selfieURL);
                        new DownloadImageTask(driverSelfieImg).execute(selfieURL);

                        if (driverNumberAPI != null) {
                            String s1 = driverNumberAPI.substring(2, 12);
                            driverMobile.setText(s1);
                            checkPhoneForDriverDetails(driverNumberAPI);
                        }

                        if (driverEmailAPI == null) {
                            driverEmailId.setText("");
                        } else {
                            driverEmailId.setText(driverEmailAPI);
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
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void uploadDriverLicense(String driverId1, String picPath) {

        File file = new File(picPath);

        MultipartBody.Part body = prepareFilePart("dl", Uri.fromFile(file));

        Call<UploadDriverLicenseResponse> call = ApiClient.getUploadDriverLicenseService().uploadDriverLicense(driverId1, body);
        call.enqueue(new Callback<UploadDriverLicenseResponse>() {
            @Override
            public void onResponse(Call<UploadDriverLicenseResponse> call, Response<UploadDriverLicenseResponse> response) {

            }

            @Override
            public void onFailure(Call<UploadDriverLicenseResponse> call, Throwable t) {

            }
        });
    }

    private void uploadDriverSelfie(String driverId1, String picPath) {

        File file = new File(picPath);

        MultipartBody.Part body = prepareFilePart("selfie", Uri.fromFile(file));

        Call<UploadDriverSelfieResponse> call = ApiClient.getUploadDriverSelfieService().uploadDriverSelfie(driverId1, body);
        call.enqueue(new Callback<UploadDriverSelfieResponse>() {
            @Override
            public void onResponse(Call<UploadDriverSelfieResponse> call, Response<UploadDriverSelfieResponse> response) {

            }

            @Override
            public void onFailure(Call<UploadDriverSelfieResponse> call, Throwable t) {

            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

    public void onClickPreviewDrivingLicense(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogDL.show();
        previewDialogDL.getWindow().setAttributes(lp);
    }

    public void onClickPreviewSelfie(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogSelfie.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogSelfie.show();
        previewDialogSelfie.getWindow().setAttributes(lp);
    }

    //----------------------------------------------------------------------------------------------
    private void checkPhoneInAPI(String getMobile) {
        String receivedMobile = getMobile;
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userIdAPI = data.getString("user_id");
                        mobileNoAPI = data.getString("phone_number");
                        pinCodeAPI = data.getString("pin_code");
                        nameAPI = data.getString("name");
                        roleAPI = data.getString("user_type");
                        cityAPI = data.getString("preferred_location");
                        addressAPI = data.getString("address");
                        stateAPI = data.getString("isRegistration_done");

                        arrayUserId.add(userIdAPI);
                        arrayMobileNo.add(mobileNoAPI);
                        arrayAddress.add(addressAPI);
                        arrayState.add(stateAPI);
                        arrayName.add(nameAPI);
                        arrayRole.add(roleAPI);
                        arrayCity.add(cityAPI);
                        arrayPinCode.add(pinCodeAPI);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(receivedMobile)) {
//                            driverUserIdGet = arrayUserId.get(j);
//                            String nameGet = arrayName.get(j);
//                            String phoneGet = arrayMobileNo.get(j);
//                            String addressGet = arrayAddress.get(j);
//                            String pinCodeGet = arrayPinCode.get(j);
//                            String cityGet = arrayCity.get(j);
//                            String roleGet = arrayRole.get(j);
//                            String isRegistrationDoneGet = arrayRegDone.get(j);

                            alreadyDriver = true;
                            Log.i("Already", "Driver");

                            break;
                        } else {
                            alreadyDriver = false;
                        }
                    }

                    Log.i("Driver Status", String.valueOf(alreadyDriver));
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
        //------------------------------------------------------------------------------------------------
    }

    private void checkPhoneForDriverDetails(String getMobile) {
        String receivedMobile = getMobile;
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userIdAPI = data.getString("user_id");
                        mobileNoAPI = data.getString("phone_number");
                        pinCodeAPI = data.getString("pin_code");
                        nameAPI = data.getString("name");
                        roleAPI = data.getString("user_type");
                        cityAPI = data.getString("preferred_location");
                        addressAPI = data.getString("address");
                        stateAPI = data.getString("state_code");

                        arrayUserId.add(userIdAPI);
                        arrayMobileNo.add(mobileNoAPI);
                        arrayAddress.add(addressAPI);
                        arrayState.add(stateAPI);
                        arrayName.add(nameAPI);
                        arrayRole.add(roleAPI);
                        arrayCity.add(cityAPI);
                        arrayPinCode.add(pinCodeAPI);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(receivedMobile)) {
                            driverUserIdGet = arrayUserId.get(j);
                            Log.i("DriverUserId", driverUserIdGet);
                            String nameGet = arrayName.get(j);
                            String phoneGet = arrayMobileNo.get(j);
                            String addressGet = arrayAddress.get(j);
                            String pinCodeGet = arrayPinCode.get(j);
                            String cityGet = arrayCity.get(j);
                            String roleGet = arrayRole.get(j);
                            String stateGet = arrayState.get(j);

                            address.setText(addressGet);
                            Log.i("Address Driver", addressGet);
                            pinCode.setText(pinCodeGet);
                            selectStateText.setText(stateGet);
                            selectDistrictText.setText(cityGet);

                        }
                    }

                    Log.i("Driver Status", String.valueOf(alreadyDriver));
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }

    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(DriverDetailsActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverDetailsActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(DriverDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverDetailsActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(DriverDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverDetailsActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    public void onClickIsSelf(View view) {
        if (selfCheckBox.isChecked()) {
            getUserDetails();

            setCurrentLocation.setVisibility(View.GONE);
            driverName.setCursorVisible(false);
            driverName.setEnabled(false);
            driverMobile.setCursorVisible(false);
            driverMobile.setEnabled(false);
            driverAlternateMobile.setCursorVisible(false);
            driverAlternateMobile.setEnabled(false);
            address.setCursorVisible(false);
            address.setEnabled(false);
            pinCode.setCursorVisible(false);
            pinCode.setEnabled(false);
            driverEmailId.setCursorVisible(false);
            driverEmailId.setEnabled(false);
            selectDistrictText.setEnabled(false);
            selectStateText.setEnabled(false);
            mapLocation.setVisibility(View.GONE);

        } else if (!selfCheckBox.isChecked()) {

            setCurrentLocation.setVisibility(View.VISIBLE);
            mapLocation.setVisibility(View.VISIBLE);
            driverName.setCursorVisible(true);
            driverName.setEnabled(true);
            driverMobile.setCursorVisible(true);
            driverMobile.setEnabled(true);
            driverAlternateMobile.setCursorVisible(true);
            driverAlternateMobile.setEnabled(true);
            address.setCursorVisible(true);
            address.setEnabled(true);
            pinCode.setCursorVisible(true);
            pinCode.setEnabled(true);
            driverEmailId.setCursorVisible(true);
            driverEmailId.setEnabled(true);
            selectDistrictText.setEnabled(true);
            selectStateText.setEnabled(true);

            driverName.getText().clear();
            driverMobile.getText().clear();
            address.getText().clear();
            pinCode.getText().clear();
            driverEmailId.getText().clear();
            selectDistrictText.setText("");
            selectStateText.setText("");
        }
    }

    public void getUserDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String userNameAPI = obj.getString("name");
                        String userMobileAPI = obj.getString("phone_number");
                        String emailIdAPI = obj.getString("email_id");
                        String userAddressAPI = obj.getString("address");
                        String userStateAPI = obj.getString("state_code");
                        String userCityAPI = obj.getString("preferred_location");
                        String userPinCodeAPI = obj.getString("pin_code");
                        String userRole = obj.getString("user_type");
                        String alternateMob = obj.getString("alternate_ph_no");
                        isDriverDetailsDoneAPI = obj.getString("isDriver_added");
                        String isDriverAsSelfAlreadyAdded = obj.getString("is_self_added_asDriver");

                        try{
                            if (isDriverAsSelfAlreadyAdded.equals("1")){
                                selfCheckBox.setVisibility(View.GONE);
                            }else{
                                selfCheckBox.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){

                        }


                        if (selfCheckBox.isChecked()) {
                            driverName.setText(userNameAPI);
                            String s1 = userMobileAPI.substring(2, 12);
                            driverMobile.setText(s1);
                            address.setText(userAddressAPI);
                            selectStateText.setText(userStateAPI);
                            selectDistrictText.setText(userCityAPI);
                            pinCode.setText(userPinCodeAPI);
                            driverEmailId.setText(emailIdAPI);
                            try {
                                if (alternateMob.equals("null") || alternateMob == null) {
                                    driverAlternateMobile.setText("");
                                } else {
                                    String s2 = alternateMob.substring(2, 12);
                                    driverAlternateMobile.setText(s2);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

    public void onClickOpenMaps(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Places.initialize(getApplicationContext(), "AIzaSyDAAes8x5HVKYB5YEIGBmdnCdyBrAHUijM");
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
        ShowAlert.loadingDialog(DriverDetailsActivity.this);
        JumpTo.goToServiceProviderDashboard(DriverDetailsActivity.this, mobile, true, true);
    }

    private void DialogChooseForDriverSelfie() {
        try {
            requestPermissionsForCamera();
            requestPermissionsForGalleryWRITE();
            requestPermissionsForGalleryREAD();

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        } catch (Exception e) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(DriverDetailsActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Please_Upload_From_Gallery));
            alertMessage.setText(getString(R.string.Choose_from_Gallery));
            alertPositiveButton.setVisibility(View.GONE);

            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
                    alert.dismiss();
                }
            });
            //------------------------------------------------------------------------------------------
        }
    }

    private void DialogChoose() {
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        requestPermissionsForCamera();
        Dialog chooseDialog;

        chooseDialog = new Dialog(DriverDetailsActivity.this);
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
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                chooseDialog.dismiss();
            }
        });
    }
}