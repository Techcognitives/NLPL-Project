package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.ui.ui.adapters.DriversAdapter;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewDriverDetailsActivity extends AppCompatActivity {

    private ArrayList<DriverModel> driverList = new ArrayList<>();
    private DriversAdapter driverListAdapter;
    private RecyclerView driverListRecyclerView;
    private RequestQueue mQueue;

    ArrayList<String> arrayUserDriverId, arrayDriverMobileNo;
    Dialog previewDialogDriverDetails;
    TextView previewDriverDetailsDriverBankAdd;
    TextView previewDriverDetailsDriverName, previewDriverDetailsDriverNumber, previewDriverDetailsEmailId;
    ImageView previewDrivingLicense, previewDriverSelfie;
    TextView addDriver;
    String mobileNoDriverAPI, userDriverIdAPI, driverUserIdGet;

    String phone, userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver_details);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }
        mQueue = Volley.newRequestQueue(ViewDriverDetailsActivity.this);

        addDriver = findViewById(R.id.addDriverDone);

        previewDialogDriverDetails = new Dialog(ViewDriverDetailsActivity.this);
        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_details);
        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverDetailsDriverName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_name_text_view);
        previewDriverDetailsDriverNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_phone_number_text_view);
        previewDriverDetailsEmailId = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driver_email_id_text_view);
        previewDrivingLicense = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_driving_license_image_view);
        previewDriverSelfie = (ImageView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_selfie_image_view);
        previewDriverDetailsDriverBankAdd = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_add_driver_bank);

        previewDriverDetailsDriverBankAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewDriverDetailsActivity.this, BankDetailsActivity.class);
                intent.putExtra("isEdit", false);
                intent.putExtra("userId", driverUserIdGet);
                intent.putExtra("mobile", phone);
                startActivity(intent);
            }
        });

        //---------------------------- Get Driver Details -------------------------------------------
        driverListRecyclerView = (RecyclerView) findViewById(R.id.driver_list_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversAdapter(ViewDriverDetailsActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);
        getDriverDetailsList();
        //------------------------------------------------------------------------------------------
    }

    public void getDriverDetailsList() {
        //---------------------------- Get Driver Details ------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/userId/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    driverList = new ArrayList<>();
                    JSONArray driverLists = response.getJSONArray("data");
                    for (int i = 0; i < driverLists.length(); i++) {
                        JSONObject obj = driverLists.getJSONObject(i);
                        DriverModel modelDriver = new DriverModel();
                        modelDriver.setUser_id(obj.getString("user_id"));
                        modelDriver.setDriver_id(obj.getString("driver_id"));
                        modelDriver.setDriver_name(obj.getString("driver_name"));
                        modelDriver.setUpload_lc(obj.getString("upload_dl"));
                        modelDriver.setDriver_selfie(obj.getString("driver_selfie"));
                        modelDriver.setDriver_number(obj.getString("driver_number"));
                        modelDriver.setDriver_emailId(obj.getString("driver_emailId"));
                        driverList.add(modelDriver);
                    }
                    if (driverList.size() > 0) {
                        driverListAdapter.updateData(driverList);
                    } else {

                    }

//                    if (driverList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        driverListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        driverListRecyclerView.setLayoutParams(params);
//                    }

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
        //-------------------------------------------------------------------------------------------
    }

    public void getDriverDetails(DriverModel obj) {
        Intent intent = new Intent(ViewDriverDetailsActivity.this, DriverDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("driverId", obj.getDriver_id());
        intent.putExtra("mobile", phone);

        startActivity(intent);
    }

    public void onClickPreviewDriverLicense(DriverModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogDriverDetails.show();
        previewDialogDriverDetails.getWindow().setAttributes(lp);
        previewDriverDetailsDriverName.setText(" Driver Name: " + obj.getDriver_name());
        previewDriverDetailsDriverNumber.setText(" Mobile Number: +" + obj.getDriver_number());
        previewDriverDetailsEmailId.setText(" Email Id: " + obj.getDriver_emailId());

        getUserDriverId(obj.getDriver_number());

        String drivingLicenseURL = obj.getUpload_lc();
        Log.i("IMAGE DL URL", drivingLicenseURL);
        new DownloadImageTask(previewDrivingLicense).execute(drivingLicenseURL);

        String selfieURL = obj.getDriver_selfie();
        Log.i("IMAGE Selfie URL", selfieURL);
        new DownloadImageTask(previewDriverSelfie).execute(selfieURL);
    }

    private void getUserDriverId(String getMobile) {
        String receivedMobile = getMobile;
        //------------------------------get user details by mobile Number---------------------------------
        //-----------------------------------Get User Details---------------------------------------
        String url = getString(R.string.baseURL) + "/user/get";
        Log.i("URL at Profile:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userDriverIdAPI = data.getString("user_id");
                        mobileNoDriverAPI = data.getString("phone_number");
                        arrayUserDriverId = new ArrayList<>();
                        arrayDriverMobileNo = new ArrayList<>();
                        arrayUserDriverId.add(userDriverIdAPI);
                        arrayDriverMobileNo.add(mobileNoDriverAPI);
                    }

                    for (int j = 0; j < arrayDriverMobileNo.size(); j++) {
                        if (arrayDriverMobileNo.get(j).equals(receivedMobile)) {
                            driverUserIdGet = arrayUserDriverId.get(j);
                            Log.i("DriverUserId", driverUserIdGet);

                            getUserDriverBankDetails(driverUserIdGet);
                        }
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

        //------------------------------------------------------------------------------------------------

    }

    private void getUserDriverBankDetails(String driverUserId) {

        String url = getString(R.string.baseURL) + "/bank/getBkByUserId/" + driverUserId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String bankName = obj.getString("bank_name");
                        String bankAccountNumber = obj.getString("account_number");
                        String ifsiCode = obj.getString("IFSI_CODE");

                        TextView previewDriverDetailsDriverBankName = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_bank_name);
                        TextView previewDriverDetailsDriverBankAccountNumber = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_account_number);
                        TextView previewDriverDetailsDriverBankIFSICode = (TextView) previewDialogDriverDetails.findViewById(R.id.dialog_driver_details_ifsc_code);

                        previewDriverDetailsDriverBankName.setText(" Bank Name: " + bankName);
                        previewDriverDetailsDriverBankAccountNumber.setText(" Account Number: " + bankAccountNumber);
                        previewDriverDetailsDriverBankIFSICode.setText(" IFSI Code: " + ifsiCode);


                        if (previewDriverDetailsDriverBankName.getText().toString() == null) {
                            previewDriverDetailsDriverBankAdd.setVisibility(View.VISIBLE);
                        } else {
                            previewDriverDetailsDriverBankAdd.setVisibility(View.INVISIBLE);
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

    public void onClickBackViewDriverDetails(View view) {
    }

    public void onClickPreviewDriverSelfie(DriverModel obj) {
    }
}