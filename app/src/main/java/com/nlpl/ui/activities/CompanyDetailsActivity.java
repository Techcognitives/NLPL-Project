package com.nlpl.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.nlpl.R;
import com.nlpl.model.Requests.CompanyRequest;
import com.nlpl.model.Responses.CompanyResponse;
import com.nlpl.model.UpdateMethods.UpdateCompanyDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    String stateByPinCode, distByPinCode, selectedState;
    EditText companyName, pinCode, address, gstNumber, panNumber;
    Button okButton;
    TextView selectStateText, selectDistrictText;
    String companyType = "Proprietary", userId, mobile;

    RadioButton proprietaryRadioButton, partnershipRadioButton, pvtLtdRadioButton;
    Boolean isEdit;
    GetCurrentLocation getCurrentLocation;

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

        action_bar = findViewById(R.id.company_details_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText(getString(R.string.Company_Details));
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToViewPersonalDetailsActivity(CompanyDetailsActivity.this, userId, mobile, true);
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

        getCurrentLocation = new GetCurrentLocation();

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

        companyName.setOnClickListener(view -> companyName.setCursorVisible(true));

        pinCode.setOnClickListener(view -> pinCode.setCursorVisible(true));

        address.setOnClickListener(view -> address.setCursorVisible(true));

        selectStateText.setOnClickListener(view -> {
            companyName.setCursorVisible(false);
            pinCode.setCursorVisible(false);
            address.setCursorVisible(false);

            SelectState.selectState(CompanyDetailsActivity.this, selectStateText, selectDistrictText);
        });

        selectDistrictText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyName.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);
                if (!selectStateText.getText().toString().isEmpty()) {
                    selectedState = selectStateText.getText().toString();
                    SelectCity.selectCity(CompanyDetailsActivity.this, selectedState, selectDistrictText);
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
        ShowAlert.loadingDialog(CompanyDetailsActivity.this);
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
            JumpTo.goToViewPersonalDetailsActivity(CompanyDetailsActivity.this, userId, mobile, true);
        } else {
            saveCompany(createCompany());
            //Update User Company (isCompanyAdded)
            UpdateUserDetails.updateUserIsCompanyAdded(userId, "1");
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(CompanyDetailsActivity.this);
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

            alertTitle.setText(getString(R.string.Company_Details));
            alertMessage.setText(getString(R.string.Company_Details_added_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    JumpTo.goToViewPersonalDetailsActivity(CompanyDetailsActivity.this, userId, mobile, true);
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
                selectStateText.setEnabled(true);
                selectDistrictText.setEnabled(true);
            } else {
                String enteredPinCode = pinCode.getText().toString();
                getStateAndDistrict(enteredPinCode);
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                selectStateText.setEnabled(false);
                selectDistrictText.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private void getStateAndDistrict(String enteredPin) {

        Log.i("Entered PIN", enteredPin);

        String url = "http://13.234.163.179:3000/user/locationData/"+enteredPin;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject obj = response.getJSONObject("data");
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
        getCurrentLocation.getCurrentLocationMaps(CompanyDetailsActivity.this, address, pinCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        JumpTo.goToViewPersonalDetailsActivity(CompanyDetailsActivity.this, userId, mobile, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCurrentLocation.setAddressAndPin(CompanyDetailsActivity.this, data, address, pinCode);
    }
}