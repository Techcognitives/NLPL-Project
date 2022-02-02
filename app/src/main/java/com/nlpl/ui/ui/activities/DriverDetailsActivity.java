package com.nlpl.ui.ui.activities;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.nlpl.R;
import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.Responses.UploadDriverSelfieResponse;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.model.Responses.UploadDriverLicenseResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverName;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverNumber;
import com.nlpl.model.UpdateModel.Models.UpdateDriverDetails.UpdateDriverTruckId;
import com.nlpl.model.UpdateModel.Models.UpdateTruckDetails.UpdateTruckDriverId;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, actionBarSkipButton;
    ImageView actionBarMenuButton;

    EditText driverName, driverMobile, driverEmailId;
    ImageView actionBarBackButton;
    Intent data;
    int requestCode;
    int resultCode;
    String isDriverDetailsDoneAPI;
    CheckBox selfCheckBox;

    Button uploadDL, okDriverDetails, uploadSelfie;
    TextView textDL, editDL, series, textDS, editDS;
    int GET_FROM_GALLERY = 0;
    int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST = 1;
    int CAMERA_PIC_REQUEST1 = 3;
    ImageView driverLicenseImage, driverSelfieImg;

    private RequestQueue mQueue;

    String driverUserId, driverUserIdGet;

    String pathForDL, pathForSelfie, img_type, userId, driverId, driverNameAPI, driverNumberAPI, driverEmailAPI, mobile;
    Boolean fromBidNow = false, isDLUploaded = false, isEdit, isSelfieUploded = false, idDLEdited = false;
    ImageView previewDrivingLicense, previewSelfie, previewDLImageView, previewSelfieImageView;
    Dialog previewDialogDL, previewDialogSelfie;
    View personalAndAddress;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    TextView selectStateText, selectDistrictText, getCurrentLocation;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState;
    EditText pinCode, address;

    String userIdAPI, nameAPI, stateAPI, pinCodeAPI, addressAPI, mobileNoAPI, cityAPI, roleAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayState;
    Boolean alreadyDriver = true, isSelfieEdited = false;
    String truckIdPass, driverIdPass;

    FusedLocationProviderClient fusedLocationProviderClient;

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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        personalAndAddress = (View) findViewById(R.id.driver_details_personal_and_address);
        action_bar = (View) findViewById(R.id.driver_details_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarSkipButton = (TextView) action_bar.findViewById(R.id.action_bar_skip);
        actionBarMenuButton = (ImageView) action_bar.findViewById(R.id.action_bar_menu);
        actionBarMenuButton.setVisibility(View.GONE);

        driverMobile = (EditText) personalAndAddress.findViewById(R.id.registration_mobile_no_edit);
        driverName = personalAndAddress.findViewById(R.id.registration_edit_name);
        okDriverDetails = findViewById(R.id.driver_details_ok_button);
        series = (TextView) personalAndAddress.findViewById(R.id.registration_prefix);
        driverEmailId = (EditText) personalAndAddress.findViewById(R.id.registration_email_id_edit);
        getCurrentLocation = (TextView) personalAndAddress.findViewById(R.id.personal_and_address_get_current_location);
        getCurrentLocation.setVisibility(View.VISIBLE);

        previewDLImageView = (ImageView) findViewById(R.id.driver_details_preview_driving_license);
        previewSelfieImageView = (ImageView) findViewById(R.id.driver_details_preview_selfie);
        ConstraintLayout personalConstrain = (ConstraintLayout) personalAndAddress.findViewById(R.id.personal_registration_sp_constrain);
        personalConstrain.setVisibility(View.GONE);

        pinCode = (EditText) personalAndAddress.findViewById(R.id.registration_pin_code_edit);
        address = (EditText) personalAndAddress.findViewById(R.id.registration_address_edit);
        selectStateText = (TextView) personalAndAddress.findViewById(R.id.registration_select_state);
        selectDistrictText = (TextView) personalAndAddress.findViewById(R.id.registration_select_city);

        driverName.setHint("Enter Driver Name");
        driverMobile.setHint("Enter 10 digit Driver Number");
        driverEmailId.setHint("Enter Driver Email Id");

        driverName.addTextChangedListener(driverNameWatcher);
        driverMobile.addTextChangedListener(driverMobileWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(driverNameWatcher);

        address.setFilters(new InputFilter[]{filter});

        if (isEdit) {
            checkPhoneInAPI(mobile);
        }


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

        actionBarTitle.setText("Driver Details");
        actionBarBackButton.setVisibility(View.GONE);
        actionBarSkipButton.setVisibility(View.VISIBLE);
        actionBarSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDriverDetailsDoneAPI.equals("1")) {
                    Intent intent = new Intent(DriverDetailsActivity.this, ViewTruckDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
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

                    alertTitle.setText("Please add a Driver");
                    alertMessage.setText("You cannot bid unless you have a Driver");
                    alertPositiveButton.setText("+ Add Truck Driver");
                    alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });

                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            Intent i8 = new Intent(DriverDetailsActivity.this, ViewTruckDetailsActivity.class);
                            i8.putExtra("mobile", mobile);
                            i8.putExtra("userId", userId);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        });

        uploadDL = findViewById(R.id.driver_details_upload_driver_license);
        uploadSelfie = findViewById(R.id.upload_driver_selfie);
        editDL = findViewById(R.id.driver_details_edit_driver_license);
        textDL = findViewById(R.id.driver_details_driver_license_text_image);
        driverLicenseImage = (ImageView) findViewById(R.id.driver_details_driver_license_image);
        driverSelfieImg = findViewById(R.id.driver_selfie_img);
        textDS = findViewById(R.id.driver_selfie_text);
        editDS = findViewById(R.id.driver_details_edit_selfie_text);
        selfCheckBox = (CheckBox) findViewById(R.id.driver_details_self_checkbox);

        previewDialogDL = new Dialog(DriverDetailsActivity.this);
        previewDialogDL.setContentView(R.layout.dialog_preview_images);
        previewDialogDL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDrivingLicense = (ImageView) previewDialogDL.findViewById(R.id.dialog_preview_image_view);

        previewDialogSelfie = new Dialog(DriverDetailsActivity.this);
        previewDialogSelfie.setContentView(R.layout.dialog_preview_images);
        previewDialogSelfie.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewSelfie = (ImageView) previewDialogSelfie.findViewById(R.id.dialog_preview_image_view);

        String mobileNoWatcher = driverMobile.getText().toString().trim();
        String nameWatcher = driverName.getText().toString().trim();

