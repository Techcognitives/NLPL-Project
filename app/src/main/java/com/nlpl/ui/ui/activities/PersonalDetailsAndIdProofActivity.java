package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.nlpl.model.UserUpdate;
import com.nlpl.services.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonalDetailsAndIdProofActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    View personalAndAddressView;
    Button personalAddressButton;
    View personalView;
    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    TextView selectStateText, selectDistrictText, series;
    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState, role;
    int parentID;
    EditText name, pinCode, address, mobileEdit;
    Button okButton;
    //--------------------------------------------------------------------------------------------------
    View panAndAadharView;
    Button panAndAadharButton;
    View panView;

    Button uploadPAN, uploadF, uploadB;

    TextView panCardText, editPAN, editBack, editFront, frontText, backText;
    ImageView imgPAN, imgF, imgB;

    String nameAPI, mobileAPI, addressAPI, pinCodeAPI, roleAPI, cityAPI, stateAPI;

    private int GET_FROM_GALLERY = 0;
    private int GET_FROM_GALLERY1 = 1;
    private int GET_FROM_GALLERY2 = 2;

    private static String BASE_URL = "http://65.2.3.41:8080";
    private UserService userService;

    private RequestQueue mQueue;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details_and_id_proof);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            Log.i("Mobile No", userId);
        }

        action_bar = findViewById(R.id.personal_details_id_proof_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setText(getString(R.string.english));
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(PersonalDetailsAndIdProofActivity.this);
                languageDialog.setContentView(R.layout.dialog_language);
                languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(languageDialog.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp2.gravity = Gravity.BOTTOM;

                languageDialog.show();
                languageDialog.getWindow().setAttributes(lp2);

                TextView english = languageDialog.findViewById(R.id.english);
                TextView marathi = languageDialog.findViewById(R.id.marathi);
                TextView hindi = languageDialog.findViewById(R.id.hindi);

                english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.english));
                    }
                });

                marathi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.marathi));
                    }
                });

                hindi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.hindi));
                    }
                });

            }
        });

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
        pinCode = (EditText) personalAndAddressView.findViewById(R.id.registration_pin_code_edit);
        address = (EditText) personalAndAddressView.findViewById(R.id.registration_address_edit);
        mobileEdit = (EditText) personalAndAddressView.findViewById(R.id.registration_mobile_no_edit);
        series = (TextView) personalAndAddressView.findViewById(R.id.registration_prefix);
        selectStateText = (TextView) personalAndAddressView.findViewById(R.id.registration_select_state);
        selectDistrictText = (TextView) personalAndAddressView.findViewById(R.id.registration_select_city);
        okButton = (Button) findViewById(R.id.personal_details_id_proof_ok_button);

        name.addTextChangedListener(proofAndPersonalWatcher);
        selectStateText.addTextChangedListener(proofAndPersonalWatcher);
        selectDistrictText.addTextChangedListener(proofAndPersonalWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(proofAndPersonalWatcher);
        mobileEdit.addTextChangedListener(mobileNumberTextWatcher);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);

        name.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        name.setFilters(new InputFilter[]{filter});

