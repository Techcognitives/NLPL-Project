package com.nlpl.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.nlpl.R;
import com.nlpl.ui.activities.PostATripActivity;

public class SelectBudget {

    public static void budgetSet(Activity activity, TextView budgetTextView) {
        Dialog setBudget = new Dialog(activity);
        setBudget.setContentView(R.layout.dialog_budget);

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(setBudget.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;

        setBudget.show();
        setBudget.setCancelable(false);
        setBudget.getWindow().setAttributes(lp2);

        EditText budget = setBudget.findViewById(R.id.dialog_budget_edit);
        Button okBudget = setBudget.findViewById(R.id.dialog_budget_ok_btn);
        TextView amountInWords = setBudget.findViewById(R.id.dialog_budget_amount_in_words);
        Button cancelButton = setBudget.findViewById(R.id.dialog_budget_cancel_button);

        budget.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String newPreviousBudget = budgetTextView.getText().toString().replaceAll(",", "");
        budget.setText(newPreviousBudget);

        cancelButton.setOnClickListener(view -> setBudget.dismiss());
//        if (!previousBudget.isEmpty()) {
        okBudget.setEnabled(true);
        okBudget.setBackgroundResource((R.drawable.button_active));
//        } else {
//
//            okBudget.setEnabled(false);
//            okBudget.setBackgroundResource((R.drawable.button_de_active));
//        }

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                budgetTextView.setText("0");
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {

                    String finalBudget, lastThree = "";
                    String budget1 = budget.getText().toString();
                    if (budget1.length() > 3) {
                        lastThree = budget1.substring(budget1.length() - 3);
                    }
                    if (budget1.length() == 1) {
                        finalBudget = budget1;
                        budgetTextView.setText(finalBudget);
                    } else if (budget1.length() == 2) {
                        finalBudget = budget1;
                        budgetTextView.setText(finalBudget);
                    } else if (budget1.length() == 3) {
                        finalBudget = budget1;
                        budgetTextView.setText(finalBudget);
                    } else if (budget1.length() == 4) {
                        Character fourth = budget1.charAt(0);
                        finalBudget = fourth + "," + lastThree;
                        budgetTextView.setText(finalBudget);
                    } else if (budget1.length() == 5) {
                        Character fifth = budget1.charAt(0);
                        Character fourth = budget1.charAt(1);
                        finalBudget = fifth + "" + fourth + "," + lastThree;
                        budgetTextView.setText(finalBudget);
                    } else if (budget1.length() == 6) {
                        Character fifth = budget1.charAt(1);
                        Character fourth = budget1.charAt(2);
                        Character sixth = budget1.charAt(0);
                        finalBudget = sixth + "," + fifth + "" + fourth + "," + lastThree;
                        budgetTextView.setText(finalBudget);
                    } else if (budget1.length() == 7) {
                        Character seventh = budget1.charAt(0);
                        Character sixth = budget1.charAt(1);
                        Character fifth = budget1.charAt(2);
                        Character fourth = budget1.charAt(3);
                        finalBudget = seventh + "" + sixth + "," + fifth + "" + fourth + "," + lastThree;
                        budgetTextView.setText(finalBudget);
                    }
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                } else {
                    budgetTextView.setText("0");
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                }

                TextView amountInWords = setBudget.findViewById(R.id.dialog_budget_amount_in_words);
                if (budgetEditText.length() > 0) {
                    String return_val_in_english = EnglishNumberToWords.convert(Long.parseLong(budgetEditText));
                    amountInWords.setText(return_val_in_english);
                } else {
                    amountInWords.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        okBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (budget.getText().toString().isEmpty()) {
                    budgetTextView.setText("0");
                }
                setBudget.dismiss();
            }
        });

    }
}