//        if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
//            okDriverDetails.setEnabled(true);
//            okDriverDetails.setBackgroundResource(R.drawable.button_active);
//        }



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
            okDriverDetails.setEnabled(false);
            okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
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
                requestPermissionsForCamera();
                requestPermissionsForGalleryWRITE();
                requestPermissionsForGalleryREAD();

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        editDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelfieEdited = true;
                requestPermissionsForCamera();
                requestPermissionsForGalleryWRITE();
                requestPermissionsForGalleryREAD();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
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


        selectStateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStateDialog = new Dialog(DriverDetailsActivity.this);
                selectStateDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectStateDialog.show();
                selectStateDialog.setCancelable(true);
                ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);

                selectStateArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
                selectStateUnionCode = ArrayAdapter.createFromResource(DriverDetailsActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

                stateList.setAdapter(selectStateArray);


                stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                        selectStateText.setText(selectStateUnionCode.getItem(i)); //Set Selected Credentials
                        selectStateDialog.dismiss();
                        selectDistrictText.performClick();
                    }
                });
            }
        });

        selectDistrictText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectStateText.getText().toString().isEmpty()) {

                    selectedState = selectStateText.getText().toString();
                    selectDistrictDialog = new Dialog(DriverDetailsActivity.this);
                    selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                    selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    selectDistrictDialog.show();
                    TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
                    title.setText("Select City");
                    ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

                    if (selectedState.equals("AP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_assam_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("BR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_bihar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CG")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_goa_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_gujarat_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_haryana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_jharkhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_karnataka_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_kerala_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_maharashtra_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_manipur_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("ML")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_meghalaya_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MZ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_mizoram_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("NL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_nagaland_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("OD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_odisha_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_punjab_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("RJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_rajasthan_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("SK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_sikkim_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_telangana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_tripura_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("WB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_west_bengal_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CH/PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_chandigarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD2")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_daman_diu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_delhi_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_ladakh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PY")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(DriverDetailsActivity.this,
                                R.array.array_puducherry_districts, R.layout.custom_list_row);
                    }
                    districtList.setAdapter(selectDistrictArray);

                    districtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            selectDistrictText.setText(selectDistrictArray.getItem(i)); //Set Selected Credentials
                            selectDistrictDialog.dismiss();
                            selectedDistrict = selectDistrictArray.getItem(i).toString();
                        }
                    });
                }
            }
        });
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
            } else {
                String enteredPinCode = pinCode.getText().toString();
                getStateAndDistrict(enteredPinCode);
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void getStateAndDistrict(String enteredPin) {

        Log.i("Entered PIN", enteredPin);

        String url = "http://13.234.163.179:3000/user/locationData/" + enteredPin;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = response.getJSONObject("data");
                    String stateByPinCode = obj.getString("stateCode");
                    String distByPinCode = obj.getString("district");

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

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

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

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            driverLicenseImage.setImageBitmap(BitmapFactory.decodeFile(path));
            previewDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForDL = path;
            return path;
        }
        return "";
    }

    private String selfieImagePickerWithoutAlert() {

        if (requestCode == CAMERA_PIC_REQUEST) {

            isSelfieUploded = true;

            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

            textDS.setText("Selfie Uploaded");
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            driverSelfieImg.setImageBitmap(BitmapFactory.decodeFile(path));
            previewSelfie.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForSelfie = path;
            return path;

        }
        return "";
    }

    private String DLimagePicker() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

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

            alertTitle.setText("Driver Details");
            alertMessage.setText("Driving License uploaded successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

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
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

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

            alertTitle.setText("Driver Details");
            alertMessage.setText("Driving License uploaded successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

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
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;

            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            driverLicenseImage.setImageBitmap(BitmapFactory.decodeFile(path));
            previewDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(path));

            pathForDL = path;
            return path;
        }
        return "";
    }


    private String selfieImagePicker() {

        if (requestCode == CAMERA_PIC_REQUEST) {
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

            alertTitle.setText("Driver Details");
            alertMessage.setText("Driver Selfie uploaded successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();

                }
            });
            //------------------------------------------------------------------------------------------

            isSelfieUploded = true;

            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

            textDS.setText("Selfie Uploaded");
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            driverSelfieImg.setImageBitmap(BitmapFactory.decodeFile(path));
            previewSelfie.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForSelfie = path;
            return path;

        }
        return "";
    }

    public void onClickDriverDetailsOk(View view) {
        String driverMobileText = driverMobile.getText().toString();
        String driverNameText = driverName.getText().toString();

        if (!driverNameText.isEmpty() && !driverMobileText.isEmpty() && isDLUploaded && isSelfieUploded) {
            if (driverMobileText.length() != 10) {
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

                alertTitle.setText("Invalid Mobile Number");
                alertMessage.setText("Please enter a 10 digit valid mobile number.");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            } else {
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
                        updateDriverName();
                        //update Driver as a user (Name)
                        UpdateUserDetails.updateUserName(driverUserIdGet, driverName.getText().toString());
                    }
                    if (driverEmailId.getText().toString() != null) {
                        updateDriverEmailId();
                        //update Driver as a user (Email)
                        UpdateUserDetails.updateUserEmailId(driverUserIdGet, driverEmailId.getText().toString());
                    }

                    if (driverMobile.getText().toString() != null && !driverNumberAPI.equals("91" + driverMobile.getText().toString())) {
                        updateDriverNumber();
                        //update Driver as a user (Phone)
                        UpdateUserDetails.updateUserPhoneNumber(driverUserIdGet, "91" + driverMobile.getText().toString());
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

                    Intent i8 = new Intent(DriverDetailsActivity.this, ViewDriverDetailsActivity.class);
                    i8.putExtra("userId", userId);
                    i8.putExtra("mobile", mobile);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    finish();
                    overridePendingTransition(0, 0);

                } else {

                    if (alreadyDriver) {
                        saveDriver(createDriver());
                    } else {
                        saveDriver(createDriver());
                        saveDriverUser(createDriverUser());
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

                    alertTitle.setText("Driver Details added successfully");
                    alertMessage.setText("Would you like to add Driver's Bank Details?");

                    alertPositiveButton.setText("Add Driver Bank Details");
                    alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            Intent intent2 = new Intent(DriverDetailsActivity.this, BankDetailsActivity.class);
                            if (alreadyDriver) {
                                intent2.putExtra("userId", userId);
                            } else {
                                intent2.putExtra("userId", driverUserId);
                            }
                            intent2.putExtra("isEdit", false);
                            intent2.putExtra("mobile", mobile);
                            startActivity(intent2);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });

                    alertNegativeButton.setText("May be Later");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));
                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            if (fromBidNow) {
                                DriverDetailsActivity.this.finish();
                            } else {
                                Intent i8 = new Intent(DriverDetailsActivity.this, ViewDriverDetailsActivity.class);
                                i8.putExtra("mobile", mobile);
                                i8.putExtra("userId", userId);
                                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i8);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
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
        return addDriverRequest;
    }

    public void saveDriver(AddDriverRequest addDriverRequest) {
        Call<AddDriverResponse> addDriverResponseCall = ApiClient.addDriverService().saveDriver(addDriverRequest);
        addDriverResponseCall.enqueue(new Callback<AddDriverResponse>() {
            @Override
            public void onResponse(Call<AddDriverResponse> call, Response<AddDriverResponse> response) {
                AddDriverResponse driverResponse = response.body();
                driverIdPass = driverResponse.getData().getDriver_id();
                updateTruckDriverId(driverIdPass);

                uploadDriverLicense(driverIdPass, pathForDL);
                uploadDriverSelfie(driverIdPass, pathForSelfie);

            }

            @Override
            public void onFailure(Call<AddDriverResponse> call, Throwable t) {

            }
        });
    }

    private void updateTruckDriverId(String truckDriverId) {

        UpdateTruckDriverId updateTruckDriverId = new UpdateTruckDriverId(truckDriverId);

        Call<UpdateTruckDriverId> call = ApiClient.addTruckService().updateTruckDriverId("" + truckIdPass, updateTruckDriverId);

        call.enqueue(new Callback<UpdateTruckDriverId>() {
            @Override
            public void onResponse(Call<UpdateTruckDriverId> call, Response<UpdateTruckDriverId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckDriverId> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }
    //-----------------------------------------------------------------------------------------------------

    private TextWatcher driverNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String mobileNoWatcher = driverMobile.getText().toString().trim();
            String nameWatcher = driverName.getText().toString().trim();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            } else {
//                okDriverDetails.setEnabled(false);
                okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
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
                if (!nameWatcher.isEmpty() && isDLUploaded && isSelfieUploded) {
                    okDriverDetails.setEnabled(true);
                    okDriverDetails.setBackgroundResource(R.drawable.button_active);
                } else {
//                    okDriverDetails.setEnabled(false);
                    okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
                }


                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            } else {
//                okDriverDetails.setEnabled(false);
                okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
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

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverName() {

        Log.i("driver Id at update", driverId);
        UpdateDriverName updateDriverName = new UpdateDriverName(driverName.getText().toString());

        Call<UpdateDriverName> call = ApiClient.addDriverService().updateDriverName("" + driverId, updateDriverName);

        call.enqueue(new Callback<UpdateDriverName>() {

            @Override
            public void onResponse(Call<UpdateDriverName> call, Response<UpdateDriverName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverName> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateDriverTruckId() {

        UpdateDriverTruckId updateDriverTruckId = new UpdateDriverTruckId(truckIdPass);

        Call<UpdateDriverTruckId> call = ApiClient.addDriverService().updateDriverTruckId("" + driverId, updateDriverTruckId);

        call.enqueue(new Callback<UpdateDriverTruckId>() {
            @Override
            public void onResponse(Call<UpdateDriverTruckId> call, Response<UpdateDriverTruckId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Truck Id");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverTruckId> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverNumber() {

        UpdateDriverNumber updateDriverNumber = new UpdateDriverNumber("91" + driverMobile.getText().toString());

        Call<UpdateDriverNumber> call = ApiClient.addDriverService().updateDriverNumber("" + driverId, updateDriverNumber);

        call.enqueue(new Callback<UpdateDriverNumber>() {
            @Override
            public void onResponse(Call<UpdateDriverNumber> call, Response<UpdateDriverNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Number");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverEmailId() {

        UpdateDriverEmailId updateDriverEmailId = new UpdateDriverEmailId(driverEmailId.getText().toString());

        Call<UpdateDriverEmailId> call = ApiClient.addDriverService().updateDriverEmailId("" + driverId, updateDriverEmailId);

        call.enqueue(new Callback<UpdateDriverEmailId>() {
            @Override
            public void onResponse(Call<UpdateDriverEmailId> call, Response<UpdateDriverEmailId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Email");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverEmailId> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
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
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

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
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

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

    //--------------------------------------create User in API -------------------------------------
    public UserRequest createDriverUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(driverName.getText().toString());
        userRequest.setPhone_number("91" + driverMobile.getText().toString());
        userRequest.setAddress(address.getText().toString());
        userRequest.setUser_type("Driver");
        userRequest.setEmail_id(driverEmailId.getText().toString());
        userRequest.setIsRegistration_done(1);
        userRequest.setPin_code(pinCode.getText().toString());
        userRequest.setPreferred_location(selectDistrictText.getText().toString());
        userRequest.setState_code(selectStateText.getText().toString());
        return userRequest;
    }

    public void saveDriverUser(UserRequest userRequest) {
        Call<UserResponse> userResponseCall = ApiClient.getUserService().saveUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                Log.i("Message UserCreated:", userResponse.getData().getPhone_number());
                UserResponse userResponse = response.body();
                Log.i("Msg Success", String.valueOf(userResponse.getData().getUser_id()));
                driverUserId = String.valueOf(userResponse.getData().getUser_id());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
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

                            if (receivedMobile.equals("91" + driverMobile.getText().toString())) {
                                if (isEdit) {
                                    selfCheckBox.setVisibility(View.GONE);
                                    getCurrentLocation.setVisibility(View.INVISIBLE);
                                    driverName.setCursorVisible(false);
                                    driverName.setEnabled(false);
                                    driverMobile.setCursorVisible(false);
                                    driverMobile.setEnabled(false);
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
                                    getCurrentLocation.setVisibility(View.INVISIBLE);
                                    driverName.setCursorVisible(true);
                                    driverName.setEnabled(true);
                                    driverMobile.setCursorVisible(true);
                                    driverMobile.setEnabled(true);
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

            getCurrentLocation.setVisibility(View.INVISIBLE);
            driverName.setCursorVisible(false);
            driverName.setEnabled(false);
            driverMobile.setCursorVisible(false);
            driverMobile.setEnabled(false);
            address.setCursorVisible(false);
            address.setEnabled(false);
            pinCode.setCursorVisible(false);
            pinCode.setEnabled(false);
            driverEmailId.setCursorVisible(false);
            driverEmailId.setEnabled(false);
            selectDistrictText.setEnabled(false);
            selectStateText.setEnabled(false);

        } else if (!selfCheckBox.isChecked()) {

            getCurrentLocation.setVisibility(View.INVISIBLE);
            driverName.setCursorVisible(true);
            driverName.setEnabled(true);
            driverMobile.setCursorVisible(true);
            driverMobile.setEnabled(true);
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

    private void getUserDetails() {

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
                        isDriverDetailsDoneAPI = obj.getString("isDriver_added");

                        if (selfCheckBox.isChecked()) {
                            driverName.setText(userNameAPI);
                            String s1 = userMobileAPI.substring(2, 12);
                            driverMobile.setText(s1);
                            address.setText(userAddressAPI);
                            selectStateText.setText(userStateAPI);
                            selectDistrictText.setText(userCityAPI);
                            pinCode.setText(userPinCodeAPI);
                            driverEmailId.setText(emailIdAPI);
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

    public void onClickGetCurrentLocation(View view) {
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(DriverDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(DriverDetailsActivity.this, Locale.getDefault());
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
                            selectStateText.setText(stateCurrent);
                            selectDistrictText.setText(cityCurrent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        } else {
            ActivityCompat.requestPermissions(DriverDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i8 = new Intent(DriverDetailsActivity.this, DashboardActivity.class);
        i8.putExtra("mobile2", mobile);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);

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