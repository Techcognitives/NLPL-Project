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
import com.nlpl.R;
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalDetailsAndIdProofActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    View personalAndAddressView;
    Button personalAddressButton;
    View personalView;
    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    TextView selectStateText, selectDistrictText, series;
    String isPersonalDetailsDone, stateByPinCode, distByPinCode, selectedDistrict, selectedState, role, img_type, alternateMobileNumberAPI;

    EditText name, pinCode, address, mobileEdit, emailIdEdit, alternateMobileNumber;
    Button okButton;
    //----------------------------------------------------------------------------------------------
    View panAndAadharView;
    Button panAndAadharButton;
    View panView;

    Button uploadPAN, uploadF, uploadProfile;
    Dialog previewDialogPan, previewDialogAadhar, previewDialogProfile;
    Boolean isPanAdded = false, isAadharAdded = false, noChange = true, isProfileAdded = false;

    TextView panCardText, editPAN, editFront, frontText, backText, profileText, editProfile, setCurrentLocation;
    ImageView imgPAN, imgF, previewPan, previewAadhar , imgProfile, previewProfile;

    ConstraintLayout aadharConstrain, panConstrain;
    TextView uploadAadharTitle, uploadPanTitle;
    String nameAPI, mobileAPI, addressAPI, pinCodeAPI, roleAPI, cityAPI, stateAPI, emailAPI;

    GetCurrentLocation getCurrentLocation;
    private int GET_FROM_GALLERY = 0;
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
        personalAddressButton = (Button) findViewById(R.id.personal_details_id_proof_personal_address_button);
        personalView = (View) findViewById(R.id.personal_details_id_proof_personal_view);
        //------------------------------------------------------------------------------------------
        panAndAadharView = (View) findViewById(R.id.personal_details_id_proof_pan_and_aadhar_layout);
        panAndAadharButton = (Button) findViewById(R.id.personal_details_id_proof_pan_aadhar);
        panView = (View) findViewById(R.id.personal_details_id_proof_pan_view);
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
        previewPan = (ImageView) panAndAadharView.findViewById(R.id.pan_aadhar_preview_pan);
        previewAadhar = (ImageView) panAndAadharView.findViewById(R.id.pan_aadhar_preview_aadhar);
        setCurrentLocation = (TextView) personalAndAddressView.findViewById(R.id.personal_and_address_get_current_location);
        alternateMobileNumber = (EditText) personalAndAddressView.findViewById(R.id.registration_mobile_no_edit_alternate);

        aadharConstrain = panAndAadharView.findViewById(R.id.aadhar_constrain);
        panConstrain = panAndAadharView.findViewById(R.id.pan_card_constrain);
        uploadPanTitle = panAndAadharView.findViewById(R.id.upload_pan_text);
        uploadAadharTitle = panAndAadharView.findViewById(R.id.upload_aadhar_text);

        aadharConstrain.setVisibility(View.VISIBLE);
        panConstrain.setVisibility(View.VISIBLE);
        uploadAadharTitle.setVisibility(View.VISIBLE);
        uploadPanTitle.setVisibility(View.VISIBLE);

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

