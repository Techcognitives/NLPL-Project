package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.UploadTruckInsuranceResponse;
import com.nlpl.model.Responses.UploadTruckRCResponse;
import com.nlpl.model.UpdateMethods.UpdateTruckDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;
    int requestCode;
    int resultCode;
    Intent data;
    EditText vehicleNumberEdit;
    TextView selectModel, selectFt, selectCapacity;
    ImageView openType, closedType, tarpaulinType, imgRC, imgI;
    String bodyTypeSelected, mobile, truckIdPass, driverIdBundle;

    Dialog selectModelDialog, selectFeetDialog, selectCapacityDialog;
    String isDriverDetailsDoneAPI, pathForRC, pathForInsurance;

    Button uploadRC, uploadInsurance, okVehicleDetails;
    TextView textRC, editRC;
    TextView textInsurance, editInsurance;
    int GET_FROM_GALLERY = 4;
    int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST1 = 7;
    int CAMERA_PIC_REQUEST2 = 12;

    String userId, truckId, vehicleNumberAPI, vehicleTypeAPI, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI, truckModelAPI, truckFtAPI, truckCapacityAPI;
    Boolean isRcEdited = false, isInsuranceEdited = false, fromBidNow = true, isEdit, isRcUploaded = false, isInsurance = false, truckSelected = false, isModelSelected = false, isAssignTruck = false;

    RadioButton openSelected, closeSelected, tarpaulinSelected;

    ArrayList<String> arrayVehicleType, arrayTruckFt, updatedArrayTruckFt, arrayCapacity, arrayToDisplayCapacity, arrayTruckFtForCompare, arrayCapacityForCompare;
    private RequestQueue mQueue;

    Dialog previewDialogRcBook, previewDialogInsurance;
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
        mQueue = Volley.newRequestQueue(VehicleDetailsActivity.this);
        getUserDetails(userId);

        action_bar = findViewById(R.id.vehicle_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Vehicle Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);
            }
        });

        vehicleNumberEdit = (EditText) findViewById(R.id.vehicle_details_vehicle_number_edit2);
        openType = (ImageView) findViewById(R.id.vehicle_details_open_type);
        closedType = (ImageView) findViewById(R.id.vehicle_details_closed_type);
        tarpaulinType = (ImageView) findViewById(R.id.vehicle_details_tarpaulin_type);

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

        openSelected = findViewById(R.id.open_radio_btn);
        closeSelected = findViewById(R.id.closed_radio_btn);
        tarpaulinSelected = findViewById(R.id.tarpaulin_radio_btn);
        selectModel = findViewById(R.id.vehicle_details_select_model);
        selectFt = findViewById(R.id.vehicle_details_select_feet);
        selectCapacity = findViewById(R.id.vehicle_details_select_capacity);

        arrayCapacity = new ArrayList<>();
        arrayVehicleType = new ArrayList<>();
        arrayTruckFt = new ArrayList<>();
        arrayTruckFtForCompare = new ArrayList<>();
        arrayCapacityForCompare = new ArrayList<>();
        arrayToDisplayCapacity = new ArrayList<>();
        updatedArrayTruckFt = new ArrayList<>();

        arrayVehicleType.add("Tata");
        arrayVehicleType.add("Mahindra");
        arrayVehicleType.add("Eicher");
        arrayVehicleType.add("Other");

        openSelected.setChecked(false);
        closeSelected.setChecked(false);
        tarpaulinSelected.setChecked(false);

        okVehicleDetails = findViewById(R.id.vehicle_details_ok_button);
