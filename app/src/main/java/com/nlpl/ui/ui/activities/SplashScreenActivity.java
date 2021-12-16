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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //------------------------------------- Handler for Animation --------------------------------------
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//------------------------------------ Go to the Sign in Screen ------------------------------------
                Intent intent = new Intent(SplashScreenActivity.this, PersonalDetailsAndIdProofActivity.class);
                startActivity(intent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_SCREEN);
    }
}