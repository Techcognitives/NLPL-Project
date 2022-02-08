package com.nlpl.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class MakePayment {

    public static void makePayment(Activity activity) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_lGpYN1TVDxAQOn");

//        checkout.setImage(R.drawable.logo);

        String amount = "100";
        int sAmount = Math.round(Float.parseFloat(amount) * 100);

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Abhijeet Gotad");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#CC2027");
            options.put("currency", "INR");
            options.put("amount", sAmount);
            options.put("prefill.email", "email@example.com");
            options.put("prefill.contact", "9272576767");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }


    }
}
