package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateUserDetails.UpdateUserAddress;
import com.nlpl.model.UpdateUserDetails.UpdateUserEmailId;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsBankDetailsGiven;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsCompanyAdded;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsDriverAdded;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsPersonalDetailsAdded;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsRegistrationDone;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsTruckAdded;
import com.nlpl.model.UpdateUserDetails.UpdateUserName;
import com.nlpl.model.UpdateUserDetails.UpdateUserPhoneNumber;
import com.nlpl.model.UpdateUserDetails.UpdateUserPinCode;
import com.nlpl.model.UpdateUserDetails.UpdateUserPreferredLanguage;
import com.nlpl.model.UpdateUserDetails.UpdateUserPreferredLocation;
import com.nlpl.model.UpdateUserDetails.UpdateUserStateCode;
import com.nlpl.model.UpdateUserDetails.UpdateUserType;
import com.nlpl.services.UserService;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonalDetailsAndIdProofActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    View personalAndAddressView;
    Button personalAddressButton;
    View personalView;
    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    TextView selectStateText, selectDistrictText, series;
    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState, role, img_type;

    EditText name, pinCode, address, mobileEdit, emailIdEdit;
    Button okButton;
    //----------------------------------------------------------------------------------------------
    View panAndAadharView;
    Button panAndAadharButton;
    View panView;

    Button uploadPAN, uploadF;
    Dialog previewDialogPan, previewDialogAadhar;
    Boolean isPanAdded = false, isAadharAdded = false, noChange = true;

    TextView panCardText, editPAN, editFront, frontText, backText;
    ImageView imgPAN, imgF, previewPan, previewAadhar;

    String nameAPI, mobileAPI, addressAPI, pinCodeAPI, roleAPI, cityAPI, stateAPI, emailAPI;

    private int GET_FROM_GALLERY = 0;
    private int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST1 = 5;
    int CAMERA_PIC_REQUEST2 = 15;

    private UserService userService;

    private RequestQueue mQueue;
    String userId, mobileString, panImageURL, aadharImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details_and_id_proof);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            Log.i("Mobile No", userId);
            mobileString = bundle.getString("mobile");
        }

        action_bar = findViewById(R.id.personal_details_id_proof_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Personal Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalDetailsAndIdProofActivity.this.finish();
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);

