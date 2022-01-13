package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

public class CustomerDashboardActivity extends AppCompatActivity {

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    ConstraintLayout loadAcceptedConstrain, bidsReceivedConstrain;
    TextView loadAcceptedTextView, bidsReceivedTextView;

    String userId, phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
        }

        actionBar = findViewById(R.id.profile_registration_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Load Poster Dashboard");
        actionBarBackButton.setVisibility(View.GONE);

        bottomNav = (View) findViewById(R.id.profile_registration_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));

        loadAcceptedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_loads_accepted_constrain);
        bidsReceivedConstrain = (ConstraintLayout) findViewById(R.id.customer_dashboard_bids_received_constrain);
        loadAcceptedTextView = (TextView) findViewById(R.id.customer_dashboard_loads_accepted_button);
        bidsReceivedTextView = (TextView) findViewById(R.id.customer_dashboard_bids_received_button);
    }

    public void onClickBidsAndLoads(View view) {
        switch (view.getId()) {
            case R.id.customer_dashboard_bids_received_button:
                loadAcceptedConstrain.setVisibility(View.INVISIBLE);
                bidsReceivedConstrain.setVisibility(View.VISIBLE);
                loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                break;

            case R.id.customer_dashboard_loads_accepted_button:
                loadAcceptedConstrain.setVisibility(View.VISIBLE);
                bidsReceivedConstrain.setVisibility(View.INVISIBLE);
                loadAcceptedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
                bidsReceivedTextView.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
                break;
        }
    }

    public void onClickPostALoad(View view) {
        Intent intent = new Intent(CustomerDashboardActivity.this, PostALoadActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        startActivity(intent);
    }

    public void onClickBottomNavigation(View view){
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                Intent intent = new Intent(CustomerDashboardActivity.this, DashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);

                break;

            case R.id.bottom_nav_customer_dashboard:

                break;
        }
    }
}