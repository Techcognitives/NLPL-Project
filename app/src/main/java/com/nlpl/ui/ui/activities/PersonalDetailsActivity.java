package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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
    Button uploadPAN, uploadF, uploadB, okPersonalDetails;
    ImageView imgPAN, imgF, imgB;
    RadioButton radioAadhar, radioVoter;
    private int GET_FROM_GALLERY=0;
    private int GET_FROM_GALLERY1=1;
    private int GET_FROM_GALLERY2=2;

    View panAndAadharView;

    String driverName, vehicleNo, mobile, name, address, pinCode,city, idProof, bankName, accNo, role;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, isPanUploaded=false, isFrontUploaded=false, isBackUploaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile3");
            name = bundle.getString("name3");
            address = bundle.getString("address");
            pinCode = bundle.getString("pinCode");
            city = bundle.getString("city");
            bankName = bundle.getString("bankName");
            accNo = bundle.getString("accNo");
            vehicleNo = bundle.getString("vehicleNo");
            driverName = bundle.getString("driverName");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            role = bundle.getString("role");
            Log.i("Mobile No", mobile);
            Log.i("Name", name);
        }

        if (isPanUploaded && isFrontUploaded && isBackUploaded){
            okPersonalDetails.setBackgroundResource((R.drawable.button_active));
        }

        action_bar = findViewById(R.id.personal_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setText(getString(R.string.english));
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

                TextView english = languageDialog.findViewById(R.id.english);
                TextView marathi = languageDialog.findViewById(R.id.marathi);
                TextView hindi = languageDialog.findViewById(R.id.hindi);

                english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.english));
                    }
                });

                marathi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.marathi));
                    }
                });

                hindi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getString(R.string.hindi));
                    }
                });

            }
        });

        actionBarTitle.setText("Personal Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalDetailsActivity.this.finish();
            }
        });
//--------------------------------------------------------------------------------------------------
        panAndAadharView = (View) findViewById(R.id.personal_details_pan_and_aadhar);

        panCardText = panAndAadharView.findViewById(R.id.pancard1);
        frontText = panAndAadharView.findViewById(R.id.frontText);
        backText = panAndAadharView.findViewById(R.id.profile_registration_name_text);
        uploadPAN = panAndAadharView.findViewById(R.id.uploadPan);
        uploadF = panAndAadharView.findViewById(R.id.uploadF);
        uploadB = panAndAadharView.findViewById(R.id.uploadB);
        imgPAN = panAndAadharView.findViewById(R.id.imagePan);
        imgF = panAndAadharView.findViewById(R.id.imageF);
        imgB = panAndAadharView.findViewById(R.id.imageB);
        editPAN = panAndAadharView.findViewById(R.id.edit1);
        editFront = panAndAadharView.findViewById(R.id.editFront);
        editBack = panAndAadharView.findViewById(R.id.editBack);
        radioAadhar = panAndAadharView.findViewById(R.id.radioAadhar);
        radioVoter = panAndAadharView.findViewById(R.id.radioVoter);
        okPersonalDetails = findViewById(R.id.okPersonalDetails);

        if (isPersonalDetailsDone){
            panCardText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);

            frontText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);

            backText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadB.setVisibility(View.INVISIBLE);
            editBack.setVisibility(View.VISIBLE);
        }

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

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsActivity.this);
            my_alert.setTitle("PAN Card Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            isPanUploaded = true;

            if (isPanUploaded && isFrontUploaded && isBackUploaded){
                okPersonalDetails.setBackgroundResource(R.drawable.button_active);
            }

            Uri selectedImage = data.getData();
            imgPAN.setImageURI(selectedImage);
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

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsActivity.this);
            my_alert.setTitle("Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            frontText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            isFrontUploaded = true;

            if (isPanUploaded && isFrontUploaded && isBackUploaded){
                okPersonalDetails.setBackgroundResource(R.drawable.button_active);
            }

            Uri selectedImage = data.getData();
            imgF.setImageURI(selectedImage);
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

            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsActivity.this);
            my_alert.setTitle("Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            backText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
            uploadB.setVisibility(View.INVISIBLE);
            editBack.setVisibility(View.VISIBLE);
            isBackUploaded = true;

            if (isPanUploaded && isFrontUploaded && isBackUploaded){
                okPersonalDetails.setBackgroundResource(R.drawable.button_active);
            }

            Uri selectedImage = data.getData();
            imgB.setImageURI(selectedImage);
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
            AlertDialog.Builder my_alert = new AlertDialog.Builder(PersonalDetailsActivity.this);
            my_alert.setTitle("Personal Details added successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent i8 = new Intent(PersonalDetailsActivity.this, ProfileAndRegistrationActivity.class);
                    i8.putExtra("mobile2", mobile);
                    i8.putExtra("name2", name);
                    i8.putExtra("address", address);
                    i8.putExtra("pinCode", pinCode);
                    i8.putExtra("city", city);
                    i8.putExtra("bankName", bankName);
                    i8.putExtra("accNo", accNo);
                    i8.putExtra("vehicleNo", vehicleNo);
                    i8.putExtra("driverName", driverName);
                    i8.putExtra("isPersonal", true);
                    i8.putExtra("isBank", isBankDetailsDone);
                    i8.putExtra("isTrucks", isAddTrucksDone);
                    i8.putExtra("isDriver",isAddDriversDone);
                    i8.putExtra("role", role);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    PersonalDetailsActivity.this.finish();
                }
            });
            my_alert.show();


        }
    }
}