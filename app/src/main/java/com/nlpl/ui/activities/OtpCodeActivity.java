package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nlpl.R;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.OTPReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OtpCodeActivity extends AppCompat {

    TextView countdown, otpTitle, reSendOtp;
    String mobile, otpId;
    Button otpButton;
    String mobileNoFirebase, otp, userId, userIdAPI, name, nameAPI, phone, isRegistrationDone, isRegistrationDoneAPI, pinCode, pinCodeAPI, address, addressAPI, mobileNoAPI, cityAPI, city, roleAPI, role;
    FirebaseAuth mAuth;
    private RequestQueue mQueue;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;
    Boolean isEditPhone;
    String userIdBundle;
    PinView otpCode;
    Dialog loadingDialog;

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

        //------------------------------------------------------------------------------------------
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);
        //------------------------------------------------------------------------------------------

        countdown = findViewById(R.id.countdown);
        otpTitle = findViewById(R.id.otp_text);
        otpButton = findViewById(R.id.otp_button);
        requestPermissions();
        reSendOtp = findViewById(R.id.resend_otp);
        String enterCode = getString(R.string.enter_code);

        String s = mobile.substring(3, 13);
        mobileNoFirebase = mobile.substring(1, 13);
        otpTitle.setText(enterCode + "+91 " + s);

        otpCode = (PinView) findViewById(R.id.pin_view);

        otpCode.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        new OTPReceiver().setEditText_otp(otpCode, otpButton);

        mQueue = Volley.newRequestQueue(OtpCodeActivity.this); //To Select Specialty and Credentials
        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();

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
                Dialog loadingDialog = new Dialog(OtpCodeActivity.this);
                loadingDialog.setContentView(R.layout.dialog_loading);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

                loadingDialog.show();
                loadingDialog.setCancelable(false);
                Animation rotate = AnimationUtils.loadAnimation(OtpCodeActivity.this, R.anim.clockwiserotate);
                loading_img.startAnimation(rotate);
//                ShowAlert.loadingDialog(OtpCodeActivity.this);


                otp = otpCode.getText().toString();
                if (otp.length()==6){
                    //------------------------ Without OTP ---------------------------------------------
                    if (isEditPhone) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        JumpTo.goToViewPersonalDetailsActivity(OtpCodeActivity.this, userIdBundle, mobile, true);
                    } else {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        checkPhoneInAPI(mobileNoFirebase);
                    }
                    //----------------------------------------------------------------------------------

                    //----------------------- With OTP -------------------------------------------------
//                try {
//                    if (otpCode.getText().toString().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Field is blank", Toast.LENGTH_LONG).show();
//                    } else {
////                    Log.i("OTP", otp);
////                    Log.i("OTP ID", otpId);
//                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId, otp);
//                        signInWithPhoneAuthCredential(credential);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
                    //----------------------------------------------------------------------------------
                }else{
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(OtpCodeActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText(getString(R.string.Invalid_OTP));
                    alertMessage.setText(getString(R.string.six_digit_OTP));
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            otpCode.getText().clear();
                        }
                    });
                }
            }
        });

        reSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                initiateOtp();
                setCountdown();
                reSendOtp.setVisibility(View.INVISIBLE);
                countdown.setVisibility(View.VISIBLE);
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
                reSendOtp.setVisibility(View.VISIBLE);
//                otpEdit.setEnabled(false);
                countdown.setVisibility(View.GONE);
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
                    if (isEditPhone) {
                        Log.i("userId at otp code", userIdBundle + " " + mobileNoFirebase);
                        UpdateUserDetails.updateUserPhoneNumber(userIdBundle, mobileNoFirebase);
                        checkMobileNumberWithOTP(mobileNoFirebase);
                    } else {
                        checkMobileNumberWithOTP(mobileNoFirebase);
                    }
                } else {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(OtpCodeActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText(getString(R.string.Invalid_OTP));
                    alertMessage.setText(getString(R.string.six_digit_OTP));
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            otpCode.getText().clear();
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
                            JumpTo.goToCustomerDashboard(OtpCodeActivity.this, phone, true);
                        } else {
                            JumpTo.goToServiceProviderDashboard(OtpCodeActivity.this, phone, true,true);
                        }

                    } else {
//                      Log.i("mobile no not equal", mobileNoAPI);
                        JumpTo.goToLanguageActivity(OtpCodeActivity.this, receivedMobile);
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                    otpButton.performClick();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                otpButton.performClick();
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
                            JumpTo.goToCustomerDashboard(OtpCodeActivity.this, phone, true);
                        } else {
                            JumpTo.goToServiceProviderDashboard(OtpCodeActivity.this, phone, true,true);
                        }

                    } else {
                        JumpTo.goToLanguageActivity(OtpCodeActivity.this, getMobileReceived);
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                    otpButton.performClick();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                otpButton.performClick();
            }
        });
        mQueue.add(request);
        //------------------------------------------------------------------------------------------------
    }

    public void onClickChangeNumber(View view) {
        JumpTo.goToLogInActivity(OtpCodeActivity.this);
    }

    @Override
    public void onBackPressed() {
        JumpTo.goToLogInActivity(OtpCodeActivity.this);
    }

    public void showLoading(){
        loadingDialog.show();
    }

    public void dismissLoading(){
        loadingDialog.dismiss();
    }
}