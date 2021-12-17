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

import com.nlpl.R;

import java.io.FileNotFoundException;
import java.io.IOException;

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
    String mobile;
    EditText name, pinCode, address, mobileEdit;
    Button okButton;
//--------------------------------------------------------------------------------------------------
    View panAndAadharView;
    Button panAndAadharButton;
    View panView;

    TextView panCardText, editPAN, editBack, editFront, frontText, backText;
    Button uploadPAN, uploadF, uploadB;
    ImageView imgPAN, imgF, imgB;
    RadioButton radioAadhar, radioVoter;
    private int GET_FROM_GALLERY=0;
    private int GET_FROM_GALLERY1=1;
    private int GET_FROM_GALLERY2=2;

    String vehicleNo, city, idProof, bankName, accNo;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, isPanUploaded=false, isFrontUploaded=false, isBackUploaded=false;
    String personName, addressBundle, pinCodeBundle, bankName2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details_and_id_proof);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile3");
            personName = bundle.getString("name3");
            addressBundle = bundle.getString("address");
            pinCodeBundle = bundle.getString("pinCode");
            city = bundle.getString("city");
            bankName2 = bundle.getString("bankName");
            accNo = bundle.getString("accNo");
            vehicleNo = bundle.getString("vehicleNo");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            Log.i("Mobile No", mobile);
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
        actionBarBackButton.setVisibility(View.GONE);

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

        String s = mobile.substring(3,13);

        name.setText(personName);
        pinCode.setText(pinCodeBundle);
        address.setText(addressBundle);
        mobileEdit.setText(s);

        name.addTextChangedListener(proofAndPersonalWatcher);
        selectStateText.addTextChangedListener(proofAndPersonalWatcher);
        selectDistrictText.addTextChangedListener(proofAndPersonalWatcher);
        pinCode.addTextChangedListener(proofAndPersonalWatcher);
        address.addTextChangedListener(proofAndPersonalWatcher);
        mobileEdit.addTextChangedListener(proofAndPersonalWatcher);

        name.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        name.setFilters(new InputFilter[] { filter });

//        if (!name.getText().toString().isEmpty() && !selectStateText.getText().toString().isEmpty() && !selectDistrictText.getText().toString().isEmpty() && role != null){
//            okButton.setBackground(getDrawable(R.drawable.button_active));
//        }else if (name.getText().toString().isEmpty() || selectStateText.getText().toString().isEmpty() || selectDistrictText.getText().toString().isEmpty() || role == null) {
//            okButton.setBackground(getDrawable(R.drawable.button_de_active));
//        }

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
                selectStateDialog.setContentView(R.layout.dialog_select_state);