//        okVehicleDetails.setEnabled(false);

        vehicleNumberEdit.addTextChangedListener(vehicleTextWatecher);

        vehicleNumberEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        vehicleNumberEdit.setFilters(new InputFilter[]{filter});

        if (isEdit) {

            isRcUploaded = true;
            isInsurance = true;
            truckSelected = true;
            isModelSelected = true;

//            okVehicleDetails.setEnabled(true);
//            okVehicleDetails.setBackgroundResource(R.drawable.button_active);

            uploadRC.setVisibility(View.INVISIBLE);
            uploadInsurance.setVisibility(View.INVISIBLE);

            editInsurance.setVisibility(View.VISIBLE);
            editRC.setVisibility(View.VISIBLE);
            previewRcBook.setVisibility(View.VISIBLE);
            previewInsurance.setVisibility(View.VISIBLE);

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            getVehicleDetails();
        }

        getVehicleTypeList();

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

    public void onClickVehicle(View view) {
        truckSelected = true;
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
            okVehicleDetails.setEnabled(true);
            okVehicleDetails.setBackgroundResource(R.drawable.button_active);
        }
        switch (view.getId()) {
            case R.id.vehicle_details_open_type:
                openType.setBackgroundResource(R.drawable.image_view_border_selected);
                closedType.setBackgroundResource(R.drawable.image_view_border);
                openSelected.setChecked(true);
                closeSelected.setChecked(false);
                tarpaulinSelected.setChecked(false);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                bodyTypeSelected = "Open";
                break;

            case R.id.vehicle_details_closed_type:
                openType.setBackgroundResource(R.drawable.image_view_border);
                closedType.setBackgroundResource(R.drawable.image_view_border_selected);
                openSelected.setChecked(false);
                closeSelected.setChecked(true);
                tarpaulinSelected.setChecked(false);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                bodyTypeSelected = "Closed";
                break;

            case R.id.vehicle_details_tarpaulin_type:
                openType.setBackgroundResource(R.drawable.image_view_border);
                closedType.setBackgroundResource(R.drawable.image_view_border);
                openSelected.setChecked(false);
                closeSelected.setChecked(false);
                tarpaulinSelected.setChecked(true);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border_selected);
                bodyTypeSelected = "Tarpaulin";
                break;
        }
    }

    public void onClickRadioButtons(View view) {
        truckSelected = true;
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
            okVehicleDetails.setEnabled(true);
            okVehicleDetails.setBackgroundResource(R.drawable.button_active);
        }
        switch (view.getId()) {
            case R.id.open_radio_btn:
                openType.setBackgroundResource(R.drawable.image_view_border_selected);
                closedType.setBackgroundResource(R.drawable.image_view_border);
                openSelected.setChecked(true);
                closeSelected.setChecked(false);
                tarpaulinSelected.setChecked(false);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                bodyTypeSelected = "Open";
                break;

            case R.id.closed_radio_btn:
                openType.setBackgroundResource(R.drawable.image_view_border);
                closedType.setBackgroundResource(R.drawable.image_view_border_selected);
                openSelected.setChecked(false);
                closeSelected.setChecked(true);
                tarpaulinSelected.setChecked(false);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                bodyTypeSelected = "Closed";
                break;

            case R.id.tarpaulin_radio_btn:
                openType.setBackgroundResource(R.drawable.image_view_border);
                closedType.setBackgroundResource(R.drawable.image_view_border);
                openSelected.setChecked(false);
                closeSelected.setChecked(false);
                tarpaulinSelected.setChecked(true);
                tarpaulinType.setBackgroundResource(R.drawable.image_view_border_selected);
                bodyTypeSelected = "Tarpaulin";
                break;

        }
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
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

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

            isRcUploaded = true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            imgRC.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForRC = path;
            return path;

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
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

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
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            imgI.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForInsurance = path;
            return path;

        }
        return "";
    }


    private String rcImagePicker() {
        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Truck Details");
            alertMessage.setText("RC Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

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

            isRcUploaded = true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Truck Details");
            alertMessage.setText("RC Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

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

            isRcUploaded = true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            imgRC.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForRC = path;
            return path;

        }
        return "";
    }

    private String insuranceImagePicker() {
        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Truck Details");
            alertMessage.setText("Insurance Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

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
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

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
            alert.setCancelable(true);

            TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText("Truck Details");
            alertMessage.setText("Insurance Uploaded Successfully");
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText("OK");
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

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
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            imgI.setImageBitmap(BitmapFactory.decodeFile(path));
            pathForInsurance = path;
            return path;
        }
        return "";
    }


    public void onClickVehicleDetailsOk(View view) {
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {

            if (isEdit) {

                if (isRcEdited) {
                    uploadTruckRC(truckId, pathForRC);
                }
                if (isInsuranceEdited) {
                    uploadTruckInsurance(truckId, pathForInsurance);
                }


                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_active));

                if (vehicleNumberEdit.getText().toString() != null) {
                    UpdateTruckDetails.updateTruckNumber(truckId, vehicleNumberEdit.getText().toString());
                }
                if (selectModel.getText().toString() != null) {
                    UpdateTruckDetails.updateTruckModel(truckId, selectModel.getText().toString());
                }
                if (selectCapacity.getText().toString() != null) {
                    UpdateTruckDetails.updateTruckCarryingCapacity(truckId, selectCapacity.getText().toString());
                }
                if (bodyTypeSelected != null) {
                    UpdateTruckDetails.updateTruckBodyType(truckId, bodyTypeSelected);
                }
                if (selectFt.getText().toString() != null) {
                    UpdateTruckDetails.updateTruckFeet(truckId, selectFt.getText().toString());
                }
                JumpTo.goToViewVehicleDetailsActivity(VehicleDetailsActivity.this, userId, mobile, true);

            } else {
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

                alertTitle.setText("Truck Details");
                alertMessage.setText("Vehicle Details added successfully");
                alertPositiveButton.setText("+ Add");
                alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowAlert.loadingDialog(VehicleDetailsActivity.this);
                        if (fromBidNow) {
                            JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, true, true, truckIdPass, null);
                        }
                        alert.dismiss();
                        JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, false, true, truckIdPass, null);
                    }
                });

                alertNegativeButton.setText("Skip");
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isDriverDetailsDoneAPI.equals("1")) {
                            if (fromBidNow) {
                                VehicleDetailsActivity.this.finish();
                            } else {
                                ShowAlert.loadingDialog(VehicleDetailsActivity.this);
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

                            alertTitle.setText("Driver Details");
                            alertMessage.setText("You cannot bid unless you have a Driver");
                            alertPositiveButton.setText("+ Add");
                            alertPositiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ShowAlert.loadingDialog(VehicleDetailsActivity.this);
                                    if (fromBidNow) {
                                        JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, true, false, truckIdPass, null);
                                    }
                                    alert.dismiss();
                                    JumpTo.goToDriverDetailsActivity(VehicleDetailsActivity.this, userId, mobile, false, false, true, truckIdPass, null);
                                }
                            });

                            alertNegativeButton.setText("OK");
                            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alert.dismiss();
                                    ShowAlert.loadingDialog(VehicleDetailsActivity.this);
                                    if (fromBidNow) {
                                        VehicleDetailsActivity.this.finish();
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
        } else {
//            okVehicleDetails.setEnabled(false);
//            okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_de_active));
        }
    }

    //--------------------------------------create vehicle Details in API -------------------------------------
    public AddTruckRequest createTruck() {
        AddTruckRequest addTruckRequest = new AddTruckRequest();
        addTruckRequest.setUser_id(userId);
        addTruckRequest.setVehicle_no(vehicleNumberEdit.getText().toString());
        addTruckRequest.setVehicle_type(bodyTypeSelected);
        addTruckRequest.setTruck_type(selectModel.getText().toString());
        addTruckRequest.setTruck_ft(selectFt.getText().toString() + " Ft");
        addTruckRequest.setTruck_carrying_capacity(selectCapacity.getText().toString());
        if (isAssignTruck){
            addTruckRequest.setDriver_id(driverIdBundle);
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

    private TextWatcher vehicleTextWatecher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String vehicleNum = vehicleNumberEdit.getText().toString().trim();
            if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                okVehicleDetails.setEnabled(true);
                okVehicleDetails.setBackgroundResource((R.drawable.button_active));
            } else {
                okVehicleDetails.setEnabled(false);
                okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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

    private void getVehicleCapacityByFeet(String selectedFeet) {
        arrayToDisplayCapacity.clear();
        arrayTruckFtForCompare.clear();
        String url = getString(R.string.baseURL) + "/trucktype/getAllTruckType";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_typeAPI = obj.getString("vehicle_model");
                        truck_ftAPI = obj.getString("truck_ft");
                        truck_carrying_capacityAPI = obj.getString("truck_carrying_capacity");

                        arrayTruckFtForCompare.add(truck_ftAPI);
                        arrayCapacityForCompare.add(truck_carrying_capacityAPI);

                        Log.i("type:", vehicle_typeAPI);

                    }

                    for (int i = 0; i < arrayTruckFtForCompare.size(); i++) {
                        if (selectedFeet.equals(arrayTruckFtForCompare.get(i))) {
                            arrayToDisplayCapacity.add(arrayCapacityForCompare.get(i));
                        }
                    }
                    selectCapacity.setText(arrayToDisplayCapacity.get(0));


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


    private void getVehicleTypeList() {
        String url = getString(R.string.baseURL) + "/trucktype/getAllTruckType";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_typeAPI = obj.getString("vehicle_model");
                        truck_ftAPI = obj.getString("truck_ft");
                        truck_carrying_capacityAPI = obj.getString("truck_carrying_capacity");

                        arrayTruckFt.add(truck_ftAPI);
                        arrayCapacity.add(truck_carrying_capacityAPI);

                        Log.i("type:", vehicle_typeAPI);

                    }
                    int size3 = arrayTruckFt.size();

                    if (size3 == 1) {
                        updatedArrayTruckFt.add(arrayTruckFt.get(0));
                    } else {
                        for (int i = 0; i < size3 - 1; i++) {
                            if (!arrayTruckFt.get(i).equals(arrayTruckFt.get(i + 1))) {
                                updatedArrayTruckFt.add(arrayTruckFt.get(i));
                            }
                        }
                        for (int k = 0; k < size3; k++) {
                            if (k == size3 - 1) {
                                updatedArrayTruckFt.add(arrayTruckFt.get(k));
                            }
                        }
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
                        vehicleTypeAPI = obj.getString("vehicle_type");
                        truckModelAPI = obj.getString("truck_type");
                        truckFtAPI = obj.getString("truck_ft");
                        truckCapacityAPI = obj.getString("truck_carrying_capacity");

                        vehicleNumberEdit.setText(vehicleNumberAPI);
                        selectModel.setText(truckModelAPI);
                        selectFt.setText(truckFtAPI);
                        selectCapacity.setText(truckCapacityAPI);

                        String drivingLicenseURL = obj.getString("rc_book");
                        String insuranceURL = obj.getString("vehicle_insurance");

                        new DownloadImageTask(previewRcBook).execute(drivingLicenseURL);
                        new DownloadImageTask(imgRC).execute(drivingLicenseURL);

                        new DownloadImageTask(previewInsurance).execute(insuranceURL);
                        new DownloadImageTask(imgI).execute(insuranceURL);

                        if (vehicleTypeAPI.equals("Open")) {
                            openType.setBackgroundResource(R.drawable.image_view_border_selected);
                            closedType.setBackgroundResource(R.drawable.image_view_border);
                            openSelected.setChecked(true);
                            closeSelected.setChecked(false);
                            tarpaulinSelected.setChecked(false);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                            bodyTypeSelected = "Open";
                        } else if (vehicleTypeAPI.equals("Closed")) {
                            openType.setBackgroundResource(R.drawable.image_view_border);
                            closedType.setBackgroundResource(R.drawable.image_view_border_selected);
                            openSelected.setChecked(false);
                            closeSelected.setChecked(true);
                            tarpaulinSelected.setChecked(false);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                            bodyTypeSelected = "Closed";
                        } else if (vehicleTypeAPI.equals("Tarpaulin")) {
                            openType.setBackgroundResource(R.drawable.image_view_border);
                            closedType.setBackgroundResource(R.drawable.image_view_border);
                            openSelected.setChecked(false);
                            closeSelected.setChecked(false);
                            tarpaulinSelected.setChecked(true);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border_selected);
                            bodyTypeSelected = "Tarpaulin";
                        } else {

                        }
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
                selectModelDialog = new Dialog(VehicleDetailsActivity.this);
                selectModelDialog.setContentView(R.layout.dialog_spinner);
                selectModelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectModelDialog.show();
                selectModelDialog.setCancelable(true);
                TextView model_title = selectModelDialog.findViewById(R.id.dialog_spinner_title);
                model_title.setText("Select Vehicle Model");

                ListView modelList = (ListView) selectModelDialog.findViewById(R.id.list_state);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayVehicleType);
                modelList.setAdapter(adapter1);

                modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectModel.setText(adapter1.getItem(i));
                        isModelSelected = true;
                        String vehicleNum = vehicleNumberEdit.getText().toString().trim();
                        if (!vehicleNum.isEmpty() && isRcUploaded && isInsurance && truckSelected && isModelSelected) {
                            okVehicleDetails.setEnabled(true);
                            okVehicleDetails.setBackgroundResource((R.drawable.button_active));
                        } else {
                            okVehicleDetails.setEnabled(false);
                            okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                        }
                        if (!isEdit) {
                            selectFeetDialog = new Dialog(VehicleDetailsActivity.this);
                            selectFeetDialog.setContentView(R.layout.dialog_spinner);
                            selectFeetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            selectFeetDialog.show();
                            selectFeetDialog.setCancelable(true);

                            TextView feetTitle = selectFeetDialog.findViewById(R.id.dialog_spinner_title);
                            feetTitle.setText("Select Vehicle Feet");

                            ListView capacityList = (ListView) selectFeetDialog.findViewById(R.id.list_state);
                            ArrayAdapter<String> adapter3 = new ArrayAdapter<>(VehicleDetailsActivity.this, R.layout.custom_list_row, updatedArrayTruckFt);
                            capacityList.setAdapter(adapter3);

                            capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    selectFeetDialog.dismiss();
                                    selectFt.setText(adapter3.getItem(i));
                                    getVehicleCapacityByFeet(selectFt.getText().toString());
                                    selectModelDialog.dismiss();
                                }
                            });
                        } else {
                            selectModelDialog.dismiss();
                        }
                    }
                });

                break;

            case R.id.vehicle_details_select_feet:

                arrayToDisplayCapacity.clear();
                selectFeetDialog = new Dialog(VehicleDetailsActivity.this);
                selectFeetDialog.setContentView(R.layout.dialog_spinner);
                selectFeetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectFeetDialog.show();
                selectFeetDialog.setCancelable(true);

                TextView feetTitle = selectFeetDialog.findViewById(R.id.dialog_spinner_title);
                feetTitle.setText("Select Vehicle Feet");

                ListView feetList = (ListView) selectFeetDialog.findViewById(R.id.list_state);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_row, updatedArrayTruckFt);
                feetList.setAdapter(adapter);

                feetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectFt.setText(adapter.getItem(i));
                        getVehicleCapacityByFeet(selectFt.getText().toString());
                        selectFeetDialog.dismiss();

                    }
                });

                break;

            case R.id.vehicle_details_select_capacity:
                if (arrayToDisplayCapacity.size() == 1) {

                } else {
                    selectCapacityDialog = new Dialog(VehicleDetailsActivity.this);
                    selectCapacityDialog.setContentView(R.layout.dialog_spinner);
                    selectCapacityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    selectCapacityDialog.show();
                    selectCapacityDialog.setCancelable(true);

                    TextView capacity_title = selectCapacityDialog.findViewById(R.id.dialog_spinner_title);
                    capacity_title.setText("Select Vehicle Capacity");

                    ListView capacityList = (ListView) selectCapacityDialog.findViewById(R.id.list_state);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayToDisplayCapacity);
                    capacityList.setAdapter(adapter2);

                    capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            selectCapacity.setText(adapter2.getItem(i));
                            selectCapacityDialog.dismiss();
                        }
                    });
                }
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
        ShowAlert.loadingDialog(VehicleDetailsActivity.this);
        JumpTo.goToServiceProviderDashboard(VehicleDetailsActivity.this, mobile, true);

    }

}