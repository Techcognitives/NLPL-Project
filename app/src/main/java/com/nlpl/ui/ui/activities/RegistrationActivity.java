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

import com.nlpl.R;
import com.nlpl.model.Requests.UserRequest;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    RadioButton ownerButton, driverButton, brokerButton, customerButton;
    View action_bar;
    TextView actionBarTitle, selectStateText, selectDistrictText;
    ImageView actionBarBackButton;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState, role;
    String mobile;

    EditText name, pinCode, address, mobileNoEdit, email_id;
    TextView series;
    Button okButton;
    View personalAndAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile1");
            Log.i("Mobile No Registration", mobile);
        }

        action_bar = (View) findViewById(R.id.registration_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Registration");
        actionBarBackButton.setVisibility(View.GONE);
        //------------------------------------------------------------------------------------------

        personalAndAddress = (View) findViewById(R.id.registration_personal_and_address);
        name = (EditText) personalAndAddress.findViewById(R.id.registration_edit_name);
        pinCode = (EditText) personalAndAddress.findViewById(R.id.registration_pin_code_edit);
        address = (EditText) personalAndAddress.findViewById(R.id.registration_address_edit);
        mobileNoEdit = (EditText) personalAndAddress.findViewById(R.id.registration_mobile_no_edit);
        series = (TextView) personalAndAddress.findViewById(R.id.registration_prefix);
        selectStateText = (TextView) personalAndAddress.findViewById(R.id.registration_select_state);
        selectDistrictText = (TextView) personalAndAddress.findViewById(R.id.registration_select_city);
        okButton = (Button) findViewById(R.id.registration_ok);
        email_id = findViewById(R.id.registration_email_id_edit);

        name.addTextChangedListener(registrationWatcher);
        selectStateText.addTextChangedListener(registrationWatcher);
        selectDistrictText.addTextChangedListener(registrationWatcher);
        pinCode.addTextChangedListener(pinCodeWatcher);
        address.addTextChangedListener(registrationWatcher);
        email_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String email = email_id.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.matches(emailPattern) && s.length() > 0) {
                    email_id.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                } else {
                    email_id.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                }

            }
        });

        mobileNoEdit.setVisibility(View.GONE);
        series.setVisibility(View.GONE);

        name.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        name.setFilters(new InputFilter[] { filter });

//        if (!name.getText().toString().isEmpty() && !selectStateText.getText().toString().isEmpty() && !selectDistrictText.getText().toString().isEmpty() && role != null){
//            okButton.setBackground(getDrawable(R.drawable.button_active));
//        }else if (name.getText().toString().isEmpty() || selectStateText.getText().toString().isEmpty() || selectDistrictText.getText().toString().isEmpty() || role == null) {
//            okButton.setBackground(getDrawable(R.drawable.button_de_active));
//        }

        ownerButton = (RadioButton) personalAndAddress.findViewById(R.id.registration_truck_owner);
        driverButton = (RadioButton) personalAndAddress.findViewById(R.id.registration_driver);
        brokerButton = (RadioButton) personalAndAddress.findViewById(R.id.registration_broker);
        customerButton = (RadioButton) personalAndAddress.findViewById(R.id.registration_customer);

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
                selectStateDialog = new Dialog(RegistrationActivity.this);
                selectStateDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectStateDialog.show();
                selectStateDialog.setCancelable(false);
                ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);

                selectStateArray = ArrayAdapter.createFromResource(RegistrationActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
                selectStateUnionCode = ArrayAdapter.createFromResource(RegistrationActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

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
                    selectDistrictDialog = new Dialog(RegistrationActivity.this);
                    selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
                    selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    selectDistrictDialog.show();
                    TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
                    title.setText("Select City");
                    ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

                    if (selectedState.equals("AP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_assam_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("BR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_bihar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CG")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_goa_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("GJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_gujarat_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_haryana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("HP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_jharkhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_karnataka_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("KL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_kerala_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MH")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_maharashtra_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_manipur_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("ML")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_meghalaya_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("MZ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_mizoram_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("NL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_nagaland_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("OD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_odisha_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_punjab_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("RJ")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_rajasthan_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("SK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_sikkim_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TS")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_telangana_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("TR")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_tripura_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UP")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("UK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("WB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_west_bengal_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("AN")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("CH/PB")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_chandigarh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DD2")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_daman_diu_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("DL")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_delhi_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("JK")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LD")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("LA")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
                                R.array.array_ladakh_districts, R.layout.custom_list_row);
                    } else if (selectedState.equals("PY")) {
                        selectDistrictArray = ArrayAdapter.createFromResource(RegistrationActivity.this,
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

    public void onClickRegistration(View view) {

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
            saveUser(createUser());
            AlertDialog.Builder my_alert = new AlertDialog.Builder(RegistrationActivity.this);
            my_alert.setTitle("Registration Successful");
            my_alert.setMessage("Welcome to " +getString(R.string.app_name)+"\n\nPlease update your profile and explore the platform benefits.");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent i8 = new Intent(RegistrationActivity.this, ProfileAndRegistrationActivity.class);
                    i8.putExtra("mobile2", mobile);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                }
            });
            my_alert.show();

        }
//            RegistrationActivity.this.finish();
    }

    private TextWatcher registrationWatcher = new TextWatcher() {
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
            boolean owner = ownerButton.isChecked();
            boolean driver = driverButton.isChecked();
            boolean broker = brokerButton.isChecked();
            boolean customer = customerButton.isChecked();

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

    private TextWatcher pinCodeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String pinCodeWatcher = pinCode.getText().toString().trim();

            if (pinCodeWatcher.length() != 6){
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }else{
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
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

    //--------------------------------------create User in API -------------------------------------
    public UserRequest createUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name.getText().toString());
        userRequest.setPhone_number(mobile);
        userRequest.setAddress(address.getText().toString());
        userRequest.setUser_type(role);
        userRequest.setEmail_id(email_id.getText().toString());
        userRequest.setIsRegistration_done(1);
        userRequest.setPin_code(pinCode.getText().toString());
        userRequest.setPreferred_location(selectDistrictText.getText().toString());
        userRequest.setState_code(selectStateText.getText().toString());
        return userRequest;
    }

    public void saveUser(UserRequest userRequest) {
        Call<UserResponse> userResponseCall = ApiClient.getUserService().saveUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                Log.i("Message UserCreated:", userResponse.getData().getPhone_number());
                UserResponse userResponse = response.body();
                Log.i("Msg Success", String.valueOf(userResponse));
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------
}