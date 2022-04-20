package com.nlpl.ui.activities;

import static com.nlpl.R.drawable.blue_profile_small;
import static com.nlpl.R.drawable.ic_down;
import static com.nlpl.R.drawable.ic_up;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.nlpl.R;
import com.nlpl.databinding.ActivityViewPersonalDetailsBinding;
import com.nlpl.model.MainResponse;
import com.nlpl.model.Requests.ImageRequest;

import com.nlpl.model.Responses.ImageResponse;

import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.JumpTo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewPersonalDetailsActivity extends AppCompat {

    Boolean personalVisible = false, firmVisible = false;
    TextView userFirmGSTTextview, userFirmGSTTextviewTitle, userFirmPANTextview, userFirmPANTextviewTitle, userNameTextView, userPhoneNumberTextView, userEmailTextView, userAddressTextView, userFirmNameTextView, userFirmAddressTextView, userFirmNameTitleTextView, userFirmAddressTitleTextView, userFirmTitle, userFirmAddCompany, userEditFirmDetailsTextView;
    String img_type;
    String phone, userId;

    private int CAMERA_PIC_REQUEST_profile = 8;
    private int GET_FROM_GALLERY_profile = 5;
    TextView uploadPanAAdharBtn, uploadPanAAdharBtnTitle, roleProfile;

    Dialog previewDialogPan, previewDialogAadhar, previewDialogProfile;

    View actionBar;
    TextView actionBarTitle, actionBarSkip, previewAadharBtn, panText, aadharText, panNumber, aadharNumber, previewPANBtn, userAlternateNumber, bankCount, truckCount, driverCount;
    ImageView actionBarBackButton, actionBarMenuButton, profilePic, arrowPersonal, arrowFirm;

    ConstraintLayout constrainProfileDetails, constrainFirmDetails, truckConstrain, driverConstrain;

    ActivityViewPersonalDetailsBinding binding;
    String userNameAPI, userPhoneNumberAPI, userAlternatePhoneNumberAPI, userUserTypeAPI, userCityAPI, userPreferredLanguageAPI, userAddressAPI, userStateAPI, userPinCodeAPI, userEmailIdAPI, userPayTypeAPI, userIsRegistrationDoneAPI, userIsProfilePicAddedAPI;
    String userIsTruckAddedAPI, userIsDriverAddedAPI, userIsBankDetailsAddedAPI, userIsCompanyAddedAPI, userIsPersonalAddedAPI, userIsAadhaarVerifiedAPI, userIsPanVerifiedAPI, userIsUserVerifiedAPI, userIsAccountActiveAPI, userCreatedAtAPI, userUpdatedAtAPI;
    String userUpdatedByAPI, userDeletedAtAPI, userDeletedByAPI, idAPI, userLatitudeAPI, userLongitudeAPI, userDeviceIdAPI, userPanNumberAPI, userAadhaarNumberAPI, userIsSelfAddedAsDriverAPI;

    String userCompanyNameAPI, userCompanyTypeAPI, userCompanyGSTAPI, userCompanyPANAPI, userCompanyAddressAPI, userCompanyStateAPI, userCompanyCityAPI, userCompanyPINCodeAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_personal_details);
        binding.setHandlers(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }

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
                if (userUserTypeAPI.equals("Customer")) {
                    JumpTo.goToCustomerDashboard(ViewPersonalDetailsActivity.this, phone, true);
                } else {
                    JumpTo.goToServiceProviderDashboard(ViewPersonalDetailsActivity.this, phone, true, true);
                }
            }
        });

        actionBarSkip.setVisibility(View.VISIBLE);
        actionBarSkip.setText(getString(R.string.edit));
        actionBarSkip.setOnClickListener(view -> {
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
        truckConstrain = findViewById(R.id.profile_truck_details);

        driverCount = findViewById(R.id.profile_number_of_drivers);
        driverConstrain = findViewById(R.id.profile_driver_details);

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

        getUserDetailsMain();
    }

    private void getUserDetailsMain() {
        Call<MainResponse> responseCall = ApiClient.getUserService().mainResponse(userId);
        responseCall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, retrofit2.Response<MainResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        MainResponse response1 = response.body();
                        MainResponse.Data list = response1.getData();

                        //GET USER DETAILS
                        userNameAPI = list.getName();
                        userPhoneNumberAPI = String.valueOf(list.getPhone_number());
                        userAlternatePhoneNumberAPI = String.valueOf(list.getAlternate_ph_no());
                        userUserTypeAPI = list.getUser_type();
                        userCityAPI = list.getPreferred_location();
                        userPreferredLanguageAPI = list.getPreferred_language();
                        userAddressAPI = list.getAddress();
                        userStateAPI = list.getState_code();
                        userPinCodeAPI = String.valueOf(list.getPin_code());
                        userEmailIdAPI = list.getEmail_id();
                        userPayTypeAPI = list.getPay_type();
                        userIsRegistrationDoneAPI = String.valueOf(list.getIsRegistration_done());
                        userIsProfilePicAddedAPI = String.valueOf(list.getIsProfile_pic_added());
                        userIsTruckAddedAPI = String.valueOf(list.getIsTruck_added());
                        userIsDriverAddedAPI = String.valueOf(list.getIsDriver_added());
                        userIsBankDetailsAddedAPI = String.valueOf(list.getIsBankDetails_given());
                        userIsCompanyAddedAPI = String.valueOf(list.getIsCompany_added());
                        userIsPersonalAddedAPI = String.valueOf(list.getIsPersonal_dt_added());
                        userIsAadhaarVerifiedAPI = String.valueOf(list.getIs_Addhar_verfied());
                        userIsPanVerifiedAPI = String.valueOf(list.getIs_pan_verfied());
                        userIsUserVerifiedAPI = String.valueOf(list.getIs_user_verfied());
                        userIsAccountActiveAPI = String.valueOf(list.getIs_account_active());
                        userCreatedAtAPI = list.getCreated_at();
                        userUpdatedAtAPI = list.getUpdated_at();
                        userUpdatedByAPI = list.getUpdated_by();
                        userDeletedAtAPI = list.getDeleted_at();
                        userDeletedByAPI = list.getDeleted_by();
                        idAPI = String.valueOf(list.getId());
                        userLatitudeAPI = list.getLatitude();
                        userLongitudeAPI = list.getLongitude();
                        userDeviceIdAPI = list.getDevice_id();
                        userPanNumberAPI = list.getPan_number();
                        userAadhaarNumberAPI = list.getAadhaar_number();
                        userIsSelfAddedAsDriverAPI = String.valueOf(list.getIs_self_added_asDriver());

                        userNameTextView.setText(userNameAPI);
                        String s1 = userPhoneNumberAPI.substring(2, 12);

                        userPhoneNumberTextView.setText("+91 " + s1);
                        try {
                            String s2 = userAlternatePhoneNumberAPI.substring(2, 12);
                            userAlternateNumber.setText("+91 " + s2);

                            userEmailTextView.setText(userEmailIdAPI);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        userAddressTextView.setText(userAddressAPI + ", " + userCityAPI + " " + userPinCodeAPI);

                        if (userUserTypeAPI.equals("Customer")) {
                            truckConstrain.setVisibility(View.GONE);
                            driverConstrain.setVisibility(View.GONE);
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
                            truckConstrain.setVisibility(View.VISIBLE);
                            driverConstrain.setVisibility(View.VISIBLE);
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

                        if (userIsPersonalAddedAPI.equals("1")) {
                            previewAadharBtn.setVisibility(View.VISIBLE);
                            previewPANBtn.setVisibility(View.VISIBLE);
                            uploadPanAAdharBtn.setText("Edit KYC");
                            uploadPanAAdharBtnTitle.setVisibility(View.GONE);
                            panText.setVisibility(View.VISIBLE);
                            aadharText.setVisibility(View.VISIBLE);

                            try {
                                if (userPanNumberAPI.isEmpty() || userPanNumberAPI.equals("null") || userPanNumberAPI == null) {
                                    panNumber.setVisibility(View.INVISIBLE);
                                } else {
                                    panNumber.setText(userPanNumberAPI);
                                    panNumber.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                panNumber.setVisibility(View.INVISIBLE);
                            }

                            try {
                                if (userAadhaarNumberAPI.length() > 5) {
                                    aadharNumber.setVisibility(View.VISIBLE);
                                    aadharNumber.setText(userAadhaarNumberAPI);
                                } else {
                                    aadharNumber.setVisibility(View.INVISIBLE);
                                }
                            } catch (Exception e) {
                                aadharNumber.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            previewAadharBtn.setVisibility(View.INVISIBLE);
                            previewPANBtn.setVisibility(View.INVISIBLE);
                            uploadPanAAdharBtn.setText("Complete KYC");
                            uploadPanAAdharBtnTitle.setVisibility(View.VISIBLE);
                            panText.setVisibility(View.INVISIBLE);
                            aadharText.setVisibility(View.INVISIBLE);
                            panNumber.setVisibility(View.INVISIBLE);
                            aadharNumber.setVisibility(View.INVISIBLE);
                        }

                        if (userIsProfilePicAddedAPI.equals("1")) {

                        } else {
                            profilePic.setImageDrawable(getResources().getDrawable(blue_profile_small));
                        }

                        //GET TRUCK DETAILS
                        ArrayList<MainResponse.Data.TruckDetails> truckList = new ArrayList<>();
                        truckList.addAll(list.getTruckdetails());
                        if (truckList.size() <= 1) {
                            truckCount.setText(truckList.size() + " Truck");
                        } else {
                            truckCount.setText(truckList.size() + " Trucks");
                        }

                        //GET DRIVER DETAILS
                        ArrayList<MainResponse.Data.DriverDetails> driverList = new ArrayList<>();
                        driverList.addAll(list.getDriverDetails());
                        if (driverList.size() <= 1) {
                            driverCount.setText(driverList.size() + " Driver");
                        } else {
                            driverCount.setText(driverList.size() + " Drivers");
                        }

                        //GET BANK DETAILS
                        ArrayList<MainResponse.Data.BankDetails> bankList = new ArrayList<>();
                        bankList.addAll(list.getBankDetails());
                        if (bankList.size() <= 1) {
                            bankCount.setText(bankList.size() + " Bank");
                        } else {
                            bankCount.setText(bankList.size() + " Banks");
                        }

                        //GET COMPANY DETAILS
                        ArrayList<MainResponse.Data.CompanyDetails> companyDetails = new ArrayList<>();
                        companyDetails.addAll(list.getCompanyDetails());

                        try {
                            userCompanyNameAPI = companyDetails.get(0).company_name;
                            userCompanyTypeAPI = companyDetails.get(0).company_type;
                            userCompanyGSTAPI = companyDetails.get(0).company_gst_no;
                            userCompanyPANAPI = companyDetails.get(0).company_pan;
                            userCompanyAddressAPI = companyDetails.get(0).comp_add;
                            userCompanyStateAPI = companyDetails.get(0).comp_state;
                            userCompanyCityAPI = companyDetails.get(0).comp_city;
                            userCompanyPINCodeAPI = companyDetails.get(0).comp_zip;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Log.i("Company Log", userIsCompanyAddedAPI);
                        if (userIsCompanyAddedAPI.equals("1")) {
                            userFirmGSTTextview.setVisibility(View.VISIBLE);
                            userFirmGSTTextviewTitle.setVisibility(View.VISIBLE);
                            userFirmPANTextviewTitle.setVisibility(View.VISIBLE);
                            userFirmPANTextview.setVisibility(View.VISIBLE);

                            userFirmGSTTextview.setText(userCompanyGSTAPI);
                            userFirmPANTextview.setText(userCompanyPANAPI);

                            userFirmAddCompany.setVisibility(View.INVISIBLE);
                            userFirmTitle.setVisibility(View.VISIBLE);
                            userFirmTitle.setText(getString(R.string.Firm_Details));
                            userFirmNameTitleTextView.setVisibility(View.VISIBLE);
                            userFirmNameTextView.setVisibility(View.VISIBLE);
                            userFirmAddressTitleTextView.setVisibility(View.VISIBLE);
                            userFirmAddressTextView.setVisibility(View.VISIBLE);
                            userEditFirmDetailsTextView.setVisibility(View.VISIBLE);
                            userFirmNameTextView.setText(userCompanyNameAPI);
                            userFirmAddressTextView.setText(userCompanyAddressAPI + ", " + userCompanyCityAPI + ", " + userCompanyPINCodeAPI);
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

                        //GET POST LOAD DETAILS
                        ArrayList<MainResponse.Data.PostaLoadDetails> loadList = new ArrayList<>();
                        loadList.addAll(list.getPostaLoadDetails());

                        //GET PREFERRED LOCATIONS
                        ArrayList<MainResponse.Data.PreferredLocation> preferredLocationList = new ArrayList<>();
                        preferredLocationList.addAll(list.getPreferredLocations());


                        //GET USER RATINGS
                        ArrayList<MainResponse.Data.UserRatings> ratingsList = new ArrayList<>();
                        ratingsList.addAll(list.getUserRatings());

                        //GET USER IMAGE
                        ArrayList<MainResponse.Data.UserImages> imagesList = new ArrayList<>();
                        imagesList.addAll(list.getUserImages());
                        for (int i = 0; i <= imagesList.size(); i++) {
                            if (imagesList.get(i).image_type.equals("profile")) {
                                String userProfileURL = imagesList.get(i).image_url;
                                new DownloadImageTask(profilePic).execute(userProfileURL);
                                new DownloadImageTask((ImageView) previewDialogProfile.findViewById(R.id.dialog_preview_image_view_profile)).execute(userProfileURL);
                            }
                            if (imagesList.get(i).image_type.equals("pan")) {
                                String userPanURL = imagesList.get(i).image_url;
                                try {
                                    new DownloadImageTask((ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view)).execute(userPanURL);
                                } catch (Exception e) {
                                    previewPANBtn.setVisibility(View.INVISIBLE);
                                }
                            }
                            if (imagesList.get(i).image_type.equals("aadhar")) {
                                String userAadhaarURL = imagesList.get(i).image_url;
                                try {
                                    new DownloadImageTask((ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view)).execute(userAadhaarURL);
                                } catch (Exception e) {
                                    previewAadharBtn.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        //GET SP BID DETAILS
                        ArrayList<MainResponse.Data.SpBidDetails> spBidDetailsList = new ArrayList<>();
                        spBidDetailsList.addAll(list.getSpBidDetails());
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                getUserDetailsMain();
            }
        });
    }

    public void ViewSPProfile(View view) {
        if (userIsProfilePicAddedAPI.equals("1")) {
            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
            lp2.copyFrom(previewDialogProfile.getWindow().getAttributes());
            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp2.gravity = Gravity.CENTER;

            previewDialogProfile.show();
            previewDialogProfile.getWindow().setAttributes(lp2);

            TextView editProfilePic = previewDialogProfile.findViewById(R.id.editProfilePic);

            editProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadProfileDialogChoose();
                }
            });
        } else {
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

    public void onClickEditFirmDetailsView(View view) {
        JumpTo.goToCompanyDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, true, false);
    }

    public void onClickAddCompanyDetails(View view) {
        JumpTo.goToCompanyDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false);
    }

    public void onClickBottomNavigation(View view) {
        if (userUserTypeAPI.equals("Customer")) {
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    JumpTo.goToCustomerDashboard(ViewPersonalDetailsActivity.this, phone, true);
                    break;

                case R.id.bottom_nav_customer_dashboard:
                    JumpTo.goToFindTrucksActivity(ViewPersonalDetailsActivity.this, userId, phone);
                    break;

                case R.id.bottom_nav_track:
                    JumpTo.goToLPTrackActivity(ViewPersonalDetailsActivity.this, phone, false);
                    break;

                case R.id.bottom_nav_trip:
                    JumpTo.goToFindTripLPActivity(ViewPersonalDetailsActivity.this, phone, userId, false);
                    break;

                case R.id.bottom_nav_profile:
                    RearrangeItems();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    JumpTo.goToServiceProviderDashboard(ViewPersonalDetailsActivity.this, phone, true, true);
                    break;

                case R.id.bottom_nav_customer_dashboard:
                    JumpTo.goToFindLoadsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);

                    break;

                case R.id.bottom_nav_track:
                    JumpTo.goToSPTrackActivity(ViewPersonalDetailsActivity.this, phone, false);
                    break;

                case R.id.bottom_nav_profile:
                    RearrangeItems();
                    break;
            }
        }
    }

    public void RearrangeItems() {
        JumpTo.goToViewPersonalDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (userUserTypeAPI.equals("Customer")) {
            JumpTo.goToCustomerDashboard(ViewPersonalDetailsActivity.this, phone, true);
        } else {
            JumpTo.goToServiceProviderDashboard(ViewPersonalDetailsActivity.this, phone, true, true);
        }
    }

    public void onClickAddPersonalDetails(View view) {
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

    public void onClickAddDetails(View view) {
        if (userUserTypeAPI.equals("Customer")) {
            switch (view.getId()) {
                case R.id.profile_view_add_bank:
                    if (userIsBankDetailsAddedAPI.equals("1")) {
                        JumpTo.goToViewBankDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToBankDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, null);
                    }
                    break;

                case R.id.profile_view_settings:
                    JumpTo.getToSettingAndPreferences(ViewPersonalDetailsActivity.this, phone, userId, "Customer", false);
                    break;

                case R.id.profile_view_history:
                    JumpTo.goToCustomerLoadHistoryActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    break;

                case R.id.customer_menu_kyc:
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
        } else {
            switch (view.getId()) {
                case R.id.profile_view_add_bank:
                    if (userIsBankDetailsAddedAPI.equals("1")) {
                        JumpTo.goToViewBankDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToBankDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, null);
                    }
                    break;

                case R.id.profile_view_add_truck:
                    if (userIsTruckAddedAPI.equals("1")) {
                        JumpTo.goToViewVehicleDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToVehicleDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, false, false, null, null);
                    }
                    break;

                case R.id.profile_view_add_driver:
                    if (userIsDriverAddedAPI.equals("1")) {
                        JumpTo.goToViewDriverDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false);
                    } else {
                        JumpTo.goToDriverDetailsActivity(ViewPersonalDetailsActivity.this, userId, phone, false, false, false, null, null);
                    }
                    break;

                case R.id.profile_view_settings:
                    JumpTo.getToSettingAndPreferences(ViewPersonalDetailsActivity.this, phone, userId, userUserTypeAPI, false);
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