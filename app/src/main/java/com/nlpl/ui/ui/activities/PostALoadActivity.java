package com.nlpl.ui.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nlpl.R;
import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.PostLoadResponse;
import com.nlpl.model.UpdateLoadPost.UpdateCustomerBudget;
import com.nlpl.model.UpdateLoadPost.UpdateCustomerNoteForSP;
import com.nlpl.model.UpdateLoadPost.UpdateLoadBodyType;
import com.nlpl.model.UpdateLoadPost.UpdateLoadCapacity;
import com.nlpl.model.UpdateLoadPost.UpdateLoadDropAdd;
import com.nlpl.model.UpdateLoadPost.UpdateLoadDropCity;
import com.nlpl.model.UpdateLoadPost.UpdateLoadDropCountry;
import com.nlpl.model.UpdateLoadPost.UpdateLoadDropPinCode;
import com.nlpl.model.UpdateLoadPost.UpdateLoadDropState;
import com.nlpl.model.UpdateLoadPost.UpdateLoadFeet;
import com.nlpl.model.UpdateLoadPost.UpdateLoadKmApprox;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPickAdd;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPickCity;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPickCountry;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPickPinCode;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPickState;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPostPickUpDate;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPostPickUpTime;
import com.nlpl.model.UpdateLoadPost.UpdateLoadVehicleModel;
import com.nlpl.services.PostLoadService;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.GetLocationDrop;
import com.nlpl.utils.GetLocationPickUp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostALoadActivity extends AppCompatActivity {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    double latitude1, latitude2, longitude1, longitude2;

    Dialog pickUpAddressDialog;

    String isPickDrop = "0", pickUpAddress, pickUpPinCode, pickupState, pickUpCity, dropAddress, dropPinCode, dropState, dropCiy;

    TextView pick_up_date, pick_up_time, select_budget, select_model, select_feet, select_capacity, select_truck_body_type, addressDialogState, addressDialogCity, addressDialogOkButton, addressDialogTitle;
    EditText addressDialogAddress, addressDialogPinCode, note_to_post_load;

    String distByPinCode, stateByPinCode, phone, userId, selectedDistrict, selectedState, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI, customerBudget, sDate, eDate, monthS, monthE, startingDate, endingDate, todayDate;
    int sMonth, eMonth, count, startCount;
    Date currentDate, date1, date2, date3, date4;
    ArrayList currentSepDate;
    TextView setApproxDistance;
    long startD, endD, todayD, diff, diff1;
    Dialog selectDistrictDialog, selectStateDialog, setBudget, selectFeetDialog, selectCapacityDialog, selectBodyTypeDialog, selectModelDialog;
    boolean isModelSelected;
    Button Ok_PostLoad;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    ArrayList<String> arrayTruckBodyType, arrayVehicleType, updatedArrayTruckFt, arrayCapacityForCompare, arrayTruckFtForCompare, arrayToDisplayCapacity, arrayTruckFt, arrayCapacity;

    private RequestQueue mQueue;
    Boolean isEdit;
    String loadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_aload);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            loadId = bundle.getString("loadId");
        }

//        bottomNav = (View) findViewById(R.id.post_a_load_bottom_nav_bar0);
//        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
//        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
//        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
//        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));

        action_bar = findViewById(R.id.post_a_load_action_bar);

        actionBarTitle = (TextView) action_bar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) action_bar.findViewById(R.id.action_bar_back_button);
        actionBarTitle.setText("Post a Load");
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostALoadActivity.this.finish();
            }
        });
