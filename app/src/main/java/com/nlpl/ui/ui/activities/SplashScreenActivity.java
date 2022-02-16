package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.nlpl.utils.JumpTo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000; //Delay for Animation
    String mobileNoFirebase, role, roleAPI, cityAPI, city, pinCode, pinCodeAPI, phone, userId, mobileNoAPI, userIdAPI, name, nameAPI, addressAPI, address, isRegistrationDoneAPI, isRegistrationDone;
    private FirebaseAuth mFireAuth;
    ArrayList<String> arrayUserId, arrayMobileNo, arrayPinCode, arrayName, arrayRole, arrayCity, arrayAddress, arrayRegDone;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        deleteCache(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        deleteCache(this);

        arrayUserId = new ArrayList<>();
        arrayMobileNo = new ArrayList<>();
        arrayAddress = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayPinCode = new ArrayList<>();
        arrayName = new ArrayList<>();
        arrayRole = new ArrayList<>();
        arrayRegDone = new ArrayList<>();
        mQueue = Volley.newRequestQueue(SplashScreenActivity.this); //To Select Specialty and Credentials

        //------------------------------------- Handler for Animation --------------------------------------
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//------------------------------------ Go to the Sign in Screen ------------------------------------
                mFireAuth = FirebaseAuth.getInstance();
                FirebaseUser mFireBaseUser = mFireAuth.getCurrentUser();

                if (mFireBaseUser != null) {
                    mobileNoFirebase = mFireBaseUser.getPhoneNumber();
                    mobileNoFirebase = mobileNoFirebase.substring(1, 13);
                    Log.i("Mobile Number for JSON", mobileNoFirebase);

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
                                    if (arrayMobileNo.get(j).equals(mobileNoFirebase)) {
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
                                        JumpTo.goToCustomerDashboard(SplashScreenActivity.this, phone, true);
                                    }else{
                                        JumpTo.goToServiceProviderDashboard(SplashScreenActivity.this, phone, true);
                                    }

                                } else {
                                    JumpTo.goToRegistrationActivity(SplashScreenActivity.this, mobileNoFirebase, true);
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

                } else {
                    JumpTo.goToLogInActivity(SplashScreenActivity.this);
                }
            }
        }, SPLASH_SCREEN);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}