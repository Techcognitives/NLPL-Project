package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nlpl.R;
import com.nlpl.model.Responses.PreferedLocationResponse;
import com.nlpl.ui.adapters.PreferredLocationAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.CreateUser;
import com.nlpl.utils.GetLocationPickUp;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsAndPreferences extends AppCompat {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    PreferredLocationAdapter adapter;
    ArrayList<PreferedLocationResponse.UserList> userList = new ArrayList<>();

    String phone, userId, role, latForAddress, longForAddress;
    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings_and_preferences);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
            role = bundle.getString("role");
        }

        //-------------------------------------action Bar-------------------------------------------
        actionBar = findViewById(R.id.customer_setting_and_preferences_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.Settings_and_Preferences));
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.INVISIBLE);

        actionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (role.equals("Customer")) {
//                    ShowAlert.loadingDialog(SettingsAndPreferences.this);
//                    JumpTo.goToCustomerDashboard(SettingsAndPreferences.this, phone, true);
//                } else {
//                    ShowAlert.loadingDialog(SettingsAndPreferences.this);
//                    JumpTo.goToServiceProviderDashboard(SettingsAndPreferences.this, phone, true , true);
//                }
                JumpTo.goToViewPersonalDetailsActivity(SettingsAndPreferences.this, userId, phone, true);
            }
        });
        //------------------------------------------------------------------------------------------

        recyclerView = findViewById(R.id.settings_location_list);
        progressBar = findViewById(R.id.settings_progressBar);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PreferredLocationAdapter(SettingsAndPreferences.this, userList);
        recyclerView.setAdapter(adapter);

        fetchPreferredLocations();
    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(SettingsAndPreferences.this);
        JumpTo.getToSettingAndPreferences(SettingsAndPreferences.this, phone, userId, role, true);
    }

    private void fetchPreferredLocations() {
        progressBar.setVisibility(View.VISIBLE);
        Call<PreferedLocationResponse> call = ApiClient.getPreferredLocationService().getPreferredLocation(userId);
        call.enqueue(new Callback<PreferedLocationResponse>() {
            @Override
            public void onResponse(Call<PreferedLocationResponse> call, Response<PreferedLocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PreferedLocationResponse preferedLocationResponse = response.body();
                    if (preferedLocationResponse != null)
                        userList.addAll(preferedLocationResponse.getData());
                    adapter.refreshData(userList);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PreferedLocationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingsAndPreferences.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        if (role.equals("Customer")) {
//            ShowAlert.loadingDialog(SettingsAndPreferences.this);
//            JumpTo.goToCustomerDashboard(SettingsAndPreferences.this, phone, true);
//        } else {
//            ShowAlert.loadingDialog(SettingsAndPreferences.this);
//            JumpTo.goToServiceProviderDashboard(SettingsAndPreferences.this, phone, true, true);
//        }
        JumpTo.goToViewPersonalDetailsActivity(SettingsAndPreferences.this, userId, phone, true);
    }

    public void deleteLocation(PreferedLocationResponse.UserList obj) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(SettingsAndPreferences.this);
        alert.setContentView(R.layout.dialog_alert);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(true);

        TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText(getString(R.string.Delete_Preferred_Location));
        alertMessage.setText(getString(R.string.Are_you_sure_you_want_to_delete_Preferred_Location));
        alertPositiveButton.setText(getString(R.string.ok));
        alertNegativeButton.setText(getString(R.string.cancel));
        alertPositiveButton.setOnClickListener(view -> {
            deletePreferredLocation(obj.getPref_locations_id());
            alert.dismiss();
            RearrangeItems();
        });
        alertNegativeButton.setOnClickListener(view -> alert.dismiss());
    }

    private void deletePreferredLocation(String locationId) {
        Call<PreferedLocationResponse> call = ApiClient.getPreferredLocationService().deleteLocation(locationId);
        call.enqueue(new Callback<PreferedLocationResponse>() {
            @Override
            public void onResponse(Call<PreferedLocationResponse> call, retrofit2.Response<PreferedLocationResponse> response) {

            }

            @Override
            public void onFailure(Call<PreferedLocationResponse> call, Throwable t) {
            }
        });
    }

    public void onClickAddPreferredLocation(View view) {
        Dialog dialogSelectSC = new Dialog(SettingsAndPreferences.this);
        dialogSelectSC.setContentView(R.layout.dialog_state_city);
        dialogSelectSC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSelectSC.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialogSelectSC.show();
        dialogSelectSC.getWindow().setAttributes(lp);
        dialogSelectSC.setCancelable(true);

        TextView selectState = dialogSelectSC.findViewById(R.id.registration_select_state);
        TextView selectCity = dialogSelectSC.findViewById(R.id.registration_select_city);
        TextView okButton = dialogSelectSC.findViewById(R.id.dialog_alert_negative_button);

        selectState.setOnClickListener(view1 -> SelectState.selectState(SettingsAndPreferences.this, selectState, selectCity));
        selectCity.setOnClickListener(view1 -> {
            if (!selectState.getText().toString().isEmpty()) {
                SelectCity.selectCity(SettingsAndPreferences.this, selectState.getText().toString(), selectCity);
            } else {
                Toast.makeText(this, "Please select State first", Toast.LENGTH_SHORT).show();
            }
        });
        okButton.setOnClickListener(view1 -> {
            if (selectState.getText().toString().isEmpty()) {
                Toast.makeText(this, "please select State", Toast.LENGTH_SHORT).show();
            } else if (selectCity.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select City", Toast.LENGTH_SHORT).show();
            } else {
                GetLocationPickUp geoLocation = new GetLocationPickUp();
                String addressFull = selectState.getText().toString() + " " + selectCity.getText().toString();
                geoLocation.geLatLongPickUp(addressFull, getApplicationContext(), new GeoHandlerLatitude());

                CreateUser.savePreferredLocation(CreateUser.createPreferredLocation(userId, selectState.getText().toString(), selectCity.getText().toString(), "", latForAddress, longForAddress));
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(SettingsAndPreferences.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                lp1.copyFrom(alert.getWindow().getAttributes());
                lp1.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp1.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp1.gravity = Gravity.CENTER;

                alert.show();
                alert.getWindow().setAttributes(lp1);
                alert.setCancelable(false);

                TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                alertTitle.setText(getString(R.string.Company_Details));
                alertMessage.setText(getString(R.string.Company_Details_added_Successfully));
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                        RearrangeItems();
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });
    }

    private class GeoHandlerLatitude extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String latLong, lat = null, lon = null;
            switch (msg.what) {
                case 1:
                    try {
                        Bundle bundle = msg.getData();
                        latLong = bundle.getString("latLong1");
                        String[] arrSplit = latLong.split(" ");
                        for (int i = 0; i < arrSplit.length; i++) {
                            lat = arrSplit[0];
                            lon = arrSplit[1];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    lat = null;
                    lon = null;
            }
            try {
                latForAddress = lat;
                longForAddress = lon;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}