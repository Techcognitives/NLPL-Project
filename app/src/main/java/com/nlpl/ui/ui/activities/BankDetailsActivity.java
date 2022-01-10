package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nlpl.R;
import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Requests.ImageRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.ImageResponse;
import com.nlpl.model.Responses.UploadChequeResponse;
import com.nlpl.model.Responses.UploadImageResponse;
import com.nlpl.model.UpdateBankDetails.UpdateBankAccountHolderName;
import com.nlpl.model.UpdateBankDetails.UpdateBankAccountNumber;
import com.nlpl.model.UpdateBankDetails.UpdateBankIFSICode;
import com.nlpl.model.UpdateBankDetails.UpdateBankName;
import com.nlpl.model.UpdateBankDetails.UpdateBankReEnterAccountNumber;
import com.nlpl.model.UpdateUserDetails.UpdateUserIsBankDetailsGiven;
import com.nlpl.services.BankService;
import com.nlpl.services.UserService;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BankDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;
    String userId;

    int requestCode;
    int resultCode;
    Intent data;


    EditText bankName, accountNo, reAccount, ifscCode;
    Button okButton;

    Button uploadCC;
    TextView textCC, editCC;
    int GET_FROM_GALLERY = 0;
//    int CAMERA_PIC_REQUEST1 = 1;
    ImageView cancelledCheckImage, previewCancelledCheque, previewDialogCancelledChequeImageView, canceledCheckBlurImage, accountDetailsBlurImage;
    Boolean isEdit, isImgUploaded = false;

    RadioButton canceledCheckRadioButton, acDetailsRadioButton;
    String bankId, mobile;
    private RequestQueue mQueue;

    private UserService userService;
    private BankService bankService;

    Dialog previewDialogCancelledCheque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            bankId = bundle.getString("bankDetailsID");
            mobile = bundle.getString("mobile");
        }

        action_bar = findViewById(R.id.bank_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);

        actionBarTitle.setText("Bank Details");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDetailsActivity.this.finish();
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        bankService = retrofit.create(BankService.class);

        mQueue = Volley.newRequestQueue(BankDetailsActivity.this);

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
            canceledCheckRadioButton.setChecked(true);
            acDetailsRadioButton.setChecked(false);
            Log.i("Bank Id in Bank Details", bankId);
            isImgUploaded = true;
            okButton.setEnabled(true);
            editCC.setEnabled(false);
            okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            uploadCC.setVisibility(View.INVISIBLE);
            editCC.setVisibility(View.VISIBLE);
            previewCancelledCheque.setVisibility(View.VISIBLE);
            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            getBankDetails();
        }

        uploadCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

//                camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
//                        chooseDialog.dismiss();
//                    }
//                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                        chooseDialog.dismiss();
                    }
                });

            }
        });

        editCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

//                    camera.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
//                            chooseDialog.dismiss();
//                        }
//                    });

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
            return picturePath;

