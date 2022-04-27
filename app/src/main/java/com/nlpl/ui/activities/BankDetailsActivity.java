package com.nlpl.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
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

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nlpl.R;
import com.nlpl.databinding.ActivityBankDetailsBinding;
import com.nlpl.model.GetBankDetailsResponse;
import com.nlpl.model.MainResponse;
import com.nlpl.model.Requests.BankRequest;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.BankVerificationResponse;
import com.nlpl.model.Responses.UploadChequeResponse;
import com.nlpl.model.UpdateMethods.UpdateBankDetails;
import com.nlpl.model.UpdateMethods.UpdateUserDetails;

import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.FileUtils;
import com.nlpl.utils.JumpTo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompat {

    String userId, PathForCC = "", bankId, mobile, userRoleAPI, ccUploadedAPI;
    int requestCode, resultCode, GET_FROM_GALLERY = 0, CAMERA_PIC_REQUEST1 = 0;
    Intent data;
    Dialog previewDialogCancelledCheque;
    ImageView previewDialogCancelledChequeImageView;
    Boolean isEdit, isImgUploaded = false, bankVerified = true;
    ActivityBankDetailsBinding binding;
    ImagePicker imagePicker;
    Uri newUriForCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bank_details);
        binding.setHandlers(BankDetailsActivity.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            if (userId != null) {
                Log.i("Bank Details", userId);
            }
            isEdit = bundle.getBoolean("isEdit");
            bankId = bundle.getString("bankDetailsID");
            mobile = bundle.getString("mobile");
            imagePicker = new ImagePicker(this);
        }

        //------------------------------------------------------------------------------------------

        getUserDetailsMain();

        binding.bankDetailsActionBar.actionBarTitle.setText(getString(R.string.bank_details));
        binding.bankDetailsActionBar.actionBarBackButton.setOnClickListener(view -> JumpTo.goToViewPersonalDetailsActivity(BankDetailsActivity.this, userId, mobile, true));

        binding.bankDetailsPersonNameTextEdit.setFilters(new InputFilter[]{filter});
        int maxLength = 11;
        binding.bankDetailsIfscEdit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(maxLength)});

        binding.bankDetailsPersonNameTextEdit.addTextChangedListener(bankDetailsWatcher);
        binding.bankDetailsAccountNumberEdit.addTextChangedListener(bankDetailsWatcher);
        binding.bankDetailsReenterAccountNumberEdit.addTextChangedListener(bankDetailsWatcher);
        binding.bankDetailsIfscEdit.addTextChangedListener(bankDetailsWatcher);

        binding.bankDetailsPreviewCancelledChequeImageView.setVisibility(View.INVISIBLE);

        previewDialogCancelledCheque = new Dialog(BankDetailsActivity.this);
        previewDialogCancelledCheque.setContentView(R.layout.dialog_preview_images);
        previewDialogCancelledCheque.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogCancelledChequeImageView = previewDialogCancelledCheque.findViewById(R.id.dialog_preview_image_view);

        if (isEdit) {
            binding.bankDetailsPersonNameTextEdit.setText(getString(R.string.Edit_Bank_Details));
            getBankDetails();
        }

        binding.bankDetailsCanceledCheckUpload.setOnClickListener(view -> imagePicker.selectSource(imageCallBack()));
        binding.bankDetailsEditCanceledCheck.setOnClickListener(view -> DialogChoose());
    }

    private Function1<ImageResult<? extends Uri>, Unit> imageCallBack() {
        return imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                try {
                    Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
//                imageView.setImageURI(uri);
                    binding.bankDetailsCanceledCheckImage.setImageURI(uri);
                    previewDialogCancelledChequeImageView.setImageURI(uri);
                    newUriForCC= uri;
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();

                    isImgUploaded = true;
                    PathForCC = String.valueOf(uri);
//                    saveBank(createBankAcc());

                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                String errorString = ((ImageResult.Failure) imageResult).getErrorString();
                Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
            }
            return null;
        };
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

        camera.setOnClickListener(view -> {
            activityResultForCancelledCheque.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            CAMERA_PIC_REQUEST1 = 1;
            GET_FROM_GALLERY = 0;
            chooseDialog.dismiss();
        });

        gallery.setOnClickListener(view -> {
            activityResultForCancelledCheque.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI));
            CAMERA_PIC_REQUEST1 = 0;
            GET_FROM_GALLERY = 1;
            chooseDialog.dismiss();
        });
    }

    //-----------------------------------------------upload Image-----------------------------------
    ActivityResultLauncher<Intent> activityResultForCancelledCheque = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        resultCode = result.getResultCode();
                        requestCode = result.getResultCode();
                        data = result.getData();
                        imagePicker();
                    }
                }
            });

    @SuppressLint("UseCompatLoadingForDrawables")
    private void imagePicker() {
        if (GET_FROM_GALLERY == 1 && resultCode == Activity.RESULT_OK) {
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

            TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.bank_details));
            alertMessage.setText(getString(R.string.Cancelled_cheque_uploaded_successfully));
            alertPositiveButton.setVisibility(View.GONE);
            alertNegativeButton.setText(getString(R.string.ok));
            alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

            alertNegativeButton.setOnClickListener(view -> alert.dismiss());
            //------------------------------------------------------------------------------------------
            binding.bankDetailsCanceledCheckText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            binding.bankDetailsCanceledCheckUpload.setVisibility(View.INVISIBLE);
            binding.bankDetailsEditCanceledCheck.setVisibility(View.VISIBLE);
            binding.bankDetailsPreviewCancelledChequeImageView.setVisibility(View.VISIBLE);

            isImgUploaded = true;

            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            binding.bankDetailsCanceledCheckImage.setImageURI(selectedImage);
            previewDialogCancelledChequeImageView.setImageURI(selectedImage);

            PathForCC = picturePath;

        } else if (CAMERA_PIC_REQUEST1 == 1 && resultCode == Activity.RESULT_OK) {

            binding.bankDetailsCanceledCheckText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
            binding.bankDetailsCanceledCheckUpload.setVisibility(View.INVISIBLE);
            binding.bankDetailsEditCanceledCheck.setVisibility(View.VISIBLE);
            binding.bankDetailsPreviewCancelledChequeImageView.setVisibility(View.VISIBLE);

            isImgUploaded = true;

            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(this, image));
                binding.bankDetailsCanceledCheckImage.setImageBitmap(BitmapFactory.decodeFile(path));
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

                TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.bank_details));
                alertMessage.setText(getString(R.string.Cancelled_cheque_uploaded_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(view -> alert.dismiss());
                //------------------------------------------------------------------------------------------
            } catch (Exception e) {
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

                TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.bank_details));
                alertMessage.setText(getString(R.string.Cancelled_cheque_not_uploaded_successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(view -> {
                    alert.dismiss();
                    if (isEdit) {
                        binding.bankDetailsCanceledCheckUpload.setVisibility(View.VISIBLE);
                    } else {
                        binding.bankDetailsCanceledCheckText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        binding.bankDetailsCanceledCheckUpload.setVisibility(View.VISIBLE);
                        binding.bankDetailsEditCanceledCheck.setVisibility(View.INVISIBLE);
                        binding.bankDetailsPreviewCancelledChequeImageView.setVisibility(View.INVISIBLE);
                        isImgUploaded = false;
                    }
                });
                //------------------------------------------------------------------------------------------
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickBankDetailsOk(View view) {
        if (isImgUploaded) {
            UpdateUserDetails.updateUserIsBankDetailsGiven(userId, "1");
            if (!isEdit) {
                saveBank(createBankAcc());
                uploadCheque(bankId, PathForCC);
                JumpTo.goToViewPersonalDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
            } else {
                uploadCheque(bankId, PathForCC);
                UpdateBankDetails.updateBankName(bankId, binding.bankDetailsPersonNameTextEdit.getText().toString());
                UpdateBankDetails.updateBankAccountNumber(bankId, binding.bankDetailsAccountNumberEdit.getText().toString());
                UpdateBankDetails.updateBankReEnterAccountNumber(bankId, binding.bankDetailsReenterAccountNumberEdit.getText().toString());
                UpdateBankDetails.updateBankIFSICode(bankId, binding.bankDetailsIfscEdit.getText().toString());
                JumpTo.goToViewPersonalDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
            }
        } else {
            if (binding.bankDetailsPersonNameTextEdit.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please upload cancelled cheque or Enter Bank details", Toast.LENGTH_SHORT).show();
            } else if (binding.bankDetailsAccountNumberEdit.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter Account Number", Toast.LENGTH_SHORT).show();
            } else if (binding.bankDetailsReenterAccountNumberEdit.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter reAccount Number", Toast.LENGTH_SHORT).show();
            } else if (!binding.bankDetailsAccountNumberEdit.getText().toString().equals(binding.bankDetailsReenterAccountNumberEdit.getText().toString())) {
                Toast.makeText(this, getString(R.string.Account_number_does_not_match), Toast.LENGTH_SHORT).show();
            } else if (binding.bankDetailsIfscEdit.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter IFSC Code", Toast.LENGTH_SHORT).show();
            } else if (binding.bankDetailsIfscEdit.getText().toString().length() != 11) {
                Toast.makeText(this, "Please enter correct IFSC Code", Toast.LENGTH_SHORT).show();
            } else {
                if (isEdit) {
                    uploadCheque(bankId, PathForCC);
                    UpdateBankDetails.updateBankName(bankId, binding.bankDetailsPersonNameTextEdit.getText().toString());
                    UpdateBankDetails.updateBankAccountNumber(bankId, binding.bankDetailsAccountNumberEdit.getText().toString());
                    UpdateBankDetails.updateBankReEnterAccountNumber(bankId, binding.bankDetailsReenterAccountNumberEdit.getText().toString());
                    UpdateBankDetails.updateBankIFSICode(bankId, binding.bankDetailsIfscEdit.getText().toString());
                    JumpTo.goToViewPersonalDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
                } else {
                    if (bankVerified) {
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

                        TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
                        TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
                        TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
                        TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

                        alertTitle.setText(getString(R.string.bank_details));
                        alertMessage.setText(getString(R.string.Bank_Details_added_successfully));
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText(getString(R.string.ok));
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                        alertNegativeButton.setOnClickListener(view1 -> {
                            alert.dismiss();
                            //Update User Bank (IsBankAdded)
                            UpdateUserDetails.updateUserIsBankDetailsGiven(userId, "1");
                            JumpTo.goToViewPersonalDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
                        });
                    } else {
                        Toast.makeText(this, "Please enter correct Bank Details", Toast.LENGTH_SHORT).show();
                    }
                }
                binding.bankDetailsReenterAccountNumberEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }
        }
    }

    private void getUserDetailsMain() {
        Dialog loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.show();
        loadingDialog.setCancelable(false);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);

        Call<MainResponse> responseCall = ApiClient.getUserService().mainResponse(userId);
        responseCall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(@NonNull Call<MainResponse> call, @NonNull retrofit2.Response<MainResponse> response) {
                try {
                    MainResponse response1 = response.body();
                    MainResponse.Data list;
                    if (response1 != null) {
                        list = response1.getData();
                        userRoleAPI = list.getUser_type();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MainResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private final TextWatcher bankDetailsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String bankName1 = binding.bankDetailsPersonNameTextEdit.getText().toString().trim();
            String accNo1 = binding.bankDetailsAccountNumberEdit.getText().toString().trim();
            String reAccNo1 = binding.bankDetailsReenterAccountNumberEdit.getText().toString().trim();
            String ifscCode1 = binding.bankDetailsIfscEdit.getText().toString().trim();

            if (accNo1.equals(reAccNo1)) {
                binding.bankDetailsReenterAccountNumberEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                if (ifscCode1.length() == 11 && !accNo1.isEmpty() && !reAccNo1.isEmpty()) {
                    if (!bankName1.isEmpty()) {
//                        checkBankDetail(accNo1, ifscCode1);
                    }
                }
            } else {
                binding.bankDetailsReenterAccountNumberEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checkBankDetail(String accountNumber, String ifscCode) {
        Call<BankVerificationResponse> bankModelCall = ApiClient.getVerification().checkBankDetail(userId, "" + accountNumber, "" + ifscCode);
        bankModelCall.enqueue(new Callback<BankVerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<BankVerificationResponse> call, @NonNull Response<BankVerificationResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        BankVerificationResponse bankModel = response.body();
                        BankVerificationResponse.UserList list = null;
                        if (bankModel != null) {
                            list = bankModel.getData().get(0);
                        }
                        if (Objects.requireNonNull(list).getSuccess().equals("1")) {
                            binding.bankDetailsPersonNameTextEdit.setEnabled(false);
                            binding.bankDetailsPersonNameTextEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                            binding.bankDetailsAccountNumberEdit.setEnabled(false);
                            binding.bankDetailsAccountNumberEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                            binding.bankDetailsReenterAccountNumberEdit.setEnabled(false);
                            binding.bankDetailsReenterAccountNumberEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                            binding.bankDetailsIfscEdit.setEnabled(false);
                            binding.bankDetailsIfscEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_small, 0);
                            bankVerified = true;
                        } else {
                            Toast.makeText(BankDetailsActivity.this, "Please enter valid Bank Details", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(BankDetailsActivity.this, "Please enter valid Bank Details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BankDetailsActivity.this, "Please enter valid Bank Details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BankVerificationResponse> call, Throwable t) {
                Toast.makeText(BankDetailsActivity.this, "Please enter valid Bank Details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //--------------------------------------create Bank Details in API -------------------------------------
    public BankRequest createBankAcc() {
        BankRequest bankRequest = new BankRequest();
        bankRequest.setUser_id(userId);
        bankRequest.setBank_name(binding.bankDetailsPersonNameTextEdit.getText().toString());
        bankRequest.setAccount_number(binding.bankDetailsAccountNumberEdit.getText().toString());
        bankRequest.setRe_enter_acc_num(binding.bankDetailsReenterAccountNumberEdit.getText().toString());
        bankRequest.setIFSI_CODE(binding.bankDetailsIfscEdit.getText().toString());
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
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {


            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    public void onAccCheck(View view) {
        if (binding.bankDetailsAccountNumberEdit.getText().toString().equals(binding.bankDetailsReenterAccountNumberEdit.getText().toString())) {
            binding.bankDetailsReenterAccountNumberEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
        } else {
            binding.bankDetailsReenterAccountNumberEdit.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
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


    private void getBankDetails() {

        Call<GetBankDetailsResponse> responseCall = ApiClient.getBankService().getBankDetailsByBankId(bankId);
        responseCall.enqueue(new Callback<GetBankDetailsResponse>() {
            @Override
            public void onResponse(Call<GetBankDetailsResponse> call, retrofit2.Response<GetBankDetailsResponse> response) {
                try {
                    GetBankDetailsResponse response1 = response.body();
                    GetBankDetailsResponse.bankDetailByBankId list = response1.getData().get(0);

                    String bankNAME = list.getBank_name();
                    Log.i("BANK NAME", bankNAME);
                    binding.bankDetailsPersonNameTextEdit.setText(bankNAME);
                    binding.bankDetailsAccountNumberEdit.setText(list.getAccount_number());
                    binding.bankDetailsReenterAccountNumberEdit.setText(list.getRe_enter_acc_num());
                    binding.bankDetailsIfscEdit.setText(list.getIFSI_CODE());
                    ccUploadedAPI = list.getCancelled_cheque();

                    if (list.getCancelled_cheque().equals("null") || list.getCancelled_cheque() == null) {
                        binding.bankDetailsCanceledCheckUpload.setVisibility(View.VISIBLE);
                        binding.bankDetailsEditCanceledCheck.setVisibility(View.INVISIBLE);
                        isImgUploaded = false;
                        binding.bankDetailsPreviewCancelledChequeImageView.setVisibility(View.INVISIBLE);
                        binding.bankDetailsCanceledCheckText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        binding.bankDetailsCanceledCheckUpload.setVisibility(View.INVISIBLE);
                        binding.bankDetailsEditCanceledCheck.setVisibility(View.VISIBLE);
                        isImgUploaded = true;
                        binding.bankDetailsPreviewCancelledChequeImageView.setVisibility(View.VISIBLE);
                        binding.bankDetailsCanceledCheckText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success, 0);
                    }

                    String cancelledChequeURL = list.getCancelled_cheque();
                    new DownloadImageTask(binding.bankDetailsCanceledCheckImage).execute(cancelledChequeURL);
                    new DownloadImageTask(previewDialogCancelledCheque.findViewById(R.id.dialog_preview_image_view)).execute(cancelledChequeURL);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetBankDetailsResponse> call, Throwable t) {

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

    private void uploadCheque(String bankId1, String picPath) {

//        File file = new File(picPath);
//        File file = new File(getExternalFilesDir("/").getAbsolutePath(), file);

//        MultipartBody.Part body = prepareFilePart("cheque", Uri.fromFile(file));
        MultipartBody.Part body = prepareFilePart("cheque", newUriForCC);

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
//        if (userRoleAPI.equals("Customer")) {
//            JumpTo.goToCustomerDashboard(BankDetailsActivity.this, mobile, true);
//
//        } else {
//            JumpTo.goToServiceProviderDashboard(BankDetailsActivity.this, mobile, true, true);
//        }
        JumpTo.goToViewPersonalDetailsActivity(BankDetailsActivity.this, userId, mobile, true);
    }

}