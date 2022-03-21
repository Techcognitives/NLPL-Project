package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.nlpl.R;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.LanguageManager;

public class LanguageActivity extends AppCompatActivity {

    String mobile;
    TextView english, marathi, hindi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile1");
            Log.i("Mobile No Registration", mobile);
        }

        english = findViewById(R.id.english);
        marathi = findViewById(R.id.marathi);
        hindi = findViewById(R.id.hindi);

        LanguageManager lang = new LanguageManager(this);

        english.setOnClickListener(view ->
        {
            lang.updateResource("en");
            JumpTo.goToRegistrationActivity(LanguageActivity.this, mobile, true);
        });

        hindi.setOnClickListener(view ->
        {
            lang.updateResource("hi");
            recreate();
            JumpTo.goToRegistrationActivity(LanguageActivity.this, mobile, true);
        });

        marathi.setOnClickListener(view ->
        {
            lang.updateResource("mr");
            recreate();
            JumpTo.goToRegistrationActivity(LanguageActivity.this, mobile, true);
        });

    }
}