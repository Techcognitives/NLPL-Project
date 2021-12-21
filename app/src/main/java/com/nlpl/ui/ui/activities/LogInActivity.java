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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nlpl.R;
import com.nlpl.model.UserResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {


    EditText mobileNo;
    TextView series;
    Button getStarted;
    String  mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mobileNo = (EditText) findViewById(R.id.log_in_mobile_no);
        getStarted = (Button) findViewById(R.id.log_in_get_otp_button);
        series = (TextView) findViewById(R.id.log_in_series);

        mobileNo.addTextChangedListener(mobileNumberTextWatcher);

        mobileNo.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = "+91" + mobileNo.getText().toString();
                if (mobileNo.getText().length()==10) {
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(LogInActivity.this);
                    my_alert.setTitle("OTP is sent to "+mobile);
                    my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent i5 = new Intent(LogInActivity.this, OtpCodeActivity.class);
                            i5.putExtra("mobile", mobile);
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
}