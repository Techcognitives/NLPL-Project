package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nlpl.R;
import com.nlpl.utils.JumpTo;

public class FindTrucksActivity extends AppCompatActivity {

    String phone, userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trucks);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
            Log.i("userId find loads", userId);
        }
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                break;

            case R.id.bottom_nav_customer_dashboard:
                JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
                break;
        }
    }
}