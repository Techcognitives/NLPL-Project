package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.nlpl.R;

public class PostALoadActivity extends AppCompatActivity {


    TextView pick_up_date, pick_up_time, budget, select_model, select_feet, select_capacity, select_truck_body_type, pick_up_state, pick_up_city, drop_state, drop_city, auto_calculated_KM;
    EditText pick_up_address, drop_address, pick_up_pinCode, drop_pinCode, note_to_post_load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_aload);

        pick_up_date = (TextView) findViewById(R.id.post_a_load_date_text_view);
        pick_up_time = (TextView) findViewById(R.id.post_a_load_time_text_view);
        budget = (TextView) findViewById(R.id.post_a_load_budget_text_view);
        select_model = (TextView) findViewById(R.id.post_a_load_vehicle_model);
        select_feet = (TextView) findViewById(R.id.post_a_load_feet_text_view);
        select_capacity = (TextView) findViewById(R.id.post_a_load_capacity_text_view);
        select_truck_body_type = (TextView) findViewById(R.id.post_a_load_body_type_text_view);
        pick_up_state = (TextView) findViewById(R.id.post_a_load_pick_up_state_text_view);
        pick_up_city = (TextView) findViewById(R.id.post_a_load_pick_up_city_text_view);
        drop_state = (TextView) findViewById(R.id.post_a_load_drop_state_text_view);
        drop_city = (TextView) findViewById(R.id.post_a_load_drop_city_text_view);
        auto_calculated_KM = (TextView) findViewById(R.id.post_a_load_auto_calculated_km_edit_text);
        pick_up_address = (EditText) findViewById(R.id.post_a_load_address_edit_text);
        drop_address = (EditText) findViewById(R.id.post_a_load_drop_address_text_view);
        pick_up_pinCode = (EditText) findViewById(R.id.post_a_load_pin_code_pick_up_edit_text);
        drop_pinCode = (EditText) findViewById(R.id.post_a_load_drop_pin_edit_text);
        note_to_post_load = (EditText) findViewById(R.id.post_a_load_notes_edit_text);

        
    }
}