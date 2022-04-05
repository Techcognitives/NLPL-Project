package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.nlpl.R;
import com.nlpl.model.MapsModel.LocationModel;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.ModelForRecyclerView.SearchLoadModel;
import com.nlpl.model.Responses.TripResponse;
import com.nlpl.model.Responses.TruckResponse;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.ui.adapters.AllTripAdapter;
import com.nlpl.ui.adapters.GoogleMapTextInfoAdapter;
import com.nlpl.ui.adapters.SearchLoadAdapter;
import com.nlpl.ui.adapters.SearchTripAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTrucksActivity extends AppCompat implements OnMapReadyCallback {

    String phone, userId, latitudeCurrent, longitudeCurrent;
    private GoogleMap mMap;
    Marker marker;
    ArrayList<UserResponse.UserList> userDetails = new ArrayList<>();
    Dialog loadingDialog;

    @SuppressLint({"UseCompatLoadingForColorStateLists", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trucks);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }

        getCurrentLocation(FindTrucksActivity.this);

        //--------------------------- action bar ---------------------------------------------------
        View actionBar = findViewById(R.id.find_trucks_action_bar);
        TextView actionBarTitle = actionBar.findViewById(R.id.action_bar_title);
        ImageView actionBarBackButton = actionBar.findViewById(R.id.action_bar_back_button);
        ImageView actionBarMenuButton = actionBar.findViewById(R.id.action_bar_menu);
        ImageView actionBarWhatsApp = actionBar.findViewById(R.id.action_bar_whats_app);
        actionBarWhatsApp.setVisibility(View.VISIBLE);

        actionBarTitle.setText("Trucks");
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setOnClickListener(view -> {
            if (userId == null) {
                ShowAlert.loadingDialog(FindTrucksActivity.this);
                JumpTo.goToRegistrationActivity(FindTrucksActivity.this, phone, true);
            } else {
                ShowAlert.loadingDialog(FindTrucksActivity.this);
                JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
            }
        });
        //------------------------------------------------------------------------------------------

        View bottomNav = findViewById(R.id.find_trucks_bottom_nav);
        ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        ConstraintLayout customerDashboard = bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        customerDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        TextView profileText = bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText("Trucks");
        profileImageView.setImageDrawable(getDrawable(R.drawable.bottom_nav_search_small));
        View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
        spView.setVisibility(View.INVISIBLE);
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
        customerView.setVisibility(View.VISIBLE);
        //------------------------------------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        //------------------------------------------------------------------------------------------
    }

    //----------------------------------------------------------------------------------------------
    public void getAllUserDetails() {
        Call<UserResponse> call = ApiClient.getUserService().getAllUserDetails();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                loadingDialog.dismiss();
                UserResponse userResponse = response.body();
                if (userResponse != null) userDetails.addAll(userResponse.getData());
                getAllDataLocation(userDetails);
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadingDialog = new Dialog(this);
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

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);

        getAllUserDetails();

    }

    private void getAllDataLocation(ArrayList<UserResponse.UserList> userDetails) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting...");
        progressDialog.show();

        initMarker(userDetails);
        progressDialog.dismiss();
    }

    private void initMarker(ArrayList<UserResponse.UserList> userDetails) {

        LatLng latLng = new LatLng(Double.parseDouble(latitudeCurrent), Double.parseDouble(longitudeCurrent));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 10.0f));

        mMap.getUiSettings().setMapToolbarEnabled(false);

        for (int i = 0; i < userDetails.size(); i++) {
            if (!userDetails.get(i).getUser_type().equals("Customer")) {
                double latitudes = Double.parseDouble(userDetails.get(i).getLatitude());
                double longitudes = Double.parseDouble(userDetails.get(i).getLongitude());

                LatLng location = new LatLng(latitudes,
                        longitudes);

                marker = mMap.addMarker(new MarkerOptions().position(location)
                        .title(userDetails.get(i).getUser_id())
                        .snippet(userDetails.get(i).getUser_type()));

                LocationModel info = new LocationModel();
                info.setUrl("");
                marker.setTag(info);

//        if(mListMarker.size() != 0){
                GoogleMapTextInfoAdapter googleMapTextInfoAdapter = new GoogleMapTextInfoAdapter(this, userDetails);
                mMap.setInfoWindowAdapter(googleMapTextInfoAdapter);
//        }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    public void getCurrentLocation(Activity activity) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        latitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLatitude()));
                        longitudeCurrent = String.valueOf(Html.fromHtml("" + addresses.get(0).getLongitude()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                if (userId == null) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(FindTrucksActivity.this);
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

                    TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText(getString(R.string.Please_Register));
                    alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
                    alertPositiveButton.setText(getString(R.string.Register_Now));
                    alertNegativeButton.setText(getString(R.string.cancel));

                    alertNegativeButton.setOnClickListener(view12 -> alert.dismiss());

                    alertPositiveButton.setOnClickListener(view1 -> {
                        alert.dismiss();
                        ShowAlert.loadingDialog(FindTrucksActivity.this);
                        JumpTo.goToRegistrationActivity(FindTrucksActivity.this, phone, true);
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    ShowAlert.loadingDialog(FindTrucksActivity.this);
                    JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
                }
                break;

            case R.id.bottom_nav_customer_dashboard:
                ShowAlert.loadingDialog(FindTrucksActivity.this);
                JumpTo.goToFindTrucksActivity(FindTrucksActivity.this, userId, phone);
                break;

            case R.id.bottom_nav_track:
                if (userId == null) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(FindTrucksActivity.this);
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

                    TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText(getString(R.string.Please_Register));
                    alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
                    alertPositiveButton.setText(getString(R.string.Register_Now));
                    alertNegativeButton.setText(getString(R.string.cancel));

                    alertNegativeButton.setOnClickListener(view12 -> alert.dismiss());

                    alertPositiveButton.setOnClickListener(view1 -> {
                        alert.dismiss();
                        ShowAlert.loadingDialog(FindTrucksActivity.this);
                        JumpTo.goToRegistrationActivity(FindTrucksActivity.this, phone, true);
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    ShowAlert.loadingDialog(FindTrucksActivity.this);
                    JumpTo.goToLPTrackActivity(FindTrucksActivity.this, phone, true);
                }
                break;

            case R.id.bottom_nav_trip:
                if (userId == null) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(FindTrucksActivity.this);
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

                    TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText(getString(R.string.Please_Register));
                    alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
                    alertPositiveButton.setText(getString(R.string.Register_Now));
                    alertNegativeButton.setText(getString(R.string.cancel));

                    alertNegativeButton.setOnClickListener(view12 -> alert.dismiss());

                    alertPositiveButton.setOnClickListener(view1 -> {
                        alert.dismiss();
                        ShowAlert.loadingDialog(FindTrucksActivity.this);
                        JumpTo.goToRegistrationActivity(FindTrucksActivity.this, phone, true);
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    ShowAlert.loadingDialog(FindTrucksActivity.this);
                    JumpTo.goToFindTripLPActivity(FindTrucksActivity.this, userId, phone, true);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (userId == null) {
            //----------------------- Alert Dialog -------------------------------------------------
            Dialog alert = new Dialog(FindTrucksActivity.this);
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

            TextView alertTitle = alert.findViewById(R.id.dialog_alert_title);
            TextView alertMessage = alert.findViewById(R.id.dialog_alert_message);
            TextView alertPositiveButton = alert.findViewById(R.id.dialog_alert_positive_button);
            TextView alertNegativeButton = alert.findViewById(R.id.dialog_alert_negative_button);

            alertTitle.setText(getString(R.string.Please_Register));
            alertMessage.setText(getString(R.string.You_cannot_bid_without_Registration));
            alertPositiveButton.setText(getString(R.string.Register_Now));
            alertNegativeButton.setText(getString(R.string.cancel));

            alertNegativeButton.setOnClickListener(view12 -> alert.dismiss());

            alertPositiveButton.setOnClickListener(view1 -> {
                alert.dismiss();
                ShowAlert.loadingDialog(FindTrucksActivity.this);
                JumpTo.goToRegistrationActivity(FindTrucksActivity.this, phone, true);
            });
            //------------------------------------------------------------------------------------------
        } else {
            ShowAlert.loadingDialog(FindTrucksActivity.this);
            JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickWhatsApp(View view) {
        Dialog chooseDialog = new Dialog(FindTrucksActivity.this);
        chooseDialog.setContentView(R.layout.dialog_choose);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(chooseDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.BOTTOM;

        chooseDialog.show();
        chooseDialog.getWindow().setAttributes(lp2);

        TextView cameraText = chooseDialog.findViewById(R.id.dialog_camera_text);
        cameraText.setText(getString(R.string.whats_app));
        TextView galleryText = chooseDialog.findViewById(R.id.dialog_photo_library_text);
        galleryText.setText(getString(R.string.call));

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        camera.setImageDrawable(getResources().getDrawable(R.drawable.whats_app_small));
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);
        gallery.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
        gallery.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        camera.setOnClickListener(view1 -> {
            chooseDialog.dismiss();
            String mobileNumber = "8806930081";
            String message = "";
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + mobileNumber + "&text=" + message));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(FindTrucksActivity.this, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
            }
        });

        gallery.setOnClickListener(view12 -> {
            chooseDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + "+918806930081"));
            startActivity(intent);
        });
    }

}