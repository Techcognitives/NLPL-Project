package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.Responses.UploadDriverSelfieResponse;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.model.Responses.UploadDriverLicenseResponse;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverName;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverNumber;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverTruckId;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckDriverId;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleNumber;
import com.nlpl.model.UpdateUserDetails.UpdateUserAddress;
import com.nlpl.model.UpdateUserDetails.UpdateUserEmailId;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsDriverAdded;
import com.nlpl.model.UpdateUserDetails.UpdateUserName;
import com.nlpl.model.UpdateUserDetails.UpdateUserPhoneNumber;
import com.nlpl.model.UpdateUserDetails.UpdateUserPinCode;
import com.nlpl.model.UpdateUserDetails.UpdateUserPreferredLocation;
import com.nlpl.model.UpdateUserDetails.UpdateUserStateCode;
import com.nlpl.services.AddDriverService;
import com.nlpl.services.AddTruckService;
import com.nlpl.services.UserService;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.sax2.Driver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, actionBarSkipButton;
    ImageView actionBarMenuButton;

    EditText driverName, driverMobile, driverEmailId;
    ImageView actionBarBackButton;
    Intent data;
    int requestCode;
    int resultCode;
    String pathDLGallery, pathDLCamera, isDriverDetailsDoneAPI;
    RadioButton selfCheckBox;

    Button uploadDL, okDriverDetails, uploadSelfie;
    TextView textDL, editDL, series, textDS, editDS;
    int GET_FROM_GALLERY = 0;
    int GET_FROM_GALLERY1 = 1;
    //    int CAMERA_PIC_REQUEST = 1;
//    int CAMERA_PIC_REQUEST1 = 3;
    ImageView driverLicenseImage, driverSelfieImg;

    private RequestQueue mQueue;

    private UserService userService;
    private AddTruckService addTruckService;
    private AddDriverService addDriverService;

    String driverUserId, driverUserIdGet;

    String img_type, userId, driverId, driverNameAPI, driverNumberAPI, driverEmailAPI, mobile;
    Boolean isDLUploaded = false, isEdit;
    //    Boolean isSelfieUploaded = false;
    ImageView previewDrivingLicense, previewSelfie, previewDLImageView, previewSelfieImageView;
    Dialog previewDialogDL, previewDialogSelfie;
    View personalAndAddress;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    TextView selectStateText, selectDistrictText;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState;
    EditText pinCode, address;

    String userIdAPI, nameAPI, stateAPI, pinCodeAPI, addressAPI, mobileNoAPI, cityAPI, roleAPI;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayState;
    Boolean alreadyDriver = true;
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
            mobile = bundle.getString("mobile");
            truckIdPass = bundle.getString("truckIdPass");
        }

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        addDriverService = retrofit.create(AddDriverService.class);
        addTruckService = retrofit.create(AddTruckService.class);

        driverName.addTextChangedListener(driverNameWatcher);
        driverMobile.addTextChangedListener(driverMobileWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);

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

                if (email.matches(emailPattern) && s.length() > 0) {
                    String driverMobileText = driverMobile.getText().toString();
                    String driverNameText = driverName.getText().toString();
                    String driverEmailIdText = driverEmailId.getText().toString();

                    if (!driverNameText.isEmpty() && !driverMobileText.isEmpty() && !driverEmailIdText.isEmpty() && isDLUploaded) {
                        okDriverDetails.setEnabled(true);
                        okDriverDetails.setBackgroundResource(R.drawable.button_active);
                    } else {
//                        okDriverDetails.setEnabled(false);
                        okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
                    }
                    driverEmailId.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                } else {
//                    okDriverDetails.setEnabled(false);
                    okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
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
                if (isDriverDetailsDoneAPI.equals("1")){
                    Intent intent = new Intent(DriverDetailsActivity.this, ViewTruckDetailsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                    my_alert.setTitle("You can not bid unless you have a Driver");
                    my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent i8 = new Intent(DriverDetailsActivity.this, ViewTruckDetailsActivity.class);
                            i8.putExtra("mobile", mobile);
                            i8.putExtra("userId", userId);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            DriverDetailsActivity.this.finish();
                        }
                    });
                    my_alert.setNegativeButton("+ Add Truck Driver", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    my_alert.show();
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
        selfCheckBox = (RadioButton) findViewById(R.id.driver_details_self_checkbox);

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
        String emailWtacher = driverEmailId.getText().toString();

        if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded) {
            okDriverDetails.setEnabled(true);
            okDriverDetails.setBackgroundResource(R.drawable.button_active);
        }

        mQueue = Volley.newRequestQueue(DriverDetailsActivity.this);
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
            okDriverDetails.setEnabled(true);
            okDriverDetails.setBackgroundResource(R.drawable.button_active);
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

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);

