package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nlpl.R;
import com.nlpl.model.MapsModel.LocationModel;
import com.nlpl.model.Responses.UserResponse;
import com.nlpl.ui.adapters.GoogleMapTextInfoAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTrucksActivity extends AppCompat implements OnMapReadyCallback {

    String phone, userId, URLString;
    private GoogleMap mMap;
    Marker marker;
    ArrayList<UserResponse.UserList> userDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trucks);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
            Log.i("userId find loads", userId);
        }

        //--------------------------- action bar ---------------------------------------------------
        View actionBar = findViewById(R.id.find_trucks_action_bar);
        TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        ImageView actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        ImageView actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);
        ImageView actionBarWhatsApp = (ImageView) actionBar.findViewById(R.id.action_bar_whats_app);
        actionBarWhatsApp.setVisibility(View.VISIBLE);

        actionBarTitle.setText(getString(R.string.Find_Trucks));
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        actionBarBackButton.setOnClickListener(view -> {
            JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
        });
        //------------------------------------------------------------------------------------------

        View bottomNav = (View) findViewById(R.id.find_trucks_bottom_nav);
        ConstraintLayout spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        ConstraintLayout customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        customerDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileText.setText(getString(R.string.Find_Trucks));
        profileImageView.setImageDrawable(getDrawable(R.drawable.bottom_nav_search_small));
        View spView = (View) bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
        spView.setVisibility(View.INVISIBLE);
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
        customerView.setVisibility(View.VISIBLE);
        //------------------------------------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //----------------------------------------------------------------------------------------------
    public void getAllUserDetails() {
        Call<UserResponse> call = ApiClient.getUserService().getAllUserDetails();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                for (int i = 0; i < userResponse.getData().size(); i++) {
                    userDetails.add(userResponse.getData().get(i));
                }
                getAllDataLocation(userDetails);
//                UserResponse.UserList list =userResponse.getData().get(0);
//                Log.i("Details", list.getName());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        getAllUserDetails();

    }

    private void getAllDataLocation(ArrayList<UserResponse.UserList> userDetails) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting...");
        progressDialog.show();

        initmarker(userDetails);
        progressDialog.dismiss();
    }

    private void initmarker(ArrayList<UserResponse.UserList> userDetails) {
        LatLngBounds boundsIndia = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundsIndia, padding);
        mMap.animateCamera(cameraUpdate);

        mMap.getUiSettings().setMapToolbarEnabled(false);

        for (int i = 0; i < userDetails.size(); i++) {
            if (!userDetails.get(i).getUser_type().equals("Customer")) {
                double latt = Double.parseDouble(userDetails.get(i).getLatitude());
                double longi = Double.parseDouble(userDetails.get(i).getLongitude());

//        double latt = 18.5204;
//        double longi = 73.8567;

                LatLng location = new LatLng(latt,
                        longi);

                marker = mMap.addMarker(new MarkerOptions().position(location)
                        .title(userDetails.get(i).getUser_id())
                        .snippet(userDetails.get(i).getUser_type()));

                LocationModel info = new LocationModel();
                info.setUrl("");
                marker.setTag(info);

//            LatLng latLng = new LatLng(latt,
//                    longi);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 5.0f));

//                LatLngBounds boundsIndia = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
//                int padding = 0; // offset from edges of the map in pixels
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundsIndia, padding);
//                mMap.animateCamera(cameraUpdate);

                mMap.getUiSettings().setMapToolbarEnabled(false);

//        if(mListMarker.size() != 0){
                GoogleMapTextInfoAdapter googleMapTextInfoAdapter = new GoogleMapTextInfoAdapter(this, userDetails);

                mMap.setInfoWindowAdapter(googleMapTextInfoAdapter);

//        }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(FindTrucksActivity.this);
                JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
                break;

            case R.id.bottom_nav_customer_dashboard:
                ShowAlert.loadingDialog(FindTrucksActivity.this);
                JumpTo.goToFindTrucksActivity(FindTrucksActivity.this, userId, phone);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(FindTrucksActivity.this);
        JumpTo.goToCustomerDashboard(FindTrucksActivity.this, phone, true);
    }

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
        cameraText.setText("Whats App");
        TextView galleryText = chooseDialog.findViewById(R.id.dialog_photo_library_text);
        galleryText.setText("Call");

        ImageView camera = chooseDialog.findViewById(R.id.dialog_choose_camera_image);
        camera.setImageDrawable(getResources().getDrawable(R.drawable.whats_app_small));
        ImageView gallery = chooseDialog.findViewById(R.id.dialog__choose_photo_lirary_image);
        gallery.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
        gallery.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + "+918806930081"));
                startActivity(intent);
            }
        });
    }
}