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
import com.nlpl.model.Requests.AddTruckRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckRcBook;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckType;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleInsurance;
import com.nlpl.model.UpdateTruckDetails.UpdateTruckVehicleNumber;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsTruckAdded;
import com.nlpl.services.AddTruckService;
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

public class VehicleDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    EditText vehicleNumberEdit;
    ImageView openType, closedType, tarpaulinType, imgRC, imgI;
    TextView openText, closedText, tarpaulinText;
    String bodyTypeSelected, mobile;

    Button uploadRC, uploadInsurance, okVehicleDetails;
    TextView textRC, editRC;
    TextView textInsurance, editInsurance;
    int GET_FROM_GALLERY = 0;
    int GET_FROM_GALLERY1 = 1;

    private UserService userService;
    private AddTruckService addTruckService;

    String userId, truckId, vehicleNumberAPI, vehicleTypeAPI;
    Boolean isEdit, isRcUploaded=false, isInsurance=false, truckSelected=false;
    private RequestQueue mQueue;

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

        vehicleNumberEdit = (EditText) findViewById(R.id.vehicle_details_select_model);
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
        imgRC = findViewById(R.id.vehicle_details_rc_image);
        imgI = findViewById(R.id.vehicle_details_insurance_image);

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

        uploadRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            imgRC.setImageURI(selectedImage);
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
            imgI.setImageURI(selectedImage);
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
        addTruckRequest.setVehicle_body_type(bodyTypeSelected);
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
                        vehicleTypeAPI = obj.getString("vehicle_body_type");

                        vehicleNumberEdit.setText(vehicleNumberAPI);

                        if (vehicleTypeAPI.equals("Open")){
                            openType.setBackgroundResource(R.drawable.image_view_border_selected);
                            closedType.setBackgroundResource(R.drawable.image_view_border);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                            openText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                            closedText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            tarpaulinText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            bodyTypeSelected = "Open";
                        }else if (vehicleTypeAPI.equals("Closed")){
                            openType.setBackgroundResource(R.drawable.image_view_border);
                            closedType.setBackgroundResource(R.drawable.image_view_border_selected);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border);
                            openText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            closedText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
                            tarpaulinText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            bodyTypeSelected = "Closed";
                        }else if (vehicleTypeAPI.equals("Tarpaulin")){
                            openType.setBackgroundResource(R.drawable.image_view_border);
                            closedType.setBackgroundResource(R.drawable.image_view_border);
                            tarpaulinType.setBackgroundResource(R.drawable.image_view_border_selected);
                            openText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            closedText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            tarpaulinText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.success,0);
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

}