//                requestPermissionsForCamera();
//                requestPermissionsForGalleryWRITE();
//                requestPermissionsForGalleryREAD();
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        editDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);

//                String selfiePath = selfieImagePickerWithoutAlert();
//                uploadDriverSelfie(driverId, selfiePath);
//
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        uploadDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissionsForCamera();
                requestPermissionsForGalleryWRITE();
                requestPermissionsForGalleryREAD();
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

//                camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
//                        chooseDialog.dismiss();
//                    }
//                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                        chooseDialog.dismiss();
                    }
                });
            }
        });

        editDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String DLpath = DLimagePickerWithoutAlert();
                uploadDriverLicense(driverId, DLpath);


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

//                camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
//                        chooseDialog.dismiss();
//                    }
//                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                        chooseDialog.dismiss();
                    }
                });
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
                selectStateDialog.setCancelable(false);
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

            if (pinCodeWatcher.length() == 6) {
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.resultCode = resultCode;
        this.requestCode = requestCode;
        this.data = data;
        DLimagePicker();
        selfieImagePicker();
        DLimagePickerWithoutAlert();
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
            String emailWtacher = driverEmailId.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded) {
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

            returnPath(picturePath);
            pathDLGallery = picturePath;

            driverLicenseImage.setImageURI(selectedImage);
            previewDrivingLicense.setImageURI(selectedImage);
            return picturePath;

//        } else if (requestCode == CAMERA_PIC_REQUEST1) {
//
//            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
//            uploadDL.setVisibility(View.INVISIBLE);
//            editDL.setVisibility(View.VISIBLE);
//            previewDLImageView.setVisibility(View.VISIBLE);
//            previewSelfieImageView.setVisibility(View.VISIBLE);
//
//            isDLUploaded = true;
//
//            String mobileNoWatcher = driverMobile.getText().toString();
//            String nameWatcher = driverName.getText().toString();
//            String emailWtacher = driverEmailId.getText().toString();
//
//            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded && isSelfieUploaded) {
//                okDriverDetails.setEnabled(true);
//                okDriverDetails.setBackgroundResource(R.drawable.button_active);
//            }
//
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            String path = getRealPathFromURI(getImageUri(this, image));
//            driverLicenseImage.setImageBitmap(BitmapFactory.decodeFile(path));
//            previewDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(path));
//
//            pathDLCamera = path;
//            return path;
        }
        return "";
    }

    private String selfieImagePickerWithoutAlert() {
        if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();
            String emailWtacher = driverEmailId.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded) {
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

            returnPath(picturePath);
            pathDLGallery = picturePath;

            driverLicenseImage.setImageURI(selectedImage);
            previewDrivingLicense.setImageURI(selectedImage);
            return picturePath;
        }

        //Detects request code for PAN
