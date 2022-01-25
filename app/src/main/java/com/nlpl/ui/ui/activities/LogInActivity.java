package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nlpl.R;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {


    EditText mobileNo;
    TextView series;
    Spinner selectCountry;
    Button getStarted;
    String  mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mobileNo = (EditText) findViewById(R.id.log_in_mobile_no);
        getStarted = (Button) findViewById(R.id.log_in_get_otp_button);
        series = (TextView) findViewById(R.id.log_in_series);
        selectCountry = findViewById(R.id.selectCountry);
        registerForContextMenu(selectCountry);

        selectCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Change the selected item's text color
                try {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });


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
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(LogInActivity.this).setCancelable(false);
                    my_alert.setTitle("OTP sent to "+mobile);
                    my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent i5 = new Intent(LogInActivity.this, OtpCodeActivity.class);
                            i5.putExtra("mobile", mobile);
                            i5.putExtra("isEditPhone", false);
                            startActivity(i5);
                            overridePendingTransition(0, 0);
                        }
                    });
                    my_alert.show();
                } else {
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(LogInActivity.this);
                    my_alert.setTitle("Invalid mobile number");
                    my_alert.setMessage("Please enter a 10 digit valid mobile number.");
                    my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    my_alert.show();
                    my_alert.setCancelable(false);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    //    public void onOtp(View view) {
//        if (mobileNo.getText().length()==10) {
//            Intent i5 = new Intent(LogInActivity.this, OtpCodeActivity.class);
//            i5.putExtra("mobile", mobileNo.getText().toString());
//            startActivity(i5);
//            overridePendingTransition(0, 0);
//        }else{
//            AlertDialog.Builder my_alert = new AlertDialog.Builder(LogInActivity.this);
//            my_alert.setTitle("Invalid Mobile Number");
//            my_alert.setMessage("Please enter a valid mobile number");
//            my_alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            my_alert.show();
//        }
//    }

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