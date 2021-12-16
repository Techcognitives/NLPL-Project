package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.nlpl.R;

public class CompanyDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    Dialog selectStateDialog, selectDistrictDialog;
    String selectedDistrict, selectedState;
    int parentID;
    EditText companyName, pinCode, address, gstNumber, panNumber;
    Button okButton;
    TextView selectStateText, selectDistrictText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        action_bar = findViewById(R.id.company_details_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setText(getString(R.string.english));
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(CompanyDetailsActivity.this);
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

        companyName.addTextChangedListener(companyWatcher);
        gstNumber.addTextChangedListener(companyWatcher);
        panNumber.addTextChangedListener(companyWatcher);
        selectStateText.addTextChangedListener(companyWatcher);
        selectDistrictText.addTextChangedListener(companyWatcher);
        pinCode.addTextChangedListener(companyWatcher);
        address.addTextChangedListener(companyWatcher);

        companyName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        companyName.setFilters(new InputFilter[]{filter});

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
                selectStateDialog.setContentView(R.layout.dialog_select_state);
//                dialog.getWindow().setLayout(1000,3000);
                selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectStateDialog.show();
                selectStateDialog.setCancelable(false);
                ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);
                EditText searchState = (EditText) selectStateDialog.findViewById(R.id.search_state);

                selectStateArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
                selectStateUnionCode = ArrayAdapter.createFromResource(CompanyDetailsActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

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
                        selectDistrictDialog = new Dialog(CompanyDetailsActivity.this);
                        selectDistrictDialog.setContentView(R.layout.dialog_select_state);
//                dialog.getWindow().setLayout(1000,3000);
                        selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        selectDistrictDialog.show();
                        ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);
                        EditText searchDistrict = (EditText) selectDistrictDialog.findViewById(R.id.search_state);

                        if (parentID == R.id.list_state) {
                            switch (selectedState) {
                                case "Andhra Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this, R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Arunachal Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Assam":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_assam_districts, R.layout.custom_list_row);
                                    break;
                                case "Bihar":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_bihar_districts, R.layout.custom_list_row);
                                    break;
                                case "Chhattisgarh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
                                    break;
                                case "Goa":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_goa_districts, R.layout.custom_list_row);
                                    break;
                                case "Gujarat":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_gujarat_districts, R.layout.custom_list_row);
                                    break;
                                case "Haryana":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_haryana_districts, R.layout.custom_list_row);
                                    break;
                                case "Himachal Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Jharkhand":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_jharkhand_districts, R.layout.custom_list_row);
                                    break;
                                case "Karnataka":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_karnataka_districts, R.layout.custom_list_row);
                                    break;
                                case "Kerala":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_kerala_districts, R.layout.custom_list_row);
                                    break;
                                case "Madhya Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Maharashtra":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_maharashtra_districts, R.layout.custom_list_row);
                                    break;
                                case "Manipur":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_manipur_districts, R.layout.custom_list_row);
                                    break;
                                case "Meghalaya":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_meghalaya_districts, R.layout.custom_list_row);
                                    break;
                                case "Mizoram":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_mizoram_districts, R.layout.custom_list_row);
                                    break;
                                case "Nagaland":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_nagaland_districts, R.layout.custom_list_row);
                                    break;
                                case "Odisha":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_odisha_districts, R.layout.custom_list_row);
                                    break;
                                case "Punjab":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_punjab_districts, R.layout.custom_list_row);
                                    break;
                                case "Rajasthan":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_rajasthan_districts, R.layout.custom_list_row);
                                    break;
                                case "Sikkim":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_sikkim_districts, R.layout.custom_list_row);
                                    break;
                                case "Tamil Nadu":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
                                    break;
                                case "Telangana":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_telangana_districts, R.layout.custom_list_row);
                                    break;
                                case "Tripura":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_tripura_districts, R.layout.custom_list_row);
                                    break;
                                case "Uttar Pradesh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
                                    break;
                                case "Uttarakhand":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_uttarakhand_districts, R.layout.custom_list_row);
                                    break;
                                case "West Bengal":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_west_bengal_districts, R.layout.custom_list_row);
                                    break;
                                case "Andaman and Nicobar Islands":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
                                    break;
                                case "Chandigarh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_chandigarh_districts, R.layout.custom_list_row);
                                    break;
                                case "Dadra and Nagar Haveli":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
                                    break;
                                case "Daman and Diu":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_daman_diu_districts, R.layout.custom_list_row);
                                    break;
                                case "Delhi":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_delhi_districts, R.layout.custom_list_row);
                                    break;
                                case "Jammu and Kashmir":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
                                    break;
                                case "Lakshadweep":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_lakshadweep_districts, R.layout.custom_list_row);
                                    break;
                                case "Ladakh":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
                                            R.array.array_ladakh_districts, R.layout.custom_list_row);
                                    break;
                                case "Puducherry":
                                    selectDistrictArray = ArrayAdapter.createFromResource(CompanyDetailsActivity.this,
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
                companyName.setCursorVisible(false);
                pinCode.setCursorVisible(false);
                address.setCursorVisible(false);
                if (!selectStateText.getText().toString().isEmpty()) {
                    selectDistrictDialog.show();
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

            if (pinCodeWatcher.length() != 6){
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }else{
                pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }

            if (!nameWatcher.isEmpty() && !gstNumberWatcher.isEmpty() && !panNumberWatcher.isEmpty() && !pinCodeWatcher.isEmpty() && !addressWatcher.isEmpty() && pinCodeWatcher.length()==6 && !stateWatcher.isEmpty() && !cityWatcher.isEmpty()) {
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
    }
}