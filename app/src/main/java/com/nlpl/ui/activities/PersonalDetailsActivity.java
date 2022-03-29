package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chaos.view.PinView;
import com.nlpl.R;
import com.nlpl.model.Responses.AadharIdResponse;
import com.nlpl.model.Responses.AadharInfoResponse;
import com.nlpl.model.Responses.PANVerificationResponse;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;

import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadImageResponse;

import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.InAppNotification;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.OTPReceiver;
import com.nlpl.utils.ShowAlert;

import java.io.ByteArrayOutputStream;
import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalDetailsActivity extends AppCompat {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;
    Dialog chooseDialog;

    TextView panCardText, editPAN, editFront, frontText, profileText, editProfile;
    Button uploadPAN, uploadF, uploadProfile, okPersonalDetails;
    ImageView imgPAN, imgF, imgProfile, previewProfile, previewPan, previewAadhar;
    private int GET_FROM_GALLERY = 0;
    private int GET_FROM_GALLERY1 = 1;
    private int CAMERA_PIC_REQUEST = 3;
    private int CAMERA_PIC_REQUEST1 = 2;
    private int CAMERA_PIC_REQUEST2 = 4;
    private int GET_FROM_GALLERY2 = 5;

    View panAndAadharView;
    ConstraintLayout aadharConstrain, panConstrain, profileConstrain;
    TextView uploadAadharTitle, uploadPanTitle, uploadProfileTitle;

    String userId, mobile, requestIdForAadhar;
    Boolean profilePic, isPanUploaded = false, isFrontUploaded = false, isProfileUploaded = false;
    String img_type;
    EditText panNumber, aadharNumber;

    Dialog previewDialogPan, previewDialogAadhar, previewDialogProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            profilePic = bundle.getBoolean("profile");
            userId = bundle.getString("userId");
            mobile = bundle.getString("mobile");
        }

        action_bar = findViewById(R.id.personal_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(PersonalDetailsActivity.this);
                JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsActivity.this, userId, mobile, false);
            }
        });

        if (profilePic) {
            actionBarTitle.setText(getString(R.string.Personal_Details));
        } else {
            actionBarTitle.setText(getString(R.string.kyc_verification));
        }

        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, com.nlpl.utils.OTPReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//--------------------------------------------------------------------------------------------------
        panAndAadharView = (View) findViewById(R.id.personal_details_pan_and_aadhar);
        panCardText = panAndAadharView.findViewById(R.id.pancard1);
        frontText = panAndAadharView.findViewById(R.id.frontText);
        uploadPAN = panAndAadharView.findViewById(R.id.uploadPan);
        uploadF = panAndAadharView.findViewById(R.id.uploadF);
        imgPAN = panAndAadharView.findViewById(R.id.imagePan);
        imgF = panAndAadharView.findViewById(R.id.imageF);
        editPAN = panAndAadharView.findViewById(R.id.edit1);
        editFront = panAndAadharView.findViewById(R.id.editFront);
        okPersonalDetails = findViewById(R.id.okPersonalDetails);
        previewPan = (ImageView) panAndAadharView.findViewById(R.id.pan_aadhar_preview_pan);
        previewAadhar = (ImageView) panAndAadharView.findViewById(R.id.pan_aadhar_preview_aadhar);
        previewProfile = (ImageView) panAndAadharView.findViewById(R.id.preview_profile);
        editProfile = panAndAadharView.findViewById(R.id.editProfile);
        uploadProfile = panAndAadharView.findViewById(R.id.uploadProfile);
        profileText = panAndAadharView.findViewById(R.id.ProfileText);
        imgProfile = panAndAadharView.findViewById(R.id.imageProfile);
        panNumber = panAndAadharView.findViewById(R.id.pan_aadhar_pan_number);
        panNumber.addTextChangedListener(panNumberCheck);
        aadharNumber = panAndAadharView.findViewById(R.id.pan_aadhar_aadhar_number);
        aadharNumber.addTextChangedListener(aadharTextCheck);

        aadharConstrain = panAndAadharView.findViewById(R.id.aadhar_constrain);
        panConstrain = panAndAadharView.findViewById(R.id.pan_card_constrain);
        profileConstrain = panAndAadharView.findViewById(R.id.upload_profile_constrain);
        uploadPanTitle = panAndAadharView.findViewById(R.id.upload_pan_text);
        uploadAadharTitle = panAndAadharView.findViewById(R.id.upload_aadhar_text);
        uploadProfileTitle = panAndAadharView.findViewById(R.id.upload_profile_text);

        if (profilePic) {
            uploadProfileTitle.setVisibility(View.VISIBLE);
            profileConstrain.setVisibility(View.VISIBLE);
            uploadAadharTitle.setVisibility(View.GONE);
            aadharConstrain.setVisibility(View.GONE);
            uploadPanTitle.setVisibility(View.GONE);
            panConstrain.setVisibility(View.GONE);
        } else {
            uploadProfileTitle.setVisibility(View.GONE);
            profileConstrain.setVisibility(View.GONE);
            uploadAadharTitle.setVisibility(View.VISIBLE);
            aadharConstrain.setVisibility(View.VISIBLE);
            uploadPanTitle.setVisibility(View.VISIBLE);
            panConstrain.setVisibility(View.VISIBLE);
        }

        previewDialogPan = new Dialog(PersonalDetailsActivity.this);
        previewDialogPan.setContentView(R.layout.dialog_preview_images);
        previewDialogPan.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        previewDialogAadhar = new Dialog(PersonalDetailsActivity.this);
        previewDialogAadhar.setContentView(R.layout.dialog_preview_images);
        previewDialogAadhar.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        previewDialogProfile = new Dialog(PersonalDetailsActivity.this);
        previewDialogProfile.setContentView(R.layout.dialog_preview_images);
        previewDialogProfile.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        previewPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(previewDialogPan.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;

                previewDialogPan.show();
                previewDialogPan.getWindow().setAttributes(lp);
            }
        });

        previewAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(previewDialogAadhar.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.gravity = Gravity.CENTER;

                previewDialogAadhar.show();
                previewDialogAadhar.getWindow().setAttributes(lp2);
            }
        });

        previewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(previewDialogProfile.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.gravity = Gravity.CENTER;

                previewDialogProfile.show();
                previewDialogProfile.getWindow().setAttributes(lp2);
            }
        });

        uploadF.setVisibility(View.VISIBLE);
        editFront.setVisibility(View.INVISIBLE);

        uploadPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPanDialogChoose();
            }
        });

        editPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPanDialogChoose();
            }
        });

        uploadF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAadharDialogChoose();
            }
        });

        editFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAadharDialogChoose();
            }
        });

        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileDialogChoose();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileDialogChoose();
            }
        });

    }

    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
            alertMessage.setText(getString(R.string.PAN_Card_Uploaded_Successfully));
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
            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            previewPan.setVisibility(View.VISIBLE);
            isPanUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            saveImage(imageRequest());
            uploadImage(picturePath);
            ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
            ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
            editedPan.setImageURI(selectedImage);
            imgPAN.setImageURI(selectedImage);

        } else if (requestCode == GET_FROM_GALLERY1 && resultCode == Activity.RESULT_OK) {
//----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
            alertMessage.setText(getString(R.string.Aadhar_Card_Uploaded_Successfully));
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
            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            previewAadhar.setVisibility(View.VISIBLE);
            isFrontUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            saveImage(imageRequest());
            uploadImage(picturePath);
            ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
            ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
            editedAadhar.setImageURI(selectedImage);
            imgF.setImageURI(selectedImage);

        } else if (requestCode == CAMERA_PIC_REQUEST) {

            panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadPAN.setVisibility(View.INVISIBLE);
            editPAN.setVisibility(View.VISIBLE);
            previewPan.setVisibility(View.VISIBLE);
            isPanUploaded = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                imgPAN.setImageBitmap(BitmapFactory.decodeFile(path));
                saveImage(imageRequest());
                uploadImage(path);

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
                alertMessage.setText(getString(R.string.PAN_Card_Uploaded_Successfully));
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
                Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
                alertMessage.setText(getString(R.string.PAN_Card_not_Uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        panCardText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        uploadPAN.setVisibility(View.VISIBLE);
                        editPAN.setVisibility(View.INVISIBLE);
                        previewPan.setVisibility(View.INVISIBLE);
                        isPanUploaded = false;
                    }
                });
                //------------------------------------------------------------------------------------------
            }


        } else if (requestCode == CAMERA_PIC_REQUEST1) {

            frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadF.setVisibility(View.INVISIBLE);
            editFront.setVisibility(View.VISIBLE);
            previewAadhar.setVisibility(View.VISIBLE);
            isFrontUploaded = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                imgF.setImageBitmap(BitmapFactory.decodeFile(path));
                saveImage(imageRequest());
                uploadImage(path);

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
                alertMessage.setText(getString(R.string.Aadhar_Card_Uploaded_Successfully));
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
                Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
                alertMessage.setText(getString(R.string.Aadhar_Card_not_Uploaded));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        frontText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        uploadF.setVisibility(View.VISIBLE);
                        editFront.setVisibility(View.INVISIBLE);
                        previewAadhar.setVisibility(View.INVISIBLE);
                        isFrontUploaded = false;
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        } else if (requestCode == GET_FROM_GALLERY2 && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
            profileText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadProfile.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            previewProfile.setVisibility(View.VISIBLE);
            isProfileUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            saveImage(imageRequest());
            uploadImage(picturePath);
            ImageView editedAadhar = (ImageView) previewDialogAadhar.findViewById(R.id.dialog_preview_image_view);
            ImageView editedPan = (ImageView) previewDialogPan.findViewById(R.id.dialog_preview_image_view);
            editedPan.setImageURI(selectedImage);
            imgProfile.setImageURI(selectedImage);

        } else if (requestCode == CAMERA_PIC_REQUEST2) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(PersonalDetailsActivity.this);
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

            profileText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadProfile.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            previewProfile.setVisibility(View.VISIBLE);
            isProfileUploaded = true;

            Bitmap image = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(this, image));
            imgProfile.setImageBitmap(BitmapFactory.decodeFile(path));
            saveImage(imageRequest());
            uploadImage(path);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------

    public void onClickOKPersonal(View view) {
        if (profilePic) {
            if (isProfileUploaded) {
                UpdateUserDetails.updateUserIsProfileAdded(userId, "1");

                Dialog alert = new Dialog(PersonalDetailsActivity.this);
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
                        ShowAlert.loadingDialog(PersonalDetailsActivity.this);
                        JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsActivity.this, userId, mobile, false);
                    }
                });
            } else {
                Toast.makeText(this, "Please Upload Profile Picture", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isPanUploaded && isFrontUploaded) {
                UpdateUserDetails.updateUserIsPersonalDetailsAdded(userId, "1");
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(PersonalDetailsActivity.this);
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

                alertTitle.setText(getString(R.string.Personal_Details));
                alertMessage.setText(getString(R.string.Personal_Details_added_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        ShowAlert.loadingDialog(PersonalDetailsActivity.this);
                        JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsActivity.this, userId, mobile, true);
                    }
                });
            } else {
                if (!isPanUploaded) {
                    Toast.makeText(this, "Please Upload PAN Card", Toast.LENGTH_SHORT).show();
                } else if (!isFrontUploaded) {
                    Toast.makeText(this, "Please Upload Aadhar Card", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please Upload PAN & Aadhar Card", Toast.LENGTH_SHORT).show();
                }
            }
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
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
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
        if (ContextCompat.checkSelfPermission(PersonalDetailsActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalDetailsActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(PersonalDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalDetailsActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(PersonalDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalDetailsActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void uploadPanDialogChoose() {
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "pan";

        Dialog chooseDialog;
        chooseDialog = new Dialog(PersonalDetailsActivity.this);
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
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
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

    private void uploadAadharDialogChoose() {
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "aadhar";

        Dialog chooseDialog;
        chooseDialog = new Dialog(PersonalDetailsActivity.this);
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
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY1);
                chooseDialog.dismiss();
            }
        });
    }

    private void uploadProfileDialogChoose() {
        requestPermissionsForCamera();
        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        img_type = "profile";

        Dialog chooseDialog;
        chooseDialog = new Dialog(PersonalDetailsActivity.this);
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
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY2);
                chooseDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(PersonalDetailsActivity.this);
        JumpTo.goToViewPersonalDetailsActivity(PersonalDetailsActivity.this, userId, mobile, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        InAppNotification.SendNotificationJumpToPersonalDetailsActivity(PersonalDetailsActivity.this, "Complete Your Profile", "Upload PAN and Aadhar in the Personal Details Section", userId, mobile, false);
    }

    private TextWatcher panNumberCheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String panWatcher = panNumber.getText().toString().trim();

            if (panWatcher.length() != 10) {

            } else {
                checkPAN(panWatcher);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher aadharTextCheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String aadharWatcher = aadharNumber.getText().toString().trim();

            if (aadharWatcher.length() != 12) {

            } else {
                checkAadhar(aadharWatcher);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //Check Pan number /***************************************************************************************/
    public void checkPAN(String panNumberCheck) {
        Call<PANVerificationResponse> responseCall = ApiClient.getVerification().checkPAN(userId, panNumberCheck);
        responseCall.enqueue(new Callback<PANVerificationResponse>() {
            @Override
            public void onResponse(Call<PANVerificationResponse> call, retrofit2.Response<PANVerificationResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        PANVerificationResponse response1 = response.body();
                        PANVerificationResponse.UserList list = response1.getData().get(0);
                        Log.i("Success Message", list.getSuccess());
                        if (list.getSuccess().equals("1")) {
                            panNumber.setEnabled(false);
                            panNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                        } else {
                            Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid PAN number", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid PAN number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid PAN number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PANVerificationResponse> call, Throwable t) {
                Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid PAN number", Toast.LENGTH_SHORT).show();
            }
        });
        //****************************************************************************************//
    }

    private void checkAadhar(String aadharNumber) {
        Call<AadharIdResponse> aadharIdResponseCall = ApiClient.getVerification().checkAadhar(userId, aadharNumber);
        aadharIdResponseCall.enqueue(new Callback<AadharIdResponse>() {
            @Override
            public void onResponse(Call<AadharIdResponse> call, Response<AadharIdResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        AadharIdResponse aadharIdResponse = response.body();
                        Log.i("response", String.valueOf(response.body()));
                        AadharIdResponse.UserList list = aadharIdResponse.getData().get(0);
                        requestIdForAadhar = list.getRequest_id();
                        Log.i("requestId", list.getRequest_id());
                        Log.i("requestId1", requestIdForAadhar);
                    } else {
                        Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid Aadhar Number", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid Aadhar Number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AadharIdResponse> call, Throwable t) {

            }
        });
    }

    public void openDialogForOTPValidation(String requestIdForAadhar) {
        Dialog otpRequest = new Dialog(PersonalDetailsActivity.this);
        otpRequest.setContentView(R.layout.activity_otp_code);
        otpRequest.getWindow().setBackgroundDrawable(getDrawable(R.drawable.all_rounded_small));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(otpRequest.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;

        otpRequest.show();
        otpRequest.getWindow().setAttributes(lp);
        otpRequest.setCancelable(false);

        PinView otpCode = (PinView) otpRequest.findViewById(R.id.pin_view);
        TextView changeNumber = otpRequest.findViewById(R.id.otp_change_number);
        changeNumber.setVisibility(View.INVISIBLE);
        TextView otpCodeText = otpRequest.findViewById(R.id.otp_code_text);
        TextView otpSentText = otpRequest.findViewById(R.id.otp_text);
        otpSentText.setText("Enter OTP Code sent on Aadhar linked mobile number");
        Button verifyOtp = otpRequest.findViewById(R.id.otp_button);
        TextView countdown = otpRequest.findViewById(R.id.countdown);
        countdown.setVisibility(View.INVISIBLE);

        verifyOtp.setOnClickListener(view -> {
            Log.i("reqID", requestIdForAadhar);
            checkAadharWithOTP(requestIdForAadhar, otpCode.getText().toString());
        });
    }

    public void checkAadharWithOTP(String requestIdForAadhar, String otp) {
        Log.i("AADHAR", userId + "/" + aadharNumber.getText().toString() + "/" + requestIdForAadhar + "/" + otp);
        Call<AadharInfoResponse> responseCall = ApiClient.getVerification().checkAadharWithOTP(userId, aadharNumber.getText().toString(), requestIdForAadhar, otp);
        responseCall.enqueue(new Callback<AadharInfoResponse>() {
            @Override
            public void onResponse(Call<AadharInfoResponse> call, retrofit2.Response<AadharInfoResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AadharInfoResponse response1 = response.body();
                        AadharInfoResponse.UserList list = response1.getData().get(0);
                        Log.i("Success Message", list.getSuccess());
                        if (list.getSuccess().equals("1")) {
                            aadharNumber.setEnabled(false);
                            aadharNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                        } else {
                            Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid Aadhar number", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid Aadhar number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid Aadhar number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AadharInfoResponse> call, Throwable t) {
                Toast.makeText(PersonalDetailsActivity.this, "Please enter a valid Aadhar number", Toast.LENGTH_SHORT).show();
            }
        });
        //****************************************************************************************//
    }
}