//        } else if (requestCode == CAMERA_PIC_REQUEST1) {
//
//            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
//            uploadCC.setVisibility(View.INVISIBLE);
//            editCC.setVisibility(View.VISIBLE);
//            previewCancelledCheque.setVisibility(View.VISIBLE);
//
//            isImgUploaded = true;
//
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//
//            String path = getRealPathFromURI(getImageUri(this, image));
//            cancelledCheckImage.setImageBitmap(BitmapFactory.decodeFile(path));
//            previewDialogCancelledChequeImageView.setImageBitmap(BitmapFactory.decodeFile(path));
//            return path;
//
        }
        return "";
    }

    private String imagePicker() {

        //Detects request code for PAN
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Cancelled cheque uploaded successfully");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    okButton.setEnabled(true);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();

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
            return picturePath;

//        } else if (requestCode == CAMERA_PIC_REQUEST1) {
//
//            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
//            my_alert.setTitle("Cancelled cheque uploaded successfully");
//            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    okButton.setEnabled(true);
//                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
//                    dialogInterface.dismiss();
//                }
//            });
//            my_alert.show();
//
//            textCC.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
//            uploadCC.setVisibility(View.INVISIBLE);
//            editCC.setVisibility(View.VISIBLE);
//            previewCancelledCheque.setVisibility(View.VISIBLE);
//
//            isImgUploaded = true;
//
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//
//            String path = getRealPathFromURI(getImageUri(this, image));
//            cancelledCheckImage.setImageBitmap(BitmapFactory.decodeFile(path));
//            previewDialogCancelledChequeImageView.setImageBitmap(BitmapFactory.decodeFile(path));
//
//            return path;

        }
        return "";
    }


    public void onClickBankDetailsOk(View view) {

        if (accountNo.getText().toString().equals(reAccount.getText().toString())) {
            if (isEdit) {

                String path = imagePickerWithoutAlert();
                Log.i("path of cc on edit", path);
                uploadCheque(bankId, path);
                Log.i("bankId of cc on edit", bankId);

                updateBankName();
                updateBankAccountNumber();
                updateBankReEnterAccountNumber();
                updateBankIFSICode();

            } else {
                saveBank(createBankAcc());
            }
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border));

            if (isEdit) {

                Intent i8 = new Intent(BankDetailsActivity.this, ProfileAndRegistrationActivity.class);
                i8.putExtra("mobile2", mobile);
                i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i8);
                overridePendingTransition(0, 0);
                BankDetailsActivity.this.finish();
            } else {
                AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
                my_alert.setTitle("Bank Details added successfully");
                my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        updateUserIsBankDetailsGiven();
                        dialogInterface.dismiss();
                        Intent i8 = new Intent(BankDetailsActivity.this, ProfileAndRegistrationActivity.class);
                        i8.putExtra("mobile2", mobile);
                        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i8);
                        overridePendingTransition(0, 0);
                        BankDetailsActivity.this.finish();
                    }
                });
                my_alert.show();
            }
        } else {
            reAccount.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Account number does not match");
            my_alert.setMessage("Please enter correct account number as above.");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
        }
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
                Log.i("bank id on save", bankIdOnResponse);
                String path = imagePickerWithoutAlert();
                Log.i("path on saveBank", path);
                uploadCheque(bankIdOnResponse, path);
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
            AlertDialog.Builder my_alert = new AlertDialog.Builder(BankDetailsActivity.this);
            my_alert.setTitle("Account number does not match");
            my_alert.setMessage("Please enter correct account number as above.");
            my_alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            my_alert.show();
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
                    editCC.setEnabled(true);
                    okButton.setEnabled(true);
                    okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    uploadCC.setVisibility(View.INVISIBLE);
                    editCC.setVisibility(View.VISIBLE);
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
                    uploadCC.setVisibility(View.INVISIBLE);
                    editCC.setVisibility(View.VISIBLE);
                    editCC.setEnabled(false);

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

    private void updateUserIsBankDetailsGiven() {

        UpdateUserIsBankDetailsGiven updateUserIsDriverAdded = new UpdateUserIsBankDetailsGiven("1");

        Call<UpdateUserIsBankDetailsGiven> call = userService.updateUserIsBankDetailsGiven("" + userId, updateUserIsDriverAdded);

        call.enqueue(new Callback<UpdateUserIsBankDetailsGiven>() {
            @Override
            public void onResponse(Call<UpdateUserIsBankDetailsGiven> call, Response<UpdateUserIsBankDetailsGiven> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserIsBankDetailsGiven> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateBankName() {

        UpdateBankName updateBankName = new UpdateBankName(bankName.getText().toString());

        Call<UpdateBankName> call = bankService.updateBankName("" + bankId, updateBankName);

        call.enqueue(new Callback<UpdateBankName>() {
            @Override
            public void onResponse(Call<UpdateBankName> call, Response<UpdateBankName> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankName> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }


    private void updateBankAccountNumber() {

        UpdateBankAccountNumber updateBankAccountNumber = new UpdateBankAccountNumber(accountNo.getText().toString());

        Call<UpdateBankAccountNumber> call = bankService.updateBankAccountNumber("" + bankId, updateBankAccountNumber);

        call.enqueue(new Callback<UpdateBankAccountNumber>() {
            @Override
            public void onResponse(Call<UpdateBankAccountNumber> call, Response<UpdateBankAccountNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankAccountNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateBankReEnterAccountNumber() {

        UpdateBankReEnterAccountNumber updateBankReEnterAccountNumber = new UpdateBankReEnterAccountNumber(reAccount.getText().toString());

        Call<UpdateBankReEnterAccountNumber> call = bankService.updateBankReEnterAccountNumber("" + bankId, updateBankReEnterAccountNumber);

        call.enqueue(new Callback<UpdateBankReEnterAccountNumber>() {
            @Override
            public void onResponse(Call<UpdateBankReEnterAccountNumber> call, Response<UpdateBankReEnterAccountNumber> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankReEnterAccountNumber> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
//--------------------------------------------------------------------------------------------------
    }

    private void updateBankIFSICode() {

        UpdateBankIFSICode updateBankIFSICode = new UpdateBankIFSICode(ifscCode.getText().toString());

        Call<UpdateBankIFSICode> call = bankService.updateBankIFSICode("" + bankId, updateBankIFSICode);

        call.enqueue(new Callback<UpdateBankIFSICode>() {
            @Override
            public void onResponse(Call<UpdateBankIFSICode> call, Response<UpdateBankIFSICode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "User is Bank Added");
                }
            }

            @Override
            public void onFailure(Call<UpdateBankIFSICode> call, Throwable t) {
                Log.i("Not Successful", "User is Bank Added");

            }
        });
//--------------------------------------------------------------------------------------------------
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
}