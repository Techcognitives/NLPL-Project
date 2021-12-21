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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DriverDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    EditText driverName, driverMobile;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    Button uploadDL, okDriverDetails;
    TextView textDL, editDL, series;
    int GET_FROM_GALLERY=0;
    ImageView driverLicenseImage;

    String userId, driverName2, vehicleNo, mobile, name,address, pinCode, city, bankName, accNo, role;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, isDLUploaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile3");
            name = bundle.getString("name3");
            address = bundle.getString("address");
            pinCode = bundle.getString("pinCode");
            city = bundle.getString("city");
            userId = bundle.getString("userId");
            bankName = bundle.getString("bankName");
            accNo = bundle.getString("accNo");
            vehicleNo = bundle.getString("vehicleNo");
            driverName2 = bundle.getString("driverName");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            role = bundle.getString("role");
            Log.i("Mobile No", mobile);
            Log.i("Name", name);
        }

        action_bar = findViewById(R.id.driver_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);
        driverMobile = findViewById(R.id.driver_details_mobile_number_edit);
        driverName=findViewById(R.id.driver_details_driver_name_edit);
        okDriverDetails=findViewById(R.id.driver_details_ok_button);
        series = (TextView) findViewById(R.id.driver_details_mobile_prefix);

        driverName.addTextChangedListener(driverWatcher);
        driverMobile.addTextChangedListener(driverWatcher);

        driverName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        driverName.setFilters(new InputFilter[] { filter });

        language.setText(getString(R.string.english));
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(DriverDetailsActivity.this);
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

        actionBarTitle.setText("Driver Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverDetailsActivity.this.finish();
            }
        });

        uploadDL = findViewById(R.id.driver_details_upload_driver_license);
        editDL = findViewById(R.id.driver_details_edit_driver_license);
        textDL = findViewById(R.id.driver_details_driver_license_text_image);
        driverLicenseImage = (ImageView) findViewById(R.id.driver_details_driver_license_image);


        uploadDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editDL.setOnClickListener(new View.OnClickListener() {
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

            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
            my_alert.setTitle("Driving License uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);

            isDLUploaded=true;
            String driverMobileText = driverMobile.getText().toString();
            String driverNameText = driverName.getText().toString();

            if (!driverNameText.isEmpty()&&!driverMobileText.isEmpty() && isDLUploaded  ){
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }
            Uri selectedImage = data.getData();
            driverLicenseImage.setImageURI(selectedImage);
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

    public void onClickDriverDetailsOk(View view) {
        String driverMobileText = driverMobile.getText().toString();
        String driverNameText = driverName.getText().toString();

        if (!driverNameText.isEmpty()&&!driverMobileText.isEmpty() && isDLUploaded) {
            if (driverMobileText.length() != 10) {
                AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                my_alert.setTitle("Invalid Mobile Number");
                my_alert.setMessage("Please enter a 10 digit valid mobile number.");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                my_alert.show();

            } else {
                AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                my_alert.setTitle("Driver Details added successfully");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent i8 = new Intent(DriverDetailsActivity.this, ProfileAndRegistrationActivity.class);
                        i8.putExtra("mobile2", mobile);
                        i8.putExtra("name2", name);
                        i8.putExtra("address", address);
                        i8.putExtra("pinCode", pinCode);
                        i8.putExtra("city", city);
                        i8.putExtra("userId", userId);
                        i8.putExtra("bankName", bankName);
                        i8.putExtra("accNo", accNo);
                        i8.putExtra("vehicleNo", vehicleNo);
                        i8.putExtra("driverName", driverName.getText().toString());
                        i8.putExtra("isPersonal", isPersonalDetailsDone);
                        i8.putExtra("isBank", isBankDetailsDone);
                        i8.putExtra("isTrucks", isAddTrucksDone);
                        i8.putExtra("isDriver", true);
                        i8.putExtra("role", role);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        DriverDetailsActivity.this.finish();
                    }
                });
                my_alert.show();


            }
        }
    }

    private TextWatcher driverWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String mobileNoWatcher = driverMobile.getText().toString().trim();

            if (mobileNoWatcher.length()==10){
                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            }else{
                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right_red));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left_red));
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
}