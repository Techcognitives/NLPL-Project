package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nlpl.R;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000; //Delay for Animation
    String mobileNoFirebase, role, roleAPI, cityAPI, city, pinCode, pinCodeAPI, phone, userId, mobileNoAPI, userIdAPI, name, nameAPI, addressAPI, address, isRegistrationDoneAPI, isRegistrationDone;
    private FirebaseAuth mFireAuth;
    ArrayList<String> arrayUserId, arrayMobileNo;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();
        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        mQueue = Volley.newRequestQueue(SplashScreenActivity.this); //To Select Specialty and Credentials

        //------------------------------------- Handler for Animation --------------------------------------
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//------------------------------------ Go to the Sign in Screen ------------------------------------
                mFireAuth = FirebaseAuth.getInstance();
                FirebaseUser mFireBaseUser = mFireAuth.getCurrentUser();

                if (mFireBaseUser!=null){
                mobileNoFirebase = mFireBaseUser.getPhoneNumber();
                mobileNoFirebase= mobileNoFirebase.substring(1,13);
                Log.i("Mobile Number for JSON", mobileNoFirebase);

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
//                                Log.i("arrayOfMobileNoAPI", String.valueOf(arrayMobileNo));

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
                                            Intent i8 = new Intent(SplashScreenActivity.this, ProfileAndRegistrationActivity.class);
                                            i8.putExtra("mobile2", phone);
                                            i8.putExtra("name2", name);
                                            i8.putExtra("address", address);
                                            i8.putExtra("pinCode", pinCode);
                                            i8.putExtra("userId", userId);
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
                                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i8);
                                            overridePendingTransition(0, 0);
                                            finish();

                                        } else {
                                            Intent i8 = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
                                            i8.putExtra("mobile1", phone);
                                            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i8);
                                            overridePendingTransition(0, 0);
                                            finish();
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

                }else {
                    Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_SCREEN);
    }
}