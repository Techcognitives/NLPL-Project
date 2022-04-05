package com.nlpl.ui.activities;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.model.Requests.PostATripRequest;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.PostATripResponse;
import com.nlpl.model.Responses.TripResponse;
import com.nlpl.model.Responses.TruckResponse;
import com.nlpl.model.UpdateModel.Models.UpdateTripDetails;
import com.nlpl.ui.adapters.TrucksListAdapterBid;
import com.nlpl.ui.adapters.TrucksListTripAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;

import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectBudget;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectDate;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.ShowAlert;


import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostATripActivity extends AppCompat {

    TextView selectDate, selectTime, selectBudget, selectTruck, bodyType, loadType, selectPickUpState, selectPickUpCity, selectDropState, selectDropCity, note, deleteTrip;
    String phone, userId, tripId;
    ArrayList<TruckResponse.TruckList> truckList = new ArrayList<>();
    Dialog dialogSelectTruck;
    TrucksListTripAdapter truckListAdapter;
    Boolean isEdit;
    Button postTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_atrip);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            tripId = bundle.getString("tripId");
        }

        View action_bar = findViewById(R.id.post_a_trip_action_bar);
        TextView actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        ImageView actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Post A Trip");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(PostATripActivity.this);
                JumpTo.goToFindLoadsActivity(PostATripActivity.this, userId, phone, true);
            }
        });

        selectDate = findViewById(R.id.post_a_trip_date_text_view);
        selectTime = findViewById(R.id.post_a_trip_time_text_view);
        selectBudget = findViewById(R.id.post_a_trip_budget_text_view);
        selectTruck = findViewById(R.id.post_a_trip_select_truck);
        bodyType = findViewById(R.id.post_a_trip_vehicle_model);
        loadType = findViewById(R.id.post_a_trip_capacity_text_view);
        selectPickUpState = findViewById(R.id.post_a_trip_select_state_pick_up);
        selectPickUpCity = findViewById(R.id.post_a_trip_select_city_pick_up);
        selectDropState = findViewById(R.id.post_a_trip_select_state_drop);
        selectDropCity = findViewById(R.id.post_a_trip_select_city_drop);
        note = findViewById(R.id.post_a_trip_notes_edit_text);
        deleteTrip = findViewById(R.id.delete_trip_in_post_a_load);
        postTrip = findViewById(R.id.post_a_trip_ok_button);

        if (isEdit) {
            getTripDetails();
            actionBarTitle.setText("Edit Trip");
            deleteTrip.setVisibility(View.VISIBLE);
            postTrip.setText("Update Trip");
        } else {
            deleteTrip.setVisibility(View.GONE);
        }

        getTruckDetails();

        //------------------------------------- Truck Select --------------------------------------
        dialogSelectTruck = new Dialog(PostATripActivity.this);
        dialogSelectTruck.setContentView(R.layout.dialog_spinner_bind);
        dialogSelectTruck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogSelectTruckTitle = (TextView) dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_title);
        TextView dialogSelectTruckAddTruck = (TextView) dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView dialogSelectTruckOkButton = (TextView) dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_cancel);

        dialogSelectTruckTitle.setText(getString(R.string.selectTruck));
        dialogSelectTruckAddTruck.setVisibility(View.GONE);
        dialogSelectTruckOkButton.setVisibility(View.GONE);

        RecyclerView truckListRecyclerView = dialogSelectTruck.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        truckListAdapter = new TrucksListTripAdapter(PostATripActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);
    }

    public void onClickSelectDate(View view) {
        SelectDate.selectDate(PostATripActivity.this, selectDate);
    }

    public void onClickSelectTime(View view) {
        SelectDate.selectTime(PostATripActivity.this, selectTime);
    }

    public void onClickSelectBudget(View view) {
        SelectBudget.budgetSet(PostATripActivity.this, selectBudget);
    }

    public void onClickSelectTruck(View view) {
//        truckListAdapter.updateData(truckList);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSelectTruck.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialogSelectTruck.show();
        dialogSelectTruck.getWindow().setAttributes(lp);
    }

    public void onClickPickUpState(View view) {
        SelectState.selectState(PostATripActivity.this, selectPickUpState, selectPickUpCity);
    }

    public void onClickPickUpCity(View view) {
        if (!selectPickUpState.getText().toString().isEmpty()) {
            SelectCity.selectCity(PostATripActivity.this, selectPickUpState.getText().toString(), selectPickUpCity);
        } else {
            Toast.makeText(this, "Please select Pick-up State first", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickDropState(View view) {
        SelectState.selectState(PostATripActivity.this, selectDropState, selectDropCity);
    }

    public void onClickDropCity(View view) {
        if (!selectDropState.getText().toString().isEmpty()) {
            SelectCity.selectCity(PostATripActivity.this, selectDropState.getText().toString(), selectDropCity);
        } else {
            Toast.makeText(this, "Please select Drop State first", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickShowList(View view) {
        Dialog selectNoteDialog = new Dialog(PostATripActivity.this);
        selectNoteDialog.setContentView(R.layout.dialog_spinner);
        selectNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectNoteDialog.show();
        selectNoteDialog.setCancelable(true);
        TextView model_title = selectNoteDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText(getString(R.string.Select_Material_Type));

        ListView modelList = (ListView) selectNoteDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, Arrays.asList(getResources().getStringArray(R.array.array_load_notes_suggestions)));
        modelList.setAdapter(adapter1);

        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                note.setText(adapter1.getItem(i));
                selectNoteDialog.dismiss();
            }
        });
    }

    public void onClickPostATrip(View view) {
        if (selectDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Date", Toast.LENGTH_SHORT).show();
        } else if (selectTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Time", Toast.LENGTH_SHORT).show();
        } else if (selectBudget.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Budget", Toast.LENGTH_SHORT).show();
        } else if (selectTruck.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Truck", Toast.LENGTH_SHORT).show();
        } else if (selectPickUpState.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Pick-up State", Toast.LENGTH_SHORT).show();
        } else if (selectPickUpCity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Pick-up City", Toast.LENGTH_SHORT).show();
        } else if (selectDropState.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Drop State", Toast.LENGTH_SHORT).show();
        } else if (selectDropCity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select Drop City", Toast.LENGTH_SHORT).show();
        } else {
            if (isEdit) {
                updateTripDetails();
            } else {
                PostTrip(createTripReq());
            }
        }
    }

    private void getTruckDetails() {
        Call<TruckResponse> truckModelClassCall = ApiClient.addTruckService().getTruckDetails(userId);
        truckModelClassCall.enqueue(new Callback<TruckResponse>() {
            @Override
            public void onResponse(Call<TruckResponse> call, Response<TruckResponse> response) {
                TruckResponse truckModelClass = response.body();
                if (response.isSuccessful()) truckList.addAll(truckModelClass.getData());
            }

            @Override
            public void onFailure(Call<TruckResponse> call, Throwable t) {

            }
        });
    }

    public PostATripRequest createTripReq() {
        PostATripRequest postATripRequest = new PostATripRequest();
        postATripRequest.setUser_id(userId);
        postATripRequest.setTrip_date(selectDate.getText().toString());
        postATripRequest.setTrip_start_time(selectTime.getText().toString());
        postATripRequest.setTrip_budget(selectBudget.getText().toString());
        postATripRequest.setPick_state(selectPickUpState.getText().toString());
        postATripRequest.setPick_city(selectPickUpCity.getText().toString());
        postATripRequest.setDrop_state(selectDropState.getText().toString());
        postATripRequest.setDrop_city(selectDropCity.getText().toString());
        postATripRequest.setVehicle_model(bodyType.getText().toString());
        postATripRequest.setCapacity(loadType.getText().toString());
        postATripRequest.setNotes_meterial_des(note.getText().toString());

        return postATripRequest;
    }


    private void PostTrip(PostATripRequest postATripRequest) {
        Call<PostATripResponse> post_tripCall = ApiClient.getPostTripService().PostTrip(postATripRequest);
        post_tripCall.enqueue(new Callback<PostATripResponse>() {
            @Override
            public void onResponse(Call<PostATripResponse> call, Response<PostATripResponse> response) {
                if (response.isSuccessful()) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostATripActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText("Post A Trip");
                    alertMessage.setText("Trip Posted Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            JumpTo.goToFindLoadsActivity(PostATripActivity.this, userId, phone, true);
                        }
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostATripActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText("Post A Trip");
                    alertMessage.setText("Trip Post Unsuccessful\nPlease try again");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }

            @Override
            public void onFailure(Call<PostATripResponse> call, Throwable t) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(PostATripActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText("Post A Trip");
                alertMessage.setText("Trip Post Unsuccessful\nPlease try again");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });

    }

    public void onClickDeleteTrip(View view) {
        Dialog alert = new Dialog(PostATripActivity.this);
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

        alertTitle.setText("Delete Trip");
        alertMessage.setText("Are you sure,\nYou want to delete Trip Details?");
        alertPositiveButton.setText(getString(R.string.yes));
        alertNegativeButton.setText(getString(R.string.no));

        alertPositiveButton.setOnClickListener(view1 -> {
            alert.dismiss();
            deleteTrip(tripId);
        });

        alertNegativeButton.setOnClickListener(view2 -> alert.dismiss());
    }

    public void onClickTruckList(TruckResponse.TruckList obj) {
        selectTruck.setText(obj.getVehicle_no());
        bodyType.setText(obj.getTruck_type());
        loadType.setText(obj.getTruck_carrying_capacity());
        dialogSelectTruck.dismiss();
    }

    public void updateTripDetails() {
        UpdateTripDetails updateTripDetails = new UpdateTripDetails("" + selectDate.getText().toString(),
                "" + selectTime.getText().toString(), "" + selectBudget.getText().toString(),
                "", "", "", "" + bodyType.getText().toString(),
                "", "" + loadType.getText().toString(), "", "", "",
                "" + selectPickUpCity.getText().toString(), "" + selectPickUpState.getText().toString(),
                "India", "", "", "" + selectDropCity.getText().toString(),
                "" + selectDropState.getText().toString(), "India", "" + note.getText().toString(), 0, "");
        Call<UpdateTripDetails> call = ApiClient.getPostTripService().updateTripDetails(tripId, updateTripDetails);
        call.enqueue(new Callback<UpdateTripDetails>() {
            @Override
            public void onResponse(Call<UpdateTripDetails> call, Response<UpdateTripDetails> response) {
                if (response.isSuccessful()) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostATripActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText("Update Trip");
                    alertMessage.setText("Trip Updated Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            JumpTo.goToFindLoadsActivity(PostATripActivity.this, userId, phone, true);
                        }
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostATripActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText("Update Trip");
                    alertMessage.setText("Trip Update Unsuccessful\nPlease try again");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }

            @Override
            public void onFailure(Call<UpdateTripDetails> call, Throwable t) {
                //----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(PostATripActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText("Update Trip");
                alertMessage.setText("Trip Update Unsuccessful\nPlease try again");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });
    }

    public void deleteTrip(String tripId) {
        Call<TripResponse> call = ApiClient.getPostTripService().deleteTrip(tripId);
        call.enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                if (response.isSuccessful()) {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostATripActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText("Delete Trip");
                    alertMessage.setText("Trip Deleted Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            JumpTo.goToFindLoadsActivity(PostATripActivity.this, userId, phone, true);
                        }
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostATripActivity.this);
                    alert.setContentView(R.layout.dialog_alert_single_button);
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

                    alertTitle.setText("Delete Trip");
                    alertMessage.setText("Trip Delete Unsuccessful\nPlease try again");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText(getString(R.string.ok));
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {
//----------------------- Alert Dialog -------------------------------------------------
                Dialog alert = new Dialog(PostATripActivity.this);
                alert.setContentView(R.layout.dialog_alert_single_button);
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

                alertTitle.setText("Delete Trip");
                alertMessage.setText("Trip Delete Unsuccessful\nPlease try again");
                alertPositiveButton.setVisibility(View.GONE);
                alertNegativeButton.setText(getString(R.string.ok));
                alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                //------------------------------------------------------------------------------------------
            }
        });
    }

    private void getTripDetails() {
        Call<TripResponse> tripModelClass = ApiClient.getPostTripService().getTripDetails(tripId);
        tripModelClass.enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                TripResponse tripModelClass1 = response.body();
                TripResponse.TripList list = tripModelClass1.getData().get(0);
                selectDate.setText(list.getTrip_date());
                selectTime.setText(list.getTrip_start_time());
                selectBudget.setText(list.getTrip_budget());
                bodyType.setText(list.getVehicle_model());
                loadType.setText(list.getCapacity());
                selectPickUpState.setText(list.getPick_state());
                selectPickUpCity.setText(list.getPick_city());
                selectDropState.setText(list.getDrop_state());
                selectDropCity.setText(list.getDrop_city());
                note.setText(list.getNotes_meterial_des());
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(this);
        JumpTo.goToFindLoadsActivity(this, userId, phone, true);
    }
}