package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nlpl.R;
import com.nlpl.model.Requests.CompanyRequest;
import com.nlpl.model.Responses.CompanyResponse;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyAddress;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyCity;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyGSTNumber;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyName;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyPAN;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyState;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyType;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyZip;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsCompanyAdded;
import com.nlpl.services.CompanyService;
import com.nlpl.services.UserService;
import com.nlpl.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanyDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState;
    EditText companyName, pinCode, address, gstNumber, panNumber;
    Button okButton;
    TextView selectStateText, selectDistrictText;
    String companyType = "Proprietary", userId, mobile;

    private static String BASE_URL = "http://13.234.163.179:3000";
    private CompanyService companyService;
    private UserService userService;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        companyService = retrofit.create(CompanyService.class);
        userService = retrofit.create(UserService.class);

        action_bar = findViewById(R.id.company_details_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Company Details");
        actionBarBackButton.setVisibility(View.GONE);
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

        mQueue = Volley.newRequestQueue(CompanyDetailsActivity.this);
        if (isEdit) {
            getCompanyDetails();

            if (companyName.getText().toString() != null) {
                updateCompanyName();
            }
            if (gstNumber.getText().toString() != null) {
                updateCompanyGstNumber();
            }
            if (panNumber.getText().toString() != null) {
                updateCompanyPanNumber();
            }
            if (selectStateText.getText().toString() != null) {
                updateCompanyState();
            }
            if (address.getText().toString() != null) {
                updateCompanyAddress();
            }
            if (selectDistrictText.getText().toString() != null) {
                updateCompanyCity();
            }
            if (pinCode.getText().toString() != null) {
                updateCompanyZip();
            }
            if(companyType != null){
                updateCompanyType();
            }
        }

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
                selectStateDialog.setCancelable(false);
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

            if (!nameWatcher.isEmpty() && !gstNumberWatcher.isEmpty() && !panNumberWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && pinCodeWatcher.length() == 6 && !stateWatcher.isEmpty() && !cityWatcher.isEmpty()) {
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
        saveCompany(createCompany());
        updateUserIsCompanyAdded();
        AlertDialog.Builder my_alert = new AlertDialog.Builder(CompanyDetailsActivity.this);
        my_alert.setTitle("Company Details added Successfully");
        my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent i8 = new Intent(CompanyDetailsActivity.this, ProfileAndRegistrationActivity.class);
                i8.putExtra("mobile2", mobile);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        my_alert.show();


    }

    public void onClickCompanyRadio(View view) {
        switch (view.getId()) {
            case R.id.company_details_proprietary_radio_button:
                proprietaryRadioButton.setChecked(true);
                partnershipRadioButton.setChecked(false);
                pvtLtdRadioButton.setChecked(false);

                companyType = "Proprietary";
                break;

            case R.id.company_details_partnership_radio_button:
                proprietaryRadioButton.setChecked(false);
                partnershipRadioButton.setChecked(true);
                pvtLtdRadioButton.setChecked(false);

                companyType = "Partnership";
                break;

            case R.id.company_details_pvt_ltd_radio_button:
                proprietaryRadioButton.setChecked(false);
                partnershipRadioButton.setChecked(false);
                pvtLtdRadioButton.setChecked(true);

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
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            } else {
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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
    //----------------------------------------------------------------------------------------------

    //-------------------------------------- Update Type -----------------------------------------------
    private void updateCompanyName() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyName updateCompanyName = new UpdateCompanyName(companyName.getText().toString());

        Call<UpdateCompanyName> call = companyService.updateCompanyName(""+companyIdAPI, updateCompanyName);

        call.enqueue(new Callback<UpdateCompanyName>() {
            @Override
            public void onResponse(Call<UpdateCompanyName> call, retrofit2.Response<UpdateCompanyName> response) {
                if (response.isSuccessful()) {
                    UpdateCompanyName updateCompanyName1 = response.body();
                    Log.i("Updated", String.valueOf(updateCompanyName1));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyName> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateCompanyGstNumber() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyGSTNumber updateCompanyGSTNumber = new UpdateCompanyGSTNumber(gstNumber.getText().toString());

        Call<UpdateCompanyGSTNumber> call = companyService.updateCompanyGSTNumber(""+companyIdAPI, updateCompanyGSTNumber);

        call.enqueue(new Callback<UpdateCompanyGSTNumber>() {
            @Override
            public void onResponse(Call<UpdateCompanyGSTNumber> call, retrofit2.Response<UpdateCompanyGSTNumber> response) {
                if (response.isSuccessful()) {
                    UpdateCompanyGSTNumber updateCompanyGSTNumber1 = response.body();
                    Log.i("Updated", String.valueOf(updateCompanyGSTNumber1));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyGSTNumber> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateCompanyPanNumber() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyPAN updateCompanyPAN = new UpdateCompanyPAN(panNumber.getText().toString());

        Call<UpdateCompanyPAN> call = companyService.updateCompanyPAN(""+companyIdAPI, updateCompanyPAN);

        call.enqueue(new Callback<UpdateCompanyPAN>() {
            @Override
            public void onResponse(Call<UpdateCompanyPAN> call, retrofit2.Response<UpdateCompanyPAN> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyPAN> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateCompanyState() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyState updateCompanyState = new UpdateCompanyState(selectStateText.getText().toString());

        Call<UpdateCompanyState> call = companyService.updateCompanyState(""+companyIdAPI, updateCompanyState);

        call.enqueue(new Callback<UpdateCompanyState>() {
            @Override
            public void onResponse(Call<UpdateCompanyState> call, retrofit2.Response<UpdateCompanyState> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyState> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateCompanyCity() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyCity updateCompanyCity = new UpdateCompanyCity(selectDistrictText.getText().toString());

        Call<UpdateCompanyCity> call = companyService.updateCompanyCity(""+companyIdAPI, updateCompanyCity);

        call.enqueue(new Callback<UpdateCompanyCity>() {
            @Override
            public void onResponse(Call<UpdateCompanyCity> call, retrofit2.Response<UpdateCompanyCity> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyCity> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateCompanyZip() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyZip updateCompanyZip = new UpdateCompanyZip(pinCode.getText().toString());

        Call<UpdateCompanyZip> call = companyService.updateCompanyZip(""+companyIdAPI, updateCompanyZip);

        call.enqueue(new Callback<UpdateCompanyZip>() {
            @Override
            public void onResponse(Call<UpdateCompanyZip> call, retrofit2.Response<UpdateCompanyZip> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyZip> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateCompanyAddress() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyAddress updateCompanyAddress = new UpdateCompanyAddress(address.getText().toString());

        Call<UpdateCompanyAddress> call = companyService.updateCompanyAddress(""+companyIdAPI, updateCompanyAddress);

        call.enqueue(new Callback<UpdateCompanyAddress>() {
            @Override
            public void onResponse(Call<UpdateCompanyAddress> call, retrofit2.Response<UpdateCompanyAddress> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyAddress> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }
    private void updateCompanyType() {

//------------------------------------- Update Type ------------------------------------------------
        UpdateCompanyType updateCompanyType = new UpdateCompanyType(companyType);

        Call<UpdateCompanyType> call = companyService.updateCompanyType(""+companyIdAPI, updateCompanyType);

        call.enqueue(new Callback<UpdateCompanyType>() {
            @Override
            public void onResponse(Call<UpdateCompanyType> call, retrofit2.Response<UpdateCompanyType> response) {
                if (response.isSuccessful()) {
                    Log.i("Updated", String.valueOf(response.body()));
                } else {
                    Log.i("Not Successful", "Company Details Update");
                }
            }

            @Override
            public void onFailure(Call<UpdateCompanyType> call, Throwable t) {
                Log.i("Not Successful", "Company Details Update");
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Company Added -------------------------------
    private void updateUserIsCompanyAdded() {

        UpdateUserIsCompanyAdded updateUserIsCompanyAdded = new UpdateUserIsCompanyAdded("1");

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
                        companyTypeAPI = data.getString("company_type");
                    }

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
                        companyTypeAPI = companyType;
                        if (companyTypeAPI.equals("Proprietary")) {
                            proprietaryRadioButton.setChecked(true);
                            partnershipRadioButton.setChecked(false);
                            pvtLtdRadioButton.setChecked(false);

                        } else if (companyTypeAPI.equals("Partnership")) {
                            proprietaryRadioButton.setChecked(false);
                            partnershipRadioButton.setChecked(true);
                            pvtLtdRadioButton.setChecked(false);

                        } else if (companyTypeAPI.equals("Pvt. Ltd.")) {
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
}