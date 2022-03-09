package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nlpl.R;
import com.nlpl.utils.JumpTo;

public class LogInActivity extends AppCompatActivity {

    EditText mobileNo;
    TextView series;
//    Spinner selectCountry;
    Button getStarted;
    String  mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mobileNo = (EditText) findViewById(R.id.log_in_mobile_no);
        getStarted = (Button) findViewById(R.id.log_in_get_otp_button);
        series = (TextView) findViewById(R.id.log_in_series);
//        selectCountry = findViewById(R.id.selectCountry);
//        registerForContextMenu(selectCountry);

//        selectCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//        {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//            {
//                //Change the selected item's text color
//                try {
//                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//            }
//        });


        mobileNo.addTextChangedListener(mobileNumberTextWatcher);

        mobileNo.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        requestPermissions();
        requestPermissionsForGalleryREAD();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForCamera();

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = "+91" + mobileNo.getText().toString();
                if (mobileNo.getText().length()==10) {
                    //----------------------- Alert Dialog -----------------------------------------------------
                    Dialog alert = new Dialog(LogInActivity.this);
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

                    alertTitle.setText("OTP sent successfully");
                    String s = mobile.substring(3, 13);
                    alertMessage.setText("OTP sent to " + "+91 "+s);
                    alertPositiveButton.setVisibility(View.VISIBLE);
                    alertPositiveButton.setText("OK");
                    alertNegativeButton.setText("Cancel");

                    alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            JumpTo.goToOTPActivity(LogInActivity.this, mobile, false, null);
                        }
                    });

                    alertNegativeButton.setOnClickListener(view1 -> alert.dismiss());
                    //------------------------------------------------------------------------------------------
                } else {
                    //----------------------- Alert Dialog -----------------------------------------------------
                    Dialog alert = new Dialog(LogInActivity.this);
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

                    alertTitle.setText("Invalid mobile number");
                    alertMessage.setText("Please enter a 10 digit valid mobile number.");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        });
    }

    public void onTcAndPP(View view) {
        switch (view.getId()) {
            case R.id.log_in_tc:
                Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nlpl.net/"));
                startActivity(i1);
                break;

            case R.id.log_in_pp:
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nlpl.net"));
                startActivity(i2);
                break;
        }
    }

    private TextWatcher mobileNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String mobileNoWatcher = mobileNo.getText().toString().trim();

            if (mobileNoWatcher.length() == 10){
                mobileNo.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
                getStarted.setEnabled(true);
                getStarted.setBackground(getResources().getDrawable(R.drawable.button_active));
            }else{
                mobileNo.setBackground(getResources().getDrawable(R.drawable.mobile_number_right_red));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left_red));
                getStarted.setEnabled(true);
                getStarted.setBackground(getResources().getDrawable(R.drawable.button_de_active));
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(LogInActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LogInActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(LogInActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LogInActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(LogInActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LogInActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(LogInActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LogInActivity.this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, 100);
        }
    }

}