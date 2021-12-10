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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class VehicleDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    EditText vehicleNumberEdit;
    ImageView openType, closedType, tarpaulinType;
    TextView openText, closedText, tarpaulinText;
    String bodyTypeSelected;

    Button uploadRC, uploadInsurance, okVehicleDetails;
    TextView textRC, editRC;
    TextView textInsurance, editInsurance;
    int GET_FROM_GALLERY = 0;
    int GET_FROM_GALLERY1 = 1;

    String mobile, name;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone, isRcUploaded=false, isInsurance=false, truckSelected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

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


        action_bar = findViewById(R.id.vehicle_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setText(getString(R.string.english));
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(VehicleDetailsActivity.this);
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

        actionBarTitle.setText("Vehicle Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehicleDetailsActivity.this.finish();
            }
        });

        vehicleNumberEdit = (EditText) findViewById(R.id.vehicle_details_vehicle_number_edit);
        openType = (ImageView) findViewById(R.id.vehicle_details_open_type);
        closedType = (ImageView) findViewById(R.id.vehicle_details_closed_type);
        tarpaulinType = (ImageView) findViewById(R.id.vehicle_details_tarpaulin_type);
        openText = (TextView) findViewById(R.id.vehicle_details_open_text);
        closedText = (TextView) findViewById(R.id.vehicle_details_closed_text);
        tarpaulinText = (TextView) findViewById(R.id.vehicle_details_tarpaulin_text);

        uploadRC = (Button) findViewById(R.id.vehicle_details_rc_upload);
        textRC = (TextView) findViewById(R.id.vehicle_details_rc_text);
        editRC = (TextView) findViewById(R.id.vehicle_details_edit_rc);

        uploadInsurance = (Button) findViewById(R.id.vehicle_details_insurance_upload_button);
        textInsurance = (TextView) findViewById(R.id.vehicle_details_insurance_text);
        editInsurance = (TextView) findViewById(R.id.vehicle_details_edit_insurance);

        okVehicleDetails= findViewById(R.id.vehicle_details_ok_button);

        vehicleNumberEdit.addTextChangedListener(vehicleTextWatecher);

        uploadRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        editRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        uploadInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });

        editInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
            }
        });
    }

    public void onClickVehicle(View view) {
        truckSelected=true;
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
            okVehicleDetails.setBackgroundResource(R.drawable.button_active);
        }
        switch (view.getId()) {
            case R.id.vehicle_details_open_type:
                openType.setBackgroundResource(R.drawable.image_view_border_selected);
                closedType.setBackgroundResource(R.drawable.image_view_border);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                openText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                closedText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                tarpaulinText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                bodyTypeSelected = "Open";
                break;

            case R.id.vehicle_details_closed_type:
                openType.setBackgroundResource(R.drawable.image_view_border);
                closedType.setBackgroundResource(R.drawable.image_view_border_selected);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                openText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                closedText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
                tarpaulinText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                bodyTypeSelected = "Closed";
                break;

            case R.id.vehicle_details_tarpaulin_type:
                openType.setBackgroundResource(R.drawable.image_view_border);
                closedType.setBackgroundResource(R.drawable.image_view_border);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border_selected);
                openText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                closedText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                tarpaulinText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
                bodyTypeSelected = "Tarpaulin";
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);

            isRcUploaded=true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

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
        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);

            isInsurance=true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

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

    public void onClickVehicleDetailsOk(View view) {
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty()&&isRcUploaded&&isInsurance&&truckSelected) {
            Intent i8 = new Intent(VehicleDetailsActivity.this, ProfileAndRegistrationActivity.class);
            i8.putExtra("mobile2", mobile);
            i8.putExtra("name2", name);
            i8.putExtra("isPersonal", isPersonalDetailsDone);
            i8.putExtra("isBank", isBankDetailsDone);
            i8.putExtra("isTrucks", true);
            i8.putExtra("isDriver", isAddDriversDone);
            i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i8);
            overridePendingTransition(0, 0);
            VehicleDetailsActivity.this.finish();
        }else{
            okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_de_active));

        }
    }

    private TextWatcher vehicleTextWatecher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String vehicleNum = vehicleNumberEdit.getText().toString().trim();
            if (!vehicleNum.isEmpty()&&isRcUploaded&&isInsurance&&truckSelected) {
                okVehicleDetails.setBackgroundResource((R.drawable.button_active));
            }else{
                okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_de_active));

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


}