//-------------------------------------- Today's Date ----------------------------------------------
        currentDate = Calendar.getInstance().getTime();
        Log.i("Current/Today's Date", String.valueOf(currentDate));
        pick_up_date = (TextView) findViewById(R.id.post_a_load_date_text_view);
        pick_up_time = (TextView) findViewById(R.id.post_a_load_time_text_view);
        select_budget = (TextView) findViewById(R.id.post_a_load_budget_text_view);
        select_model = (TextView) findViewById(R.id.post_a_load_vehicle_model);
        select_feet = (TextView) findViewById(R.id.post_a_load_feet_text_view);
        select_capacity = (TextView) findViewById(R.id.post_a_load_capacity_text_view);
        select_truck_body_type = (TextView) findViewById(R.id.post_a_load_body_type_text_view);
        note_to_post_load = (EditText) findViewById(R.id.post_a_load_notes_edit_text);
        setApproxDistance = (TextView) findViewById(R.id.post_a_load_auto_calculated_km_edit_text);

        //------------------------------------------------------------------------------------------
        pickUpAddressDialog = new Dialog(PostALoadActivity.this);
        pickUpAddressDialog.setContentView(R.layout.dialog_address);
        pickUpAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addressDialogState = (TextView) pickUpAddressDialog.findViewById(R.id.dialog_address_state_text_view);
        addressDialogCity = (TextView) pickUpAddressDialog.findViewById(R.id.dialog_address_city_text_view);
        addressDialogAddress = (EditText) pickUpAddressDialog.findViewById(R.id.dialog_address_address_edit_text);
        addressDialogPinCode = (EditText) pickUpAddressDialog.findViewById(R.id.dialog_address_pin_code_edit_text);
        addressDialogOkButton = (TextView) pickUpAddressDialog.findViewById(R.id.dialog_address_ok_button);
        addressDialogTitle = (TextView) pickUpAddressDialog.findViewById(R.id.dialog_address_title);
        //------------------------------------------------------------------------------------------

        Ok_PostLoad = (Button) findViewById(R.id.post_a_load_ok_button);
        currentSepDate = new ArrayList<>();
        arrayCapacity = new ArrayList<>();
        arrayVehicleType = new ArrayList<>();
        arrayTruckFt = new ArrayList<>();
        arrayTruckBodyType = new ArrayList<>();
        updatedArrayTruckFt = new ArrayList<>();
        arrayTruckFtForCompare = new ArrayList<>();
        arrayToDisplayCapacity = new ArrayList<>();
        arrayCapacityForCompare = new ArrayList<>();

        arrayVehicleType.add("Tata");
        arrayVehicleType.add("Mahindra");
        arrayVehicleType.add("Eicher");
        arrayVehicleType.add("Other");

        arrayTruckBodyType.add("Open");
        arrayTruckBodyType.add("Closed");
        arrayTruckBodyType.add("Tarpulian");

        addressDialogAddress.addTextChangedListener(PickAddressTextWatcher);
        addressDialogPinCode.addTextChangedListener(PickPinCodeTextWatcher);
        addressDialogState.addTextChangedListener(cityStateTextWatcher);
        addressDialogCity.addTextChangedListener(cityStateTextWatcher);

        addressDialogAddress.setFilters(new InputFilter[]{filter});

        mQueue = Volley.newRequestQueue(PostALoadActivity.this);

        String[] allDate = currentDate.toString().split(" ", 6);

        for (String sepDate : allDate) {
            Log.i("Sep Date", sepDate);
            currentSepDate.add(sepDate);
        }

        String dayC = (String) currentSepDate.get(0);
        String monC = (String) currentSepDate.get(1);
        String dateC = (String) currentSepDate.get(2);
        String timeC = (String) currentSepDate.get(3);
        String gmtC = (String) currentSepDate.get(4);
        String yearC = (String) currentSepDate.get(5);

        Log.i("Separated Day", dayC);
        Log.i("Separated Date", dateC);
        Log.i("Separated Month", monC);
        Log.i("Separate timeC", timeC);
        Log.i("Separated Year", yearC);

        getVehicleTypeList();

        if (monC.equals("Jan")) {
            count = 1;
        } else if (monC.equals("Feb")) {
            count = 2;
        } else if (monC.equals("Mar")) {
            count = 3;
        } else if (monC.equals("Apr")) {
            count = 4;
        } else if (monC.equals("May")) {
            count = 5;
        } else if (monC.equals("Jun")) {
            count = 6;
        } else if (monC.equals("Jul")) {
            count = 7;
        } else if (monC.equals("Aug")) {
            count = 8;
        } else if (monC.equals("Sep")) {
            count = 9;
        } else if (monC.equals("Oct")) {
            count = 10;
        } else if (monC.equals("Nov")) {
            count = 11;
        } else if (monC.equals("Dec")) {
            count = 12;
        }

        todayDate = dateC + "/" + count + "/" + yearC;
        Log.i("Today's Date", todayDate);

        if (isEdit) {
            Ok_PostLoad.setEnabled(true);
            Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            getLoadDetails();
        } else {
            if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                    && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                    && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                    && pickUpPinCode != null && pickupState != null && dropAddress != null
                    && dropCiy != null && dropPinCode != null && dropState != null) {
                Ok_PostLoad.setEnabled(true);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            } else {
                Ok_PostLoad.setEnabled(false);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
            }
        }

        pick_up_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                pickDateFromCalender();
            }
        });

        pick_up_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });

        select_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetSet(select_budget.getText().toString());
            }
        });

        select_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectModel();
            }
        });

        select_feet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeet();
            }
        });

        select_capacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCapacity();
            }
        });

        select_truck_body_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTruckBodyType();
            }
        });

        addressDialogState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectState();
            }
        });

        addressDialogCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCity();
            }
        });

        Ok_PostLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    updateLoadPost(loadId);
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostALoadActivity.this);
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

                    TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText("Post a Load");
                    alertMessage.setText("Load Updated Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            Intent intent = new Intent(PostALoadActivity.this, CustomerDashboardActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("mobile", phone);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                    //------------------------------------------------------------------------------------------
                } else {
                    if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                            && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                            && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                            && pickUpPinCode != null && pickupState != null && dropAddress != null
                            && dropCiy != null && dropPinCode != null && dropState != null) {
                        Ok_PostLoad.setEnabled(true);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    } else {
                        Ok_PostLoad.setEnabled(false);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                    }
                    saveLoad(createLoadRequest());
                    //----------------------- Alert Dialog -------------------------------------------------
                    Dialog alert = new Dialog(PostALoadActivity.this);
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

                    TextView alertTitle = (TextView) alert.findViewById(R.id.dialog_alert_title);
                    TextView alertMessage = (TextView) alert.findViewById(R.id.dialog_alert_message);
                    TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
                    TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

                    alertTitle.setText("Post a Load");
                    alertMessage.setText("Load Posted Successfully");
                    alertPositiveButton.setVisibility(View.GONE);
                    alertNegativeButton.setText("OK");
                    alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                    alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));

                    alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            Intent intent = new Intent(PostALoadActivity.this, CustomerDashboardActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("mobile", phone);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }
        });
    }

    private void budgetSet(String previousBudget) {
        setBudget = new Dialog(PostALoadActivity.this);
        setBudget.setContentView(R.layout.dialog_budget);

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(setBudget.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;

        setBudget.show();
        setBudget.setCancelable(true);
        setBudget.getWindow().setAttributes(lp2);

        EditText budget = setBudget.findViewById(R.id.dialog_budget_edit);
        Button okBudget = setBudget.findViewById(R.id.dialog_budget_ok_btn);
        TextView amountInWords = setBudget.findViewById(R.id.dialog_budget_amount_in_words);

        budget.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String newPreviousBudget = previousBudget.replaceAll(",", "");
        budget.setText(newPreviousBudget);

        if (!previousBudget.isEmpty()) {
            okBudget.setEnabled(true);
            okBudget.setBackgroundResource((R.drawable.button_active));
        } else {
            okBudget.setEnabled(false);
            okBudget.setBackgroundResource((R.drawable.button_de_active));
        }

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {

                    String finalBudget, lastThree = "";
                    String budget1 = budget.getText().toString();
                    if (budget1.length() > 3) {
                        lastThree = budget1.substring(budget1.length() - 3);
                    }
                    if (budget1.length() == 1) {
                        finalBudget = budget1;
                        select_budget.setText(finalBudget);
                    } else if (budget1.length() == 2) {
                        finalBudget = budget1;
                        select_budget.setText(finalBudget);
                    } else if (budget1.length() == 3) {
                        finalBudget = budget1;
                        select_budget.setText(finalBudget);
                    } else if (budget1.length() == 4) {
                        Character fourth = budget1.charAt(0);
                        finalBudget = fourth + "," + lastThree;
                        select_budget.setText(finalBudget);
                    } else if (budget1.length() == 5) {
                        Character fifth = budget1.charAt(0);
                        Character fourth = budget1.charAt(1);
                        finalBudget = fifth + "" + fourth + "," + lastThree;
                        select_budget.setText(finalBudget);
                    } else if (budget1.length() == 6) {
                        Character fifth = budget1.charAt(1);
                        Character fourth = budget1.charAt(2);
                        Character sixth = budget1.charAt(0);
                        finalBudget = sixth + "," + fifth + "" + fourth + "," + lastThree;
                        select_budget.setText(finalBudget);
                    } else if (budget1.length() == 7) {
                        Character seventh = budget1.charAt(0);
                        Character sixth = budget1.charAt(1);
                        Character fifth = budget1.charAt(2);
                        Character fourth = budget1.charAt(3);
                        finalBudget = seventh + "" + sixth + "," + fifth + "" + fourth + "," + lastThree;
                        select_budget.setText(finalBudget);
                    }

                    if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                            && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                            && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                            && pickUpPinCode != null && pickupState != null && dropAddress != null
                            && dropCiy != null && dropPinCode != null && dropState != null) {
                        Ok_PostLoad.setEnabled(true);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    } else {
                        Ok_PostLoad.setEnabled(false);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                    }
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                } else {
                    okBudget.setEnabled(false);
                    okBudget.setBackgroundResource((R.drawable.button_de_active));
                }

                TextView amountInWords = setBudget.findViewById(R.id.dialog_budget_amount_in_words);
                if (budgetEditText.length() > 0) {
                    String return_val_in_english = EnglishNumberToWords.convert(Long.parseLong(budgetEditText));
                    amountInWords.setText(return_val_in_english);
                } else {
                    amountInWords.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        okBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBudget.dismiss();
            }
        });

    }

    private void pickTime() {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(PostALoadActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                pick_up_time.setText(selectedHour + ":" + selectedMinute);

                int sizeOfHr = String.valueOf(selectedHour).length();
                int sizeOfMin = String.valueOf(selectedMinute).length();
                if (sizeOfHr == 2 && sizeOfMin == 2) {
                    pick_up_time.setText(selectedHour + ":" + selectedMinute);
                } else if (sizeOfHr == 1 && sizeOfMin == 2) {
                    String selectedHr = "0" + String.valueOf(selectedHour);
                    pick_up_time.setText(selectedHr + ":" + selectedMinute);
                } else if (sizeOfHr == 1 && sizeOfMin == 1) {
                    String selectedHr = "0" + String.valueOf(selectedHour);
                    String selectedMin = "0" + String.valueOf(selectedMinute);
                    pick_up_time.setText(selectedHr + ":" + selectedMin);
                } else if (sizeOfHr == 2 && sizeOfMin == 1) {
                    String selectedMin = "0" + String.valueOf(selectedMinute);
                    pick_up_time.setText(selectedHour + ":" + selectedMin);
                }

                if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                        && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                        && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                        && pickUpPinCode != null && pickupState != null && dropAddress != null
                        && dropCiy != null && dropPinCode != null && dropState != null) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void pickDateFromCalender() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PostALoadActivity.this, R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (month == 0) {
                    sMonth = 1;
                    monthS = "Jan";
                } else if (month == 1) {
                    monthS = "Feb";
                    sMonth = 2;
                } else if (month == 2) {
                    monthS = "Mar";
                    sMonth = 3;
                } else if (month == 3) {
                    sMonth = 4;
                    monthS = "Apr";
                } else if (month == 4) {
                    sMonth = 5;
                    monthS = "May";
                } else if (month == 5) {
                    sMonth = 6;
                    monthS = "Jun";
                } else if (month == 6) {
                    sMonth = 7;
                    monthS = "Jul";
                } else if (month == 7) {
                    sMonth = 8;
                    monthS = "Aug";
                } else if (month == 8) {
                    sMonth = 9;
                    monthS = "Sep";
                } else if (month == 9) {
                    sMonth = 10;
                    monthS = "Oct";
                } else if (month == 10) {
                    sMonth = 11;
                    monthS = "Nov";
                } else if (month == 11) {
                    sMonth = 12;
                    monthS = "Dec";
                }

                sDate = dayOfMonth + "-" + monthS + "-" + year;
                pick_up_date.setText(sDate);
                if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                        && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                        && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                        && pickUpPinCode != null && pickupState != null && dropAddress != null
                        && dropCiy != null && dropPinCode != null && dropState != null) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                startCount = startCount + 1;
                Log.i("Length of Start Date", String.valueOf(startCount));

                startingDate = dayOfMonth + "/" + sMonth + "/" + year;

                Log.i("Separated sDate", startingDate);
                Log.i("Separated sMonth", String.valueOf(sMonth));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date1 = simpleDateFormat.parse(startingDate);
                    Log.i("Date Parsed", String.valueOf(date1));
                    date2 = simpleDateFormat.parse(todayDate);
                    startD = date2.getTime();
                    todayD = date1.getTime();

                    diff = (todayD - startD) / 86400000;
                    Log.i("Diff Start-Today Date", String.valueOf(diff));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void selectModel() {
//        if (!isEdit) {
        selectModelDialog = new Dialog(PostALoadActivity.this);
        selectModelDialog.setContentView(R.layout.dialog_spinner);
        selectModelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectModelDialog.show();
        selectModelDialog.setCancelable(true);
        TextView model_title = selectModelDialog.findViewById(R.id.dialog_spinner_title);
        model_title.setText("Select Vehicle Model");

        ListView modelList = (ListView) selectModelDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayVehicleType);
        modelList.setAdapter(adapter1);

        modelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                        && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                        && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                        && pickUpPinCode != null && pickupState != null && dropAddress != null
                        && dropCiy != null && dropPinCode != null && dropState != null) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                select_model.setText(adapter1.getItem(i));
                selectModelDialog.dismiss();

                selectFeet();

            }
        });
