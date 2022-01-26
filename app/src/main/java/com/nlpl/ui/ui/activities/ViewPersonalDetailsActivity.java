package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewPersonalDetailsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    TextView userNameTextView, userPhoneNumberTextView, userEmailTextView, userAddressTextView, userFirmNameTextView, userFirmAddressTextView, userFirmNameTitleTextView, userFirmAddressTitleTextView, userFirmTitle, userEditFirmDetailsTextView;
    String userNameAPI, userMobileNumberAPI, userAddressAPI, userCityAPI, userPinCodeAPI, userRoleAPI, userEmailIdAPI, isPersonalDetailsDoneAPI, isFirmDetailsDoneAPI, isBankDetailsDoneAPI, isTruckDetailsDoneAPI, isDriverDetailsDoneAPI;
    String companyNameAPI, companyAddressAPI, companyCityAPI, companyZipAPI;
    String phone, userId;

    Dialog previewDialogPan, previewDialogAadhar;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(ViewPersonalDetailsActivity.this);

        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.view_personal_details_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("Personal Details");
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPersonalDetailsActivity.this.finish();
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.view_personal_details_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));
        //------------------------------------------------------------------------------------------

        userNameTextView = (TextView) findViewById(R.id.view_personal_details_name_text_view);
        userPhoneNumberTextView = (TextView) findViewById(R.id.view_personal_details_phone_number_text_view);
        userEmailTextView = (TextView) findViewById(R.id.view_personal_details_email_id_text_view);
        userAddressTextView = (TextView) findViewById(R.id.view_personal_details_address_text_view);

        userFirmTitle = (TextView) findViewById(R.id.view_personal_details_firm_title);
        userFirmNameTitleTextView = (TextView) findViewById(R.id.view_personal_details_firm_name_title);
        userFirmNameTextView = (TextView) findViewById(R.id.view_personal_details_firm_name_text_view);
        userFirmAddressTitleTextView = (TextView) findViewById(R.id.view_personal_details_firm_address_title);
        userFirmAddressTextView = (TextView) findViewById(R.id.view_personal_details_firm_address_text_view);
        userEditFirmDetailsTextView = (TextView) findViewById(R.id.view_personal_details_edit_firm_details);

        previewDialogPan = new Dialog(ViewPersonalDetailsActivity.this);
        previewDialogPan.setContentView(R.layout.dialog_preview_images);
        previewDialogPan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogAadhar = new Dialog(ViewPersonalDetailsActivity.this);
        previewDialogAadhar.setContentView(R.layout.dialog_preview_images);
        previewDialogAadhar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getUserDetails();
        getCompanyDetails();
        getImageURL();
    }

    private void getUserDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);

                        userNameAPI = obj.getString("name");
                        userMobileNumberAPI = obj.getString("phone_number");
                        userAddressAPI = obj.getString("address");
                        userCityAPI = obj.getString("preferred_location");
                        userPinCodeAPI = obj.getString("pin_code");
                        userRoleAPI = obj.getString("user_type");

                        if (userRoleAPI.equals("Customer")){
                            userFirmTitle.setVisibility(View.GONE);
                            userFirmNameTitleTextView.setVisibility(View.GONE);
                            userEditFirmDetailsTextView.setVisibility(View.GONE);
                            userFirmNameTextView.setVisibility(View.GONE);
                            userFirmAddressTitleTextView.setVisibility(View.GONE);
                            userFirmAddressTextView.setVisibility(View.GONE);
                        }

                        userEmailIdAPI = obj.getString("email_id");

                        isPersonalDetailsDoneAPI = obj.getString("isPersonal_dt_added");
                        isFirmDetailsDoneAPI = obj.getString("isCompany_added");
                        isBankDetailsDoneAPI = obj.getString("isBankDetails_given");
                        isTruckDetailsDoneAPI = obj.getString("isTruck_added");
                        isDriverDetailsDoneAPI = obj.getString("isDriver_added");

                        userNameTextView.setText(userNameAPI);


                        String s1 = userMobileNumberAPI.substring(2, 12);

                        userPhoneNumberTextView.setText("+91 " + s1);

                        userEmailTextView.setText(userEmailIdAPI);

                        userAddressTextView.setText(userAddressAPI + ", " + userCityAPI + " " + userPinCodeAPI);

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

    public void getCompanyDetails() {
        //---------------------------- Get Company Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/company/get/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray companyDetails = response.getJSONArray("data");
                    for (int i = 0; i < companyDetails.length(); i++) {
                        JSONObject data = companyDetails.getJSONObject(i);
                        companyNameAPI = data.getString("company_name");
                        companyAddressAPI = data.getString("comp_add");
                        companyCityAPI = data.getString("comp_city");
                        companyZipAPI = data.getString("comp_zip");
                    }

                    if (companyNameAPI != null) {
                        userFirmTitle.setText("Firm Details");
                        userFirmNameTitleTextView.setVisibility(View.VISIBLE);
                        userFirmNameTextView.setVisibility(View.VISIBLE);
                        userFirmAddressTitleTextView.setVisibility(View.VISIBLE);
                        userFirmAddressTextView.setVisibility(View.VISIBLE);
                        userEditFirmDetailsTextView.setVisibility(View.VISIBLE);
                        userFirmNameTextView.setText(companyNameAPI);
                        userFirmAddressTextView.setText(companyAddressAPI + ", " + companyCityAPI + ", " + companyZipAPI);
                    }else{
                        userFirmTitle.setText(getString(R.string.add_company));
                        userFirmNameTitleTextView.setVisibility(View.GONE);
                        userEditFirmDetailsTextView.setVisibility(View.GONE);
                        userFirmNameTextView.setVisibility(View.GONE);
                        userFirmAddressTitleTextView.setVisibility(View.GONE);
                        userFirmAddressTextView.setVisibility(View.GONE);
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
        //-------------------------------------------------------------------------------------------
    }

    private void getImageURL() {
        String url = getString(R.string.baseURL) + "/imgbucket/Images/" + userId;
        Log.i("Image URL", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray imageList = response.getJSONArray("data");
                    for (int i = 0; i < imageList.length(); i++) {
                        JSONObject obj = imageList.getJSONObject(i);
                        String imageType = obj.getString("image_type");

                        String panImageURL, aadharImageURL;

                        if (imageType.equals("aadhar")) {
                            aadharImageURL = obj.getString("image_url");
                            new DownloadImageTask((ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view)).execute(aadharImageURL);
                            Log.i("IMAGE AADHAR URL", aadharImageURL);
                        }

                        if (imageType.equals("pan")) {
                            panImageURL = obj.getString("image_url");
                            Log.i("IMAGE PAN URL", panImageURL);
                            new DownloadImageTask((ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view)).execute(panImageURL);
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

    public void onClickBackViewPersonalDetails(View view) {
        ViewPersonalDetailsActivity.this.finish();
    }

    public void onClickPreviewAadharCard(View view) {
        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(previewDialogAadhar.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.gravity = Gravity.CENTER;

        previewDialogAadhar.show();
        previewDialogAadhar.getWindow().setAttributes(lp2);
    }

    public void onClickPreviewPanCard(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogPan.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogPan.show();
        previewDialogPan.getWindow().setAttributes(lp);
    }

    public void onClickEditPersonalDetailsView(View view) {
        Intent intent = new Intent(ViewPersonalDetailsActivity.this, PersonalDetailsAndIdProofActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        startActivity(intent);
    }

    public void onClickEditFirmDetailsView(View view) {
        Intent intent = new Intent(ViewPersonalDetailsActivity.this, CompanyDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("mobile", phone);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    public void onClickAddCompanyDetails(View view) {
        if (companyNameAPI == null) {
            Intent intent = new Intent(ViewPersonalDetailsActivity.this, CompanyDetailsActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("mobile", phone);
            intent.putExtra("isEdit", false);
            startActivity(intent);
        }
    }

    public void onClickBottomNavigation(View view) {
        if (userRoleAPI.equals("Customer")){
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    Intent intent = new Intent(ViewPersonalDetailsActivity.this, CustomerDashboardActivity.class);
                    intent.putExtra("mobile", phone);
                    startActivity(intent);
                    break;

                case R.id.bottom_nav_customer_dashboard:

                    break;
            }
        }else{
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    Intent intent = new Intent(ViewPersonalDetailsActivity.this, DashboardActivity.class);
                    intent.putExtra("mobile2", phone);
                    startActivity(intent);
                    break;

                case R.id.bottom_nav_customer_dashboard:

                    break;
            }
        }

    }
}