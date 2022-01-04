package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckRcBook;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckType;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleInsurance;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleNumber;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsTruckAdded;
import com.nlpl.services.AddTruckService;
import com.nlpl.services.UserService;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VehicleDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    EditText vehicleNumberEdit;
    TextView selectModel, selectFt, selectCapacity;
    ImageView openType, closedType, tarpaulinType, imgRC, imgI;
    String bodyTypeSelected, mobile, img_type;

    Dialog selectModelDialog, selectFeetDialog, selectCapacityDialog;

    Button uploadRC, uploadInsurance, okVehicleDetails;
    TextView textRC, editRC;
    TextView textInsurance, editInsurance;
    int GET_FROM_GALLERY = 0;
    int GET_FROM_GALLERY1 = 1;
    int CAMERA_PIC_REQUEST1 = 2;
    int CAMERA_PIC_REQUEST2 = 12;

    private UserService userService;
    private AddTruckService addTruckService;

    String userId, truckId, vehicleNumberAPI, vehicleTypeAPI, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI, truckModelAPI, truckFtAPI, truckCapacityAPI;
    Boolean isEdit, isRcUploaded=false, isInsurance=false, truckSelected=false;
    private RequestQueue mQueue;

    RadioButton openSelected, closeSelected, tarpaulinSelected;

    ArrayList<String> arrayVehicleType, arrayTruckFt, arrayCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            truckId = bundle.getString("truckId");
            mobile = bundle.getString("mobile");
        }

        action_bar = findViewById(R.id.vehicle_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Vehicle Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehicleDetailsActivity.this.finish();
            }
        });

        vehicleNumberEdit = (EditText) findViewById(R.id.vehicle_details_vehicle_number_edit2);
        openType = (ImageView) findViewById(R.id.vehicle_details_open_type);
        closedType = (ImageView) findViewById(R.id.vehicle_details_closed_type);
        tarpaulinType = (ImageView) findViewById(R.id.vehicle_details_tarpaulin_type);

        uploadRC = (Button) findViewById(R.id.vehicle_details_rc_upload);
        textRC = (TextView) findViewById(R.id.vehicle_details_rc_text);
        editRC = (TextView) findViewById(R.id.vehicle_details_edit_rc);

        uploadInsurance = (Button) findViewById(R.id.vehicle_details_insurance_upload_button);
        textInsurance = (TextView) findViewById(R.id.vehicle_details_insurance_text);
        editInsurance = (TextView) findViewById(R.id.vehicle_details_edit_insurance);
        imgRC = findViewById(R.id.vehicle_details_rc_image);
        imgI = findViewById(R.id.vehicle_details_insurance_image);

        openSelected = findViewById(R.id.open_radio_btn);
        closeSelected = findViewById(R.id.closed_radio_btn);
        tarpaulinSelected = findViewById(R.id.tarpaulin_radio_btn);
        selectModel = findViewById(R.id.vehicle_details_select_model);
        selectFt = findViewById(R.id.vehicle_details_select_feet);
        selectCapacity = findViewById(R.id.vehicle_details_select_capacity);

        arrayCapacity = new ArrayList<>();
        arrayVehicleType = new ArrayList<>();
        arrayTruckFt = new ArrayList<>();

        arrayVehicleType.add("Tata");
        arrayVehicleType.add("Mahindra");
        arrayVehicleType.add("Eicher");
        arrayVehicleType.add("Other");

        openSelected.setChecked(false);
        closeSelected.setChecked(false);
        tarpaulinSelected.setChecked(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        addTruckService = retrofit.create(AddTruckService.class);

        okVehicleDetails= findViewById(R.id.vehicle_details_ok_button);

        vehicleNumberEdit.addTextChangedListener(vehicleTextWatecher);

        vehicleNumberEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        vehicleNumberEdit.setFilters(new InputFilter[] { filter });

        mQueue = Volley.newRequestQueue(VehicleDetailsActivity.this);
        if (isEdit){
            getVehicleDetails();
        }

        getVehicleTypeList();
        uploadRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_type = "rc";
                saveImage(imageRequest());
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
        });

        editRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_type = "rc";
                saveImage(imageRequest());
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
        });

        uploadInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_type = "insurance";
                saveImage(imageRequest());
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
        });

        editInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_type = "insurance";
                saveImage(imageRequest());
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
        truckSelected=true;
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(VehicleDetailsActivity.this);
            my_alert.setTitle("RC Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);

            isRcUploaded=true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadImage(picturePath);

            imgRC.setImageURI(selectedImage);

        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
            AlertDialog.Builder my_alert = new AlertDialog.Builder(VehicleDetailsActivity.this);
            my_alert.setTitle("Insurance Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);

            isInsurance=true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadImage(picturePath);

            imgI.setImageURI(selectedImage);

        }else  if (requestCode == CAMERA_PIC_REQUEST1) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(VehicleDetailsActivity.this);
            my_alert.setTitle("RC Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textRC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadRC.setVisibility(View.INVISIBLE);
            editRC.setVisibility(View.VISIBLE);

            isRcUploaded=true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this,image));
            imgRC.setImageBitmap(BitmapFactory.decodeFile(path));
            uploadImage(path);

        } else  if (requestCode == CAMERA_PIC_REQUEST2) {
            AlertDialog.Builder my_alert = new AlertDialog.Builder(VehicleDetailsActivity.this);
            my_alert.setTitle("Insurance Uploaded Successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
            textInsurance.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadInsurance.setVisibility(View.INVISIBLE);
            editInsurance.setVisibility(View.VISIBLE);

            isInsurance=true;
            String vehicleNum = vehicleNumberEdit.getText().toString();
            if (!vehicleNum.isEmpty()&&isRcUploaded && isInsurance && truckSelected ){
                okVehicleDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this,image));
            imgI.setImageBitmap(BitmapFactory.decodeFile(path));
            uploadImage(path);

        }
    }

    public void onClickVehicleDetailsOk(View view) {
        String vehicleNum = vehicleNumberEdit.getText().toString();
        if (!vehicleNum.isEmpty()&&isRcUploaded&&isInsurance&&truckSelected) {
            if (isEdit){

                if (vehicleNumberEdit.getText().toString() != null){
                    updateTruckNumber();
                }
                if (bodyTypeSelected != null){
                    updateTruckType();
                }

            }else{
                saveTruck(createTruck());
            }

            AlertDialog.Builder my_alert = new AlertDialog.Builder(VehicleDetailsActivity.this);
            my_alert.setTitle("Vehicle Details added successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateUserIsTruckAdded();
                    dialogInterface.dismiss();
                    Intent i8 = new Intent(VehicleDetailsActivity.this, ProfileAndRegistrationActivity.class);
                    i8.putExtra("mobile2", mobile);
                    i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i8);
                    overridePendingTransition(0, 0);
                    VehicleDetailsActivity.this.finish();
                }
            });
            my_alert.show();

        }else{
            okVehicleDetails.setBackground(getResources().getDrawable(R.drawable.button_de_active));

        }
    }

    //--------------------------------------create vehicle Details in API -------------------------------------
    public AddTruckRequest createTruck() {
        AddTruckRequest addTruckRequest = new AddTruckRequest();
        addTruckRequest.setUser_id(userId);
        addTruckRequest.setVehicle_no(vehicleNumberEdit.getText().toString());
        addTruckRequest.setVehicle_type(bodyTypeSelected);
        addTruckRequest.setTruck_type(selectModel.getText().toString());
        addTruckRequest.setTruck_ft(selectFt.getText().toString());
        addTruckRequest.setTruck_carrying_capacity(selectCapacity.getText().toString());
        return addTruckRequest;
    }

    public void saveTruck(AddTruckRequest addTruckRequest) {
        Call<AddTruckResponse> addTruckResponseCall = ApiClient.addTruckService().saveTruck(addTruckRequest);
        addTruckResponseCall.enqueue(new Callback<AddTruckResponse>() {
            @Override
            public void onResponse(Call<AddTruckResponse> call, Response<AddTruckResponse> response) {

            }

            @Override
            public void onFailure(Call<AddTruckResponse> call, Throwable t) {

            }
        });
    }
    //-----------------------------------------------------------------------------------------------------

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

    private String blockCharacterSet ="~#^|$%&*!+@₹_-()':;?/={}";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };


    private void getVehicleTypeList(){
        String url = getString(R.string.baseURL) + "/trucktype/getAllTruckType";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_typeAPI = obj.getString("vehicle_type");
                        truck_ftAPI = obj.getString("truck_ft");
                        truck_carrying_capacityAPI = obj.getString("truck_carrying_capacity");

                        arrayTruckFt.add(truck_ftAPI);
                        arrayCapacity.add(truck_carrying_capacityAPI);

                        Log.i("type:", vehicle_typeAPI);

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

                        if (vehicleTypeAPI.equals("Open")){
                            openType.setBackgroundResource(R.drawable.image_view_border_selected);
                            closedType.setBackgroundResource(R.drawable.image_view_border);
                            openSelected.setChecked(true);
                            closeSelected.setChecked(false);
                            tarpaulinSelected.setChecked(false);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                            bodyTypeSelected = "Open";
                        }else if (vehicleTypeAPI.equals("Closed")){
                            openType.setBackgroundResource(R.drawable.image_view_border);
                            closedType.setBackgroundResource(R.drawable.image_view_border_selected);
                            openSelected.setChecked(false);
                            closeSelected.setChecked(true);
                            tarpaulinSelected.setChecked(false);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                            bodyTypeSelected = "Closed";
                        }else if (vehicleTypeAPI.equals("Tarpaulin")){
                            openType.setBackgroundResource(R.drawable.image_view_border);
                            closedType.setBackgroundResource(R.drawable.image_view_border);
                            openSelected.setChecked(false);
                            closeSelected.setChecked(false);
                            tarpaulinSelected.setChecked(true);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border_selected);
                            bodyTypeSelected = "Tarpaulin";
                        }else{

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

    //-------------------------------- Update User is Truck Added ----------------------------------
    private void updateUserIsTruckAdded() {

        UpdateUserIsTruckAdded updateUserIsTruckAdded = new UpdateUserIsTruckAdded("1");

        Call<UpdateUserIsTruckAdded> call = userService.updateUserIsTruckAdded("" + userId, updateUserIsTruckAdded);

        call.enqueue(new Callback<UpdateUserIsTruckAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsTruckAdded> call, Response<UpdateUserIsTruckAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsTruckAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Truck Added ----------------------------------
    private void updateTruckNumber() {

        UpdateTruckVehicleNumber updateTruckVehicleNumber = new UpdateTruckVehicleNumber(vehicleNumberEdit.getText().toString());

        Call<UpdateTruckVehicleNumber> call = addTruckService.updateTruckVehicleNumber("" + truckId, updateTruckVehicleNumber);

        call.enqueue(new Callback<UpdateTruckVehicleNumber>() {
            @Override
            public void onResponse(Call<UpdateTruckVehicleNumber> call, Response<UpdateTruckVehicleNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckVehicleNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Truck Added ----------------------------------
    private void updateTruckRcBook() {

        UpdateTruckRcBook updateTruckRcBook = new UpdateTruckRcBook("1");

        Call<UpdateTruckRcBook> call = addTruckService.updateTruckRcBook("" + truckId, updateTruckRcBook);

        call.enqueue(new Callback<UpdateTruckRcBook>() {
            @Override
            public void onResponse(Call<UpdateTruckRcBook> call, Response<UpdateTruckRcBook> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckRcBook> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Truck Added ----------------------------------
    private void updateTruckInsurance() {

        UpdateTruckVehicleInsurance updateTruckVehicleInsurance = new UpdateTruckVehicleInsurance("1");

        Call<UpdateTruckVehicleInsurance> call = addTruckService.updateTruckVehicleInsurance("" + truckId, updateTruckVehicleInsurance);

        call.enqueue(new Callback<UpdateTruckVehicleInsurance>() {
            @Override
            public void onResponse(Call<UpdateTruckVehicleInsurance> call, Response<UpdateTruckVehicleInsurance> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckVehicleInsurance> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Truck Added ----------------------------------
    private void updateTruckType() {

        UpdateTruckType updateTruckType = new UpdateTruckType(bodyTypeSelected);

        Call<UpdateTruckType> call = addTruckService.updateTruckVehicleType("" + truckId, updateTruckType);

        call.enqueue(new Callback<UpdateTruckType>() {
            @Override
            public void onResponse(Call<UpdateTruckType> call, Response<UpdateTruckType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Truck Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateTruckType> call, Throwable t) {
                Log.i("Not Successful", "User is Truck Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    public void selectVehicleModelFeetCapacity(View view) {
        switch (view.getId()){
            case R.id.vehicle_details_select_model:

                selectModelDialog = new Dialog(VehicleDetailsActivity.this);
                selectModelDialog.setContentView(R.layout.dialog_spinner);
                selectModelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectModelDialog.show();
                TextView model_title = selectModelDialog.findViewById(R.id.dialog_spinner_title);
                model_title.setText("Select Vehicle Model");

                ListView modelList = (ListView) selectModelDialog.findViewById(R.id.list_state);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,R.layout.custom_list_row, arrayVehicleType);
                modelList.setAdapter(adapter1);

                modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectModel.setText(adapter1.getItem(i));

                        selectFeetDialog = new Dialog(VehicleDetailsActivity.this);
                        selectFeetDialog.setContentView(R.layout.dialog_spinner);
                        selectFeetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        selectFeetDialog.show();

                        TextView feetTitle = selectFeetDialog.findViewById(R.id.dialog_spinner_title);
                        feetTitle.setText("Select Vehicle Feet");

                        ListView capacityList = (ListView) selectFeetDialog.findViewById(R.id.list_state);
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(VehicleDetailsActivity.this,R.layout.custom_list_row, arrayTruckFt);
                        capacityList.setAdapter(adapter3);

                        capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectFt.setText(adapter3.getItem(i));

                                selectCapacityDialog = new Dialog(VehicleDetailsActivity.this);
                                selectCapacityDialog.setContentView(R.layout.dialog_spinner);
                                selectCapacityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                selectCapacityDialog.show();

                                TextView capacity_title = selectCapacityDialog.findViewById(R.id.dialog_spinner_title);
                                capacity_title.setText("Select Vehicle Capacity");

                                ListView capacityList = (ListView) selectCapacityDialog.findViewById(R.id.list_state);
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(VehicleDetailsActivity.this,R.layout.custom_list_row, arrayCapacity);
                                capacityList.setAdapter(adapter2);

                                capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        selectCapacity.setText(adapter2.getItem(i));
                                        selectCapacityDialog.dismiss();
                                    }
                                });

                                selectFeetDialog.dismiss();
                            }
                        });


                        selectModelDialog.dismiss();
                    }
                });

                break;

            case R.id.vehicle_details_select_feet:

                selectFeetDialog = new Dialog(VehicleDetailsActivity.this);
                selectFeetDialog.setContentView(R.layout.dialog_spinner);
                selectFeetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectFeetDialog.show();

                TextView feetTitle = selectFeetDialog.findViewById(R.id.dialog_spinner_title);
                feetTitle.setText("Select Vehicle Feet");

                ListView feetList = (ListView) selectFeetDialog.findViewById(R.id.list_state);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.custom_list_row, arrayTruckFt);
                feetList.setAdapter(adapter);

                feetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectFt.setText(adapter.getItem(i));
                        selectFeetDialog.dismiss();

                    }
                });

                break;

            case R.id.vehicle_details_select_capacity:

                selectCapacityDialog = new Dialog(VehicleDetailsActivity.this);
                selectCapacityDialog.setContentView(R.layout.dialog_spinner);
                selectCapacityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectCapacityDialog.show();

                TextView capacity_title = selectCapacityDialog.findViewById(R.id.dialog_spinner_title);
                capacity_title.setText("Select Vehicle Capacity");

                ListView capacityList = (ListView) selectCapacityDialog.findViewById(R.id.list_state);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,R.layout.custom_list_row, arrayCapacity);
                capacityList.setAdapter(adapter2);

                capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectCapacity.setText(adapter2.getItem(i));
                        selectCapacityDialog.dismiss();
                    }
                });

                break;

        }
    }


    //--------------------------------------create image in API -------------------------------------
    public ImageRequest imageRequest() {
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setUser_id(userId);
        imageRequest.setImage_type(img_type);
        return imageRequest;
    }

    public void saveImage(ImageRequest imageRequest) {
        Call<ImageResponse> imageResponseCall = ApiClient.getImageService().saveImage(imageRequest);
        imageResponseCall.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        Log.i("file uri: ", String.valueOf(fileUri));
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("mp3"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    private void uploadImage(String picPath) {

        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

        MultipartBody.Part body = prepareFilePart("file", Uri.fromFile(file));

        Call<UploadImageResponse> call = ApiClient.getImageUploadService().uploadImage(userId,img_type,body);
        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                Log.i("successful:", "success");
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("failed:","failed");
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
}