//       if (requestCode == CAMERA_PIC_REQUEST) {
//
//            isSelfieUploaded = true;
//
//            String mobileNoWatcher = driverMobile.getText().toString();
//            String nameWatcher = driverName.getText().toString();
//            String emailWatcher = driverEmailId.getText().toString();
//
//            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWatcher.isEmpty() && isDLUploaded && isSelfieUploaded) {
//                okDriverDetails.setEnabled(true);
//                okDriverDetails.setBackgroundResource(R.drawable.button_active);
//            }
//
//            textDS.setText("Selfie Uploaded");
//            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
//            uploadSelfie.setVisibility(View.INVISIBLE);
//            editDS.setVisibility(View.VISIBLE);
//
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            String path = getRealPathFromURI(getImageUri(this, image));
//            driverSelfieImg.setImageBitmap(BitmapFactory.decodeFile(path));
//            previewSelfie.setImageBitmap(BitmapFactory.decodeFile(path));
//
//            return path;
//
//        }
        return "";
    }

    private String DLimagePicker() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
            my_alert.setTitle("Driving License uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();
            String emailWtacher = driverEmailId.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded) {
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

            returnPath(picturePath);
            pathDLGallery = picturePath;

            driverLicenseImage.setImageURI(selectedImage);
            previewDrivingLicense.setImageURI(selectedImage);
            return picturePath;

//        } else if (requestCode == CAMERA_PIC_REQUEST1) {
//            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
//            my_alert.setTitle("Driving License uploaded successfully");
//            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            my_alert.show();
//
//            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
//            uploadDL.setVisibility(View.INVISIBLE);
//            editDL.setVisibility(View.VISIBLE);
//            previewDLImageView.setVisibility(View.VISIBLE);
//            previewSelfieImageView.setVisibility(View.VISIBLE);
//
//            isDLUploaded = true;
//
//            String mobileNoWatcher = driverMobile.getText().toString();
//            String nameWatcher = driverName.getText().toString();
//            String emailWtacher = driverEmailId.getText().toString();
//
//            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded && isSelfieUploaded) {
//                okDriverDetails.setEnabled(true);
//                okDriverDetails.setBackgroundResource(R.drawable.button_active);
//            }
//
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            String path = getRealPathFromURI(getImageUri(this, image));
//            driverLicenseImage.setImageBitmap(BitmapFactory.decodeFile(path));
//            previewDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(path));
//
//            pathDLCamera = path;
//            return path;
        }
        return "";
    }


    private String selfieImagePicker() {

        if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
            my_alert.setTitle("Driver Selfie uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);
            previewDLImageView.setVisibility(View.VISIBLE);
            previewSelfieImageView.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String mobileNoWatcher = driverMobile.getText().toString();
            String nameWatcher = driverName.getText().toString();
            String emailWtacher = driverEmailId.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded) {
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

            returnPath(picturePath);
            pathDLGallery = picturePath;

            driverLicenseImage.setImageURI(selectedImage);
            previewDrivingLicense.setImageURI(selectedImage);
            return picturePath;
        }

        //Detects request code for PAN
//        if (requestCode == CAMERA_PIC_REQUEST) {
//            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
//            my_alert.setTitle("Driver Selfie uploaded successfully");
//            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            my_alert.show();
//
//            isSelfieUploaded = true;
//
//            String mobileNoWatcher = driverMobile.getText().toString();
//            String nameWatcher = driverName.getText().toString();
//            String emailWtacher = driverEmailId.getText().toString();
//
//            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded && isSelfieUploaded) {
//                okDriverDetails.setEnabled(true);
//                okDriverDetails.setBackgroundResource(R.drawable.button_active);
//            }
//
//            textDS.setText("Selfie Uploaded");
//            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
//            uploadSelfie.setVisibility(View.INVISIBLE);
//            editDS.setVisibility(View.VISIBLE);
//
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            String path = getRealPathFromURI(getImageUri(this, image));
//            driverSelfieImg.setImageBitmap(BitmapFactory.decodeFile(path));
//            previewSelfie.setImageBitmap(BitmapFactory.decodeFile(path));
//
//            return path;
//
//        }
        return "";
    }

    public void onClickDriverDetailsOk(View view) {
        String driverMobileText = driverMobile.getText().toString();
        String driverNameText = driverName.getText().toString();
        String driverEmailIdText = driverEmailId.getText().toString();

        if (!driverNameText.isEmpty() && !driverMobileText.isEmpty() && !driverEmailIdText.isEmpty() && isDLUploaded) {
            if (driverMobileText.length() != 10) {
                AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                my_alert.setTitle("Invalid Mobile Number");
                my_alert.setMessage("Please enter a 10 digit valid mobile number.");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                my_alert.show();

            } else {

                updateUserIsDriverAdded();


                if (isEdit) {
                    String DLpath = DLimagePickerWithoutAlert();
                    uploadDriverLicense(driverId, DLpath);
                    String selfiePath = selfieImagePickerWithoutAlert();
                    uploadDriverSelfie(driverId, selfiePath);

                    if (driverName.getText().toString() != null) {
                        updateDriverName();
                        updateUserDriverName();
                    }
                    if (driverEmailId.getText().toString() != null) {
                        updateDriverEmailId();
                        updateUserDriverEmailId();
                    }

                    if (driverMobile.getText().toString() != null && !driverNumberAPI.equals("91" + driverMobile.getText().toString())) {
                        updateDriverNumber();
                        updateUserDriverPhoneNumber();
                    }

                    if (address.getText().toString() != null) {
                        updateUserDriverAddress();
                    }

                    if (pinCode.getText().toString() != null) {
                        updateUserDriverPinCode();
                    }

                    if (selectStateText.getText().toString() != null) {
                        updateUserDriverStateCode();
                    }

                    if (selectDistrictText.getText().toString() != null) {
                        updateUserDriverPreferredLocation();
                    }

                    Intent i8 = new Intent(DriverDetailsActivity.this, ViewTruckDetailsActivity.class);
                    i8.putExtra("userId", userId);
                    i8.putExtra("mobile", mobile);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    DriverDetailsActivity.this.finish();

                } else {

                    if (alreadyDriver) {
                        saveDriver(createDriver());
                    } else {
                        saveDriver(createDriver());
                        saveDriverUser(createDriverUser());
                    }

                    AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                    my_alert.setTitle("Driver Details added successfully");
                    my_alert.setMessage("Do you want to add Driver's Bank Details");
                    my_alert.setPositiveButton("May be Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            Intent i8 = new Intent(DriverDetailsActivity.this, ViewTruckDetailsActivity.class);
                            i8.putExtra("mobile", mobile);
                            i8.putExtra("userId", userId);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            DriverDetailsActivity.this.finish();
                        }
                    });
                    my_alert.setNegativeButton("Add Driver Bank Details", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            Intent intent2 = new Intent(DriverDetailsActivity.this, BankDetailsActivity.class);
                            if (alreadyDriver){
                                intent2.putExtra("userId", userId);
                            }else{
                                intent2.putExtra("userId", driverUserId);
                            }

                            intent2.putExtra("isEdit", false);
                            intent2.putExtra("mobile", mobile);

                            startActivity(intent2);
                        }
                    });
                    my_alert.show();
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
                String driverIdOnResponse = driverResponse.getData().getDriver_id();
                driverIdPass = driverResponse.getData().getDriver_id();
                updateTruckDriverId(driverIdPass);
                String DLpath = DLimagePickerWithoutAlert();
                String selfiePath = selfieImagePickerWithoutAlert();
                uploadDriverLicense(driverIdOnResponse, DLpath);
                uploadDriverSelfie(driverIdOnResponse, selfiePath);
            }

            @Override
            public void onFailure(Call<AddDriverResponse> call, Throwable t) {

            }
        });
    }

    private void updateTruckDriverId(String truckDriverId) {

        UpdateTruckDriverId updateTruckDriverId = new UpdateTruckDriverId(truckDriverId);

        Call<UpdateTruckDriverId> call = addTruckService.updateTruckDriverId("" + truckIdPass, updateTruckDriverId);

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
            String emailWtacher = driverEmailId.getText().toString();

            if (!nameWatcher.isEmpty() && !mobileNoWatcher.isEmpty() && !emailWtacher.isEmpty() && isDLUploaded) {
                okDriverDetails.setEnabled(true);
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            } else {
//                okDriverDetails.setEnabled(false);
                okDriverDetails.setBackgroundResource(R.drawable.button_de_active);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

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
            String emailWatcher = driverEmailId.getText().toString();

            if (mobileNoWatcher.length() == 10) {
                checkPhoneInAPI("91" + driverMobile.getText().toString());
                if (!nameWatcher.isEmpty() && !emailWatcher.isEmpty() && isDLUploaded) {
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
    private void updateUserIsDriverAdded() {

        UpdateUserIsDriverAdded updateUserIsDriverAdded = new UpdateUserIsDriverAdded("1");

        Call<UpdateUserIsDriverAdded> call = userService.updateUserIsDriverAdded("" + userId, updateUserIsDriverAdded);

        call.enqueue(new Callback<UpdateUserIsDriverAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsDriverAdded> call, Response<UpdateUserIsDriverAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsDriverAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverName() {

        UpdateDriverName updateDriverName = new UpdateDriverName(driverName.getText().toString());

        Call<UpdateDriverName> call = addDriverService.updateDriverName("" + driverId, updateDriverName);

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

        Call<UpdateDriverTruckId> call = addDriverService.updateDriverTruckId("" + driverId, updateDriverTruckId);

        call.enqueue(new Callback<UpdateDriverTruckId>() {
            @Override
            public void onResponse(Call<UpdateDriverTruckId> call, Response<UpdateDriverTruckId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
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

        Call<UpdateDriverNumber> call = addDriverService.updateDriverNumber("" + driverId, updateDriverNumber);

        call.enqueue(new Callback<UpdateDriverNumber>() {
            @Override
            public void onResponse(Call<UpdateDriverNumber> call, Response<UpdateDriverNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
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

        Call<UpdateDriverEmailId> call = addDriverService.updateDriverEmailId("" + driverId, updateDriverEmailId);

        call.enqueue(new Callback<UpdateDriverEmailId>() {
            @Override
            public void onResponse(Call<UpdateDriverEmailId> call, Response<UpdateDriverEmailId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
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

    private String returnPath(String picPath) {
        return picPath;
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

    //-------------------------------- Update User Name --------------------------------------------
    private void updateUserDriverName() {

        UpdateUserName updateUserName = new UpdateUserName(driverName.getText().toString());

        Call<UpdateUserName> call = userService.updateUserName("" + driverUserIdGet, updateUserName);

        call.enqueue(new Callback<UpdateUserName>() {
            @Override
            public void onResponse(Call<UpdateUserName> call, Response<UpdateUserName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "UserName");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserName> call, Throwable t) {
                Log.i("Not Successful", "UserName");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateUserDriverPhoneNumber() {

        UpdateUserPhoneNumber updateUserPhoneNumber = new UpdateUserPhoneNumber("91" + driverMobile.getText().toString());

        Call<UpdateUserPhoneNumber> call = userService.updateUserPhoneNumber("" + driverUserIdGet, updateUserPhoneNumber);

        call.enqueue(new Callback<UpdateUserPhoneNumber>() {
            @Override
            public void onResponse(Call<UpdateUserPhoneNumber> call, Response<UpdateUserPhoneNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "PhoneNumber");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPhoneNumber> call, Throwable t) {
                Log.i("Not Successful", "PhoneNumber");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateUserDriverEmailId() {

        UpdateUserEmailId updateUserEmailId = new UpdateUserEmailId(driverEmailId.getText().toString());

        Call<UpdateUserEmailId> call = userService.updateUserEmailId("" + driverUserIdGet, updateUserEmailId);

        call.enqueue(new Callback<UpdateUserEmailId>() {
            @Override
            public void onResponse(Call<UpdateUserEmailId> call, Response<UpdateUserEmailId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Email Id");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserEmailId> call, Throwable t) {
                Log.i("Not Successful", "User Email Id");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateUserDriverAddress() {

        UpdateUserAddress updateUserAddress = new UpdateUserAddress(address.getText().toString());

        Call<UpdateUserAddress> call = userService.updateUserAddress("" + driverUserIdGet, updateUserAddress);

        call.enqueue(new Callback<UpdateUserAddress>() {
            @Override
            public void onResponse(Call<UpdateUserAddress> call, Response<UpdateUserAddress> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Address");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserAddress> call, Throwable t) {
                Log.i("Not Successful", "UserAddress");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User Preferred Location ------------------------------
    private void updateUserDriverPreferredLocation() {

        UpdateUserPreferredLocation updateUserPreferredLocation = new UpdateUserPreferredLocation(selectDistrictText.getText().toString());

        Call<UpdateUserPreferredLocation> call = userService.updateUserPreferredLocation("" + driverUserIdGet, updateUserPreferredLocation);

        call.enqueue(new Callback<UpdateUserPreferredLocation>() {
            @Override
            public void onResponse(Call<UpdateUserPreferredLocation> call, Response<UpdateUserPreferredLocation> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Preferred Location");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPreferredLocation> call, Throwable t) {
                Log.i("Not Successful", "User Preferred Location");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User State Code --------------------------------------
    private void updateUserDriverStateCode() {

        UpdateUserStateCode updateUserStateCode = new UpdateUserStateCode(selectStateText.getText().toString());

        Call<UpdateUserStateCode> call = userService.updateUserStateCode("" + driverUserIdGet, updateUserStateCode);

        call.enqueue(new Callback<UpdateUserStateCode>() {
            @Override
            public void onResponse(Call<UpdateUserStateCode> call, Response<UpdateUserStateCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User State Code");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserStateCode> call, Throwable t) {
                Log.i("Not Successful", "User State Code");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User Pin Code ----------------------------------------
    private void updateUserDriverPinCode() {

        UpdateUserPinCode updateUserStateCode = new UpdateUserPinCode(pinCode.getText().toString());

        Call<UpdateUserPinCode> call = userService.updateUserPinCode("" + driverUserIdGet, updateUserStateCode);

        call.enqueue(new Callback<UpdateUserPinCode>() {
            @Override
            public void onResponse(Call<UpdateUserPinCode> call, Response<UpdateUserPinCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Pin Code");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPinCode> call, Throwable t) {
                Log.i("Not Successful", "User Pin Code");

            }
        });
//--------------------------------------------------------------------------------------------------
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
            selfCheckBox.setChecked(false);
            getUserDetails();
        } else if (!selfCheckBox.isChecked()) {
            selfCheckBox.setChecked(true);
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

                        driverName.setText(userNameAPI);
                        String s1 = userMobileAPI.substring(2, 12);
                        driverMobile.setText(s1);
                        address.setText(userAddressAPI);
                        selectStateText.setText(userStateAPI);
                        selectDistrictText.setText(userCityAPI);
                        pinCode.setText(userPinCodeAPI);
                        driverEmailId.setText(emailIdAPI);

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
}