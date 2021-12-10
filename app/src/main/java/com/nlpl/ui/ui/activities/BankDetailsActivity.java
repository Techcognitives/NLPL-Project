package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;

public class BankDetailsActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle, language;
    ImageView actionBarBackButton;
    Dialog languageDialog;

    String mobile, name;
    Boolean isPersonalDetailsDone, isBankDetailsDone, isAddTrucksDone, isAddDriversDone;

    EditText bankName, accountNo, reAccount, ifscCode;
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile3");
            name = bundle.getString("name3");
            isPersonalDetailsDone = bundle.getBoolean("isPersonal");
            isBankDetailsDone = bundle.getBoolean("isBank");
            isAddTrucksDone = bundle.getBoolean("isTrucks");
            isAddDriversDone = bundle.getBoolean("isDriver");
            Log.i("Mobile No", mobile);
            Log.i("Name", name);
        }

        action_bar = findViewById(R.id.bank_details_action_bar);
        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        language = (TextView) action_bar.findViewById(R.id.action_bar_language_selector);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog = new Dialog(BankDetailsActivity.this);
                languageDialog.setContentView(R.layout.dialog_language);
                languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(languageDialog.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp2.gravity = Gravity.BOTTOM;

                languageDialog.show();
                languageDialog.getWindow().setAttributes(lp2);

            }
        });

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
        ifscCode = (EditText) findViewById(R.id.editTextTextPersonName6);
        okButton = (Button) findViewById(R.id.bank_details_ok_button);
        okButton.setEnabled(false);

        if (!bankName.getText().toString().isEmpty() && !accountNo.getText().toString().isEmpty() && !reAccount.getText().toString().isEmpty() && !ifscCode.getText().toString().isEmpty()){
            okButton.setBackground(getResources().getDrawable(R.drawable.button_active));
            okButton.setEnabled(true);
        }
    }

    public void onClickBankDetailsOk(View view) {
        Intent i8 = new Intent(BankDetailsActivity.this, ProfileAndRegistrationActivity.class);
        i8.putExtra("mobile2", mobile);
        i8.putExtra("name2", name);
        i8.putExtra("isPersonal", isPersonalDetailsDone);
            i8.putExtra("isBank", true);
            i8.putExtra("isTrucks", isAddTrucksDone);
            i8.putExtra("isDriver",isAddDriversDone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        overridePendingTransition(0, 0);
        BankDetailsActivity.this.finish();
    }
}