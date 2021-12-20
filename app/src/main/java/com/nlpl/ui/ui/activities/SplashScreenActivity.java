package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nlpl.R;
import com.razorpay.Checkout;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000; //Delay for Animation
    String mobileNoFirebase;
    private FirebaseAuth mFireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //------------------------------------- Handler for Animation --------------------------------------
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//------------------------------------ Go to the Sign in Screen ------------------------------------
                mFireAuth = FirebaseAuth.getInstance();
                FirebaseUser mFireBaseUser = mFireAuth.getCurrentUser();

                if (mFireBaseUser != null ){
                    mobileNoFirebase = mFireBaseUser.getPhoneNumber();
                    mobileNoFirebase= mobileNoFirebase.substring(1,13);
                    Log.i("Mobile Number for JSON", mobileNoFirebase);
                    Intent i8 = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
                    i8.putExtra("mobile1", mFireBaseUser.getPhoneNumber());
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    SplashScreenActivity.this.finish();

                }else {
                    Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
                    startActivity(intent);
                    SplashScreenActivity.this.finish();
                }
            }
        }, SPLASH_SCREEN);
    }
}