package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nlpl.R;
import com.nlpl.model.UpdateUserDetails.UpdateUserPhoneNumber;
import com.nlpl.services.UserService;
import com.nlpl.ui.ui.adapters.OTPReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OtpCodeActivity extends AppCompatActivity {

    TextView countdown, otpTitle, reSendOtp;
    String mobile, otpId;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    Button otpButton;
    String mobileNoFirebase, otp, userId, userIdAPI, name, nameAPI, phone, isRegistrationDone, isRegistrationDoneAPI, pinCode, pinCodeAPI, address, addressAPI, mobileNoAPI, cityAPI, city, roleAPI, role;
    FirebaseAuth mAuth;
    private RequestQueue mQueue;
    private UserService userService;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;
    Boolean isEditPhone;
    String userIdBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            Log.i("Mobile No", mobile);
            isEditPhone = bundle.getBoolean("isEditPhone");
            userIdBundle = bundle.getString("userId");
        }

        countdown = findViewById(R.id.countdown);
        otpTitle = findViewById(R.id.otp_text);
        otpButton = findViewById(R.id.otp_button);
        requestPermissions();
        reSendOtp = findViewById(R.id.resend_otp);
        String enterCode = getString(R.string.enter_code);

        String s = mobile.substring(3, 13);
        mobileNoFirebase = mobile.substring(1, 13);
        otpTitle.setText(enterCode + "+91 " + s);

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

        mQueue = Volley.newRequestQueue(OtpCodeActivity.this); //To Select Specialty and Credentials
        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);


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

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(OtpCodeActivity.this);
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

                alertTitle.setText("OTP validated successfully");
                alertMessage.setText("");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText("OK");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isEditPhone) {
                            updateUserPhoneNumber(userIdBundle);
                            OtpCodeActivity.this.finish();
                        } else {
                            checkPhoneInAPI(mobileNoFirebase);
                        }
                    }
                });
                //------------------------------------------------------------------------------------------

