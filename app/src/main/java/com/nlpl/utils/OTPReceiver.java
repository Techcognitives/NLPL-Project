package com.nlpl.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.chaos.view.PinView;

import java.util.ArrayList;

public class OTPReceiver extends BroadcastReceiver {

    private static PinView otpCode;
    private static Button otpButton;

    public void setEditText_otp(PinView otpCode, Button otpButton) {
        OTPReceiver.otpCode = otpCode;
        OTPReceiver.otpButton = otpButton;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage : smsMessages) {
            String message_body = smsMessage.getMessageBody();
            try {
                String otp = message_body.substring(0, 6);
                otpCode.setText(otp);
                otpButton.performClick();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
