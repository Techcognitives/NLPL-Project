package com.nlpl.ui.activities;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.UploadChequeResponse;
import com.nlpl.model.UpdateMethods.UpdateBankDetails;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompat {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;
    String userId, PathForCC = "";

    int requestCode;
    int resultCode;
    Intent data;

    EditText bankName, accountNo, reAccount, ifscCode;
    Button okButton;

    Button uploadCC;
    TextView textCC, editCC;
    int GET_FROM_GALLERY = 0;
    int CAMERA_PIC_REQUEST1 = 1;
    ImageView cancelledCheckImage, previewCancelledCheque, previewDialogCancelledChequeImageView, canceledCheckBlurImage, accountDetailsBlurImage;
    Boolean isEdit, isImgUploaded = false;

    RadioButton canceledCheckRadioButton, acDetailsRadioButton;
    String bankId, mobile, userRoleAPI, ccUploadedAPI;
    private RequestQueue mQueue;

    Dialog previewDialogCancelledCheque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            if (userId != null) {
                Log.i("Bank Details", userId);
            }
            isEdit = bundle.getBoolean("isEdit");
            bankId = bundle.getString("bankDetailsID");
            mobile = bundle.getString("mobile");
        }

        action_bar = findViewById(R.id.bank_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);

        actionBarTitle.setText(getString(R.string.bank_details));
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToViewBankDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
            }
        });

        bankName = (EditText) findViewById(R.id.bank_details_person_name_text_edit);
        accountNo = (EditText) findViewById(R.id.bank_details_account_number_edit);
        reAccount = (EditText) findViewById(R.id.bank_details_reenter_account_number_edit);
        ifscCode = (EditText) findViewById(R.id.bank_details_ifsc_edit);
        okButton = (Button) findViewById(R.id.bank_details_ok_button);
        canceledCheckBlurImage = (ImageView) findViewById(R.id.bank_details_blur_image_canceled_check);
        accountDetailsBlurImage = (ImageView) findViewById(R.id.bank_details_blur_image_account_details);
        okButton.setEnabled(false);

        mQueue = Volley.newRequestQueue(BankDetailsActivity.this);
        getUserDetails();

        bankName.setFilters(new InputFilter[]{filter});
        ifscCode.setFilters(new InputFilter[]{filter});

        bankName.setFilters(new InputFilter[]{filter});
        ifscCode.setFilters(new InputFilter[]{filter});

        bankName.addTextChangedListener(bankDetailsWatcher);
        accountNo.addTextChangedListener(bankDetailsWatcher);
        reAccount.addTextChangedListener(bankDetailsWatcher);
        ifscCode.addTextChangedListener(bankDetailsWatcher);

        uploadCC = findViewById(R.id.bank_details_canceled_check_upload);
        editCC = findViewById(R.id.bank_details_edit_canceled_check);
        textCC = findViewById(R.id.bank_details_canceled_check_text);
        cancelledCheckImage = (ImageView) findViewById(R.id.bank_details_canceled_check_image);
        previewCancelledCheque = (ImageView) findViewById(R.id.bank_details_preview_cancelled_cheque_image_view);

        canceledCheckRadioButton = (RadioButton) findViewById(R.id.bank_details_cancelled_check_radio_button);
        acDetailsRadioButton = (RadioButton) findViewById(R.id.bank_details_ac_details_radio_button);

        previewDialogCancelledCheque = new Dialog(BankDetailsActivity.this);
        previewDialogCancelledCheque.setContentView(R.layout.dialog_preview_images);
        previewDialogCancelledCheque.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogCancelledChequeImageView = (ImageView) previewDialogCancelledCheque.findViewById(R.id.dialog_preview_image_view);

        bankName.setEnabled(false);
        accountNo.setEnabled(false);
        reAccount.setEnabled(false);
        ifscCode.setEnabled(false);

        if (isEdit) {
            actionBarTitle.setText(getString(R.string.Edit_Bank_Details));
            canceledCheckRadioButton.setChecked(true);
            acDetailsRadioButton.setChecked(false);
            getBankDetails();
        }

        uploadCC.setOnClickListener(view -> DialogChoose());

        editCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChoose();
            }
        });

    }

    private void DialogChoose() {

        requestPermissionsForGalleryWRITE();
        requestPermissionsForGalleryREAD();
        requestPermissionsForCamera();
        Dialog chooseDialog;

        chooseDialog = new Dialog(BankDetailsActivity.this);
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


    //-----------------------------------------------upload Image------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.resultCode = resultCode;
        this.requestCode = requestCode;
        this.data = data;

        imagePicker();
        imagePickerWithoutAlert();
    }

    private String imagePickerWithoutAlert() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadCC.setVisibility(View.INVISIBLE);
            editCC.setVisibility(View.VISIBLE);
            previewCancelledCheque.setVisibility(View.VISIBLE);

            isImgUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            cancelledCheckImage.setImageURI(selectedImage);
            previewDialogCancelledChequeImageView.setImageURI(selectedImage);

            PathForCC = picturePath;

        } else if (requestCode == CAMERA_PIC_REQUEST1) {

            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadCC.setVisibility(View.INVISIBLE);
            editCC.setVisibility(View.VISIBLE);
            previewCancelledCheque.setVisibility(View.VISIBLE);

            isImgUploaded = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                cancelledCheckImage.setImageBitmap(BitmapFactory.decodeFile(path));
                previewDialogCancelledChequeImageView.setImageBitmap(BitmapFactory.decodeFile(path));
                PathForCC = path;
                return path;
            }catch (Exception e){
                isImgUploaded = false;
            }

        }
        return "";
    }

    private String imagePicker() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(BankDetailsActivity.this);
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

            alertTitle.setText(getString(R.string.bank_details));
            alertMessage.setText(getString(R.string.Cancelled_cheque_uploaded_successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                    okButton.setEnabled(true);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                }
            });
            //------------------------------------------------------------------------------------------
            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadCC.setVisibility(View.INVISIBLE);
            editCC.setVisibility(View.VISIBLE);
            previewCancelledCheque.setVisibility(View.VISIBLE);

            isImgUploaded = true;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            cancelledCheckImage.setImageURI(selectedImage);
            previewDialogCancelledChequeImageView.setImageURI(selectedImage);

            PathForCC = picturePath;

        } else if (requestCode == CAMERA_PIC_REQUEST1) {

            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            uploadCC.setVisibility(View.INVISIBLE);
            editCC.setVisibility(View.VISIBLE);
            previewCancelledCheque.setVisibility(View.VISIBLE);

            isImgUploaded = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                cancelledCheckImage.setImageBitmap(BitmapFactory.decodeFile(path));
                previewDialogCancelledChequeImageView.setImageBitmap(BitmapFactory.decodeFile(path));
                PathForCC = path;

                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(BankDetailsActivity.this);
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

                alertTitle.setText(getString(R.string.bank_details));
                alertMessage.setText(getString(R.string.Cancelled_cheque_uploaded_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        okButton.setEnabled(true);
                        okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    }
                });
                //------------------------------------------------------------------------------------------
                return path;
            }catch (Exception e){
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(BankDetailsActivity.this);
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

                alertTitle.setText(getString(R.string.bank_details));
                alertMessage.setText(getString(R.string.Cancelled_cheque_not_uploaded_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        if (isEdit){
                            uploadCC.setVisibility(View.VISIBLE);
                        }else{
                            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            uploadCC.setVisibility(View.VISIBLE);
                            editCC.setVisibility(View.INVISIBLE);
                            previewCancelledCheque.setVisibility(View.INVISIBLE);
                            isImgUploaded = false;
                        }
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        }
        return "";
    }


    public void onClickBankDetailsOk(View view) {
        ShowAlert.loadingDialog(BankDetailsActivity.this);
        if (accountNo.getText().toString().equals(reAccount.getText().toString())) {
            if (isEdit) {
                uploadCheque(bankId, PathForCC);
                UpdateBankDetails.updateBankName(bankId, bankName.getText().toString());
                UpdateBankDetails.updateBankAccountNumber(bankId, accountNo.getText().toString());
                UpdateBankDetails.updateBankReEnterAccountNumber(bankId, reAccount.getText().toString());
                UpdateBankDetails.updateBankIFSICode(bankId, ifscCode.getText().toString());

                JumpTo.goToViewBankDetailsActivity(BankDetailsActivity.this, userId, mobile, true);

            } else {
                saveBank(createBankAcc());
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(BankDetailsActivity.this);
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

                alertTitle.setText(getString(R.string.bank_details));
                alertMessage.setText(getString(R.string.Bank_Details_added_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        //Update User Bank (IsBankAdded)
                        UpdateUserDetails.updateUserIsBankDetailsGiven(userId, "1");
                        JumpTo.goToViewBankDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
                    }
                });
            }
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));

        } else {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(BankDetailsActivity.this);
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

            alertTitle.setText(getString(R.string.Account_number_does_not_match));
            alertMessage.setText(getString(R.string.Please_enter_correct_account_number_as_above_));
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

    private void getUserDetails() {
        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        userRoleAPI = obj.getString("user_type");
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

    private TextWatcher bankDetailsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String bankName1 = bankName.getText().toString().trim();
            String accNo1 = accountNo.getText().toString().trim();
            String reAccNo1 = reAccount.getText().toString().trim();
            String ifscCode1 = ifscCode.getText().toString().trim();

            if (!bankName1.isEmpty() && !accNo1.isEmpty() && !reAccNo1.isEmpty() && !ifscCode1.isEmpty()) {

                okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                okButton.setEnabled(true);

            } else {
                okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                okButton.setEnabled(false);
            }

            if (accNo1.equals(reAccNo1)) {
                reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //--------------------------------------create Bank Details in API -------------------------------------
    public BankRequest createBankAcc() {
        BankRequest bankRequest = new BankRequest();
        bankRequest.setUser_id(userId);
        bankRequest.setBank_name(bankName.getText().toString());
        bankRequest.setAccount_number(accountNo.getText().toString());
        bankRequest.setRe_enter_acc_num(reAccount.getText().toString());
        bankRequest.setIFSI_CODE(ifscCode.getText().toString());
        bankRequest.setIsBankDetails_Given("1");
        return bankRequest;
    }

    public void saveBank(BankRequest bankRequest) {
        Call<BankResponse> bankResponseCall = ApiClient.getBankService().saveBank(bankRequest);
        bankResponseCall.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                BankResponse bankResponse = response.body();
                String bankIdOnResponse = bankResponse.getData().getBank_id();
                uploadCheque(bankIdOnResponse, PathForCC);
            }

            @Override
            public void onFailure(Call<BankResponse> call, Throwable t) {

            }
        });
    }
    //-----------------------------------------------------------------------------------------------------

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

    public void onAccCheck(View view) {
        if (accountNo.getText().toString().equals(reAccount.getText().toString())) {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
        } else {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(BankDetailsActivity.this);
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

            alertTitle.setText(getString(R.string.Account_number_does_not_match));
            alertMessage.setText(getString(R.string.Please_enter_correct_account_number_as_above_));
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

    public void onClickBankDetailsChoose(View view) {

        switch (view.getId()) {
            case R.id.bank_details_cancelled_check_radio_button:

                canceledCheckRadioButton.setChecked(true);
                acDetailsRadioButton.setChecked(false);
                canceledCheckBlurImage.setVisibility(View.GONE);
                accountDetailsBlurImage.setVisibility(View.VISIBLE);

                bankName.setEnabled(false);
                accountNo.setEnabled(false);
                reAccount.setEnabled(false);
                ifscCode.setEnabled(false);

                if (!isEdit) {
                    bankName.getText().clear();
                    accountNo.getText().clear();
                    reAccount.getText().clear();
                    ifscCode.getText().clear();
                }

                String bankName2 = bankName.getText().toString().trim();
                String accNo = accountNo.getText().toString().trim();
                String reAccNo = reAccount.getText().toString().trim();
                String ifscCode2 = ifscCode.getText().toString().trim();

                if (!bankName2.isEmpty() && !accNo.isEmpty() && !reAccNo.isEmpty() && !ifscCode2.isEmpty()) {
                    okButton.setEnabled(true);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    uploadCC.setEnabled(true);
                    uploadCC.setVisibility(View.VISIBLE);
                    editCC.setVisibility(View.INVISIBLE);
                    previewCancelledCheque.setVisibility(View.VISIBLE);
                }

                if (isEdit) {
                    canceledCheckRadioButton.setChecked(true);
                    if (ccUploadedAPI.equals("null")) {
                        uploadCC.setVisibility(View.VISIBLE);
                        editCC.setVisibility(View.INVISIBLE);
                        uploadCC.setEnabled(true);
                        isImgUploaded = false;
                        previewCancelledCheque.setVisibility(View.INVISIBLE);
                        textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        isImgUploaded = true;
                        uploadCC.setVisibility(View.INVISIBLE);
                        editCC.setVisibility(View.VISIBLE);
                        editCC.setEnabled(true);
                        okButton.setEnabled(true);
                        okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    }
                } else if (isImgUploaded) {
                    editCC.setEnabled(true);
                    okButton.setEnabled(true);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    editCC.setVisibility(View.VISIBLE);
                    previewCancelledCheque.setVisibility(View.VISIBLE);
                    uploadCC.setVisibility(View.INVISIBLE);
                } else {
                    okButton.setEnabled(false);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                    uploadCC.setEnabled(true);
                    uploadCC.setVisibility(View.VISIBLE);
                    editCC.setVisibility(View.INVISIBLE);
                    previewCancelledCheque.setVisibility(View.VISIBLE);

                }
                break;

            case R.id.bank_details_ac_details_radio_button:
                canceledCheckRadioButton.setChecked(false);
                acDetailsRadioButton.setChecked(true);
                canceledCheckBlurImage.setVisibility(View.VISIBLE);
                accountDetailsBlurImage.setVisibility(View.GONE);

                bankName.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                bankName.setFocusable(true);
                bankName.setEnabled(true);
                accountNo.setEnabled(true);
                reAccount.setEnabled(true);
                ifscCode.setEnabled(true);

                String bankName3 = bankName.getText().toString().trim();
                String accNo2 = accountNo.getText().toString().trim();
                String reAccNo2 = reAccount.getText().toString().trim();
                String ifscCode3 = ifscCode.getText().toString().trim();

                if (!bankName3.isEmpty() && !accNo2.isEmpty() && !reAccNo2.isEmpty() && !ifscCode3.isEmpty()) {

                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    okButton.setEnabled(true);

                } else {
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                    okButton.setEnabled(false);
                }

                if (isEdit) {

                    String bankName1 = bankName.getText().toString().trim();
                    String accNo1 = accountNo.getText().toString().trim();
                    String reAccNo1 = reAccount.getText().toString().trim();
                    String ifscCode1 = ifscCode.getText().toString().trim();

                    if (!bankName1.isEmpty() && !accNo1.isEmpty() && !reAccNo1.isEmpty() && !ifscCode1.isEmpty()) {

                        okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        okButton.setEnabled(true);

                    } else {
                        okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                        okButton.setEnabled(false);
                    }

                    if (accNo1.equals(reAccNo1)) {
                        reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                    } else {
                        reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
                    }
//                    okButton.setEnabled(true);
//                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));

                    if (ccUploadedAPI.equals("null")) {
                        previewCancelledCheque.setVisibility(View.INVISIBLE);
                        uploadCC.setVisibility(View.VISIBLE);
                        editCC.setVisibility(View.INVISIBLE);
                        uploadCC.setEnabled(false);
                    } else {
                        uploadCC.setVisibility(View.INVISIBLE);
                        editCC.setVisibility(View.VISIBLE);
                        editCC.setEnabled(false);
                    }

                } else if (isImgUploaded) {
                    okButton.setEnabled(false);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_de_active));
                    editCC.setVisibility(View.VISIBLE);
                    uploadCC.setVisibility(View.INVISIBLE);
                    previewCancelledCheque.setVisibility(View.VISIBLE);
                } else {
                    uploadCC.setEnabled(false);
                    uploadCC.setVisibility(View.VISIBLE);
                    editCC.setVisibility(View.INVISIBLE);
                    previewCancelledCheque.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void getBankDetails() {

        String url = getString(R.string.baseURL) + "/bank/getBkByBkId/" + bankId;
        Log.i("get Bank Detail URL", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {

                        JSONObject obj = truckLists.getJSONObject(i);
                        String bankNAME = obj.getString("bank_name");
                        Log.i("BANK NAME", bankNAME);
                        bankName.setText(bankNAME);
                        accountNo.setText(obj.getString("account_number"));
                        reAccount.setText(obj.getString("re_enter_acc_num"));
                        ifscCode.setText(obj.getString("IFSI_CODE"));
                        ccUploadedAPI = obj.getString("cancelled_cheque");

                        if (obj.getString("cancelled_cheque").equals("null")||obj.getString("cancelled_cheque") == null) {
                            uploadCC.setVisibility(View.VISIBLE);
                            editCC.setVisibility(View.INVISIBLE);
                            isImgUploaded = false;
                            previewCancelledCheque.setVisibility(View.INVISIBLE);
                            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            uploadCC.setVisibility(View.INVISIBLE);
                            editCC.setVisibility(View.VISIBLE);
                            isImgUploaded = true;
                            previewCancelledCheque.setVisibility(View.VISIBLE);
                            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                        }

                        String cancelledChequeURL = obj.getString("cancelled_cheque");
                        new DownloadImageTask(cancelledCheckImage).execute(cancelledChequeURL);
                        new DownloadImageTask((ImageView) previewDialogCancelledCheque.findViewById(R.id.dialog_preview_image_view)).execute(cancelledChequeURL);

                        if (bankNAME != null) {
                            canceledCheckRadioButton.setChecked(false);
                            acDetailsRadioButton.setChecked(true);
                            canceledCheckBlurImage.setVisibility(View.VISIBLE);
                            accountDetailsBlurImage.setVisibility(View.GONE);

                            bankName.setFocusable(true);
                            bankName.setEnabled(true);
                            accountNo.setEnabled(true);
                            reAccount.setEnabled(true);
                            ifscCode.setEnabled(true);

                        } else {
                            canceledCheckRadioButton.setChecked(true);
                            acDetailsRadioButton.setChecked(false);
                            canceledCheckBlurImage.setVisibility(View.GONE);
                            accountDetailsBlurImage.setVisibility(View.VISIBLE);

                            bankName.setEnabled(false);
                            accountNo.setEnabled(false);
                            reAccount.setEnabled(false);
                            ifscCode.setEnabled(false);
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

    private void uploadCheque(String bankId1, String picPath) {

        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

        MultipartBody.Part body = prepareFilePart("cheque", Uri.fromFile(file));

        Call<UploadChequeResponse> call = ApiClient.getUploadChequeService().uploadCheque(bankId1, body);
        call.enqueue(new Callback<UploadChequeResponse>() {
            @Override
            public void onResponse(Call<UploadChequeResponse> call, Response<UploadChequeResponse> response) {

            }

            @Override
            public void onFailure(Call<UploadChequeResponse> call, Throwable t) {

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

    public void onClickPreviewCancelledCheque(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogCancelledCheque.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogCancelledCheque.show();
        previewDialogCancelledCheque.getWindow().setAttributes(lp);
    }

    private void requestPermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(BankDetailsActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BankDetailsActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    private void requestPermissionsForGalleryWRITE() {
        if (ContextCompat.checkSelfPermission(BankDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BankDetailsActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void requestPermissionsForGalleryREAD() {
        if (ContextCompat.checkSelfPermission(BankDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BankDetailsActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (userRoleAPI.equals("Customer")) {
            JumpTo.goToCustomerDashboard(BankDetailsActivity.this, mobile, true);

        } else {
            JumpTo.goToServiceProviderDashboard(BankDetailsActivity.this, mobile, true);
        }
    }

}