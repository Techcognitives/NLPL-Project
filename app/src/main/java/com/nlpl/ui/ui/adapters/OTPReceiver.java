package com.nlpl.ui.ui.adapters;

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

import com.razorpay.OTP;

import java.util.ArrayList;

public class OTPReceiver extends BroadcastReceiver {

    private static EditText otp1, otp2, otp3, otp4, otp5, otp6;
    ArrayList nameSplit;

    public void setEditText_otp(EditText editText1, EditText editText2, EditText editText3, EditText editText4, EditText editText5, EditText editText6){
        OTPReceiver.otp1 = editText1;
        OTPReceiver.otp2 = editText2;
        OTPReceiver.otp3 = editText3;
        OTPReceiver.otp4 = editText4;
        OTPReceiver.otp5 = editText5;
        OTPReceiver.otp6 = editText6;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage : smsMessages){
            String message_body = smsMessage.getMessageBody();
            try{
                String otp = message_body.substring(0,6);
                Log.i("OTP is", otp);

                String[] allName = otp.split("", 6);
                nameSplit = new ArrayList<>();
                for (String sepName : allName) {
                    Log.i("Sep Name", sepName);
                    nameSplit.add(sepName);
                }
                String first = (String) nameSplit.get(0);
                String second = (String) nameSplit.get(1);
                String third = (String) nameSplit.get(2);
                String fourth = (String) nameSplit.get(3);
                String fifth = (String) nameSplit.get(4);
                String sixth = (String) nameSplit.get(5);

                Log.i("First OTP", first);
                Log.i("Second OTP", second);
                Log.i("Third OTP", third);
                Log.i("Fourth OTP", fourth);
                Log.i("Fifth OTP", fifth);
                Log.i("Sixth OTP", sixth);

                otp1.setText(first);
                otp2.setText(second);
                otp3.setText(third);
                otp4.setText(fourth);
                otp5.setText(fifth);
                otp6.setText(sixth);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
