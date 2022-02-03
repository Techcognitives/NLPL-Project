package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
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
import com.nlpl.model.Requests.CompanyRequest;
import com.nlpl.model.Responses.CompanyResponse;
import com.nlpl.model.UpdateMethods.UpdateCompanyDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyAddress;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyCity;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyGSTNumber;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyName;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyPAN;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyState;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyType;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyZip;
import com.nlpl.model.UpdateModel.Models.UpdateUserDetails.UpdateUserIsCompanyAdded;
import com.nlpl.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;
    FusedLocationProviderClient fusedLocationProviderClient;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    Dialog selectStateDialog, selectDistrictDialog;
    String stateByPinCode, distByPinCode, selectedDistrict, selectedState;
    EditText companyName, pinCode, address, gstNumber, panNumber;
    Button okButton;
    TextView selectStateText, selectDistrictText;
    String companyType = "Proprietary", userId, mobile;

    RadioButton proprietaryRadioButton, partnershipRadioButton, pvtLtdRadioButton;
    Boolean isEdit;

    String companyNameAPI, companyAddressAPI, companyCityAPI, companyZipAPI, companyGSTNumberAPI, companyPanAPI, companyStateAPI, companyIdAPI, companyTypeAPI;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            mobile = bundle.getString("mobile");
            isEdit = bundle.getBoolean("isEdit");
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        action_bar = findViewById(R.id.company_details_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Company Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompanyDetailsActivity.this.finish();
            }
        });
