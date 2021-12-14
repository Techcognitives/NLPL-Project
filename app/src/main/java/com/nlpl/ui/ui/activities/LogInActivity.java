package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nlpl.R;

public class LogInActivity extends AppCompatActivity {

    EditText mobileNo;
    Button getStarted;
    String mobile;

    private FirebaseAuth mFireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mFireAuth = FirebaseAuth.getInstance();
        mobileNo = (EditText) findViewById(R.id.log_in_mobile_no);
        getStarted = (Button) findViewById(R.id.log_in_get_otp_button);

        mobileNo.addTextChangedListener(mobileNumberTextWatcher);

        mobileNo.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileNo.getText().length()==10) {
                    Intent i5 = new Intent(LogInActivity.this, OtpCodeActivity.class);
                    mobile = "+91" + mobileNo.getText().toString();
                    i5.putExtra("mobile", mobile);
                    startActivity(i5);
                    overridePendingTransition(0, 0);
                } else {
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(LogInActivity.this);
                    my_alert.setTitle("Invalid Mobile Number");
                    my_alert.setMessage("Please enter a valid mobile number");
                    my_alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    my_alert.show();
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
                getStarted.setEnabled(true);
                getStarted.setBackground(getResources().getDrawable(R.drawable.button_active));
            }else{
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

        FirebaseUser mFireBaseUser = mFireAuth.getCurrentUser();
        if (mFireBaseUser != null){
            Log.i("Phone", mFireBaseUser.getPhoneNumber());
            Intent i8 = new Intent(LogInActivity.this, RegistrationActivity.class);
            i8.putExtra("mobile1", mFireBaseUser.getPhoneNumber());
            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i8);
            overridePendingTransition(0, 0);
            LogInActivity.this.finish();
        }else{
            Log.i("New User", "New User");
        }

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
}