package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.LanguageManager;

public class LanguageActivity extends AppCompat {

    String mobile;
    TextView english, marathi, hindi;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile1");
            Log.i("Mobile No Registration", mobile);
        }

        //------------------------------------------------------------------------------------------
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);
        //------------------------------------------------------------------------------------------

        english = findViewById(R.id.english);
        marathi = findViewById(R.id.marathi);
        hindi = findViewById(R.id.hindi);

        LanguageManager lang = new LanguageManager(this);

        english.setOnClickListener(view ->
        {
            lang.updateResource("en");
            JumpTo.goToRegistrationActivity(LanguageActivity.this, mobile, false, null, true);
        });

        hindi.setOnClickListener(view ->
        {
            lang.updateResource("hi");
            recreate();
            JumpTo.goToRegistrationActivity(LanguageActivity.this, mobile, false, null, true);
        });

        marathi.setOnClickListener(view ->
        {
            lang.updateResource("mr");
            recreate();
            JumpTo.goToRegistrationActivity(LanguageActivity.this, mobile, false, null, true);
        });

    }

    public void showLoading(){
        loadingDialog.show();
    }

    public void dismissLoading(){
        loadingDialog.dismiss();
    }
}