//        } else {
//
//        }
    }

    private void selectFeet() {

//        if (!isEdit) {
        selectFeetDialog = new Dialog(PostALoadActivity.this);
        selectFeetDialog.setContentView(R.layout.dialog_spinner);
        selectFeetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectFeetDialog.show();
        selectFeetDialog.setCancelable(true);

        TextView feetTitle = selectFeetDialog.findViewById(R.id.dialog_spinner_title);
        feetTitle.setText("Select Vehicle Feet");

        ListView feetList = (ListView) selectFeetDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_row, updatedArrayTruckFt);
        feetList.setAdapter(adapter);

        feetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                        && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                        && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                        && pickUpPinCode != null && pickupState != null && dropAddress != null
                        && dropCiy != null && dropPinCode != null && dropState != null) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                select_feet.setText(adapter.getItem(i));
                selectFeetDialog.dismiss();
                getVehicleCapacityByFeet(select_feet.getText().toString());

            }
        });
//        }else {
//
//        }
    }

    private String blockCharacterSet = ".,[]`~#^|$%&*!+@₹_-()':;?/={}";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    private void selectCapacity() {
//        if (!isEdit) {
        if (arrayToDisplayCapacity.size() == 1) {

        } else {
            selectCapacityDialog = new Dialog(PostALoadActivity.this);
            selectCapacityDialog.setContentView(R.layout.dialog_spinner);
            selectCapacityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            selectCapacityDialog.show();
            selectCapacityDialog.setCancelable(true);

            TextView capacity_title = selectCapacityDialog.findViewById(R.id.dialog_spinner_title);
            capacity_title.setText("Select Vehicle Capacity");

            ListView capacityList = (ListView) selectCapacityDialog.findViewById(R.id.list_state);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayToDisplayCapacity);
            capacityList.setAdapter(adapter2);

            capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                            && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                            && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                            && pickUpPinCode != null && pickupState != null && dropAddress != null
                            && dropCiy != null && dropPinCode != null && dropState != null) {
                        Ok_PostLoad.setEnabled(true);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    } else {
                        Ok_PostLoad.setEnabled(false);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                    }
                    select_capacity.setText(adapter2.getItem(i));
                    selectCapacityDialog.dismiss();
                }
            });
        }
