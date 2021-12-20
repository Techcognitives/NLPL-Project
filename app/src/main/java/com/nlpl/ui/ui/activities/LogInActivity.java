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

    private RequestQueue mQueue;
    EditText mobileNo;
    TextView series;
    Button getStarted;
    String role, roleAPI, cityAPI, city, pinCode, pinCodeAPI, phone, mobile, userId, mobileNoAPI, mobileNoFirebase, userIdAPI, name, nameAPI, addressAPI, address, isRegistrationDoneAPI, isRegistrationDone;

    ArrayList<String> arrayUserId, arrayMobileNo;
    private FirebaseAuth mFireAuth;

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

        mQueue = Volley.newRequestQueue(LogInActivity.this); //To Select Specialty and Credentials
        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();

        mFireAuth = FirebaseAuth.getInstance();
        FirebaseUser mFireBaseUser = mFireAuth.getCurrentUser();

        mobileNoFirebase = mFireBaseUser.getPhoneNumber();
        mobileNoFirebase= mobileNoFirebase.substring(1,13);
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL)+"/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request =new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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

                       /* Log.i("user Id:", userIdAPI);
                        Log.i("mobileNo:",mobileNoAPI);
                        Log.i("NameAPI:",nameAPI);
                        Log.i("addressAPI:",addressAPI);
                        Log.i("iaRegDone:",isRegistrationDoneAPI);*/
                        Log.i("arrayOfMobileNoAPI", String.valueOf(arrayMobileNo));

                        for (int j = 0; j < arrayMobileNo.size(); j++) {
                            if (arrayMobileNo.get(j).equals(mobileNoFirebase)) {
                                userId = userIdAPI;
                                name = nameAPI;
                                phone = mobileNoAPI;
                                address = addressAPI;
                                pinCode = pinCodeAPI;
                                city = cityAPI;
                                role = roleAPI;
                                isRegistrationDone = isRegistrationDoneAPI;
                                Log.i("userIDAPI:", userId);
                                Log.i("userName", name);
                                Log.i("isregDone:", isRegistrationDone);


                                if (isRegistrationDone.equals("1")) {
                                    Intent i8 = new Intent(LogInActivity.this, ProfileAndRegistrationActivity.class);
                                    i8.putExtra("mobile2", phone);
                                    i8.putExtra("name2", name);
                                    i8.putExtra("address", address);
                                    i8.putExtra("pinCode", pinCode);
                                    i8.putExtra("city", city);
                                    i8.putExtra("bankName", "bankName");
                                    i8.putExtra("accNo", "accNo");
                                    i8.putExtra("vehicleNo", "vehicleNo");
                                    i8.putExtra("driverName", "driverName");
                                    i8.putExtra("isPersonal", false);
                                    i8.putExtra("isBank", false);
                                    i8.putExtra("isTrucks", false);
                                    i8.putExtra("isDriver",false);
                                    i8.putExtra("role", role);
//                                i8.putExtra("role", role);
                                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i8);
                                    overridePendingTransition(0, 0);

                                } else {
                                    Log.i("New User", "New User");
                                }

                            }
                        }

                    }
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