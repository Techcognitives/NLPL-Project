package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.nlpl.R;

public class ViewTruckDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_truck_details);
    }

    public void onClickBackViewTruckDetails(View view) {
        ViewTruckDetailsActivity.this.finish();
    }
}