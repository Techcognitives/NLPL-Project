package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.BankRequest;
import com.nlpl.model.BankResponse;
import com.nlpl.model.UserRequest;
import com.nlpl.model.UserResponse;
import com.nlpl.services.BankService;
import com.nlpl.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    String userId, name;

    EditText bankName, accountNo, reAccount, ifscCode;
    Button okButton;

    Button uploadCC;
    TextView textCC, editCC;
    int GET_FROM_GALLERY=0;
    ImageView cancelledCheckImage;
    Boolean isEdit;

    RadioButton canceledCheckRadioButton, acDetailsRadioButton;
    String bankId;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            bankId = bundle.getString("bankDetailsID");
        }

        action_bar = findViewById(R.id.bank_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setText(getString(R.string.english));
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(BankDetailsActivity.this);
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

        actionBarTitle.setText("Bank Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDetailsActivity.this.finish();
            }
        });

        bankName = (EditText) findViewById(R.id.bank_details_person_name_text_edit);
        accountNo = (EditText) findViewById(R.id.bank_details_account_number_edit);
        reAccount = (EditText) findViewById(R.id.bank_details_reenter_account_number_edit);
        ifscCode = (EditText) findViewById(R.id.bank_details_ifsc_edit);
        okButton = (Button) findViewById(R.id.bank_details_ok_button);
        okButton.setEnabled(false);

        mQueue = Volley.newRequestQueue(BankDetailsActivity.this);
        if (isEdit){
//        getBankDetails();
        }

        bankName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        bankName.setFilters(new InputFilter[] { filter });
        ifscCode.setFilters(new InputFilter[] { filter });

        bankName.addTextChangedListener(bankDetailsWatcher);
        accountNo.addTextChangedListener(bankDetailsWatcher);
        reAccount.addTextChangedListener(bankDetailsWatcher);
        ifscCode.addTextChangedListener(bankDetailsWatcher);

        uploadCC = findViewById(R.id.bank_details_canceled_check_upload);
        editCC = findViewById(R.id.bank_details_edit_canceled_check);
        textCC = findViewById(R.id.bank_details_canceled_check_text);
        cancelledCheckImage = (ImageView) findViewById(R.id.bank_details_canceled_check_image);

        canceledCheckRadioButton = (RadioButton) findViewById(R.id.bank_details_cancelled_check_radio_button);
        acDetailsRadioButton = (RadioButton) findViewById(R.id.bank_details_ac_details_radio_button);

        uploadCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        }

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Canceled Check uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadCC.setVisibility(View.INVISIBLE);
            editCC.setVisibility(View.VISIBLE);


            Uri selectedImage = data.getData();
            cancelledCheckImage.setImageURI(selectedImage);
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



    public void onClickBankDetailsOk(View view) {
        if (accountNo.getText().toString().equals(reAccount.getText().toString())) {
            if (isEdit){

            }else{
                saveBank(createBankAcc());
            }

            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Bank Details added successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                    Intent i8 = new Intent(BankDetailsActivity.this, ProfileAndRegistrationActivity.class);
                    i8.putExtra("userId", userId);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    BankDetailsActivity.this.finish();
                }
            });
            my_alert.show();
        } else {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Account number does not match");
            my_alert.setMessage("Please enter correct account number as above.");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
        }
    }

    private TextWatcher bankDetailsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String bankName1 = bankName.getText().toString().trim();
            String accNo1 = accountNo.getText().toString().trim();
            String reAccNo1 = reAccount.getText().toString().trim();
            String ifscCode1 = ifscCode.getText().toString().trim();

            if (!bankName1.isEmpty() && !accNo1.isEmpty() && !reAccNo1.isEmpty() && !ifscCode1.isEmpty()) {

                okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                okButton.setEnabled(true);
            }else
            {
                okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                okButton.setEnabled(false);
            }

            if (accNo1.equals(reAccNo1)){
                reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }else{
                reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //--------------------------------------create Bank Details in API -------------------------------------
    public BankRequest createBankAcc() {
        BankRequest bankRequest = new BankRequest();
        bankRequest.setUser_id(userId);
        bankRequest.setAccountholder_name(name);
        bankRequest.setAccount_number(accountNo.getText().toString());
        bankRequest.setRe_enter_acc_num(reAccount.getText().toString());
        bankRequest.setIFSI_CODE(ifscCode.getText().toString());
        bankRequest.setIsBankDetails_Given("1");
        return bankRequest;
    }

    public void saveBank(BankRequest bankRequest) {
        Call<BankResponse> bankResponseCall = ApiClient.getBankService().saveBank(bankRequest);
       bankResponseCall.enqueue(new Callback<BankResponse>() {
           @Override
           public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
           }

           @Override
           public void onFailure(Call<BankResponse> call, Throwable t) {

           }
       });
    }
    //-----------------------------------------------------------------------------------------------------

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

    public void onAccCheck(View view) {
        if (accountNo.getText().toString().equals(reAccount.getText().toString())) {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
        } else {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Account number does not match");
            my_alert.setMessage("Please enter correct account number as above.");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
        }

    }

    public void onClickBankDetailsChoose(View view) {
        ImageView canceledCheckBlurImage = (ImageView) findViewById(R.id.bank_details_blur_image_canceled_check);
        ImageView accountDetailsBlurImage = (ImageView) findViewById(R.id.bank_details_blur_image_account_details);

        switch (view.getId()) {
            case R.id.bank_details_cancelled_check_radio_button:
                canceledCheckRadioButton.setChecked(true);
                acDetailsRadioButton.setChecked(false);

                bankName.setClickable(false);
                accountNo.setEnabled(false);
                reAccount.setEnabled(false);
                ifscCode.setEnabled(false);

                canceledCheckBlurImage.setVisibility(View.GONE);
                accountDetailsBlurImage.setVisibility(View.VISIBLE);

                uploadCC.setEnabled(true);
                break;

            case R.id.bank_details_ac_details_radio_button:
                canceledCheckRadioButton.setChecked(false);
                acDetailsRadioButton.setChecked(true);

                bankName.setEnabled(true);
                accountNo.setEnabled(true);
                reAccount.setEnabled(true);
                ifscCode.setEnabled(true);

                canceledCheckBlurImage.setVisibility(View.VISIBLE);
                accountDetailsBlurImage.setVisibility(View.GONE);

                uploadCC.setEnabled(false);
                editCC.setVisibility(View.GONE);
                break;
        }
    }

    private void getBankDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
//                        nameAPI = obj.getString("name");
//                        mobileAPI = obj.getString("phone_number");
//                        addressAPI = obj.getString("address");
//                        stateAPI = obj.getString("state_code");
//                        cityAPI = obj.getString("preferred_location");
//                        pinCodeAPI = obj.getString("pin_code");
//                        roleAPI = obj.getString("user_type");

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