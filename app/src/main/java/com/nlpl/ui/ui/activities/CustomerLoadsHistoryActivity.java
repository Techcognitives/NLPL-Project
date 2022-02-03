package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

public class CustomerLoadsHistoryActivity extends AppCompatActivity {
    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    String phone, userId;
    TextView loadsCompleted, loadsExpired;
    ConstraintLayout loadExpiredConstrain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_loads_history);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
        }

        //----------------------------Action Bar----------------------------------------------------
        actionBar = findViewById(R.id.customer_dashboard_load_history_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Loads History");
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLoadsHistoryActivity.this, CustomerDashboardActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("mobile", phone);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        //------------------------------------------------------------------------------------------

        loadsExpired = findViewById(R.id.customer_dashboard_loads_expired_button);
        loadsCompleted = findViewById(R.id.customer_dashboard_load_completed_button);
        loadExpiredConstrain = findViewById(R.id.customer_dashboard_loads_expired_constrain);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i8 = new Intent(CustomerLoadsHistoryActivity.this, CustomerDashboardActivity.class);
        i8.putExtra("mobile", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);
    }

    public void onClickLoadsCompleted(View view) {
        loadExpiredConstrain.setVisibility(View.INVISIBLE);
        loadsCompleted.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
        loadsExpired.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
    }

    public void onClickLoadsExpired(View view) {
        loadExpiredConstrain.setVisibility(View.VISIBLE);
        loadsExpired.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
        loadsCompleted.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
    }
}