//                dialog.getWindow().setLayout(1000,3000);
                selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectStateDialog.show();
                selectStateDialog.setCancelable(false);
                ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);
                EditText searchState = (EditText) selectStateDialog.findViewById(R.id.search_state);

                selectStateArray = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
                selectStateUnionCode = ArrayAdapter.createFromResource(PersonalDetailsAndIdProofActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

                stateList.setAdapter(selectStateArray);

                searchState.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        selectStateArray.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                        selectStateText.setText(selectStateUnionCode.getItem(i)); //Set Selected Credentials
                        selectStateDialog.dismiss();

                        parentID = parent.getId();
                        Log.i("ID", String.valueOf(parentID));

                        selectedState = selectStateArray.getItem(i).toString();
                        selectDistrictDialog = new Dialog(PersonalDetailsAndIdProofActivity.this);
                        selectDistrictDialog.setContentView(R.layout.dialog_select_state);
//                dialog.getWindow().setLayout(1000,3000);
                        selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        selectDistrictDialog.show();
                        ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);
                        EditText searchDistrict = (EditText) selectDistrictDialog.findViewById(R.id.search_state);

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

                        searchDistrict.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                selectDistrictArray.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
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
        radioAadhar = panAndAadharView.findViewById(R.id.radioAadhar);
        radioVoter = panAndAadharView.findViewById(R.id.radioVoter);

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

        uploadPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        uploadF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });

        editFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });

        uploadB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
            }
        });
        editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
            }
        });

        radioVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioAadhar.setChecked(false);
                radioVoter.setChecked(true);
                frontText.setText("Voter ID Front");
                backText.setText("Voter ID Back");
                imgF.setImageDrawable(getDrawable(R.drawable.voter_id_front));
                imgB.setImageDrawable(getDrawable(R.drawable.voter_id_back));
                idProof = "voter";
            }
        });

        radioAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioAadhar.setChecked(true);
                radioVoter.setChecked(false);
                frontText.setText("Aadhar Front");
                backText.setText("Aadhar Back");
                imgF.setImageDrawable(getDrawable(R.drawable.aadhar_card_front));
                imgB.setImageDrawable(getDrawable(R.drawable.aadhar_card_back));
                idProof = "aadhar";
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

        String nameWatcher = name.getText().toString().trim();
        String stateWatcher = selectStateText.getText().toString().trim();
        String cityWatcher = selectDistrictText.getText().toString().trim();
        String pinCodeWatcher = pinCode.getText().toString().trim();
        String addressWatcher = address.getText().toString().trim();

        //--------------------------------------------------------------------------------------
        if (!nameWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && pinCodeWatcher.length()==6 && !stateWatcher.isEmpty() && !cityWatcher.isEmpty()) {
            okButton.setEnabled(true);
            okButton.setBackgroundResource((R.drawable.button_active));
        } else {
            okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
        }
        //--------------------------------------------------------------------------------------

        switch (view.getId()) {
            case R.id.registration_truck_owner:
                ownerButton.setChecked(true);
                driverButton.setChecked(false);
                brokerButton.setChecked(false);
                customerButton.setChecked(false);
                role = "Truck Owner";

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
                role = "Owner";

                break;
        }
    }

    public void onClickPersonalProof(View view) {

        String nameWatcher = name.getText().toString().trim();
        String stateWatcher = selectStateText.getText().toString().trim();
        String cityWatcher = selectDistrictText.getText().toString().trim();
        String pinCodeWatcher = pinCode.getText().toString().trim();
        String addressWatcher = address.getText().toString().trim();
        boolean owner = ownerButton.isChecked();
        boolean driver = driverButton.isChecked();
        boolean broker = brokerButton.isChecked();
        boolean customer = customerButton.isChecked();

        if (!nameWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && !stateWatcher.isEmpty() && pinCodeWatcher.length()==6 && !cityWatcher.isEmpty() && (owner || driver || broker || customer)) {
            okButton.setEnabled(true);
            okButton.setBackground(getResources().getDrawable(R.drawable.button_active));

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Registration Successful");
            my_alert.setMessage("Welcome to Find YourTruck\n\nPlease update your profile and explore the platform benefits.");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent i8 = new Intent(PersonalDetailsAndIdProofActivity.this, ProfileAndRegistrationActivity.class);
                    String mobileInt = "+91"+mobileEdit.getText().toString();
                    i8.putExtra("mobile2", mobileInt);
                    i8.putExtra("name2", name.getText().toString());
                    i8.putExtra("address", address.getText().toString());
                    i8.putExtra("pinCode", pinCode.getText().toString());
                    i8.putExtra("city", selectStateText.getText().toString());
                    i8.putExtra("bankName", bankName);
                    i8.putExtra("accNo", accNo);
                    i8.putExtra("vehicleNo", vehicleNo);
                    i8.putExtra("isPersonal", true);
                    i8.putExtra("isBank", isBankDetailsDone);
                    i8.putExtra("isTrucks", isAddTrucksDone);
                    i8.putExtra("isDriver",isAddDriversDone);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    PersonalDetailsAndIdProofActivity.this.finish();
                }
            });
            my_alert.show();

        }
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

            if (mobileWatcher.length()==10){
                mobileEdit.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            }else{
                mobileEdit.setBackground(getResources().getDrawable(R.drawable.mobile_number_right_red));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left_red));
            }

            if (pinCodeWatcher.length() != 6){
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }else{
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }

            if (!nameWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && !stateWatcher.isEmpty() && pinCodeWatcher.length()==6 && !cityWatcher.isEmpty() && (owner || driver || broker || customer)) {
                okButton.setEnabled(true);
                okButton.setBackgroundResource((R.drawable.button_active));
            } else {
                okButton.setEnabled(false);
                okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private String blockCharacterSet ="~#^|$%&*!+@₹_-()':;?/={}";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    //----------------------------------------------------------------------------------------------

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("PAN Card Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            isPanUploaded = true;

            if (isPanUploaded && isFrontUploaded && isBackUploaded){
                okButton.setBackgroundResource(R.drawable.button_active);
            }

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
        }else if (requestCode==GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK){

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            frontText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            isFrontUploaded = true;

            if (isPanUploaded && isFrontUploaded && isBackUploaded){
                okButton.setBackgroundResource(R.drawable.button_active);
            }

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
        }else if (requestCode==GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK){

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsAndIdProofActivity.this);
            my_alert.setTitle("Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            backText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadB.setVisibility(View.INVISIBLE);
            editBack.setVisibility(View.VISIBLE);
            isBackUploaded = true;

            if (isPanUploaded && isFrontUploaded && isBackUploaded){
                okButton.setBackgroundResource(R.drawable.button_active);
            }

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

}