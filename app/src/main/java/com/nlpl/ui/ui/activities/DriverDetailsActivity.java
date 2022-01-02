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
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.Requests.AddDriverRequest;
import com.nlpl.model.Responses.AddDriverResponse;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverEmailId;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverName;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverNumber;
import com.nlpl.model.UpdateDriverDetails.UpdateDriverUploadLicense;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsDriverAdded;
import com.nlpl.services.AddDriverService;
import com.nlpl.services.UserService;
import com.nlpl.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    EditText driverName, driverMobile, driverEmailId;
    ImageView actionBarBackButton;

    Button uploadDL, okDriverDetails, uploadSelfie;
    TextView textDL, editDL, series, textDS, editDS;
    int GET_FROM_GALLERY = 0;
    int CAMERA_PIC_REQUEST = 1;
    int CAMERA_PIC_REQUEST1 = 3;
    ImageView driverLicenseImage, driverSelfieImg;

    private RequestQueue mQueue;
    private UserService userService;
    private AddDriverService addDriverService;

    String userId, driverId, driverNameAPI, driverNumberAPI, driverEmailAPI, mobile;
    Boolean isDLUploaded = false, isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            driverId = bundle.getString("driverId");
            mobile = bundle.getString("mobile");
        }


        action_bar = findViewById(R.id.driver_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        driverMobile = findViewById(R.id.driver_details_mobile_number_edit);
        driverName = findViewById(R.id.driver_details_driver_name_edit);
        okDriverDetails = findViewById(R.id.driver_details_ok_button);
        series = (TextView) findViewById(R.id.driver_details_mobile_prefix);
        driverEmailId = (EditText) findViewById(R.id.driver_details_email_edit);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        addDriverService = retrofit.create(AddDriverService.class);

        driverName.addTextChangedListener(driverWatcher);
        driverMobile.addTextChangedListener(driverWatcher);

        driverName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        driverName.setFilters(new InputFilter[]{filter});

        actionBarTitle.setText("Driver Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverDetailsActivity.this.finish();
            }
        });

        uploadDL = findViewById(R.id.driver_details_upload_driver_license);
        uploadSelfie = findViewById(R.id.upload_driver_selfie);
        editDL = findViewById(R.id.driver_details_edit_driver_license);
        textDL = findViewById(R.id.driver_details_driver_license_text_image);
        driverLicenseImage = (ImageView) findViewById(R.id.driver_details_driver_license_image);
        driverSelfieImg = findViewById(R.id.driver_selfie_img);
        textDS = findViewById(R.id.driver_selfie_text);
        editDS = findViewById(R.id.driver_details_edit_selfie_text);

        mQueue = Volley.newRequestQueue(DriverDetailsActivity.this);
        if (isEdit) {
            getDriverDetails();
        }

        uploadSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        editDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        uploadDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog chooseDialog;
                chooseDialog = new Dialog(DriverDetailsActivity.this);
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

        editDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog chooseDialog;
                chooseDialog = new Dialog(DriverDetailsActivity.this);
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
    }

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
            my_alert.setTitle("Driving License uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String driverMobileText = driverMobile.getText().toString();
            String driverNameText = driverName.getText().toString();

            if (!driverNameText.isEmpty() && !driverMobileText.isEmpty() && isDLUploaded) {
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }
            Uri selectedImage = data.getData();
            driverLicenseImage.setImageURI(selectedImage);
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
        } else  if (requestCode == CAMERA_PIC_REQUEST) {
            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
            my_alert.setTitle("Driver Selfie uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textDS.setText("Selfie Uploaded");
            textDS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadSelfie.setVisibility(View.INVISIBLE);
            editDS.setVisibility(View.VISIBLE);

            Bitmap image = (Bitmap) data.getExtras().get("data");
            driverSelfieImg.setImageBitmap(image);
        } else  if (requestCode == CAMERA_PIC_REQUEST1) {
            AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
            my_alert.setTitle("Driving License uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

            textDL.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadDL.setVisibility(View.INVISIBLE);
            editDL.setVisibility(View.VISIBLE);

            isDLUploaded = true;
            String driverMobileText = driverMobile.getText().toString();
            String driverNameText = driverName.getText().toString();

            if (!driverNameText.isEmpty() && !driverMobileText.isEmpty() && isDLUploaded) {
                okDriverDetails.setBackgroundResource(R.drawable.button_active);
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");
            driverLicenseImage.setImageBitmap(image);
        }
    }

    public void onClickDriverDetailsOk(View view) {
        String driverMobileText = driverMobile.getText().toString();
        String driverNameText = driverName.getText().toString();

        if (!driverNameText.isEmpty() && !driverMobileText.isEmpty() && isDLUploaded) {
            if (driverMobileText.length() != 10) {
                AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                my_alert.setTitle("Invalid Mobile Number");
                my_alert.setMessage("Please enter a 10 digit valid mobile number.");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                my_alert.show();

            } else {
                updateUserIsDriverAdded();

                if (isEdit) {
                    if (driverName.getText().toString() != null){
                        updateDriverName();
                    }
                    if (driverMobile.getText().toString() != null){
                        updateDriverNumber();
                    }
                    if (driverEmailId.getText().toString() != null){
                        updateDriverEmailId();
                    }

                } else {
                    saveDriver(createDriver());
                }


                AlertDialog.Builder my_alert = new AlertDialog.Builder(DriverDetailsActivity.this);
                my_alert.setTitle("Driver Details added successfully");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent i8 = new Intent(DriverDetailsActivity.this, ProfileAndRegistrationActivity.class);
                        i8.putExtra("userId", userId);
                        i8.putExtra("mobile2", mobile);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        DriverDetailsActivity.this.finish();
                    }
                });
                my_alert.show();
            }
        }
    }


    //--------------------------------------create Driver Details in API -------------------------------------
    public AddDriverRequest createDriver() {
        AddDriverRequest addDriverRequest = new AddDriverRequest();
        addDriverRequest.setUser_id(userId);
        addDriverRequest.setDriver_name(driverName.getText().toString());
        addDriverRequest.setDriver_number("91" + driverMobile.getText().toString());
        addDriverRequest.setDriver_emailId(driverEmailId.getText().toString());
        return addDriverRequest;
    }

    public void saveDriver(AddDriverRequest addDriverRequest) {
        Call<AddDriverResponse> addDriverResponseCall = ApiClient.addDriverService().saveDriver(addDriverRequest);
        addDriverResponseCall.enqueue(new Callback<AddDriverResponse>() {
            @Override
            public void onResponse(Call<AddDriverResponse> call, Response<AddDriverResponse> response) {

            }

            @Override
            public void onFailure(Call<AddDriverResponse> call, Throwable t) {

            }
        });
    }
    //-----------------------------------------------------------------------------------------------------

    private TextWatcher driverWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String mobileNoWatcher = driverMobile.getText().toString().trim();

            if (mobileNoWatcher.length() == 10) {
                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left));
            } else {
                driverMobile.setBackground(getResources().getDrawable(R.drawable.mobile_number_right_red));
                series.setBackground(getResources().getDrawable(R.drawable.mobile_number_left_red));
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

    private void getDriverDetails() {

        String url = getString(R.string.baseURL) + "/driver/driverId/" + driverId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        driverNameAPI = obj.getString("driver_name");
                        driverNumberAPI = obj.getString("driver_number");
                        driverEmailAPI = obj.getString("driver_emailId");

                        driverName.setText(driverNameAPI);

                        if (driverNumberAPI != null) {
//                            String s1 = driverNumberAPI.substring(2, 12);
                            driverMobile.setText(driverNumberAPI);
                        }

                        if (driverEmailAPI == null) {

                        }else{
                            driverEmailId.setText(driverEmailAPI);
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

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateUserIsDriverAdded() {

        UpdateUserIsDriverAdded updateUserIsDriverAdded = new UpdateUserIsDriverAdded("1");

        Call<UpdateUserIsDriverAdded> call = userService.updateUserIsDriverAdded("" + userId, updateUserIsDriverAdded);

        call.enqueue(new Callback<UpdateUserIsDriverAdded>() {
            @Override
            public void onResponse(Call<UpdateUserIsDriverAdded> call, Response<UpdateUserIsDriverAdded> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsDriverAdded> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverName() {

        UpdateDriverName updateDriverName = new UpdateDriverName(driverName.getText().toString());

        Call<UpdateDriverName> call = addDriverService.updateDriverName("" + driverId, updateDriverName);

        call.enqueue(new Callback<UpdateDriverName>() {
            @Override
            public void onResponse(Call<UpdateDriverName> call, Response<UpdateDriverName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverName> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverUploadLc() {

        UpdateDriverUploadLicense updateDriverUploadLicense = new UpdateDriverUploadLicense(uploadDL.getText().toString());

        Call<UpdateDriverUploadLicense> call = addDriverService.updateDriverUploadLicense("" + driverId, updateDriverUploadLicense);

        call.enqueue(new Callback<UpdateDriverUploadLicense>() {
            @Override
            public void onResponse(Call<UpdateDriverUploadLicense> call, Response<UpdateDriverUploadLicense> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverUploadLicense> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverNumber() {

        UpdateDriverNumber updateDriverNumber = new UpdateDriverNumber(driverMobile.getText().toString());

        Call<UpdateDriverNumber> call = addDriverService.updateDriverNumber("" + driverId, updateDriverNumber);

        call.enqueue(new Callback<UpdateDriverNumber>() {
            @Override
            public void onResponse(Call<UpdateDriverNumber> call, Response<UpdateDriverNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    //-------------------------------- Update User is Driver Added ---------------------------------
    private void updateDriverEmailId() {

        UpdateDriverEmailId updateDriverEmailId = new UpdateDriverEmailId(driverEmailId.getText().toString());

        Call<UpdateDriverEmailId> call = addDriverService.updateDriverEmailId("" + driverId, updateDriverEmailId);

        call.enqueue(new Callback<UpdateDriverEmailId>() {
            @Override
            public void onResponse(Call<UpdateDriverEmailId> call, Response<UpdateDriverEmailId> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Driver Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateDriverEmailId> call, Throwable t) {
                Log.i("Not Successful", "User is Driver Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }


}