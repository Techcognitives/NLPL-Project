package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DriverDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    Button uploadDL;
    TextView textDL, editDL;
    int GET_FROM_GALLERY=0;

    String mobile, name;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile3");
            name = bundle.getString("name3");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            Log.i("Mobile No", mobile);
            Log.i("Name", name);
        }

        action_bar = findViewById(R.id.driver_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(DriverDetailsActivity.this);
                languageDialog.setContentView(R.layout.dialog_language);
                languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(languageDialog.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp2.gravity = Gravity.BOTTOM;

                languageDialog.show();
                languageDialog.getWindow().setAttributes(lp2);

            }
        });

        actionBarTitle.setText("Driver Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverDetailsActivity.this.finish();
            }
        });

        uploadDL = findViewById(R.id.uploadDL);
        editDL = findViewById(R.id.editDL);
        textDL = findViewById(R.id.textDL);

        uploadDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
//            imgPAN.setImageURI(selectedImage);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void onClickDriverDetailsOk(View view) {
        Intent i8 = new Intent(DriverDetailsActivity.this, ProfileAndRegistrationActivity.class);
        i8.putExtra("mobile2", mobile);
        i8.putExtra("name2", name);
        i8.putExtra("isPersonal", isPersonalDetailsDone);
        i8.putExtra("isBank", isBankDetailsDone);
            i8.putExtra("isTrucks", isAddTrucksDone);
            i8.putExtra("isDriver",true);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        overridePendingTransition(0, 0);
        DriverDetailsActivity.this.finish();
    }
}