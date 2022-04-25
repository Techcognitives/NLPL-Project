package com.nlpl.ui.activities;

import static com.nlpl.R.id.vehicle_details_vehicle_number_edit2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.nlpl.R;
import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.UploadTruckInsuranceResponse;
import com.nlpl.model.Responses.UploadTruckRCResponse;
import com.nlpl.model.Responses.VehicleVerificationResponse;
import com.nlpl.model.UpdateMethods.UpdateTruckDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectVehicleType;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleDetailsActivity extends AppCompat {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;
    int requestCode;
    int resultCode;
    Intent data;
    EditText vehicleNumberEdit;
    TextView selectModel, selectLoadType;
    ImageView imgRC, imgI;
    String mobile, truckIdPass, driverIdBundle;
    AwesomeValidation awesomeValidation;

    String isDriverDetailsDoneAPI, pathForRC, pathForInsurance;

    Button uploadRC, uploadInsurance;
    TextView textRC, editRC;
    TextView textInsurance, editInsurance, vehicleDetails;
    int GET_FROM_GALLERY = 4;
    int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST1 = 7;
    int CAMERA_PIC_REQUEST2 = 12;

    String userId, truckId, vehicleNumberAPI, truckModelAPI, truckCapacityAPI;
    Boolean isRcEdited = false, isInsuranceEdited = false, fromBidNow = true, isEdit, isRcUploaded = false, isInsurance = false, isAssignTruck = false, vehicleVerified = true;

    private RequestQueue mQueue;

    Dialog previewDialogRcBook, previewDialogInsurance, loadingDialog;
    ImageView previewRcBook, previewInsurance, previewRcBookImageView, previewInsuranceImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            fromBidNow = bundle.getBoolean("fromBidNow");
            truckId = bundle.getString("truckId");
            mobile = bundle.getString("mobile");
            isAssignTruck = bundle.getBoolean("assignTruck");
            driverIdBundle = bundle.getString("driverId");
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

        mQueue = Volley.newRequestQueue(VehicleDetailsActivity.this);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        getUserDetails(userId);

        action_bar = findViewById(R.id.vehicle_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText(getString(R.string.vehicle_details));
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
            }
        });

        vehicleNumberEdit = (EditText) findViewById(vehicle_details_vehicle_number_edit2);

        uploadRC = (Button) findViewById(R.id.vehicle_details_rc_upload);
        textRC = (TextView) findViewById(R.id.vehicle_details_rc_text);
        editRC = (TextView) findViewById(R.id.vehicle_details_edit_rc);
        textInsurance = (TextView) findViewById(R.id.vehicle_details_insurance_text);
        editInsurance = (TextView) findViewById(R.id.vehicle_details_edit_insurance);
        uploadInsurance = (Button) findViewById(R.id.vehicle_details_insurance_upload_button);
        imgRC = findViewById(R.id.vehicle_details_rc_image);
        imgI = findViewById(R.id.vehicle_details_insurance_image);

        previewDialogRcBook = new Dialog(VehicleDetailsActivity.this);
        previewDialogRcBook.setContentView(R.layout.dialog_preview_images);
        previewDialogRcBook.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogInsurance = new Dialog(VehicleDetailsActivity.this);
        previewDialogInsurance.setContentView(R.layout.dialog_preview_images);
        previewDialogInsurance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewInsurance = (ImageView) previewDialogInsurance.findViewById(R.id.dialog_preview_image_view);
        previewRcBook = (ImageView) previewDialogRcBook.findViewById(R.id.dialog_preview_image_view);
        previewRcBookImageView = (ImageView) findViewById(R.id.vehicle_details_preview_rc_book_image_view);
        previewInsuranceImageView = (ImageView) findViewById(R.id.vehicle_details_preview_insurance_image_view);
        vehicleDetails = (TextView) findViewById(R.id.vehicle_details_vehicle_details);

        selectModel = findViewById(R.id.vehicle_details_select_model);
        selectLoadType = findViewById(R.id.vehicle_details_select_feet);

        selectLoadType.addTextChangedListener(loadTypeTextWatcher);


        int maxLength = 10;

        vehicleNumberEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        vehicleNumberEdit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(maxLength)});

        vehicleNumberEdit.addTextChangedListener(vehicleTypeTextWatcher);

        if (isEdit) {
            actionBarTitle.setText(getString(R.string.Edit_Vehicle_Details));
            isRcUploaded = true;
            isInsurance = true;
            uploadRC.setVisibility(View.INVISIBLE);
            uploadInsurance.setVisibility(View.INVISIBLE);

            editInsurance.setVisibility(View.VISIBLE);
            editRC.setVisibility(View.VISIBLE);
            previewRcBook.setVisibility(View.VISIBLE);
            previewInsurance.setVisibility(View.VISIBLE);
            previewInsuranceImageView.setVisibility(View.VISIBLE);
            previewRcBookImageView.setVisibility(View.VISIBLE);

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            getVehicleDetails();
        }

        uploadRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChooseRC();
            }
        });

        editRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRcEdited = true;
                DialogChooseRC();
            }
        });

        uploadInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInsuranceEdited = true;
                DialogChooseInsurance();
            }
        });

        editInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChooseInsurance();
            }
        });
    }

    private void DialogChooseRC() {
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        requestPermissionsForCamera();
        Dialog chooseDialog;

        chooseDialog = new Dialog(VehicleDetailsActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                chooseDialog.dismiss();
            }
        });
    }

    private void DialogChooseInsurance() {

        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        requestPermissionsForCamera();
        Dialog chooseDialog;

        chooseDialog = new Dialog(VehicleDetailsActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST2);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
                chooseDialog.dismiss();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.resultCode = resultCode;
        this.requestCode = requestCode;
        this.data = data;

        rcImagePicker();
        insuranceImagePicker();
        rcImagePickerWithoutAlert();
        insuranceImagePickerWithoutAlert();

    }

    private String rcImagePickerWithoutAlert() {
        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);
            previewRcBook.setVisibility(View.VISIBLE);
            previewInsurance.setVisibility(View.VISIBLE);

            isRcUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath1 = cursor.getString(columnIndex);
            cursor.close();

            Log.i("path on Activity", picturePath1);

            imgRC.setImageURI(selectedImage);

            pathForRC = picturePath1;

            return picturePath1;

        } else if (requestCode == CAMERA_PIC_REQUEST1) {

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);
            previewRcBook.setVisibility(View.VISIBLE);
            previewInsurance.setVisibility(View.VISIBLE);

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                imgRC.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForRC = path;
                isRcUploaded = true;
                return path;
            } catch (Exception e) {
                isRcUploaded = false;
            }


        }
        return "";
    }

    private String insuranceImagePickerWithoutAlert() {
        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {

            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);

            isInsurance = true;
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath2 = cursor.getString(columnIndex);
            cursor.close();

            imgI.setImageURI(selectedImage);

            pathForInsurance = picturePath2;

            return picturePath2;

        } else if (requestCode == CAMERA_PIC_REQUEST2) {

            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);
            isInsurance = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                imgI.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForInsurance = path;
                return path;
            } catch (Exception e) {
                isInsurance = false;
            }

        }
        return "";
    }


    private String rcImagePicker() {
        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(VehicleDetailsActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Truck_Details));
            alertMessage.setText(getString(R.string.RC_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();

                }
            });
            //------------------------------------------------------------------------------------------

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);
            previewRcBook.setVisibility(View.VISIBLE);
            previewInsurance.setVisibility(View.VISIBLE);
            previewInsuranceImageView.setVisibility(View.VISIBLE);
            previewRcBookImageView.setVisibility(View.VISIBLE);

            isRcUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath3 = cursor.getString(columnIndex);
            cursor.close();

            imgRC.setImageURI(selectedImage);
            Log.i("path onActivityResult", picturePath3);
            pathForRC = picturePath3;
            return picturePath3;

        } else if (requestCode == CAMERA_PIC_REQUEST1) {
            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);
            previewRcBook.setVisibility(View.VISIBLE);
            previewInsurance.setVisibility(View.VISIBLE);
            previewInsuranceImageView.setVisibility(View.VISIBLE);
            previewRcBookImageView.setVisibility(View.VISIBLE);

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                imgRC.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForRC = path;
                isRcUploaded = true;

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(VehicleDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Truck_Details));
                alertMessage.setText(getString(R.string.RC_Uploaded_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();

                    }
                });
                //------------------------------------------------------------------------------------------
                return path;

            } catch (Exception e) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(VehicleDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Truck_Details));
                alertMessage.setText(getString(R.string.RC_not_Uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isEdit) {

                        } else {
                            uploadRC.setVisibility(View.VISIBLE);
                            editRC.setVisibility(View.INVISIBLE);
                            previewRcBook.setVisibility(View.INVISIBLE);
                            previewInsurance.setVisibility(View.INVISIBLE);
                            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            isRcEdited = false;
                        }
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        }
        return "";
    }

    private String insuranceImagePicker() {
        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(VehicleDetailsActivity.this);
            alert.setContentView(R.layout.dialog_alert_single_button);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;

            alert.show();
            alert.getWindow().setAttributes(lp);
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Truck_Details));
            alertMessage.setText(getString(R.string.Insurance_Uploaded_Successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();

                }
            });
            //------------------------------------------------------------------------------------------
            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);

            isInsurance = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath4 = cursor.getString(columnIndex);
            cursor.close();

            imgI.setImageURI(selectedImage);
            pathForInsurance = picturePath4;
            return picturePath4;

        } else if (requestCode == CAMERA_PIC_REQUEST2) {

            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);

            isInsurance = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                imgI.setImageBitmap(BitmapFactory.decodeFile(path));
                pathForInsurance = path;

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(VehicleDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Truck_Details));
                alertMessage.setText(getString(R.string.Insurance_Uploaded_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
                return path;
            } catch (Exception e) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(VehicleDetailsActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp);
                alert.setCancelable(true);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Truck_Details));
                alertMessage.setText(getString(R.string.Insurance_not_uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isEdit) {

                        } else {
                            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            uploadInsurance.setVisibility(View.VISIBLE);
                            editInsurance.setVisibility(View.INVISIBLE);
                            isInsurance = false;
                        }
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        }
        return "";
    }


    public void onClickVehicleDetailsOk(View view) {
        if (awesomeValidation.validate() || isRcUploaded) {
            if (selectModel.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Body type", Toast.LENGTH_SHORT).show();
            } else if (selectLoadType.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Load Type", Toast.LENGTH_SHORT).show();
            } else if (!isInsurance) {
                Toast.makeText(this, "Please Upload Insurance", Toast.LENGTH_SHORT).show();
            } else {
                if (isEdit) {
                    if (isRcEdited) {
                        uploadTruckRC(truckId, pathForRC);
                    }
                    if (isInsuranceEdited) {
                        uploadTruckInsurance(truckId, pathForInsurance);
                    }
                    if (vehicleNumberEdit.getText().toString() != null) {
                        UpdateTruckDetails.updateTruckNumber(truckId, vehicleNumberEdit.getText().toString());
                    }
                    if (selectModel.getText().toString() != null) {
                        UpdateTruckDetails.updateTruckModel(truckId, selectModel.getText().toString());
                    }
                    if (selectLoadType.getText().toString() != null) {
                        UpdateTruckDetails.updateTruckCarryingCapacity(truckId, selectLoadType.getText().toString());
                    }
                    JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
                } else {
                    saveTruckDetails();
                }
            }
        } else {
            Toast.makeText(this, "Enter Vehicle Number or Upload RC Book", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveTruckDetails() {
        saveTruck(createTruck());
        //Update User Truck (IsTruckAdded)
        UpdateUserDetails.updateUserIsTruckAdded(userId, "1");
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(VehicleDetailsActivity.this);
        alert.setContentView(R.layout.dialog_alert);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(false);

        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText(getString(R.string.Truck_Details));
        alertMessage.setText(getString(R.string.Vehicle_Details_added_successfully));
        alertPositiveButton.setText(getString(R.string.Add_Driver));
        alertPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromBidNow) {
                    JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, true, true, truckIdPass, null);
                }
                alert.dismiss();
                JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, false, true, truckIdPass, null);
            }
        });

        alertNegativeButton.setText(getString(R.string.Skip));
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                if (isDriverDetailsDoneAPI.equals("1")) {
                    if (fromBidNow) {
                        JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
                    } else {
                        JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
                    }
                } else {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(VehicleDetailsActivity.this);
                    alert.setContentView(R.layout.dialog_alert);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alert.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.gravity = Gravity.CENTER;

                    alert.show();
                    alert.getWindow().setAttributes(lp);
                    alert.setCancelable(false);

                    TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText(getString(R.string.Driver_Details));
                    alertMessage.setText(getString(R.string.You_cannot_bid_unless_you_have_a_Driver));
                    alertPositiveButton.setText(getString(R.string.Add));
                    alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (fromBidNow) {
                                JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, true, false, truckIdPass, null);
                            }
                            alert.dismiss();
                            JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, false, true, truckIdPass, null);
                        }
                    });

                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            if (fromBidNow) {
                                JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
                            } else {
                                JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
                            }
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        });
        //------------------------------------------------------------------------------------------
    }

    //--------------------------------------create vehicle Details in API -------------------------------------
    public AddTruckRequest createTruck() {
        AddTruckRequest addTruckRequest = new AddTruckRequest();
        addTruckRequest.setUser_id(userId);
        if (vehicleNumberEdit.getText().toString().isEmpty()){
            addTruckRequest.setVehicle_no("0");
        }else{
            addTruckRequest.setVehicle_no(vehicleNumberEdit.getText().toString());
        }
        addTruckRequest.setTruck_type(selectModel.getText().toString());
        addTruckRequest.setTruck_carrying_capacity(selectLoadType.getText().toString());
        if (isAssignTruck) {
            addTruckRequest.setDriver_id(driverIdBundle);
        }else{
            addTruckRequest.setDriver_id("0");
        }
        return addTruckRequest;
    }

    public void saveTruck(AddTruckRequest addTruckRequest) {
        Call<AddTruckResponse> addTruckResponseCall = ApiClient.addTruckService().saveTruck(addTruckRequest);
        addTruckResponseCall.enqueue(new Callback<AddTruckResponse>() {
            @Override
            public void onResponse(Call<AddTruckResponse> call, Response<AddTruckResponse> response) {
                AddTruckResponse addTruckResponse = response.body();

                String truckId = addTruckResponse.getData().getTruck_id();
                truckIdPass = addTruckResponse.getData().getTruck_id();

                uploadTruckInsurance(truckId, pathForInsurance);
                uploadTruckRC(truckId, pathForRC);
            }

            @Override
            public void onFailure(Call<AddTruckResponse> call, Throwable t) {
            }
        });
    }
    //-----------------------------------------------------------------------------------------------------

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        Log.i("file uri: ", String.valueOf(fileUri));
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void uploadTruckRC(String truckId, String picPath) {

        File file = new File(picPath);

        MultipartBody.Part body = prepareFilePart("rc", Uri.fromFile(file));

        Call<UploadTruckRCResponse> call = ApiClient.getUploadTruckRCBookService().UploadTruckRCBook(truckId, body);
        call.enqueue(new Callback<UploadTruckRCResponse>() {
            @Override
            public void onResponse(Call<UploadTruckRCResponse> call, Response<UploadTruckRCResponse> response) {

            }

            @Override
            public void onFailure(Call<UploadTruckRCResponse> call, Throwable t) {

            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart1(String partName, Uri fileUri) {

        Log.i("file uri: ", String.valueOf(fileUri));
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void uploadTruckInsurance(String truckId, String picPath) {

        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

        MultipartBody.Part body = prepareFilePart1("insurence", Uri.fromFile(file));

        Call<UploadTruckInsuranceResponse> call = ApiClient.getTuckInsuranceService().uploadTruckInsurance(truckId, body);
        call.enqueue(new Callback<UploadTruckInsuranceResponse>() {
            @Override
            public void onResponse(Call<UploadTruckInsuranceResponse> call, Response<UploadTruckInsuranceResponse> response) {

            }

            @Override
            public void onFailure(Call<UploadTruckInsuranceResponse> call, Throwable t) {

            }
        });
    }

    private String blockCharacterSet = "~#^|$%&*!+@₹_-()':;?/={}";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    private void getVehicleDetails() {

        String url = getString(R.string.baseURL) + "/truck/" + truckId;
        Log.i("Truck Id", truckId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicleNumberAPI = obj.getString("vehicle_no");
                        truckModelAPI = obj.getString("truck_type");
                        truckCapacityAPI = obj.getString("truck_carrying_capacity");

                        vehicleNumberEdit.setText(vehicleNumberAPI);
                        selectModel.setText(truckModelAPI);
                        selectLoadType.setText(truckCapacityAPI);

                        String drivingLicenseURL = obj.getString("rc_book");
                        String insuranceURL = obj.getString("vehicle_insurance");

                        new DownloadImageTask(previewRcBook).execute(drivingLicenseURL);
                        new DownloadImageTask(imgRC).execute(drivingLicenseURL);

                        new DownloadImageTask(previewInsurance).execute(insuranceURL);
                        new DownloadImageTask(imgI).execute(insuranceURL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);

    }

    public void selectVehicleModelFeetCapacity(View view) {
        switch (view.getId()) {
            case R.id.vehicle_details_select_model:
                SelectVehicleType.selectBodyType(VehicleDetailsActivity.this, selectModel, selectLoadType);
                break;

            case R.id.vehicle_details_select_feet:
                SelectVehicleType.selectLoadType(VehicleDetailsActivity.this, selectModel.getText().toString(), selectLoadType);
                break;
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void onClickPreviewRcBook(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogRcBook.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogRcBook.show();
        previewDialogRcBook.getWindow().setAttributes(lp);
    }

    public void onClickPreviewInsurance(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogInsurance.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogInsurance.show();
        previewDialogInsurance.getWindow().setAttributes(lp);
    }

    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(VehicleDetailsActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VehicleDetailsActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(VehicleDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VehicleDetailsActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(VehicleDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VehicleDetailsActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void getUserDetails(String userIds) {
        Dialog loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.show();
        loadingDialog.setCancelable(false);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);

        String url = getString(R.string.baseURL) + "/user/" + userIds;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        isDriverDetailsDoneAPI = obj.getString("isDriver_added");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
    }

    private TextWatcher loadTypeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            vehicleDetails.setText(selectModel.getText().toString() + " - " + selectLoadType.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private TextWatcher vehicleTypeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String truckNumber = vehicleNumberEdit.getText().toString().trim();
            if (truckNumber.length() == 10) {
//                checkVehicle(truckNumber);
                validateNumber();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void validateNumber() {
        awesomeValidation.addValidation(VehicleDetailsActivity.this, R.id.vehicle_details_vehicle_number_edit2,
                RegexTemplate.NOT_EMPTY, R.string.invalid_RC);
        awesomeValidation.addValidation(VehicleDetailsActivity.this, R.id.vehicle_details_vehicle_number_edit2,
                "[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}", R.string.invalid_RC);
        awesomeValidation.validate();
    }

    private void checkVehicle(String vehicleNumbers) {
        Call<VehicleVerificationResponse> vehicleModelCall = ApiClient.getVerification().checkVehicle(userId, vehicleNumbers);
        vehicleModelCall.enqueue(new Callback<VehicleVerificationResponse>() {
            @Override
            public void onResponse(Call<VehicleVerificationResponse> call, Response<VehicleVerificationResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        VehicleVerificationResponse vehicleModel = response.body();
                        VehicleVerificationResponse.UserList list = vehicleModel.getData().get(0);
                        Log.i("Success Message", list.getSuccess());
                        if (list.getSuccess().equals("1")) {
                            vehicleNumberEdit.setEnabled(false);
                            vehicleNumberEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                            vehicleVerified = true;
                        } else {
                            Toast.makeText(VehicleDetailsActivity.this, "Please enter valid Truck Details", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(VehicleDetailsActivity.this, "Please enter valid Truck Details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VehicleDetailsActivity.this, "Please enter valid Truck Details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleVerificationResponse> call, Throwable t) {

            }
        });
    }

}