//                if (otp1.getText().toString().isEmpty() || otp2.getText().toString().isEmpty() || otp3.getText().toString().isEmpty() || otp4.getText().toString().isEmpty() || otp5.getText().toString().isEmpty() || otp6.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Field is blank", Toast.LENGTH_LONG).show();
//                } else {
//                    Log.i("OTP", otp);
//                    Log.i("OTP ID", otpId);
//                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId, otp);
//                    signInWithPhoneAuthCredential(credential);
//
//                }
            }
        });

        reSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                initiateOtp();
                setCountdown();
            }
        });
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

        new OTPReceiver().setEditText_otp(otp1, otp2, otp3, otp4, otp5, otp6, otpButton);
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
                reSendOtp.setVisibility(View.VISIBLE);
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
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(OtpCodeActivity.this);
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

                    alertTitle.setText("OTP validated successfully");
                    alertMessage.setText("");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            if (isEditPhone) {
                                updateUserPhoneNumber(mobileNoFirebase);
                                OtpCodeActivity.this.finish();
                            } else {
                                checkMobileNumberWithOTP(mobileNoFirebase);
                            }
                        }
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(OtpCodeActivity.this);
                    alert.setContentView(R.layout.dialog_alert);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alert.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.gravity = Gravity.CENTER;

                    alert.show();
                    alert.getWindow().setAttributes(lp);
                    alert.setCancelable(true);

                    TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText("Invalid OTP");
                    alertMessage.setText("Please enter a 6 digit OTP sent to your mobile number.");
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

            if (!otpEdit1.isEmpty() && !otpEdit2.isEmpty() && !otpEdit3.isEmpty() && !otpEdit4.isEmpty() && !otpEdit5.isEmpty() && !otpEdit6.isEmpty()) {
                otpButton.setEnabled(true);
                otpButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            } else {
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

    //-------------------------------- Update User Phone Number ------------------------------------
    private void updateUserPhoneNumber(String getUserId) {

        UpdateUserPhoneNumber updateUserPhoneNumber = new UpdateUserPhoneNumber(mobileNoFirebase);

        Call<UpdateUserPhoneNumber> call = userService.updateUserPhoneNumber("" + getUserId, updateUserPhoneNumber);

        call.enqueue(new Callback<UpdateUserPhoneNumber>() {
            @Override
            public void onResponse(Call<UpdateUserPhoneNumber> call, retrofit2.Response<UpdateUserPhoneNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "PhoneNumber");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserPhoneNumber> call, Throwable t) {
                Log.i("Not Successful", "PhoneNumber");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void checkPhoneInAPI(String getMobile) {
        String receivedMobile = getMobile;
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userIdAPI = data.getString("user_id");
                        mobileNoAPI = data.getString("phone_number");
                        pinCodeAPI = data.getString("pin_code");
                        nameAPI = data.getString("name");
                        roleAPI = data.getString("user_type");
                        cityAPI = data.getString("preferred_location");
                        addressAPI = data.getString("address");
                        isRegistrationDoneAPI = data.getString("isRegistration_done");

                        arrayUserId.add(userIdAPI);
                        arrayMobileNo.add(mobileNoAPI);
                        arrayAddress.add(addressAPI);
                        arrayRegDone.add(isRegistrationDoneAPI);
                        arrayName.add(nameAPI);
                        arrayRole.add(roleAPI);
                        arrayCity.add(cityAPI);
                        arrayPinCode.add(pinCodeAPI);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(receivedMobile)) {
                            userId = arrayUserId.get(j);
                            name = arrayName.get(j);
                            phone = arrayMobileNo.get(j);
                            address = arrayAddress.get(j);
                            pinCode = arrayPinCode.get(j);
                            city = arrayCity.get(j);
                            role = arrayRole.get(j);
                            isRegistrationDone = arrayRegDone.get(j);
                        }
                    }

                    if (isRegistrationDone != null) {

                        Log.i("userIDAPI:", userId);
                        Log.i("userName", name);
                        Log.i("isregDone:", isRegistrationDone);
                        Log.i("Mobile No API Matches", phone);
                        Log.i("role splash", role);

                        if (role.equals("Customer")) {
                            Intent i8 = new Intent(OtpCodeActivity.this, CustomerDashboardActivity.class);
                            i8.putExtra("mobile", phone);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            finish();
                        }else{
                            Intent i8 = new Intent(OtpCodeActivity.this, DashboardActivity.class);
                            i8.putExtra("mobile2", phone);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            finish();
                        }

                    } else {
//                      Log.i("mobile no not equal", mobileNoAPI);
                        Intent i8 = new Intent(OtpCodeActivity.this, RegistrationActivity.class);
                        i8.putExtra("mobile1", receivedMobile);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        finish();
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }

    private void checkMobileNumberWithOTP(String mobileReceived) {
        String getMobileReceived = mobileReceived;
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userIdAPI = data.getString("user_id");
                        mobileNoAPI = data.getString("phone_number");
                        pinCodeAPI = data.getString("pin_code");
                        nameAPI = data.getString("name");
                        roleAPI = data.getString("user_type");
                        cityAPI = data.getString("preferred_location");
                        addressAPI = data.getString("address");
                        isRegistrationDoneAPI = data.getString("isRegistration_done");

                        arrayUserId.add(userIdAPI);
                        arrayMobileNo.add(mobileNoAPI);
                        arrayAddress.add(addressAPI);
                        arrayRegDone.add(isRegistrationDoneAPI);
                        arrayName.add(nameAPI);
                        arrayRole.add(roleAPI);
                        arrayCity.add(cityAPI);
                        arrayPinCode.add(pinCodeAPI);
                    }

                    for (int j = 0; j < arrayMobileNo.size(); j++) {
                        if (arrayMobileNo.get(j).equals(getMobileReceived)) {
                            userId = arrayUserId.get(j);
                            name = arrayName.get(j);
                            phone = arrayMobileNo.get(j);
                            address = arrayAddress.get(j);
                            pinCode = arrayPinCode.get(j);
                            city = arrayCity.get(j);
                            role = arrayRole.get(j);
                            isRegistrationDone = arrayRegDone.get(j);
                        }
                    }

                    if (isRegistrationDone != null) {

                        Log.i("userIDAPI:", userId);
                        Log.i("userName", name);
                        Log.i("isregDone:", isRegistrationDone);
                        Log.i("Mobile No API Matches", phone);
                        Log.i("role OTP", role);

                        if (role.equals("Customer")) {
                            Intent i8 = new Intent(OtpCodeActivity.this, CustomerDashboardActivity.class);
                            i8.putExtra("mobile", phone);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            finish();
                        }else{
                            Intent i8 = new Intent(OtpCodeActivity.this, DashboardActivity.class);
                            i8.putExtra("mobile2", phone);
                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i8);
                            overridePendingTransition(0, 0);
                            finish();
                        }

                    } else {
//                                            Log.i("mobile no not equal", mobileNoAPI);
                        Intent i8 = new Intent(OtpCodeActivity.this, RegistrationActivity.class);
                        i8.putExtra("mobile1", getMobileReceived);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        finish();
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }


}