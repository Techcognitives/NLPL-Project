package com.nlpl.ui.activities;

import static com.nlpl.R.drawable.blue_profile_small;
import static com.nlpl.R.drawable.find;
import static com.nlpl.R.drawable.ic_down;
import static com.nlpl.R.drawable.ic_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseAuth;
import com.nlpl.R;
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.AddDriverResponseGet;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.BankResponseGet;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.TruckResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
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

public class ViewPersonalDetailsActivity extends AppCompat {

    private RequestQueue mQueue;
    Boolean profileAdded, personalVisible = false, firmVisible = false;
    TextView userFirmGSTTextview, userFirmGSTTextviewTitle, userFirmPANTextview, userFirmPANTextviewTitle, userNameTextView, userPhoneNumberTextView, userEmailTextView, userAddressTextView, userFirmNameTextView, userFirmAddressTextView, userFirmNameTitleTextView, userFirmAddressTitleTextView, userFirmTitle, userFirmAddCompany, userEditFirmDetailsTextView;
    String userNameAPI, userMobileNumberAPI, userAddressAPI, userCityAPI, userPinCodeAPI, userRoleAPI, userEmailIdAPI, isPersonalDetailsDoneAPI, isFirmDetailsDoneAPI, isBankDetailsDoneAPI, isTruckDetailsDoneAPI, isDriverDetailsDoneAPI;
    String companyNameAPI, companyAddressAPI, companyCityAPI, companyZipAPI, companyPanAPI, companyGstAPI, img_type;
    String phone, userId, isPersonalDetailsDone, isProfileAdded, isBankDetailsDone, isTruckDetailsDone, isDriverDetailsDone;

    private int CAMERA_PIC_REQUEST_profile = 8;
    private int GET_FROM_GALLERY_profile = 5;
    TextView uploadPanAAdharBtn, uploadPanAAdharBtnTitle, roleProfile;

    Dialog previewDialogPan, previewDialogAadhar, previewDialogProfile;

    View actionBar;
    TextView actionBarTitle, actionBarSkip, previewAadharBtn, panText, aadharText, panNumber, aadharNumber, previewPANBtn, userAlternateNumber, bankCount, truckCount, driverCount;
    ImageView actionBarBackButton, actionBarMenuButton, profilePic, arrowPersonal, arrowFirm;

    View bottomNav;
    ConstraintLayout constrainProfileDetails, constrainFirmDetails;

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
        actionBarSkip = (TextView) actionBar.findViewById(R.id.action_bar_skip);