//        name.requestFocus();

        getWindow().

                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        name.setFilters(new InputFilter[]

                {
                        filter
                });

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

        mQueue = Volley.newRequestQueue(PersonalDetailsAndIdProofActivity.this);

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

        okButton.setEnabled(true);
        okButton.setBackground(

                getDrawable(R.drawable.button_active));

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
                selectStateDialog = new Dialog(PersonalDetailsAndIdProofActivity.this);
                selectStateDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectStateDialog.show();
                selectStateDialog.setCancelable(false);
                ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);

                selectStateArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
                selectStateUnionCode = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

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
                name.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);

                if (!selectStateText.getText().toString().isEmpty()) {

                    selectedState = selectStateText.getText().toString();
                    selectDistrictDialog = new Dialog(PersonalDetailsAndIdProofActivity.this);
                    selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                    selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    selectDistrictDialog.show();
                    TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
                    title.setText("Select City");
                    ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

                    if (selectedState.equals("AP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_assam_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("BR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_bihar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CG")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_goa_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_gujarat_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_haryana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_jharkhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_karnataka_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_kerala_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_maharashtra_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_manipur_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("ML")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_meghalaya_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MZ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_mizoram_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("NL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_nagaland_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("OD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_odisha_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_punjab_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("RJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_rajasthan_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("SK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_sikkim_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_telangana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_tripura_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("WB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_west_bengal_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CH/PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_chandigarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD2")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_daman_diu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_delhi_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                R.array.array_ladakh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PY")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
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
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        panCardText = panAndAadharView.findViewById(R.id.pancard1);
        frontText = panAndAadharView.findViewById(R.id.frontText);
        backText = panAndAadharView.findViewById(R.id.profile_registration_name_text);
        uploadPAN = panAndAadharView.findViewById(R.id.uploadPan);
        uploadF = panAndAadharView.findViewById(R.id.uploadF);
        imgPAN = panAndAadharView.findViewById(R.id.imagePan);
        imgF = panAndAadharView.findViewById(R.id.imageF);
        editPAN = panAndAadharView.findViewById(R.id.edit1);
        editFront = panAndAadharView.findViewById(R.id.editFront);

        uploadPAN.setVisibility(View.INVISIBLE);
        editPAN.setVisibility(View.VISIBLE);
        previewPan.setVisibility(View.VISIBLE);
        previewAadhar.setVisibility(View.VISIBLE);
        uploadF.setVisibility(View.INVISIBLE);
        editFront.setVisibility(View.VISIBLE);

        frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
        panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);


        editPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_type = "pan";
                saveImage(imageRequest());
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
        });

        editFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_type = "aadhar";
                saveImage(imageRequest());
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

                if (!nameWatcher.isEmpty() && !stateWatcher.isEmpty() && !cityWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && !mobileWatcher.isEmpty() && !emailIdWatcher.isEmpty()) {
                    okButton.setEnabled(true);
                    okButton.setBackground(getDrawable(R.drawable.button_active));
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getDrawable(R.drawable.button_de_active));
                }

                personalAddressButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                personalView.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                personalAndAddressView.setVisibility(View.VISIBLE);

                panAndAadharButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                panView.setBackgroundColor(getResources().getColor(R.color.medium_blue));
                panAndAadharView.setVisibility(View.GONE);
                break;

            case R.id.personal_details_id_proof_pan_aadhar:

                okButton.setEnabled(true);
                okButton.setBackground(getDrawable(R.drawable.button_active));

                panAndAadharButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                panView.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                panAndAadharView.setVisibility(View.VISIBLE);

                personalAddressButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                personalView.setBackgroundColor(getResources().getColor(R.color.medium_blue));
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
            updateUserName();
        }

        if (emailIdEdit.getText().toString() != null) {
            updateUserEmailId();
        }

        if (address.getText().toString() != null) {
            updateUserAddress();
        }

        if (pinCode.getText().toString() != null) {
            updateUserPinCode();
        }

        if (selectStateText.getText().toString() != null) {
            updateUserStateCode();
        }

        if (selectDistrictText.getText().toString() != null) {
            updateUserPreferredLocation();
        }

        if (role != null) {
            updateUserType();
        }

       if (mobileString.equals("91"+mobileEdit.getText().toString()) || mobileEdit.getText().toString().isEmpty()) {

                    Intent i8 = new Intent(PersonalDetailsAndIdProofActivity.this, ProfileAndRegistrationActivity.class);
                    i8.putExtra("mobile2", mobileAPI);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    PersonalDetailsAndIdProofActivity.this.finish();

        } else {
            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Do you really want to update your phone number?");
            my_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
                    my_alert.setTitle("OTP is sent to " + "+91" + mobileEdit.getText().toString());
                    my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent i8 = new Intent(PersonalDetailsAndIdProofActivity.this, OtpCodeActivity.class);
                            i8.putExtra("mobile", "+91" + mobileEdit.getText().toString());
                            i8.putExtra("isEditPhone", true);
                            i8.putExtra("userId", userId);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            PersonalDetailsAndIdProofActivity.this.finish();
                        }
                    });
                    my_alert.show();
                }
            });
            my_alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
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

    private TextWatcher pinCodeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String pinCodeWatcher = pinCode.getText().toString().trim();

            if (pinCodeWatcher.length() == 6) {
                okButton.setEnabled(true);
                okButton.setBackground(getDrawable(R.drawable.button_active));
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                okButton.setEnabled(false);
                okButton.setBackground(getDrawable(R.drawable.button_de_active));
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
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
    //----------------------------------------------------------------------------------------------

    //-------------------------------upload Image---------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("PAN Card Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isPanAdded = true;
                    noChange = false;
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            previewPan.setVisibility(View.VISIBLE);
            previewAadhar.setVisibility(View.VISIBLE);

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

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Aadhar Card Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isAadharAdded = true;
                    noChange = false;
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);

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
            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("PAN Card Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isPanAdded = true;
                    noChange = false;
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            previewPan.setVisibility(View.VISIBLE);
            previewAadhar.setVisibility(View.VISIBLE);

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
            editedPan.setImageBitmap(BitmapFactory.decodeFile(path));
            imgPAN.setImageBitmap(BitmapFactory.decodeFile(path));
            uploadImage(path);

        } else if (requestCode == CAMERA_PIC_REQUEST2) {
            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Aadhar Card Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isAadharAdded = true;
                    noChange = false;
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);

            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
            editedAadhar.setImageBitmap(image);
            String path = getRealPathFromURI(getImageUri(this, image));
            imgF.setImageBitmap(BitmapFactory.decodeFile(path));
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
                        Log.i("EmailId", emailAPI);

                        role = roleAPI;
                        name.setText(nameAPI);

                        String s1 = mobileAPI.substring(2, 12);
                        mobileEdit.setText(s1);

                        if (emailAPI != null) {
                            emailIdEdit.setText(emailAPI);
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

    //-------------------------------- Update User Name --------------------------------------------
    private void updateUserName() {

        UpdateUserName updateUserName = new UpdateUserName(name.getText().toString());

        Call<UpdateUserName> call = userService.updateUserName("" + userId, updateUserName);

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

    //-------------------------------- Update User Phone Number ------------------------------------
    private void updateUserPhoneNumber() {

        UpdateUserPhoneNumber updateUserPhoneNumber = new UpdateUserPhoneNumber("91" + mobileEdit.getText().toString());

        Call<UpdateUserPhoneNumber> call = userService.updateUserPhoneNumber("" + userId, updateUserPhoneNumber);

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

    //-------------------------------- Update User Type --------------------------------------------
    private void updateUserType() {

        UpdateUserType updateUserType = new UpdateUserType(role);

        Call<UpdateUserType> call = userService.updateUserType("" + userId, updateUserType);

        call.enqueue(new Callback<UpdateUserType>() {
            @Override
            public void onResponse(Call<UpdateUserType> call, Response<UpdateUserType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "UserType");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserType> call, Throwable t) {
                Log.i("Not Successful", "UserType");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User Preferred Language ------------------------------
    private void updateUserPreferredLanguage() {

        UpdateUserPreferredLanguage updateUserPreferredLanguage = new UpdateUserPreferredLanguage(mobileEdit.getText().toString());

        Call<UpdateUserPreferredLanguage> call = userService.updateUserPreferredLanguage("" + userId, updateUserPreferredLanguage);

        call.enqueue(new Callback<UpdateUserPreferredLanguage>() {
            @Override
            public void onResponse(Call<UpdateUserPreferredLanguage> call, Response<UpdateUserPreferredLanguage> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User Preferred Language");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPreferredLanguage> call, Throwable t) {
                Log.i("Not Successful", "User Preferred Language");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //---------------------------- Update User is Registration Done --------------------------------
    private void updateUserIsRegistrationDone() {

        UpdateUserIsRegistrationDone updateUserIsRegistrationDone = new UpdateUserIsRegistrationDone(mobileEdit.getText().toString());

        Call<UpdateUserIsRegistrationDone> call = userService.updateUserIsRegistrationDone("" + userId, updateUserIsRegistrationDone);

        call.enqueue(new Callback<UpdateUserIsRegistrationDone>() {
            @Override
            public void onResponse(Call<UpdateUserIsRegistrationDone> call, Response<UpdateUserIsRegistrationDone> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Registration Done");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsRegistrationDone> call, Throwable t) {
                Log.i("Not Successful", "User is Registration Done");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //------------------------------------ Update User Address -------------------------------------
    private void updateUserAddress() {

        UpdateUserAddress updateUserAddress = new UpdateUserAddress(address.getText().toString());

        Call<UpdateUserAddress> call = userService.updateUserAddress("" + userId, updateUserAddress);

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
    private void updateUserPreferredLocation() {

        UpdateUserPreferredLocation updateUserPreferredLocation = new UpdateUserPreferredLocation(selectDistrictText.getText().toString());

        Call<UpdateUserPreferredLocation> call = userService.updateUserPreferredLocation("" + userId, updateUserPreferredLocation);

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
    private void updateUserStateCode() {

        UpdateUserStateCode updateUserStateCode = new UpdateUserStateCode(selectStateText.getText().toString());

        Call<UpdateUserStateCode> call = userService.updateUserStateCode("" + userId, updateUserStateCode);

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
    private void updateUserPinCode() {

        UpdateUserPinCode updateUserStateCode = new UpdateUserPinCode(pinCode.getText().toString());

        Call<UpdateUserPinCode> call = userService.updateUserPinCode("" + userId, updateUserStateCode);

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

    //-------------------------------- Update User is Truck Added ----------------------------------
    private void updateUserIsTruckAdded() {

        UpdateUserIsTruckAdded updateUserIsTruckAdded = new UpdateUserIsTruckAdded(mobileEdit.getText().toString());

        Call<UpdateUserIsTruckAdded> call = userService.updateUserIsTruckAdded("" + userId, updateUserIsTruckAdded);

        call.enqueue(new Callback<UpdateUserIsTruckAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsTruckAdded> call, Response<UpdateUserIsTruckAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsTruckAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateUserIsDriverAdded() {

        UpdateUserIsDriverAdded updateUserIsDriverAdded = new UpdateUserIsDriverAdded(mobileEdit.getText().toString());

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

    //-------------------------------- Update User is Bank Added -----------------------------------
    private void updateUserIsBankDetailsGiven() {

        UpdateUserIsBankDetailsGiven updateUserIsDriverAdded = new UpdateUserIsBankDetailsGiven(mobileEdit.getText().toString());

        Call<UpdateUserIsBankDetailsGiven> call = userService.updateUserIsBankDetailsGiven("" + userId, updateUserIsDriverAdded);

        call.enqueue(new Callback<UpdateUserIsBankDetailsGiven>() {
            @Override
            public void onResponse(Call<UpdateUserIsBankDetailsGiven> call, Response<UpdateUserIsBankDetailsGiven> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsBankDetailsGiven> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Company Added -------------------------------
    private void updateUserIsCompanyAdded() {

        UpdateUserIsCompanyAdded updateUserIsCompanyAdded = new UpdateUserIsCompanyAdded(mobileEdit.getText().toString());

        Call<UpdateUserIsCompanyAdded> call = userService.updateUserIsCompanyAdded("" + userId, updateUserIsCompanyAdded);

        call.enqueue(new Callback<UpdateUserIsCompanyAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsCompanyAdded> call, Response<UpdateUserIsCompanyAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Company Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsCompanyAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Company Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Personal Details -----------------------------
    private void updateUserIsPersonalDetailsAdded() {

        UpdateUserIsPersonalDetailsAdded updateUserIsPersonalDetailsAdded = new UpdateUserIsPersonalDetailsAdded(mobileEdit.getText().toString());

        Call<UpdateUserIsPersonalDetailsAdded> call = userService.updateUserIsPersonalDetailsAdded("" + userId, updateUserIsPersonalDetailsAdded);

        call.enqueue(new Callback<UpdateUserIsPersonalDetailsAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsPersonalDetailsAdded> call, Response<UpdateUserIsPersonalDetailsAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Personal Details");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsPersonalDetailsAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Personal Details");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User Email Id ----------------------------------------
    private void updateUserEmailId() {

        UpdateUserEmailId updateUserEmailId = new UpdateUserEmailId(emailIdEdit.getText().toString());

        Call<UpdateUserEmailId> call = userService.updateUserEmailId("" + userId, updateUserEmailId);

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

    private void getImageURL() {

        String url = getString(R.string.baseURL) + "/imgbucket/Images/4";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");

                        if (imageType.equals("adhar")) {
                            aadharImageURL = obj.getString("image_url");
                            new DownloadImageTask(imgF).execute(aadharImageURL);

                            if (isAadharAdded == false) {
                                new DownloadImageTask((ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view)).execute(aadharImageURL);
                            }

                            Log.i("IMAGE AADHAR URL", aadharImageURL);
                        }

                        if (imageType.equals("pan")) {
                            panImageURL = obj.getString("image_url");
                            new DownloadImageTask(imgPAN).execute(panImageURL);
                            if (isPanAdded == false) {
                                new DownloadImageTask((ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view)).execute(panImageURL);
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
}