//        if (!name.getText().toString().isEmpty() && !selectStateText.getText().toString().isEmpty() && !selectDistrictText.getText().toString().isEmpty() && role != null){
//            okButton.setBackground(getDrawable(R.drawable.button_active));
//        }else if (name.getText().toString().isEmpty() || selectStateText.getText().toString().isEmpty() || selectDistrictText.getText().toString().isEmpty() || role == null) {
//            okButton.setBackground(getDrawable(R.drawable.button_de_active));
//        }

        mQueue = Volley.newRequestQueue(PersonalDetailsAndIdProofActivity.this);
        getUserDetails();

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

                        parentID = parent.getId();
                        Log.i("ID", String.valueOf(parentID));

                        selectedState = selectStateArray.getItem(i).toString();
                        selectDistrictDialog = new Dialog(PersonalDetailsAndIdProofActivity.this);
                        selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                        selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        selectDistrictDialog.show();
                        ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

                        if (parentID == R.id.list_state) {
                            switch (selectedState) {
                                case "Andhra Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Arunachal Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Assam":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_assam_districts, R.layout.custom_list_row);
                                    break;
                                case "Bihar":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_bihar_districts, R.layout.custom_list_row);
                                    break;
                                case "Chhattisgarh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                                    break;
                                case "Goa":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_goa_districts, R.layout.custom_list_row);
                                    break;
                                case "Gujarat":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_gujarat_districts, R.layout.custom_list_row);
                                    break;
                                case "Haryana":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_haryana_districts, R.layout.custom_list_row);
                                    break;
                                case "Himachal Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Jharkhand":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_jharkhand_districts, R.layout.custom_list_row);
                                    break;
                                case "Karnataka":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_karnataka_districts, R.layout.custom_list_row);
                                    break;
                                case "Kerala":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_kerala_districts, R.layout.custom_list_row);
                                    break;
                                case "Madhya Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Maharashtra":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_maharashtra_districts, R.layout.custom_list_row);
                                    break;
                                case "Manipur":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_manipur_districts, R.layout.custom_list_row);
                                    break;
                                case "Meghalaya":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_meghalaya_districts, R.layout.custom_list_row);
                                    break;
                                case "Mizoram":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_mizoram_districts, R.layout.custom_list_row);
                                    break;
                                case "Nagaland":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_nagaland_districts, R.layout.custom_list_row);
                                    break;
                                case "Odisha":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_odisha_districts, R.layout.custom_list_row);
                                    break;
                                case "Punjab":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_punjab_districts, R.layout.custom_list_row);
                                    break;
                                case "Rajasthan":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_rajasthan_districts, R.layout.custom_list_row);
                                    break;
                                case "Sikkim":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_sikkim_districts, R.layout.custom_list_row);
                                    break;
                                case "Tamil Nadu":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                                    break;
                                case "Telangana":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_telangana_districts, R.layout.custom_list_row);
                                    break;
                                case "Tripura":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_tripura_districts, R.layout.custom_list_row);
                                    break;
                                case "Uttar Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Uttarakhand":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                                    break;
                                case "West Bengal":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_west_bengal_districts, R.layout.custom_list_row);
                                    break;
                                case "Andaman and Nicobar Islands":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                                    break;
                                case "Chandigarh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_chandigarh_districts, R.layout.custom_list_row);
                                    break;
                                case "Dadra and Nagar Haveli":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                                    break;
                                case "Daman and Diu":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_daman_diu_districts, R.layout.custom_list_row);
                                    break;
                                case "Delhi":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_delhi_districts, R.layout.custom_list_row);
                                    break;
                                case "Jammu and Kashmir":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                                    break;
                                case "Lakshadweep":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                                    break;
                                case "Ladakh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_ladakh_districts, R.layout.custom_list_row);
                                    break;
                                case "Puducherry":
                                    selectDistrictArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this,
                                            R.array.array_puducherry_districts, R.layout.custom_list_row);
                                    break;
                                default:
                                    break;
                            }
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
                    selectDistrictDialog.show();
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
        uploadB = panAndAadharView.findViewById(R.id.uploadB);
        imgPAN = panAndAadharView.findViewById(R.id.imagePan);
        imgF = panAndAadharView.findViewById(R.id.imageF);
        imgB = panAndAadharView.findViewById(R.id.imageB);
        editPAN = panAndAadharView.findViewById(R.id.edit1);
        editFront = panAndAadharView.findViewById(R.id.editFront);
        editBack = panAndAadharView.findViewById(R.id.editBack);

//        if (isPersonalDetailsDone){
//            panCardText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
//            uploadPAN.setVisibility(View.INVISIBLE);
//            editPAN.setVisibility(View.VISIBLE);
//
//            frontText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
//            uploadF.setVisibility(View.INVISIBLE);
//            editFront.setVisibility(View.VISIBLE);
//
//            backText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
//            uploadB.setVisibility(View.INVISIBLE);
//            editBack.setVisibility(View.VISIBLE);
//        }

        editPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });

        editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
            }
        });
        //------------------------------------------------------------------------------------------
    }

    public void onClickPersonalOrAadhar(View view) {
        switch (view.getId()) {
            case R.id.personal_details_id_proof_personal_address_button:
                personalAddressButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                personalView.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                personalAndAddressView.setVisibility(View.VISIBLE);

                panAndAadharButton.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                panView.setBackgroundColor(getResources().getColor(R.color.medium_blue));
                panAndAadharView.setVisibility(View.GONE);
                break;

            case R.id.personal_details_id_proof_pan_aadhar:
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
        updateUserDetails();

        AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
        my_alert.setTitle("Details updated Successfully");
        my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent i8 = new Intent(PersonalDetailsAndIdProofActivity.this, ProfileAndRegistrationActivity.class);
                i8.putExtra("userId", userId);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
                PersonalDetailsAndIdProofActivity.this.finish();
            }
        });
        my_alert.show();

//            RegistrationActivity.this.finish();
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
            boolean owner = ownerButton.isChecked();
            boolean driver = driverButton.isChecked();
            boolean broker = brokerButton.isChecked();
            boolean customer = customerButton.isChecked();
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

            if (pinCodeWatcher.length() != 6) {
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            } else {
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
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
                mobileEdit.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            } else {
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
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            imgPAN.setImageURI(selectedImage);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            imgF.setImageURI(selectedImage);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (requestCode == GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            backText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadB.setVisibility(View.INVISIBLE);
            editBack.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            imgB.setImageURI(selectedImage);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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

                        name.setText(nameAPI);

                        String s1 = mobileAPI.substring(2, 12);
                        mobileEdit.setText(s1);

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

    //-------------------------------- Update User Details -----------------------------------------
    private void updateUserDetails() {

//------------------------------------- Update Type ------------------------------------------------
        UserUpdate userUpdate = new UserUpdate("" + userId, "Abhi Gotad","918796543114", "", "paid", null, null, null, 1, null, null, null, 0, 1, 0, 1, 0, "abhijeetgotad@gmail.com");

        Call<UserUpdate> call = userService.updateUserDetails("" + userId, userUpdate);

        call.enqueue(new Callback<UserUpdate>() {
            @Override
            public void onResponse(Call<UserUpdate> call, retrofit2.Response<UserUpdate> response) {

            }

            @Override
            public void onFailure(Call<UserUpdate> call, Throwable t) {

            }
        });
//--------------------------------------------------------------------------------------------------
    }

}