        actionBarTitle.setText("Profile");
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                if (userRoleAPI.equals("Customer")) {
                    JumpTo.goToCustomerDashboard(ViewPersonalDetailsActivity.this, phone, true);
                } else {
                    JumpTo.goToServiceProviderDashboard(ViewPersonalDetailsActivity.this, phone, true, true);
                }
            }
        });

        actionBarSkip.setVisibility(View.VISIBLE);
        actionBarSkip.setText(getString(R.string.edit));
        actionBarSkip.setOnClickListener(view -> {
            ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
            JumpTo.goToRegistrationActivity(ViewPersonalDetailsActivity.this, phone, true, userId, true);
        });

        //------------------------------------------------------------------------------------------
        userNameTextView = (TextView) findViewById(R.id.view_personal_details_name_text_view);
        userPhoneNumberTextView = (TextView) findViewById(R.id.view_personal_details_phone_number_text_view);
        userEmailTextView = (TextView) findViewById(R.id.view_personal_details_email_id_text_view);
        userAddressTextView = (TextView) findViewById(R.id.view_personal_details_address_text_view);
        userAlternateNumber = (TextView) findViewById(R.id.personal_details_alternate_numbr);
        profilePic = (ImageView) findViewById(R.id.profile_picture_on_sp_menu);
        constrainProfileDetails = findViewById(R.id.profile_all_other_personal_details);
        constrainFirmDetails = findViewById(R.id.profile_company_constrain);
        arrowPersonal = findViewById(R.id.profile_down_personal);
        arrowFirm = findViewById(R.id.profile_down_firm);
        roleProfile = findViewById(R.id.profile_role);

        bankCount = findViewById(R.id.profile_number_of_banks);

        truckCount = findViewById(R.id.profile_number_of_trucks);

        driverCount = findViewById(R.id.profile_number_of_drivers);

        uploadPanAAdharBtnTitle = (TextView) findViewById(R.id.view_personal_details_complete_personal_details_text);
        uploadPanAAdharBtn = (TextView) findViewById(R.id.view_personal_details_add_personal_details);
        userFirmGSTTextview = (TextView) findViewById(R.id.view_personal_details_firm_gst_number_set);
        userFirmGSTTextviewTitle = (TextView) findViewById(R.id.view_personal_details_firm_gst_number);
        userFirmPANTextview = (TextView) findViewById(R.id.view_personal_details_firm_pan_number_set);
        userFirmPANTextviewTitle = (TextView) findViewById(R.id.view_personal_details_firm_pan_number);

        userFirmTitle = (TextView) findViewById(R.id.view_personal_details_firm_title);
        userFirmAddCompany = findViewById(R.id.view_personal_details_firm_btn);
        userFirmNameTitleTextView = (TextView) findViewById(R.id.view_personal_details_firm_name_title);
        userFirmNameTextView = (TextView) findViewById(R.id.view_personal_details_firm_name_text_view);
        userFirmAddressTitleTextView = (TextView) findViewById(R.id.view_personal_details_firm_address_title);
        userFirmAddressTextView = (TextView) findViewById(R.id.view_personal_details_firm_address_text_view);
        userEditFirmDetailsTextView = (TextView) findViewById(R.id.view_personal_details_edit_firm_details);

        previewAadharBtn = findViewById(R.id.view_personal_details_preview_aadhar_card);
        panText = findViewById(R.id.pan_text);
        panNumber = findViewById(R.id.personal_details_pan_number);
        previewPANBtn = findViewById(R.id.view_personal_details_preview_pan_card);
        aadharText = findViewById(R.id.aadhar_text);
        aadharNumber = findViewById(R.id.personal_details_aadhar_number);

        previewDialogPan = new Dialog(ViewPersonalDetailsActivity.this);
        previewDialogPan.setContentView(R.layout.dialog_preview_images);
        previewDialogPan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogAadhar = new Dialog(ViewPersonalDetailsActivity.this);
        previewDialogAadhar.setContentView(R.layout.dialog_preview_images);
        previewDialogAadhar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogProfile = new Dialog(ViewPersonalDetailsActivity.this);
        previewDialogProfile.setContentView(R.layout.dialog_preview_profile);
        previewDialogProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        getUserDetails();
        getImageURL();
        bankDetailsByUserId();
        getTruckDetailsByUserId();
        getDriverDetailsByUserId();
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
                        isPersonalDetailsDone = obj.getString("isPersonal_dt_added");
                        isProfileAdded = obj.getString("isProfile_pic_added");
                        String userAlternateMobileNumber = obj.getString("alternate_ph_no");
                        String panNumberAPI = obj.getString("pan_number");
                        String aadharNumberAPI = obj.getString("aadhaar_number");

