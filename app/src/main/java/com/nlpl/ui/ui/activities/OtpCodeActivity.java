package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nlpl.R;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class OtpCodeActivity extends AppCompatActivity {

    TextView countdown, otpTitle, reSendOtp, copyOTP;
    String mobile, otpId;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    Button otpButton;
    String otp;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            Log.i("Mobile No", mobile);
        }

        countdown = findViewById(R.id.countdown);
        otpTitle = findViewById(R.id.otp_text);
        otpButton = findViewById(R.id.otp_button);
        requestPermissions();
        copyOTP = (TextView) findViewById(R.id.copy_otp);
//        reSendOtp = findViewById(R.id.resend_otp);
        String enterCode = getString(R.string.enter_code);
        String s = mobile.substring(3,13);
        otpTitle.setText(enterCode + "+91 "+s);

        otp1 = (EditText) findViewById(R.id.enter_otp_1);
        otp2 = (EditText) findViewById(R.id.enter_otp_2);
        otp3 = (EditText) findViewById(R.id.enter_otp_3);
        otp4 = (EditText) findViewById(R.id.enter_otp_4);
        otp5 = (EditText) findViewById(R.id.enter_otp_5);
        otp6 = (EditText) findViewById(R.id.enter_otp_6);





        otp1.addTextChangedListener(otpWatcher);
        otp2.addTextChangedListener(otpWatcher);
        otp3.addTextChangedListener(otpWatcher);
        otp4.addTextChangedListener(otpWatcher);
        otp5.addTextChangedListener(otpWatcher);
        otp6.addTextChangedListener(otpWatcher);

        otp1.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        setupOTPInputs();
        mAuth = FirebaseAuth.getInstance();

//        copyOTP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new OTPReceiver().setEditText_otp(otpEdit, otpButton);
//            }
//        });

//        final ProgressDialog dialog = ProgressDialog.show(this, "Fetching OTP", "Please wait....", true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                    dialog.dismiss();
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }).start();

//        initiateOtp();
        setCountdown();

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
                Intent i8 = new Intent(OtpCodeActivity.this, RegistrationActivity.class);
                i8.putExtra("mobile1", mobile);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
                OtpCodeActivity.this.finish();

//                if (otp1.getText().toString().isEmpty() || otp2.getText().toString().isEmpty() || otp3.getText().toString().isEmpty() || otp4.getText().toString().isEmpty() || otp5.getText().toString().isEmpty() || otp6.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Field is blank", Toast.LENGTH_LONG).show();
//                } else {
//                    Log.i("OTP", otp);
//                    Log.i("OTP ID", otpId);
//                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId, otp);
//                    signInWithPhoneAuthCredential(credential);
//                }
            }
        });

//        reSendOtp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                initiateOtp();
//                setCountdown();
//            }
//        });
    }

    private void setupOTPInputs() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp2.setFocusableInTouchMode(true);
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp3.setFocusableInTouchMode(true);
                    otp3.requestFocus();
                } else {
                    otp1.setFocusableInTouchMode(true);
                    otp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp4.setFocusableInTouchMode(true);
                    otp4.requestFocus();
                } else {
                    otp2.setFocusableInTouchMode(true);
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp5.setFocusableInTouchMode(true);
                    otp5.requestFocus();
                } else {
                    otp3.setFocusableInTouchMode(true);
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp6.setFocusableInTouchMode(true);
                    otp6.requestFocus();
                } else {
                    otp4.setFocusableInTouchMode(true);
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    otp5.setFocusableInTouchMode(true);
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setCountdown() {
        // Time is in millisecond so 50sec = 50000 I have used
        // countdown Interval is 1sec = 1000 I have used
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    f = new DecimalFormat("00");
                }
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    countdown.setText(f.format(min) + " : " + f.format(sec));
                }
            }

            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                countdown.setText("00:00");
//                otpEdit.setEnabled(false);
            }
        }.start();
    }

    private void initiateOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpId = s;
                        Log.i("OTP Id", s);
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent i8 = new Intent(OtpCodeActivity.this, RegistrationActivity.class);
                    i8.putExtra("mobile1", mobile);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    OtpCodeActivity.this.finish();
                } else {
                    AlertDialog.Builder my_alert = new AlertDialog.Builder(OtpCodeActivity.this);
                    my_alert.setTitle("Invalid OTP");
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

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(OtpCodeActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OtpCodeActivity.this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, 100);
        }
    }

    private TextWatcher otpWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String otpEdit1 = otp1.getText().toString().trim();
            String otpEdit2 = otp2.getText().toString().trim();
            String otpEdit3 = otp3.getText().toString().trim();
            String otpEdit4 = otp4.getText().toString().trim();
            String otpEdit5 = otp5.getText().toString().trim();
            String otpEdit6 = otp6.getText().toString().trim();

            if (!otpEdit1.isEmpty() && !otpEdit2.isEmpty() && !otpEdit3.isEmpty() && !otpEdit4.isEmpty() && !otpEdit5.isEmpty() && !otpEdit6.isEmpty()){
                otpButton.setEnabled(true);
                otpButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            }else {
                otpButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                otpButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void onClickBack(View view) {
        OtpCodeActivity.this.finish();
    }
}