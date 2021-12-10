package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nlpl.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener  {

    Button payBtn;
    TextView payText;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);
        Checkout.preload(getApplicationContext());

        payText=(TextView)findViewById(R.id.payment_text);
        payBtn=(Button)findViewById(R.id.payment_button);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });
    }

    private void makePayment() {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_xWrIEsyEwGvW61");

//        checkout.setImage(R.drawable.logo);
        final Activity activity = this;

        amount = "100";
        int sAmount = Math.round(Float.parseFloat(amount)*100);

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Abhijeet Gotad");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", sAmount);
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact","9272576767");
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment ID");
        builder.setMessage(s);
        builder.show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}