//        if (!name.getText().toString().isEmpty() && !selectStateText.getText().toString().isEmpty() && !selectDistrictText.getText().toString().isEmpty() && role != null){
//            okButton.setBackground(getDrawable(R.drawable.button_active));
//        }else if (name.getText().toString().isEmpty() || selectStateText.getText().toString().isEmpty() || selectDistrictText.getText().toString().isEmpty() || role == null) {
//            okButton.setBackground(getDrawable(R.drawable.button_de_active));
//        }

        previewDialogPan = new Dialog(PersonalDetailsAndIdProofActivity.this);
        previewDialogPan.setContentView(R.layout.dialog_preview_images);
        previewDialogPan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        previewDialogAadhar = new Dialog(PersonalDetailsAndIdProofActivity.this);
        previewDialogAadhar.setContentView(R.layout.dialog_preview_images);
        previewDialogAadhar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogProfile = new Dialog(PersonalDetailsAndIdProofActivity.this);
        previewDialogProfile.setContentView(R.layout.dialog_preview_images);
        previewDialogProfile.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));


        mQueue = Volley.newRequestQueue(PersonalDetailsAndIdProofActivity.this);
        getCurrentLocation = new GetCurrentLocation();


        //------------------------------------------------------------------------------------------
        panCardText = panAndAadharView.findViewById(R.id.pancard1);
        frontText = panAndAadharView.findViewById(R.id.frontText);
        backText = panAndAadharView.findViewById(R.id.menu_name_text);
        uploadPAN = panAndAadharView.findViewById(R.id.uploadPan);
        uploadF = panAndAadharView.findViewById(R.id.uploadF);
        imgPAN = panAndAadharView.findViewById(R.id.imagePan);
        imgF = panAndAadharView.findViewById(R.id.imageF);
        editPAN = panAndAadharView.findViewById(R.id.edit1);
        editFront = panAndAadharView.findViewById(R.id.editFront);
        previewProfile = (ImageView) panAndAadharView.findViewById(R.id.preview_profile);
        editProfile = panAndAadharView.findViewById(R.id.editProfile);
        uploadProfile = panAndAadharView.findViewById(R.id.uploadProfile);
        profileText = panAndAadharView.findViewById(R.id.ProfileText);
        imgProfile = panAndAadharView.findViewById(R.id.imageProfile);

        getImageURL();
        getUserDetails();

        previewPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(previewDialogPan.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                previewDialogPan.show();
                previewDialogPan.getWindow().setAttributes(lp);
            }
        });

        previewAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(previewDialogAadhar.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.gravity = Gravity.CENTER;

                previewDialogAadhar.show();
                previewDialogAadhar.getWindow().setAttributes(lp2);
            }
        });

        previewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(previewDialogProfile.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.gravity = Gravity.CENTER;

                previewDialogProfile.show();
                previewDialogProfile.getWindow().setAttributes(lp2);
            }
        });

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

        uploadPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPanDialogChoose();
            }
        });

        uploadF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAadharDialogChoose();
            }
        });

        editPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             uploadPanDialogChoose();
            }
        });

        editFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAadharDialogChoose();
            }
        });

        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileDialogChoose();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileDialogChoose();
            }
        });

        //------------------------------------------------------------------------------------------
    }

    public void onClickPersonalOrAadhar(View view) {
        switch (view.getId()) {
            case R.id.personal_details_id_proof_personal_address_button:

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
                personalView.setVisibility(View.VISIBLE);
                panView.setVisibility(View.INVISIBLE);

                if (!nameWatcher.isEmpty() && !stateWatcher.isEmpty() && !cityWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && !mobileWatcher.isEmpty() && !emailIdWatcher.isEmpty()) {
                    okButton.setEnabled(true);
                    okButton.setBackground(getDrawable(R.drawable.button_active));
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getDrawable(R.drawable.button_de_active));
                }

                personalAddressButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                personalAndAddressView.setVisibility(View.VISIBLE);

                panAndAadharButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                panAndAadharView.setVisibility(View.GONE);
                break;

            case R.id.personal_details_id_proof_pan_aadhar:

                okButton.setEnabled(true);
                okButton.setBackground(getDrawable(R.drawable.button_active));
                personalView.setVisibility(View.INVISIBLE);
                panView.setVisibility(View.VISIBLE);

                panAndAadharButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                panAndAadharView.setVisibility(View.VISIBLE);

                personalAddressButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                personalAndAddressView.setVisibility(View.GONE);
                break;
        }
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
        }

        if (pinCode.getText().toString() != null) {
            UpdateUserDetails.updateUserPinCode(userId, pinCode.getText().toString());
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

        getCurrentLocation.setAddressAndPin(PersonalDetailsAndIdProofActivity.this, data, address, pinCode);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            saveImage(imageRequest());
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Personal_Details));
            alertMessage.setText(getString(R.string.PAN_Card_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    noChange = false;
                }
            });
            //------------------------------------------------------------------------------------------

            isPanAdded = true;

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            previewPan.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
            editedPan.setImageURI(selectedImage);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadImage(picturePath);

            imgPAN.setImageURI(selectedImage);

        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            saveImage(imageRequest());
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Personal_Details));
            alertMessage.setText(getString(R.string.Aadhar_Card_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    noChange = false;
                }
            });
            //------------------------------------------------------------------------------------------

            isAadharAdded = true;

            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            previewAadhar.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
            editedAadhar.setImageURI(selectedImage);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadImage(picturePath);

            imgF.setImageURI(selectedImage);

        } else if (requestCode == CAMERA_PIC_REQUEST1) {
            //----------------------- Alert Dialog -------------------------------------------------
            saveImage(imageRequest());
            isPanAdded = true;
            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            previewPan.setVisibility(View.VISIBLE);

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
                editedPan.setImageBitmap(BitmapFactory.decodeFile(path));
                imgPAN.setImageBitmap(BitmapFactory.decodeFile(path));
                uploadImage(path);

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
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.PAN_Card_Uploaded_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        noChange = false;
                    }
                });
                //------------------------------------------------------------------------------------------
            }catch (Exception e){
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
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.PAN_Card_not_Uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        noChange = false;
                    }
                });
                //------------------------------------------------------------------------------------------
            }


        } else if (requestCode == CAMERA_PIC_REQUEST2) {
            //----------------------- Alert Dialog -------------------------------------------------

            isAadharAdded = true;

            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            previewAadhar.setVisibility(View.VISIBLE);

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
                editedAadhar.setImageBitmap(image);
                String path = getRealPathFromURI(getImageUri(this, image));
                imgF.setImageBitmap(BitmapFactory.decodeFile(path));
                uploadImage(path);

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
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Aadhar_Card_Uploaded_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        noChange = false;
                        saveImage(imageRequest());
                    }
                });
                //------------------------------------------------------------------------------------------
            }catch (Exception e){
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
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Aadhar_Card_not_Uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        noChange = false;
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        } else if (requestCode == GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            saveImage(imageRequest());
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Personal_Details));
            alertMessage.setText(getString(R.string.Profile_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    noChange = false;
                }
            });
            //------------------------------------------------------------------------------------------
            isPanAdded = true;
            profileText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadProfile.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            previewProfile.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
            editedPan.setImageURI(selectedImage);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadImage(picturePath);

            imgProfile.setImageURI(selectedImage);

        } else if (requestCode == CAMERA_PIC_REQUEST3) {
            //----------------------- Alert Dialog -------------------------------------------------
            saveImage(imageRequest());
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Personal_Details));
            alertMessage.setText(getString(R.string.Profile_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    noChange = false;
                }
            });
            //------------------------------------------------------------------------------------------

            isProfileAdded = true;
            profileText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadProfile.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            previewProfile.setVisibility(View.VISIBLE);

            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
            editedAadhar.setImageBitmap(image);
            String path = getRealPathFromURI(getImageUri(this, image));
            imgProfile.setImageBitmap(BitmapFactory.decodeFile(path));
            uploadImage(path);
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

                        if (isPersonalDetailsDone.equals("1")) {
                            panAndAadharButton.setEnabled(true);
                            uploadProfile.setVisibility(View.INVISIBLE);
                            uploadPAN.setVisibility(View.INVISIBLE);
                            editPAN.setVisibility(View.VISIBLE);
                            editProfile.setVisibility(View.VISIBLE);
                            previewPan.setVisibility(View.VISIBLE);
                            previewAadhar.setVisibility(View.VISIBLE);
                            previewProfile.setVisibility(View.VISIBLE);
                            uploadF.setVisibility(View.INVISIBLE);
                            editFront.setVisibility(View.VISIBLE);
                            profileText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                        } else {
                            panAndAadharButton.setEnabled(false);
                        }


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
                            new DownloadImageTask(imgF).execute(aadharImageURL);
                            if (isAadharAdded == false) {
                                new DownloadImageTask((ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view)).execute(aadharImageURL);
                            }
                        }

                        if (imageType.equals("pan")) {
                            panImageURL = obj.getString("image_url");
                            new DownloadImageTask(imgPAN).execute(panImageURL);
                            if (isPanAdded == false) {
                                new DownloadImageTask((ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view)).execute(panImageURL);
                            }
                        }

                        if (imageType.equals("profile")) {
                            profileImgUrl = obj.getString("image_url");
                            new DownloadImageTask(imgProfile).execute(profileImgUrl);
                            if (isProfileAdded == false) {
                                new DownloadImageTask((ImageView) previewDialogProfile.findViewById(R.id.dialog_preview_image_view)).execute(profileImgUrl);
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
        getCurrentLocation.getCurrentLocationMaps(PersonalDetailsAndIdProofActivity.this, address, pinCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(PersonalDetailsAndIdProofActivity.this);
        JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsAndIdProofActivity.this, userId, mobileAPI, false);
    }

    private void uploadPanDialogChoose(){

        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "pan";
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

    private void uploadAadharDialogChoose() {
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "aadhar";
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
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST2);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
                chooseDialog.dismiss();
            }
        });
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

}