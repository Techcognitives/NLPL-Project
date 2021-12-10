package com.nlpl.ui.ui.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

public class OTPReceiver extends BroadcastReceiver {

    private static EditText editText_otp;
    private  static Button optButton;

    public void setEditText_otp(EditText editText, Button button){
        OTPReceiver.editText_otp = editText;
        OTPReceiver.optButton = button;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage : smsMessages){
            String message_body = smsMessage.getMessageBody();
//            String getOTP = message_body.substring(0,6);
//            Log.i("OTP is", getOTP);
//            editText_otp.setText(getOTP);
//            optButton.performClick();
        }
    }
}
