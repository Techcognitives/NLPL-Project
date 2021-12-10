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
import android.widget.RadioButton;
import android.widget.TextView;

import com.nlpl.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PersonalDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    TextView panCardText, editPAN, editBack, editFront, frontText, backText;
    Button uploadPAN, uploadF, uploadB;
    ImageView imgPAN, imgF, imgB;
    RadioButton radioAadhar, radioVoter;
    private int GET_FROM_GALLERY=0;
    private int GET_FROM_GALLERY1=1;
    private int GET_FROM_GALLERY2=2;

    String mobile, name, idProof;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, isPanUploaded=false, isFrontUploaded=false, isBackUploaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

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

        action_bar = findViewById(R.id.personal_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(PersonalDetailsActivity.this);
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

        actionBarTitle.setText("Personal Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalDetailsActivity.this.finish();
            }
        });

        panCardText = findViewById(R.id.pancard1);
        frontText = findViewById(R.id.frontText);
        backText = findViewById(R.id.backText);
        uploadPAN = findViewById(R.id.uploadPan);
        uploadF = findViewById(R.id.uploadF);
        uploadB = findViewById(R.id.uploadB);
        imgPAN = findViewById(R.id.imagePan);
        imgF = findViewById(R.id.imageF);
        imgB = findViewById(R.id.imageB);
        editPAN = findViewById(R.id.edit1);
        editFront = findViewById(R.id.editFront);
        editBack = findViewById(R.id.editBack);
        radioAadhar = findViewById(R.id.radioAadhar);
        radioVoter = findViewById(R.id.radioVoter);

        uploadPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        uploadF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });

        editFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });

        uploadB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
            }
        });
        editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
            }
        });

        radioVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioAadhar.setChecked(false);
                radioVoter.setChecked(true);
                frontText.setText("Voter ID Front");
                backText.setText("Voter ID Back");
                imgF.setImageDrawable(getDrawable(R.drawable.voter_id_front));
                imgB.setImageDrawable(getDrawable(R.drawable.voter_id_back));
                idProof = "voter";
            }
        });

        radioAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioAadhar.setChecked(true);
                radioVoter.setChecked(false);
                frontText.setText("Aadhar Front");
                backText.setText("Aadhar Back");
                imgF.setImageDrawable(getDrawable(R.drawable.aadhar_card_front));
                imgB.setImageDrawable(getDrawable(R.drawable.aadhar_card_back));
                idProof = "aadhar";
            }
        });
    }

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            panCardText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            isPanUploaded = true;

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
        }else if (requestCode==GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK){
            frontText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            isFrontUploaded = true;

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
        }else if (requestCode==GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK){
            backText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadB.setVisibility(View.INVISIBLE);
            editBack.setVisibility(View.VISIBLE);
            isBackUploaded = true;

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
    //-------------------------------------------------------------------------------------------------------------------

    public void onClickOKPersonal(View view) {
        if (isPanUploaded && isFrontUploaded && isBackUploaded){
            Intent i8 = new Intent(PersonalDetailsActivity.this, ProfileAndRegistrationActivity.class);
            i8.putExtra("mobile2", mobile);
            i8.putExtra("name2", name);
            i8.putExtra("isPersonal", true);
            i8.putExtra("isBank", isBankDetailsDone);
            i8.putExtra("isTrucks", isAddTrucksDone);
            i8.putExtra("isDriver",isAddDriversDone);
            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i8);
            overridePendingTransition(0, 0);
            PersonalDetailsActivity.this.finish();
        }
    }
}