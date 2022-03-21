package com.nlpl.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlpl.R;
import com.nlpl.ui.activities.OtpCodeActivity;

public class ShowAlert {

    public static void showAlert(Activity activity, String title, String message, Boolean visibilityRightButton, Boolean visibilityOfLeftButton, String rightButtonText, String leftButtonText){
        Dialog alert = new Dialog(activity);
        alert.setContentView(R.layout.dialog_alert);
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

        alertTitle.setText(title);
        alertMessage.setText(message);

        alertPositiveButton.setText(leftButtonText);
        if (visibilityOfLeftButton) {
            alertPositiveButton.setVisibility(View.VISIBLE);
        }else{
            alertPositiveButton.setVisibility(View.GONE);
        }

        alertNegativeButton.setText(rightButtonText);
        if (visibilityRightButton){
            alertNegativeButton.setVisibility(View.VISIBLE);
        }else{
            alertNegativeButton.setVisibility(View.GONE);
        }

        alertNegativeButton.setBackground(activity.getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.button_blue)));

        alertNegativeButton.setOnClickListener(view -> {
            alert.dismiss();
        });
        alertPositiveButton.setOnClickListener(View -> {
            alert.dismiss();
        });
    }

    public static void loadingDialog(Activity activity){
        Dialog loadingDialog = new Dialog(activity);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.show();
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(activity, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);
    }

}
