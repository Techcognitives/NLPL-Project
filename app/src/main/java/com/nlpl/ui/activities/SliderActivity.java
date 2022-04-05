package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nlpl.R;
import com.nlpl.ui.adapters.SliderAdapter;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class SliderActivity extends AppCompat {

    String url1 = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png";
    String url2 = "https://qphs.fs.quoracdn.net/main-qimg-8e203d34a6a56345f86f1a92570557ba.webp";
    String url3 = "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            Log.i("Mobile No Registration", mobile);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        ArrayList<Drawable> sliderDataArrayList = new ArrayList<>();
        // initializing the slider view.
        SliderView sliderView = findViewById(R.id.slider);

        // adding the urls inside array list
        sliderDataArrayList.add(getDrawable(R.drawable.slider_slide_three));
        sliderDataArrayList.add(getDrawable(R.drawable.slider_slide_one));
        sliderDataArrayList.add(getDrawable(R.drawable.slider_slide_two));

        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);

        // below method is use to set
        // scroll time in seconds.
        sliderView.setScrollTimeInSec(4);

        // to set it scrollable automatically
        // we use below method.
        sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        sliderView.startAutoCycle();
    }

    public void onClickRegisterNow(View view) {
        ShowAlert.loadingDialog(SliderActivity.this);
        JumpTo.goToRegistrationActivity(SliderActivity.this, mobile, false, null, true);
    }

    public void onClickSkipRegistration(View view) {
        ShowAlert.loadingDialog(SliderActivity.this);
        JumpTo.goToServiceProviderDashboard(SliderActivity.this, mobile, true, true);
    }
}