//--------------------------------------------------------------------------------------------------

        companyName = (EditText) findViewById(R.id.company_details_company_name_edit);
        pinCode = (EditText) findViewById(R.id.company_details_pin_code_edit);
        address = (EditText) findViewById(R.id.company_details_address_edit);
        gstNumber = (EditText) findViewById(R.id.company_details_gst_number_edit);
        panNumber = (EditText) findViewById(R.id.company_details_pan_number);
        selectStateText = (TextView) findViewById(R.id.company_details_select_state);
        selectDistrictText = (TextView) findViewById(R.id.company_details_select_city);
        okButton = (Button) findViewById(R.id.company_details_ok_button);
        proprietaryRadioButton = (RadioButton) findViewById(R.id.company_details_proprietary_radio_button);
        partnershipRadioButton = (RadioButton) findViewById(R.id.company_details_partnership_radio_button);
        pvtLtdRadioButton = (RadioButton) findViewById(R.id.company_details_pvt_ltd_radio_button);

        companyName.addTextChangedListener(companyWatcher);
        gstNumber.addTextChangedListener(companyWatcher);
        panNumber.addTextChangedListener(companyWatcher);
        selectStateText.addTextChangedListener(companyWatcher);
        selectDistrictText.addTextChangedListener(companyWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(companyWatcher);

        companyName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        companyName.setFilters(new InputFilter[]{filter});
        address.setFilters(new InputFilter[]{filter});

        mQueue = Volley.newRequestQueue(CompanyDetailsActivity.this);
        if (isEdit) {
            getCompanyDetails();
        }
//
//        if (!name.getText().toString().isEmpty() && !selectStateText.getText().toString().isEmpty() && !selectDistrictText.getText().toString().isEmpty() && role != null){
//            okButton.setBackground(getDrawable(R.drawable.button_active));
//        }else if (name.getText().toString().isEmpty() || selectStateText.getText().toString().isEmpty() || selectDistrictText.getText().toString().isEmpty() || role == null) {
//            okButton.setBackground(getDrawable(R.drawable.button_de_active));
//        }

        companyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyName.setCursorVisible(true);
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
                companyName.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);
                selectStateDialog = new Dialog(CompanyDetailsActivity.this);
                selectStateDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectStateDialog.show();
                selectStateDialog.setCancelable(true);
                ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);

                selectStateArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
                selectStateUnionCode = ArrayAdapter.createFromResource(CompanyDetailsActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

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
                companyName.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);
                if (!selectStateText.getText().toString().isEmpty()) {
                    selectedState = selectStateText.getText().toString();
                    selectDistrictDialog = new Dialog(CompanyDetailsActivity.this);
                    selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                    selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    selectDistrictDialog.show();
                    TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
                    title.setText("Select City");
                    ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

                    if (selectedState.equals("AP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_assam_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("BR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_bihar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CG")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_goa_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_gujarat_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_haryana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_jharkhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_karnataka_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_kerala_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_maharashtra_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_manipur_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("ML")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_meghalaya_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MZ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_mizoram_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("NL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_nagaland_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("OD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_odisha_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_punjab_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("RJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_rajasthan_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("SK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_sikkim_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_telangana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_tripura_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("WB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_west_bengal_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CH/PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_chandigarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD2")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_daman_diu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_delhi_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                R.array.array_ladakh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PY")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
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

    private TextWatcher companyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nameWatcher = companyName.getText().toString().trim();
            String stateWatcher = selectStateText.getText().toString().trim();
            String cityWatcher = selectDistrictText.getText().toString().trim();
            String pinCodeWatcher = pinCode.getText().toString().trim();
            String addressWatcher = address.getText().toString().trim();
            String gstNumberWatcher = gstNumber.getText().toString().trim();
            String panNumberWatcher = panNumber.getText().toString().trim();

            if ( !nameWatcher.isEmpty() && !gstNumberWatcher.isEmpty() && !panNumberWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && pinCodeWatcher.length() == 6 && !stateWatcher.isEmpty() && !cityWatcher.isEmpty()) {
                okButton.setEnabled(true);
                okButton.setBackgroundResource((R.drawable.button_active));
            } else {
                okButton.setEnabled(false);
                okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
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

    public void onClickCompanyDetailsOK(View view) {
        if (isEdit) {

            if (companyName.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyName(companyIdAPI, companyName.getText().toString());
            }
            if (gstNumber.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyGstNumber(companyIdAPI, gstNumber.getText().toString());
            }
            if (panNumber.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyPanNumber(companyIdAPI, panNumber.getText().toString());
            }
            if (selectStateText.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyState(companyIdAPI, selectStateText.getText().toString());
            }
            if (address.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyAddress(companyIdAPI, address.getText().toString());
            }
            if (selectDistrictText.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyCity(companyIdAPI, selectDistrictText.getText().toString());
            }
            if (pinCode.getText().toString() != null) {
                UpdateCompanyDetails.updateCompanyPinCode(companyIdAPI, pinCode.getText().toString());
            }
            if (companyType != null) {
                UpdateCompanyDetails.updateCompanyType(companyIdAPI, companyType);
            }
            Intent i8 = new Intent(CompanyDetailsActivity.this, ViewPersonalDetailsActivity.class);
            i8.putExtra("mobile", mobile);
            i8.putExtra("userId", userId);
            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i8);
            finish();
            overridePendingTransition(0, 0);
        } else {
            saveCompany(createCompany());
            //Update User Company (isCompanyAdded)
            UpdateUserDetails.updateUserIsCompanyAdded(userId, "1");
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(CompanyDetailsActivity.this);
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

            alertTitle.setText("Company Details");
            alertMessage.setText("Company Details added Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    Intent i8 = new Intent(CompanyDetailsActivity.this, ViewPersonalDetailsActivity.class);
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

    public void onClickCompanyRadio(View view) {
        switch (view.getId()) {
            case R.id.company_details_proprietary_radio_button:
                proprietaryRadioButton.setChecked(true);
                partnershipRadioButton.setChecked(false);
                pvtLtdRadioButton.setChecked(false);

                String nameWatcher = companyName.getText().toString().trim();
                String stateWatcher = selectStateText.getText().toString().trim();
                String cityWatcher = selectDistrictText.getText().toString().trim();
                String pinCodeWatcher = pinCode.getText().toString().trim();
                String addressWatcher = address.getText().toString().trim();
                String gstNumberWatcher = gstNumber.getText().toString().trim();
                String panNumberWatcher = panNumber.getText().toString().trim();

                if ( !nameWatcher.isEmpty() && !gstNumberWatcher.isEmpty() && !panNumberWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && pinCodeWatcher.length() == 6 && !stateWatcher.isEmpty() && !cityWatcher.isEmpty()) {
                    okButton.setEnabled(true);
                    okButton.setBackgroundResource((R.drawable.button_active));
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                }

                companyType = "Proprietary";
                break;

            case R.id.company_details_partnership_radio_button:
                proprietaryRadioButton.setChecked(false);
                partnershipRadioButton.setChecked(true);
                pvtLtdRadioButton.setChecked(false);

                String nameWatcher1 = companyName.getText().toString().trim();
                String stateWatcher1 = selectStateText.getText().toString().trim();
                String cityWatcher1 = selectDistrictText.getText().toString().trim();
                String pinCodeWatcher1 = pinCode.getText().toString().trim();
                String addressWatcher1 = address.getText().toString().trim();
                String gstNumberWatcher1 = gstNumber.getText().toString().trim();
                String panNumberWatcher1 = panNumber.getText().toString().trim();

                if ( !nameWatcher1.isEmpty() && !gstNumberWatcher1.isEmpty() && !panNumberWatcher1.isEmpty() && !pinCodeWatcher1.isEmpty() && !addressWatcher1.isEmpty() && pinCodeWatcher1.length() == 6 && !stateWatcher1.isEmpty() && !cityWatcher1.isEmpty()) {
                    okButton.setEnabled(true);
                    okButton.setBackgroundResource((R.drawable.button_active));
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                }

                companyType = "Partnership";
                break;

            case R.id.company_details_pvt_ltd_radio_button:
                proprietaryRadioButton.setChecked(false);
                partnershipRadioButton.setChecked(false);
                pvtLtdRadioButton.setChecked(true);

                String nameWatcher2 = companyName.getText().toString().trim();
                String stateWatcher2 = selectStateText.getText().toString().trim();
                String cityWatcher2 = selectDistrictText.getText().toString().trim();
                String pinCodeWatcher2 = pinCode.getText().toString().trim();
                String addressWatcher2 = address.getText().toString().trim();
                String gstNumberWatcher2 = gstNumber.getText().toString().trim();
                String panNumberWatcher2 = panNumber.getText().toString().trim();

                if ( !nameWatcher2.isEmpty() && !gstNumberWatcher2.isEmpty() && !panNumberWatcher2.isEmpty() && !pinCodeWatcher2.isEmpty() && !addressWatcher2.isEmpty() && pinCodeWatcher2.length() == 6 && !stateWatcher2.isEmpty() && !cityWatcher2.isEmpty()) {
                    okButton.setEnabled(true);
                    okButton.setBackgroundResource((R.drawable.button_active));
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                }

                companyType = "Pvt. Ltd.";
                break;
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

        String url = "https://findyourtruck-393a4-default-rtdb.asia-southeast1.firebasedatabase.app/indianPinCodes.json?orderBy=%22pincode%22&equalTo=%22"+enteredPin+"%22";
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject obj = response.getJSONObject("81066");
                    stateByPinCode = obj.getString("stateCode");
                    distByPinCode = obj.getString("district");

                    Log.i("state By PIN Code", stateByPinCode);
                    Log.i("Dist By PIN Code", distByPinCode);

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

    //----------------------------------------------------------------------------------------------

    //--------------------------------------create User in API -------------------------------------
    public CompanyRequest createCompany() {
        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompany_name(companyName.getText().toString());
        companyRequest.setCompany_gst_no(gstNumber.getText().toString());
        companyRequest.setCompany_pan(panNumber.getText().toString());
        companyRequest.setComp_state(selectStateText.getText().toString());
        companyRequest.setComp_city(selectDistrictText.getText().toString());
        companyRequest.setCompany_type(companyType);
        companyRequest.setComp_zip(pinCode.getText().toString());
        companyRequest.setComp_add(address.getText().toString());
        companyRequest.setUser_id(userId);
        return companyRequest;
    }

    public void saveCompany(CompanyRequest companyRequest) {
        Call<CompanyResponse> companyResponseCall = ApiClient.getCompanyService().saveCompany(companyRequest);
        companyResponseCall.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                CompanyResponse companyResponse = response.body();
//                Log.i("Message UserCreated:", userResponse.getData().getPhone_number());
                Log.i("Company Msg Success", String.valueOf(companyResponse.getData().getCompany_id()));
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {

            }
        });
    }

    public void getCompanyDetails() {
        //---------------------------- Get Company Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/company/get/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray companyDetails = response.getJSONArray("data");
                    for (int i = 0; i < companyDetails.length(); i++) {
                        JSONObject data = companyDetails.getJSONObject(i);
                        companyNameAPI = data.getString("company_name");
                        companyGSTNumberAPI = data.getString("company_gst_no");
                        companyPanAPI = data.getString("company_pan");
                        companyStateAPI = data.getString("comp_state");
                        companyAddressAPI = data.getString("comp_add");
                        companyCityAPI = data.getString("comp_city");
                        companyZipAPI = data.getString("comp_zip");
                        companyIdAPI = data.getString("company_id");
                        Log.i("CompanyId Metho", companyIdAPI);
                        companyTypeAPI = data.getString("company_type");
                    }

                    Log.i("company Type", companyTypeAPI);

                    if (companyNameAPI != null) {
                        companyName.setText(companyNameAPI);
                    }
                    if (companyGSTNumberAPI != null) {
                        gstNumber.setText(companyGSTNumberAPI);
                    }
                    if (companyPanAPI != null) {
                        panNumber.setText(companyPanAPI);
                    }
                    if (companyStateAPI != null) {
                        selectStateText.setText(companyStateAPI);
                    }
                    if (companyAddressAPI != null) {
                        address.setText(companyAddressAPI);
                    }
                    if (companyCityAPI != null) {
                        selectDistrictText.setText(companyCityAPI);
                    }
                    if (companyZipAPI != null) {
                        pinCode.setText(companyZipAPI);
                    }

                    if (companyTypeAPI != null) {
                        companyType = companyTypeAPI;
                        if (companyTypeAPI.equals("Proprietary")) {
                            proprietaryRadioButton.setChecked(true);
                            partnershipRadioButton.setChecked(false);
                            pvtLtdRadioButton.setChecked(false);

                        }  if (companyTypeAPI.equals("Partnership")) {
                            proprietaryRadioButton.setChecked(false);
                            partnershipRadioButton.setChecked(true);
                            pvtLtdRadioButton.setChecked(false);

                        }  if (companyTypeAPI.equals("Pvt. Ltd.")) {
                            proprietaryRadioButton.setChecked(false);
                            partnershipRadioButton.setChecked(false);
                            pvtLtdRadioButton.setChecked(true);
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
        //-------------------------------------------------------------------------------------------
    }

    public void onClickGetCurrentCompanyLocation(View view) {
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(CompanyDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(CompanyDetailsActivity.this, Locale.getDefault());
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
            ActivityCompat.requestPermissions(CompanyDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i8 = new Intent(CompanyDetailsActivity.this, ViewPersonalDetailsActivity.class);
        i8.putExtra("userId", userId);
        i8.putExtra("mobile", mobile);
        startActivity(i8);
    }

}