//                        if (userRoleAPI.equals("Customer")) {
//                            userFirmAddCompany.setVisibility(View.GONE);
//                            userFirmTitle.setVisibility(View.GONE);
//                            userFirmGSTTextview.setVisibility(View.GONE);
//                            userFirmGSTTextviewTitle.setVisibility(View.GONE);
//                            userFirmPANTextviewTitle.setVisibility(View.GONE);
//                            userFirmPANTextview.setVisibility(View.GONE);
//                            userFirmNameTitleTextView.setVisibility(View.GONE);
//                            userEditFirmDetailsTextView.setVisibility(View.GONE);
//                            userFirmNameTextView.setVisibility(View.GONE);
//                            userFirmAddressTitleTextView.setVisibility(View.GONE);
//                            userFirmAddressTextView.setVisibility(View.GONE);
//                        }

                        isBankDetailsDone = obj.getString("isBankDetails_given");
                        isTruckDetailsDone = obj.getString("isTruck_added");
                        isDriverDetailsDone = obj.getString("isDriver_added");

                        if (userRoleAPI.equals("Customer")) {
                            View bottomNav = findViewById(R.id.view_personal_details_bottom_nav_bar);
                            ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
                            spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
                            TextView profileText = bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
                            ImageView profileImageView = bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
                            profileText.setText(getString(R.string.Trucks));
                            profileImageView.setImageDrawable(getDrawable(R.drawable.bottom_nav_search_small));
                            View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
                            spView.setVisibility(View.INVISIBLE);
                            ConstraintLayout profile = bottomNav.findViewById(R.id.bottom_nav_profile);
                            profile.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                            View profileUnderline = bottomNav.findViewById(R.id.bottom_nav_bar_profile_underline);
                            profileUnderline.setVisibility(View.VISIBLE);
                            roleProfile.setText("Load Poster");
                        } else {
                            //---------------------------- Bottom Nav --------------------------------------------------
                            View bottomNav = (View) findViewById(R.id.view_personal_details_bottom_nav_bar);
                            TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
                            ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
                            profileImageView.setImageDrawable(getDrawable(R.drawable.black_truck_small));
                            ConstraintLayout customerDashboard = bottomNav.findViewById(R.id.bottom_nav_trip);
                            ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
                            spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
                            View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
                            spView.setVisibility(View.INVISIBLE);
                            View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
                            profileText.setText(getString(R.string.Trips));
                            ConstraintLayout truck = findViewById(R.id.bottom_nav_trip);
                            ConstraintLayout profile = bottomNav.findViewById(R.id.bottom_nav_profile);
                            profile.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                            View profileUnderline = bottomNav.findViewById(R.id.bottom_nav_bar_profile_underline);
                            profileUnderline.setVisibility(View.VISIBLE);
                            truck.setVisibility(View.GONE);
                            roleProfile.setText("Service Provider");
                        }

                        if (isProfileAdded.equals("1")) {

                        } else {
                            profilePic.setImageDrawable(getResources().getDrawable(blue_profile_small));
                        }

                        if (isPersonalDetailsDone.equals("1")) {
                            previewAadharBtn.setVisibility(View.VISIBLE);
                            previewPANBtn.setVisibility(View.VISIBLE);
                            uploadPanAAdharBtn.setVisibility(View.GONE);
                            uploadPanAAdharBtnTitle.setVisibility(View.GONE);
                            panText.setVisibility(View.VISIBLE);
                            aadharText.setVisibility(View.VISIBLE);

                            try {
                                if (panNumberAPI.isEmpty() || panNumberAPI.equals("null") || panNumberAPI == null) {
                                    panNumber.setVisibility(View.INVISIBLE);
                                } else {
                                    panNumber.setText(panNumberAPI);
                                    panNumber.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                panNumber.setVisibility(View.INVISIBLE);
                            }

                            try {
                                if (aadharNumberAPI.length() > 5) {
                                    aadharNumber.setVisibility(View.VISIBLE);
                                    aadharNumber.setText(aadharNumberAPI);
                                } else {
                                    aadharNumber.setVisibility(View.INVISIBLE);
                                }
                            } catch (Exception e) {
                                aadharNumber.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            previewAadharBtn.setVisibility(View.INVISIBLE);
                            previewPANBtn.setVisibility(View.INVISIBLE);
                            uploadPanAAdharBtn.setVisibility(View.VISIBLE);
                            uploadPanAAdharBtnTitle.setVisibility(View.VISIBLE);
                            panText.setVisibility(View.INVISIBLE);
                            aadharText.setVisibility(View.INVISIBLE);
                            panNumber.setVisibility(View.INVISIBLE);
                            aadharNumber.setVisibility(View.INVISIBLE);
                        }

                        getCompanyDetails();

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

                        try {
                            String s2 = userAlternateMobileNumber.substring(2, 12);
                            userAlternateNumber.setText("+91 " + s2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

    public void ViewSPProfile(View view) {
        if (isProfileAdded.equals("1")) {
            String url1 = getString(R.string.baseURL) + "/imgbucket/Images/" + userId;
            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray imageList = response.getJSONArray("data");
                        for (int i = 0; i < imageList.length(); i++) {
                            JSONObject obj = imageList.getJSONObject(i);
                            String imageType = obj.getString("image_type");

                            String profileImgUrl = "";
                            if (imageType.equals("profile")) {
                                profileImgUrl = obj.getString("image_url");
                                profileAdded = true;
                                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                lp2.copyFrom(previewDialogProfile.getWindow().getAttributes());
                                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                                lp2.gravity = Gravity.CENTER;

                                previewDialogProfile.show();
                                previewDialogProfile.getWindow().setAttributes(lp2);
                                new DownloadImageTask((ImageView) previewDialogProfile.findViewById(R.id.dialog_preview_image_view_profile)).execute(profileImgUrl);

                                TextView editProfilePic = previewDialogProfile.findViewById(R.id.editProfilePic);

                                editProfilePic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        uploadProfileDialogChoose();
                                    }
                                });
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
            mQueue.add(request1);
        } else {
            profileAdded = false;
            uploadProfileDialogChoose();
        }
    }

    private void uploadProfileDialogChoose() {
        img_type = "profile";

        Dialog chooseDialog;
        chooseDialog = new Dialog(ViewPersonalDetailsActivity.this);
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
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST_profile);
                chooseDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY_profile);
                chooseDialog.dismiss();
            }
        });
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
                        companyPanAPI = data.getString("company_pan");
                        companyGstAPI = data.getString("company_gst_no");
                    }

                    if (companyNameAPI != null) {
                        userFirmGSTTextview.setVisibility(View.VISIBLE);
                        userFirmGSTTextviewTitle.setVisibility(View.VISIBLE);
                        userFirmPANTextviewTitle.setVisibility(View.VISIBLE);
                        userFirmPANTextview.setVisibility(View.VISIBLE);

                        userFirmGSTTextview.setText(companyGstAPI);
                        userFirmPANTextview.setText(companyPanAPI);

                        userFirmAddCompany.setVisibility(View.INVISIBLE);
                        userFirmTitle.setVisibility(View.VISIBLE);
                        userFirmTitle.setText(getString(R.string.Firm_Details));
                        userFirmTitle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.redDark)));
                        userFirmNameTitleTextView.setVisibility(View.VISIBLE);
                        userFirmNameTextView.setVisibility(View.VISIBLE);
                        userFirmAddressTitleTextView.setVisibility(View.VISIBLE);
                        userFirmAddressTextView.setVisibility(View.VISIBLE);
                        userEditFirmDetailsTextView.setVisibility(View.VISIBLE);
                        userFirmNameTextView.setText(companyNameAPI);
                        userFirmAddressTextView.setText(companyAddressAPI + ", " + companyCityAPI + ", " + companyZipAPI);
                    } else {
                        userFirmAddCompany.setVisibility(View.VISIBLE);
                        userFirmTitle.setVisibility(View.VISIBLE);
                        userFirmGSTTextview.setVisibility(View.GONE);
                        userFirmGSTTextviewTitle.setVisibility(View.GONE);
                        userFirmPANTextviewTitle.setVisibility(View.GONE);
                        userFirmPANTextview.setVisibility(View.GONE);
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

                        String panImageURL, aadharImageURL, profileImgUrl;

                        if (imageType.equals("aadhar")) {
                            aadharImageURL = obj.getString("image_url");
                            try {
                                new DownloadImageTask((ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view)).execute(aadharImageURL);
                                Log.i("IMAGE AADHAR URL", aadharImageURL);
                            } catch (Exception e) {
                                previewAadharBtn.setVisibility(View.INVISIBLE);
                            }
                        }

                        if (imageType.equals("pan")) {
                            panImageURL = obj.getString("image_url");
                            try {
                                Log.i("IMAGE PAN URL", panImageURL);
                                new DownloadImageTask((ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view)).execute(panImageURL);
                            } catch (Exception e) {
                                previewPANBtn.setVisibility(View.INVISIBLE);
                            }

                        }

                        if (imageType.equals("profile")) {
                            profileImgUrl = obj.getString("image_url");
                            Log.i("IMAGE PAN URL", profileImgUrl);
                            new DownloadImageTask((ImageView) previewDialogProfile.findViewById(R.id.dialog_preview_image_view_profile)).execute(profileImgUrl);
                            new DownloadImageTask(profilePic).execute(profileImgUrl);
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

    public void onClickPreviewProfile(View view) {
        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(previewDialogProfile.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.gravity = Gravity.CENTER;

        previewDialogProfile.show();
        previewDialogProfile.getWindow().setAttributes(lp2);
    }

    public void onClickEditFirmDetailsView(View view) {
        ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
        JumpTo.goToCompanyDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, true, false);
    }

    public void onClickAddCompanyDetails(View view) {
        if (companyNameAPI == null) {
            ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
            JumpTo.goToCompanyDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false);
        }
    }

    public void onClickBottomNavigation(View view) {
        if (userRoleAPI.equals("Customer")) {
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                    JumpTo.goToCustomerDashboard(ViewPersonalDetailsActivity.this, phone, true);
                    break;

                case R.id.bottom_nav_customer_dashboard:
                    ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                    JumpTo.goToFindTrucksActivity(ViewPersonalDetailsActivity.this, userId, phone);
                    break;

                case R.id.bottom_nav_track:
                    JumpTo.goToLPTrackActivity(ViewPersonalDetailsActivity.this, phone, false);
                    break;

                case R.id.bottom_nav_trip:
                    ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                    JumpTo.goToFindTripLPActivity(ViewPersonalDetailsActivity.this, phone, userId, false);
                    break;

                case R.id.bottom_nav_profile:
                    RearrangeItems();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                    JumpTo.goToServiceProviderDashboard(ViewPersonalDetailsActivity.this, phone, true, true);
                    break;

                case R.id.bottom_nav_customer_dashboard:
                    ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                    JumpTo.goToFindLoadsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);

                    break;

                case R.id.bottom_nav_track:
                    ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
                    JumpTo.goToSPTrackActivity(ViewPersonalDetailsActivity.this, phone, false);
                    break;

                case R.id.bottom_nav_profile:
                    RearrangeItems();
                    break;
            }
        }
    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
        JumpTo.goToViewPersonalDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
        if (userRoleAPI.equals("Customer")) {
            JumpTo.goToCustomerDashboard(ViewPersonalDetailsActivity.this, phone, true);
        } else {
            JumpTo.goToServiceProviderDashboard(ViewPersonalDetailsActivity.this, phone, true, true);
        }
    }

    public void onClickAddPersonalDetails(View view) {
        ShowAlert.loadingDialog(ViewPersonalDetailsActivity.this);
        JumpTo.goToPersonalDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false);
    }

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileImagePicker(requestCode, resultCode, data);
    }

    //-------------------------------------------------------------------------------------------------------------------
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
            public void onResponse(Call<ImageResponse> call, retrofit2.Response<ImageResponse> response) {

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    private void profileAddedAlert() {
        UpdateUserDetails.updateUserIsProfileAdded(userId, "1");

        Dialog alert = new Dialog(ViewPersonalDetailsActivity.this);
        alert.setContentView(R.layout.dialog_alert_single_button);
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

        alertTitle.setText(getString(R.string.Profile_Picture));
        alertMessage.setText(getString(R.string.Profile_Picture_added_successfully));
        alertPositiveButton.setVisibility(View.GONE);
        alertNegativeButton.setText(getString(R.string.ok));
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                RearrangeItems();
            }
        });
    }

    private String profileImagePicker(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_FROM_GALLERY_profile && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(ViewPersonalDetailsActivity.this);
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

            alertTitle.setText(getString(R.string.Personal_Details));
            alertMessage.setText(getString(R.string.Profile_Uploaded_Successfully));
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

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            saveImage(imageRequest());
            uploadImage(picturePath);

            profileAddedAlert();

        } else if (requestCode == CAMERA_PIC_REQUEST_profile) {

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                saveImage(imageRequest());
                uploadImage(path);

                profileAddedAlert();

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ViewPersonalDetailsActivity.this);
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

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Profile_Uploaded_Successfully));
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

            } catch (Exception e) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(ViewPersonalDetailsActivity.this);
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

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Profile_not_Uploaded_please_try_again));
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
            }

        }
        return "";
    }

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

    private void uploadImage(String picPath) {

        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

        MultipartBody.Part body = prepareFilePart("file", Uri.fromFile(file));

        Call<UploadImageResponse> call = ApiClient.getImageUploadService().uploadImage(userId, img_type, body);
        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, retrofit2.Response<UploadImageResponse> response) {
                Log.i("successful:", "success");
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("failed:", "failed");
            }
        });
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

    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(ViewPersonalDetailsActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewPersonalDetailsActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(ViewPersonalDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewPersonalDetailsActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(ViewPersonalDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewPersonalDetailsActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    public void onClickShowDetails(View view) {
        switch (view.getId()) {
            case R.id.profile_personal_details_drop_drown:
                if (personalVisible) {
                    constrainProfileDetails.setVisibility(View.GONE);
                    constrainFirmDetails.setVisibility(View.GONE);
                    arrowPersonal.setImageDrawable(getResources().getDrawable(ic_down));
                    arrowFirm.setImageDrawable(getResources().getDrawable(ic_down));
                    personalVisible = false;
                } else {
                    constrainProfileDetails.setVisibility(View.VISIBLE);
                    constrainFirmDetails.setVisibility(View.GONE);
                    arrowPersonal.setImageDrawable(getResources().getDrawable(ic_up));
                    arrowFirm.setImageDrawable(getResources().getDrawable(ic_down));
                    personalVisible = true;
                }

                break;
            case R.id.view_personal_details_firm_title:
                if (firmVisible) {
                    constrainProfileDetails.setVisibility(View.GONE);
                    constrainFirmDetails.setVisibility(View.GONE);
                    arrowPersonal.setImageDrawable(getResources().getDrawable(ic_down));
                    arrowFirm.setImageDrawable(getResources().getDrawable(ic_down));
                    firmVisible = false;
                } else {
                    constrainProfileDetails.setVisibility(View.GONE);
                    constrainFirmDetails.setVisibility(View.VISIBLE);
                    arrowPersonal.setImageDrawable(getResources().getDrawable(ic_down));
                    arrowFirm.setImageDrawable(getResources().getDrawable(ic_up));
                    firmVisible = true;
                }
                break;
        }
    }

    public void bankDetailsByUserId() {
        Call<BankResponseGet> responseCall = ApiClient.getBankService().getBankByUserId(userId);
        responseCall.enqueue(new Callback<BankResponseGet>() {
            @Override
            public void onResponse(Call<BankResponseGet> call, retrofit2.Response<BankResponseGet> response) {
                try {
                    BankResponseGet response1 = response.body();
                    ArrayList<BankResponseGet.bankDetailById> userBank = new ArrayList<>();
                    if (response.isSuccessful()) userBank.addAll(response1.getData());
                    if (userBank.size() <= 1) {
                        bankCount.setText(userBank.size() + " Bank");
                    } else {
                        bankCount.setText(userBank.size() + " Banks");
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<BankResponseGet> call, Throwable t) {

            }
        });
    }

    private void getTruckDetailsByUserId() {
        Call<TruckResponse> truckModelClassCall = ApiClient.addTruckService().getTruckDetails(userId);
        truckModelClassCall.enqueue(new Callback<TruckResponse>() {
            @Override
            public void onResponse(Call<TruckResponse> call, Response<TruckResponse> response) {
                try {
                    TruckResponse truckModelClass = response.body();
                    ArrayList<TruckResponse.TruckList> truckList = new ArrayList<>();
                    if (response.isSuccessful()) truckList.addAll(truckModelClass.getData());
                    if (truckList.size() <= 1) {
                        truckCount.setText(truckList.size() + " Truck");
                    } else {
                        truckCount.setText(truckList.size() + " Trucks");
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<TruckResponse> call, Throwable t) {

            }
        });
    }

    public void getDriverDetailsByUserId() {
        Call<AddDriverResponseGet> responseCall = ApiClient.addDriverService().getDriverById(userId);
        responseCall.enqueue(new Callback<AddDriverResponseGet>() {
            @Override
            public void onResponse(Call<AddDriverResponseGet> call, retrofit2.Response<AddDriverResponseGet> response) {
                try {
                    AddDriverResponseGet response1 = response.body();
                    AddDriverResponseGet.driverDetailsById list = response1.getData().get(0);

                    ArrayList<AddDriverResponseGet.driverDetailsById> driverList = new ArrayList<>();
                    if (response.isSuccessful()) driverList.addAll(response1.getData());
                    if (driverList.size() <= 1) {
                        driverCount.setText(driverList.size() + " Driver");
                    } else {
                        driverCount.setText(driverList.size() + " Drivers");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<AddDriverResponseGet> call, Throwable t) {

            }
        });
    }

    public void onClickAddDetails(View view) {
        if (userRoleAPI.equals("Customer")){

        }else{
            switch (view.getId()) {
                case R.id.profile_view_add_bank:
                    if (isBankDetailsDone.equals("1")) {
                        JumpTo.goToViewBankDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToBankDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, null);
                    }
                    break;

                case R.id.profile_view_add_truck:
                    if (isTruckDetailsDone.equals("1")) {
                        JumpTo.goToViewVehicleDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToVehicleDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, false, false, null, null);
                    }
                    break;

                case R.id.profile_view_add_driver:
                    if (isDriverDetailsDone.equals("1")) {
                        JumpTo.goToViewDriverDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToDriverDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, false, null, null);
                    }
                    break;

                case R.id.profile_view_settings:
                    JumpTo.getToSettingAndPreferences(ViewPersonalDetailsActivity.this, phone, userId, userRoleAPI, false);
                    break;

                case R.id.menu_kyc:
                    JumpTo.goToPersonalDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false);
                    break;

                case R.id.profile_log_out:
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(ViewPersonalDetailsActivity.this);
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

                    alertTitle.setText(getString(R.string.Log_Out));
                    alertMessage.setText(getString(R.string.Log_Out_message));
                    alertPositiveButton.setText(getString(R.string.yes));
                    alertNegativeButton.setText(getString(R.string.no));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });

                    alertPositiveButton.setOnClickListener(view1 -> {
                        alert.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        JumpTo.goToLogInActivity(ViewPersonalDetailsActivity.this);
                    });
                    //------------------------------------------------------------------------------------------
                    break;
            }
        }
    }
}