//        } else {
//
//        }
    }

    private void selectTruckBodyType() {
//        if (!isEdit) {
        selectBodyTypeDialog = new Dialog(PostALoadActivity.this);
        selectBodyTypeDialog.setContentView(R.layout.dialog_spinner);
        selectBodyTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectBodyTypeDialog.show();
        selectBodyTypeDialog.setCancelable(true);

        TextView capacity_title = selectBodyTypeDialog.findViewById(R.id.dialog_spinner_title);
        capacity_title.setText("Select Vehicle Body Type");

        ListView capacityList = (ListView) selectBodyTypeDialog.findViewById(R.id.list_state);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.custom_list_row, arrayTruckBodyType);
        capacityList.setAdapter(adapter2);

        capacityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                        && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                        && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                        && pickUpPinCode != null && pickupState != null && dropAddress != null
                        && dropCiy != null && dropPinCode != null && dropState != null) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                select_truck_body_type.setText(adapter2.getItem(i));
                selectBodyTypeDialog.dismiss();
            }
        });
//        } else {
//
//        }
    }

    private void getVehicleCapacityByFeet(String selectedFeet) {
        arrayToDisplayCapacity.clear();
        arrayTruckFtForCompare.clear();
        String url = getString(R.string.baseURL) + "/trucktype/getAllTruckType";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_typeAPI = obj.getString("vehicle_model");
                        truck_ftAPI = obj.getString("truck_ft");
                        truck_carrying_capacityAPI = obj.getString("truck_carrying_capacity");

                        arrayTruckFtForCompare.add(truck_ftAPI);
                        arrayCapacityForCompare.add(truck_carrying_capacityAPI);

                        Log.i("type:", vehicle_typeAPI);

                    }

                    for (int i = 0; i < arrayTruckFtForCompare.size(); i++) {
                        if (selectedFeet.equals(arrayTruckFtForCompare.get(i))) {
                            arrayToDisplayCapacity.add(arrayCapacityForCompare.get(i));
                        }
                    }
                    select_capacity.setText(arrayToDisplayCapacity.get(0));

                    Log.i("array to dcapacity", String.valueOf(arrayToDisplayCapacity));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    private void getVehicleTypeList() {
        String url = getString(R.string.baseURL) + "/trucktype/getAllTruckType";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        vehicle_typeAPI = obj.getString("vehicle_model");
                        truck_ftAPI = obj.getString("truck_ft");
                        truck_carrying_capacityAPI = obj.getString("truck_carrying_capacity");

                        arrayTruckFt.add(truck_ftAPI);
                        arrayCapacity.add(truck_carrying_capacityAPI);
                    }

                    int size3 = arrayTruckFt.size();

                    if (size3 == 1) {
                        updatedArrayTruckFt.add(arrayTruckFt.get(0));
                    } else {
                        for (int i = 0; i < size3 - 1; i++) {
                            if (!arrayTruckFt.get(i).equals(arrayTruckFt.get(i + 1))) {
                                updatedArrayTruckFt.add(arrayTruckFt.get(i));
                            }
                        }
                        for (int k = 0; k < size3; k++) {
                            if (k == size3 - 1) {
                                updatedArrayTruckFt.add(arrayTruckFt.get(k));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void selectState() {
        selectStateDialog = new Dialog(PostALoadActivity.this);
        selectStateDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
        selectStateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectStateDialog.show();
        selectStateDialog.setCancelable(true);
        ListView stateList = (ListView) selectStateDialog.findViewById(R.id.list_state);

        selectStateArray = ArrayAdapter.createFromResource(PostALoadActivity.this, R.array.array_indian_states, R.layout.custom_list_row);
        selectStateUnionCode = ArrayAdapter.createFromResource(PostALoadActivity.this, R.array.array_indian_states_union_territory_codes, R.layout.custom_list_row);

        stateList.setAdapter(selectStateArray);

        stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                        && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                        && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                        && pickUpPinCode != null && pickupState != null && dropAddress != null
                        && dropCiy != null && dropPinCode != null && dropState != null) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                addressDialogState.setText(selectStateUnionCode.getItem(i)); //Set Selected Credentials
                selectStateDialog.dismiss();
                addressDialogCity.performClick();
            }
        });
    }

    private void selectCity() {
        if (addressDialogState != null) {
            selectedState = addressDialogState.getText().toString();
            selectDistrictDialog = new Dialog(PostALoadActivity.this);
            selectDistrictDialog.setContentView(R.layout.dialog_spinner);
//                dialog.getWindow().setLayout(1000,3000);
            selectDistrictDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            selectDistrictDialog.show();
            TextView title = selectDistrictDialog.findViewById(R.id.dialog_spinner_title);
            title.setText("Select City");
            ListView districtList = (ListView) selectDistrictDialog.findViewById(R.id.list_state);

            if (selectedState.equals("AP")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_andhra_pradesh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("AR")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_arunachal_pradesh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("AS")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_assam_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("BR")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_bihar_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("CG")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_chhattisgarh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("GA")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_goa_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("GJ")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_gujarat_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("HR")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_haryana_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("HP")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_himachal_pradesh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("JH")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_jharkhand_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("KA")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_karnataka_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("KL")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_kerala_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("MP")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_madhya_pradesh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("MH")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_maharashtra_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("MN")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_manipur_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("ML")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_meghalaya_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("MZ")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_mizoram_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("NL")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_nagaland_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("OD")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_odisha_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("PB")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_punjab_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("RJ")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_rajasthan_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("SK")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_sikkim_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("TN")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_tamil_nadu_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("TS")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_telangana_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("TR")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_tripura_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("UP")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_uttar_pradesh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("UK")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_uttarakhand_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("WB")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_west_bengal_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("AN")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_andaman_nicobar_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("CH/PB")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_chandigarh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("DD")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_dadra_nagar_haveli_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("DD2")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_daman_diu_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("DL")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_delhi_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("JK")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_jammu_kashmir_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("LD")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_lakshadweep_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("LA")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_ladakh_districts, R.layout.custom_list_row);
            } else if (selectedState.equals("PY")) {
                selectDistrictArray = ArrayAdapter.createFromResource(PostALoadActivity.this,
                        R.array.array_puducherry_districts, R.layout.custom_list_row);
            }
            districtList.setAdapter(selectDistrictArray);

            districtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                            && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                            && !select_truck_body_type.getText().toString().isEmpty() && pickUpAddress != null && pickUpCity != null
                            && pickUpPinCode != null && pickupState != null && dropAddress != null
                            && dropCiy != null && dropPinCode != null && dropState != null) {
                        Ok_PostLoad.setEnabled(true);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    } else {
                        Ok_PostLoad.setEnabled(false);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                    }
                    addressDialogCity.setText(selectDistrictArray.getItem(i)); //Set Selected Credentials
                    selectDistrictDialog.dismiss();
                    selectedDistrict = selectDistrictArray.getItem(i).toString();
                }
            });
        }
    }

    //--------------------------------------create Bank Details in API -------------------------------------
    public PostLoadRequest createLoadRequest() {
        PostLoadRequest postLoadRequest = new PostLoadRequest();
        postLoadRequest.setPick_up_date(pick_up_date.getText().toString());
        postLoadRequest.setPick_up_time(pick_up_time.getText().toString());
        postLoadRequest.setBudget(select_budget.getText().toString());
        postLoadRequest.setVehicle_model(select_model.getText().toString());
        postLoadRequest.setFeet(select_feet.getText().toString() + " Ft");
        postLoadRequest.setCapacity(select_capacity.getText().toString());
        postLoadRequest.setBody_type(select_truck_body_type.getText().toString());
        postLoadRequest.setPick_add(pickUpAddress);
        postLoadRequest.setPick_city(pickUpCity);
        postLoadRequest.setPick_pin_code(pickUpPinCode);
        postLoadRequest.setPick_state(pickupState);
        postLoadRequest.setPick_country("India");
        postLoadRequest.setDrop_add(dropAddress);
        postLoadRequest.setDrop_city(dropCiy);
        postLoadRequest.setDrop_pin_code(dropPinCode);
        postLoadRequest.setDrop_state(dropState);
        postLoadRequest.setDrop_country("India");
        postLoadRequest.setUser_id(userId);
        postLoadRequest.setKm_approx(setApproxDistance.getText().toString());
        postLoadRequest.setNotes_meterial_des(note_to_post_load.getText().toString());
        postLoadRequest.setBid_status("loadPosted");
        return postLoadRequest;
    }

    public void saveLoad(PostLoadRequest postLoadRequest) {
        Call<PostLoadResponse> postLoadResponseCall = ApiClient.getPostLoadService().saveLoad(postLoadRequest);
        postLoadResponseCall.enqueue(new Callback<PostLoadResponse>() {
            @Override
            public void onResponse(Call<PostLoadResponse> call, Response<PostLoadResponse> response) {

            }

            @Override
            public void onFailure(Call<PostLoadResponse> call, Throwable t) {

            }
        });
    }
    //-----------------------------------------------------------------------------------------------------

    private TextWatcher PickAddressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!addressDialogAddress.getText().toString().isEmpty()) {
                addressDialogAddress.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                addressDialogAddress.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }

            if (!addressDialogAddress.getText().toString().isEmpty() && !addressDialogPinCode.getText().toString().isEmpty() && !addressDialogCity.getText().toString().isEmpty() && !addressDialogState.getText().toString().isEmpty()) {
                addressDialogOkButton.setEnabled(true);
                addressDialogOkButton.setBackgroundResource((R.drawable.button_active));
            } else {
                addressDialogOkButton.setEnabled(false);
                addressDialogOkButton.setBackgroundResource((R.drawable.button_de_active));
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            for (int i = s.length() - 1; i >= 0; i--) {
                if (s.charAt(i) == '\n') {
                    s.delete(i, i + 1);
                    return;
                }
            }
        }
    };

    private TextWatcher cityStateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!addressDialogAddress.getText().toString().isEmpty() && !addressDialogPinCode.getText().toString().isEmpty() && !addressDialogCity.getText().toString().isEmpty() && !addressDialogState.getText().toString().isEmpty()) {
                addressDialogOkButton.setEnabled(true);
                addressDialogOkButton.setBackgroundResource((R.drawable.button_active));
            } else {
                addressDialogOkButton.setEnabled(false);
                addressDialogOkButton.setBackgroundResource((R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher PickPinCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (addressDialogPinCode.getText().toString().length() != 6) {
                addressDialogState.setText("");
                addressDialogCity.setText("");
                addressDialogPinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            } else {
                getStateAndDistrictForPickUp(addressDialogPinCode.getText().toString());
                addressDialogPinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            }

            if (!addressDialogAddress.getText().toString().isEmpty() && !addressDialogPinCode.getText().toString().isEmpty() && !addressDialogCity.getText().toString().isEmpty() && !addressDialogState.getText().toString().isEmpty()) {
                addressDialogOkButton.setEnabled(true);
                addressDialogOkButton.setBackgroundResource((R.drawable.button_active));
            } else {
                addressDialogOkButton.setEnabled(false);
                addressDialogOkButton.setBackgroundResource((R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    //--------------------------------------Get State and city by PinCode---------------------------
    private void getStateAndDistrictForPickUp(String enteredPin) {

        Log.i("Entered PIN", enteredPin);

        String url = "http://13.234.163.179:3000/user/locationData/" + enteredPin;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject obj = response.getJSONObject("data");
                    String stateByPinCode = obj.getString("stateCode");
                    String distByPinCode = obj.getString("district");

                    addressDialogState.setText(stateByPinCode);
                    addressDialogCity.setText(distByPinCode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    //----------------------------------------------------------------------------------------------

    //-------------------------------- Update Post Load Details ----------------------------------------
    private void updateLoadPost(String loadId) {
        Log.i("Load Id", loadId);

        String pickUpDate = pick_up_date.getText().toString();
        String pickUpTime = pick_up_time.getText().toString();
        String budget = select_budget.getText().toString();
        String vehicleModel = select_model.getText().toString();
        String vehicleFeet = select_feet.getText().toString() + " Ft";
        String vehicleCapacity = select_capacity.getText().toString();
        String vehicleBodyType = select_truck_body_type.getText().toString();
        String pickUpCountry = "India";
        String dropCountry = "India";
        String notesFromLP = note_to_post_load.getText().toString();
        String kmApprox = setApproxDistance.getText().toString();

        UpdateLoadPostPickUpDate updateLoadPostPickUpDate = new UpdateLoadPostPickUpDate(pickUpDate);

        Call<UpdateLoadPostPickUpDate> callPickUpDate = ApiClient.getPostLoadService().updateLoadPostPickUpDate("" + loadId, updateLoadPostPickUpDate);

        callPickUpDate.enqueue(new Callback<UpdateLoadPostPickUpDate>() {
            @Override
            public void onResponse(Call<UpdateLoadPostPickUpDate> call, Response<UpdateLoadPostPickUpDate> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post update Pick up Date");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPostPickUpDate> call, Throwable t) {
                Log.i("Not Successful", "Load Post update pick-up Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadPostPickUpTime updateLoadPostPickUpTime = new UpdateLoadPostPickUpTime(pickUpTime);

        Call<UpdateLoadPostPickUpTime> callPickUpTime = ApiClient.getPostLoadService().updateLoadPostPickUpTime("" + loadId, updateLoadPostPickUpTime);

        callPickUpTime.enqueue(new Callback<UpdateLoadPostPickUpTime>() {
            @Override
            public void onResponse(Call<UpdateLoadPostPickUpTime> call, Response<UpdateLoadPostPickUpTime> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPostPickUpTime> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateCustomerBudget updateCustomerBudget = new UpdateCustomerBudget(budget);

        Call<UpdateCustomerBudget> callBudget = ApiClient.getPostLoadService().updateCustomerBudget("" + loadId, updateCustomerBudget);

        callBudget.enqueue(new Callback<UpdateCustomerBudget>() {
            @Override
            public void onResponse(Call<UpdateCustomerBudget> call, Response<UpdateCustomerBudget> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateCustomerBudget> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadVehicleModel updateLoadVehicleModel = new UpdateLoadVehicleModel(vehicleModel);

        Call<UpdateLoadVehicleModel> callVehicleModel = ApiClient.getPostLoadService().updateLoadVehicleModel("" + loadId, updateLoadVehicleModel);

        callVehicleModel.enqueue(new Callback<UpdateLoadVehicleModel>() {
            @Override
            public void onResponse(Call<UpdateLoadVehicleModel> call, Response<UpdateLoadVehicleModel> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadVehicleModel> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadFeet updateLoadFeet = new UpdateLoadFeet(vehicleFeet);

        Call<UpdateLoadFeet> callFeet = ApiClient.getPostLoadService().updateLoadFeet("" + loadId, updateLoadFeet);

        callFeet.enqueue(new Callback<UpdateLoadFeet>() {
            @Override
            public void onResponse(Call<UpdateLoadFeet> call, Response<UpdateLoadFeet> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadFeet> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadCapacity updateLoadCapacity = new UpdateLoadCapacity(vehicleCapacity);

        Call<UpdateLoadCapacity> callCapacity = ApiClient.getPostLoadService().updateLoadCapacity("" + loadId, updateLoadCapacity);

        callCapacity.enqueue(new Callback<UpdateLoadCapacity>() {
            @Override
            public void onResponse(Call<UpdateLoadCapacity> call, Response<UpdateLoadCapacity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadCapacity> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadBodyType updateLoadBodyType = new UpdateLoadBodyType(vehicleBodyType);

        Call<UpdateLoadBodyType> callBodyType = ApiClient.getPostLoadService().updateLoadBodyType("" + loadId, updateLoadBodyType);

        callBodyType.enqueue(new Callback<UpdateLoadBodyType>() {
            @Override
            public void onResponse(Call<UpdateLoadBodyType> call, Response<UpdateLoadBodyType> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadBodyType> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadPickAdd updateLoadPickAdd = new UpdateLoadPickAdd(pickUpAddress);

        Call<UpdateLoadPickAdd> callPickAdd = ApiClient.getPostLoadService().updateLoadPickAdd("" + loadId, updateLoadPickAdd);

        callPickAdd.enqueue(new Callback<UpdateLoadPickAdd>() {
            @Override
            public void onResponse(Call<UpdateLoadPickAdd> call, Response<UpdateLoadPickAdd> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickAdd> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadPickPinCode updateLoadPickPinCode = new UpdateLoadPickPinCode(pickUpPinCode);

        Call<UpdateLoadPickPinCode> callPinCode = ApiClient.getPostLoadService().updateLoadPickPinCode("" + loadId, updateLoadPickPinCode);

        callPinCode.enqueue(new Callback<UpdateLoadPickPinCode>() {
            @Override
            public void onResponse(Call<UpdateLoadPickPinCode> call, Response<UpdateLoadPickPinCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickPinCode> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadPickState updateLoadPickState = new UpdateLoadPickState(pickupState);

        Call<UpdateLoadPickState> callPickState = ApiClient.getPostLoadService().updateLoadPickState("" + loadId, updateLoadPickState);

        callPickState.enqueue(new Callback<UpdateLoadPickState>() {
            @Override
            public void onResponse(Call<UpdateLoadPickState> call, Response<UpdateLoadPickState> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickState> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadPickCity updateLoadPickCity = new UpdateLoadPickCity(pickUpCity);

        Call<UpdateLoadPickCity> callPickCity = ApiClient.getPostLoadService().updateLoadPickCity("" + loadId, updateLoadPickCity);

        callPickCity.enqueue(new Callback<UpdateLoadPickCity>() {
            @Override
            public void onResponse(Call<UpdateLoadPickCity> call, Response<UpdateLoadPickCity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickCity> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadDropAdd updateLoadDropAdd = new UpdateLoadDropAdd(dropAddress);

        Call<UpdateLoadDropAdd> callDropAdd = ApiClient.getPostLoadService().updateLoadDropAdd("" + loadId, updateLoadDropAdd);

        callDropAdd.enqueue(new Callback<UpdateLoadDropAdd>() {
            @Override
            public void onResponse(Call<UpdateLoadDropAdd> call, Response<UpdateLoadDropAdd> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropAdd> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadDropPinCode updateLoadDropPinCode = new UpdateLoadDropPinCode(dropPinCode);

        Call<UpdateLoadDropPinCode> callDropPinCode = ApiClient.getPostLoadService().updateLoadDropPinCode("" + loadId, updateLoadDropPinCode);

        callDropPinCode.enqueue(new Callback<UpdateLoadDropPinCode>() {
            @Override
            public void onResponse(Call<UpdateLoadDropPinCode> call, Response<UpdateLoadDropPinCode> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropPinCode> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadDropState updateLoadDropState = new UpdateLoadDropState(dropState);

        Call<UpdateLoadDropState> callDropState = ApiClient.getPostLoadService().updateLoadDropState("" + loadId, updateLoadDropState);

        callDropState.enqueue(new Callback<UpdateLoadDropState>() {
            @Override
            public void onResponse(Call<UpdateLoadDropState> call, Response<UpdateLoadDropState> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropState> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadDropCity updateLoadDropCity = new UpdateLoadDropCity(dropCiy);

        Call<UpdateLoadDropCity> callDropCity = ApiClient.getPostLoadService().updateLoadDropCity("" + loadId, updateLoadDropCity);

        callDropCity.enqueue(new Callback<UpdateLoadDropCity>() {
            @Override
            public void onResponse(Call<UpdateLoadDropCity> call, Response<UpdateLoadDropCity> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropCity> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadKmApprox updateLoadKmApprox = new UpdateLoadKmApprox(kmApprox);

        Call<UpdateLoadKmApprox> callKmApprox = ApiClient.getPostLoadService().updateLoadKmApprox("" + loadId, updateLoadKmApprox);

        callKmApprox.enqueue(new Callback<UpdateLoadKmApprox>() {
            @Override
            public void onResponse(Call<UpdateLoadKmApprox> call, Response<UpdateLoadKmApprox> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadKmApprox> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateCustomerNoteForSP updateCustomerNoteForSP = new UpdateCustomerNoteForSP(notesFromLP);

        Call<UpdateCustomerNoteForSP> call = ApiClient.getPostLoadService().updateCustomerNoteForSP("" + loadId, updateCustomerNoteForSP);

        call.enqueue(new Callback<UpdateCustomerNoteForSP>() {
            @Override
            public void onResponse(Call<UpdateCustomerNoteForSP> call, Response<UpdateCustomerNoteForSP> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateCustomerNoteForSP> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------

        UpdateLoadPickCountry updateLoadPickCountry = new UpdateLoadPickCountry(pickUpCountry);

        Call<UpdateLoadPickCountry> callPickCountry = ApiClient.getPostLoadService().updateLoadPickCountry("" + loadId, updateLoadPickCountry);

        callPickCountry.enqueue(new Callback<UpdateLoadPickCountry>() {
            @Override
            public void onResponse(Call<UpdateLoadPickCountry> call, Response<UpdateLoadPickCountry> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPickCountry> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------
        UpdateLoadDropCountry updateLoadDropCountry = new UpdateLoadDropCountry(dropCountry);

        Call<UpdateLoadDropCountry> callDropCountry = ApiClient.getPostLoadService().updateLoadDropCountry("" + loadId, updateLoadDropCountry);

        callDropCountry.enqueue(new Callback<UpdateLoadDropCountry>() {
            @Override
            public void onResponse(Call<UpdateLoadDropCountry> call, Response<UpdateLoadDropCountry> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadDropCountry> call, Throwable t) {
                Log.i("Not Successful", "Load Post Details Not Updated");

            }
        });
//--------------------------------------------------------------------------------------------------

    }


    private void getLoadDetails() {
        String url = getString(R.string.baseURL) + "/loadpost/getLoadDtByPostId/" + loadId;
        Log.i("get Bank Detail URL", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String pickUpDate = obj.getString("pick_up_date");
                        String pickUpTime = obj.getString("pick_up_time");
                        String budget = obj.getString("budget");
                        String vehicleModel = obj.getString("vehicle_model");
                        String vehicleFeet = obj.getString("feet");
                        String vehicleCapacity = obj.getString("capacity");
                        String vehicleBodyType = obj.getString("body_type");
                        String pickUpAddress1 = obj.getString("pick_add");
                        String pickUpCities = obj.getString("pick_city");
                        String pickUpPinCodes = obj.getString("pick_pin_code");
                        String pickUpStates = obj.getString("pick_state");
                        String pickUpCountry = obj.getString("pick_country");
                        String dropAddress1 = obj.getString("drop_add");
                        String dropCities = obj.getString("drop_city");
                        String dropPinCodes = obj.getString("drop_pin_code");
                        String dropStates = obj.getString("drop_state");
                        String dropCountry = obj.getString("drop_country");
                        String approxKM = obj.getString("km_approx");
                        String notesFromLP = obj.getString("notes_meterial_des");


                        pick_up_date.setText(pickUpDate);
                        pick_up_time.setText(pickUpTime);
                        select_budget.setText(budget);
                        select_model.setText(vehicleModel);
                        select_feet.setText(vehicleFeet);
                        select_capacity.setText(vehicleCapacity);
                        select_truck_body_type.setText(vehicleBodyType);
                        pickupState = pickUpStates;
                        pickUpCity = pickUpCities;
                        dropState = dropStates;
                        dropCiy = dropCities;
                        setApproxDistance.setText(approxKM);
                        pickUpAddress = pickUpAddress1;
                        dropAddress = dropAddress1;
                        pickUpPinCode = pickUpPinCodes;
                        dropPinCode = dropPinCodes;
                        note_to_post_load.setText(notesFromLP);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i8 = new Intent(PostALoadActivity.this, CustomerDashboardActivity.class);
        i8.putExtra("mobile", phone);
        i8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i8);
        finish();
        overridePendingTransition(0, 0);
    }

    private void getPickUpLocation() {
        GetLocationPickUp geoLocation = new GetLocationPickUp();
        geoLocation.geLatLongPickUp(pickUpAddress, getApplicationContext(), new GeoHandlerLatitude());
    }

    public void onClickGetCurrentLocationAddress(View view) {
        GetCurrentLocation.getCurrentLocation(PostALoadActivity.this, addressDialogAddress, addressDialogPinCode, addressDialogState, addressDialogCity);
    }

    public void onClickSetPickUpLocation(View view) {
        addressDialogTitle.setText("Pick-up Address");
        isPickDrop = "1";
        if (pickUpAddress != null && pickUpCity != null && pickUpPinCode != null && pickupState != null) {
            addressDialogAddress.setText(pickUpAddress);
            addressDialogPinCode.setText(pickUpPinCode);
            addressDialogState.setText(pickupState);
            addressDialogCity.setText(pickUpCity);
        } else {
            addressDialogAddress.setText("");
            addressDialogPinCode.setText("");
            addressDialogState.setText("");
            addressDialogCity.setText("");
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(pickUpAddressDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        pickUpAddressDialog.show();
        pickUpAddressDialog.getWindow().setAttributes(lp);
    }

    public void onClickDropLocation(View view) {
        addressDialogTitle.setText("Drop Address");
        isPickDrop = "2";
        if (dropAddress != null && dropCiy != null && dropPinCode != null && dropState != null) {
            addressDialogAddress.setText(dropAddress);
            addressDialogPinCode.setText(dropPinCode);
            addressDialogState.setText(dropState);
            addressDialogCity.setText(dropCiy);
        } else {
            addressDialogAddress.setText("");
            addressDialogPinCode.setText("");
            addressDialogState.setText("");
            addressDialogCity.setText("");
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(pickUpAddressDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        pickUpAddressDialog.show();
        pickUpAddressDialog.getWindow().setAttributes(lp);
    }

    public void onClickOkAddressDetails(View view) {
        if (isPickDrop.equals("1")) {

            pickUpAddress = addressDialogAddress.getText().toString();
            pickUpPinCode = addressDialogPinCode.getText().toString();
            pickupState = addressDialogState.getText().toString();
            pickUpCity = addressDialogCity.getText().toString();

            getPickUpLocation();
        } else if (isPickDrop.equals("2")) {

            dropAddress = addressDialogAddress.getText().toString();
            dropPinCode = addressDialogPinCode.getText().toString();
            dropState = addressDialogState.getText().toString();
            dropCiy = addressDialogCity.getText().toString();
            getDropLocation();
        } else {
        }
        pickUpAddressDialog.dismiss();

        String latitude1Check = String.valueOf(latitude1);
        String latitude2Check = String.valueOf(latitude2);
        String longitude1Check = String.valueOf(longitude1);
        String longitude2Check = String.valueOf(longitude2);
        if (latitude1Check != null && latitude2Check != null && longitude1Check != null && longitude2Check != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        distanceInKm(latitude1, longitude1, latitude2, longitude2);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        } else {
            setApproxDistance.setText("KM auto calculated");
        }

    }

    public void onClickCancelAddressDetails(View view) {
        pickUpAddressDialog.dismiss();
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
                latitude1 = Double.parseDouble(lat);
                longitude1 = Double.parseDouble(lon);
                Log.i("Lat and long 1", String.valueOf(latitude1 + " " + longitude1));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void getDropLocation() {
        GetLocationDrop geoLocation = new GetLocationDrop();
        geoLocation.geLatLongDrop(dropAddress, getApplicationContext(), new GeoHandlerLongitude());
    }

    private class GeoHandlerLongitude extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String latLong, lat = null, lon = null;
            switch (msg.what) {
                case 1:
                    try {
                        Bundle bundle = msg.getData();
                        latLong = bundle.getString("latLong2");
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
                latitude2 = Double.parseDouble(lat);
                longitude2 = Double.parseDouble(lon);
                Log.i("Lat and long 2", String.valueOf(latitude2 + " " + longitude2));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void distanceInKm(double lat1, double long1, double lat2, double long2) {
        double longDiff = long1 - long2;
        double distanceApprox = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));
        distanceApprox = Math.acos(distanceApprox);
        //Convert Distance radian to degree
        distanceApprox = rad2deg(distanceApprox);
        //Distance in Miles
        distanceApprox = distanceApprox * 60 * 1.1515;
        //Distance in kilometer
        distanceApprox = distanceApprox * 1.609344;
        //set distance on Text View
        setApproxDistance.setText(String.format(Locale.US, "%2f kilometers", distanceApprox));
    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1 * Math.PI / 180.0);
    }


}