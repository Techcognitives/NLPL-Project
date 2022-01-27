package com.nlpl.ui.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.nlpl.R;
import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.PostLoadResponse;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPostPickUpDate;
import com.nlpl.services.PostLoadService;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    TextView pick_up_date, pick_up_time, select_budget, select_model, select_feet, select_capacity, select_truck_body_type, pick_up_state, pick_up_city, drop_state, drop_city, auto_calculated_KM;
    EditText pick_up_address, drop_address, pick_up_pinCode, drop_pinCode, note_to_post_load;

    String distByPinCode, stateByPinCode, phone, userId, selectedDistrict, selectedState, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI, customerBudget, sDate, eDate, monthS, monthE, startingDate, endingDate, todayDate;
    int sMonth, eMonth, count, startCount;
    Date currentDate, date1, date2, date3, date4;
    ArrayList currentSepDate;
    long startD, endD, todayD, diff, diff1;
    Dialog selectDistrictDialog, selectStateDialog, setBudget, selectFeetDialog, selectCapacityDialog, selectBodyTypeDialog, selectModelDialog;
    boolean isModelSelected;
    Button Ok_PostLoad;

    ArrayAdapter<CharSequence> selectStateArray, selectDistrictArray, selectStateUnionCode;
    ArrayList<String> arrayTruckBodyType, arrayVehicleType, updatedArrayTruckFt, arrayCapacityForCompare, arrayTruckFtForCompare, arrayToDisplayCapacity, arrayTruckFt, arrayCapacity;

    private RequestQueue mQueue;
    private PostLoadService postLoadService;
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
        actionBarTitle.setText("Post A Load");
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
        pick_up_state = (TextView) findViewById(R.id.post_a_load_pick_up_state_text_view);
        pick_up_city = (TextView) findViewById(R.id.post_a_load_pick_up_city_text_view);
        drop_state = (TextView) findViewById(R.id.post_a_load_drop_state_text_view);
        drop_city = (TextView) findViewById(R.id.post_a_load_drop_city_text_view);
        auto_calculated_KM = (TextView) findViewById(R.id.post_a_load_auto_calculated_km_edit_text);
        pick_up_address = (EditText) findViewById(R.id.post_a_load_address_edit_text);
        drop_address = (EditText) findViewById(R.id.post_a_load_drop_address_text_view);
        pick_up_pinCode = (EditText) findViewById(R.id.post_a_load_pin_code_pick_up_edit_text);
        drop_pinCode = (EditText) findViewById(R.id.post_a_load_drop_pin_edit_text);
        note_to_post_load = (EditText) findViewById(R.id.post_a_load_notes_edit_text);

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        postLoadService = retrofit.create(PostLoadService.class);

        pick_up_address.addTextChangedListener(PickAddressTextWatcher);
        pick_up_pinCode.addTextChangedListener(PickPinCodeTextWatcher);
        drop_address.addTextChangedListener(DropAddressTextWatcher);
        drop_pinCode.addTextChangedListener(DropPinCodeTextWatcher);
        pick_up_state.addTextChangedListener(cityStateTextWatcher);
        pick_up_city.addTextChangedListener(cityStateTextWatcher);
        drop_state.addTextChangedListener(cityStateTextWatcher);
        drop_city.addTextChangedListener(cityStateTextWatcher);

        pick_up_address.setFilters(new InputFilter[]{filter});
        drop_address.setFilters(new InputFilter[]{filter});

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
                    && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                    && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                    && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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

        pick_up_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectState();
            }
        });

        pick_up_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCity();
            }
        });

        drop_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStateDrop();
            }
        });

        drop_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCityDrop();
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
                            && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                            && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                            && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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

        budget.setText(previousBudget);

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

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String budgetEditText = budget.getText().toString();
                if (!budgetEditText.isEmpty()) {
                    select_budget.setText(budgetEditText);
                    if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                            && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                            && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                            && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                            && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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
                if (budgetEditText.length()>0){
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
        mTimePicker = new TimePickerDialog(PostALoadActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(PostALoadActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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
                            && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                            && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                            && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                pick_up_state.setText(selectStateUnionCode.getItem(i)); //Set Selected Credentials
                selectStateDialog.dismiss();
                pick_up_city.performClick();
            }
        });
    }

    private void selectCity() {
        if (!pick_up_state.getText().toString().isEmpty()) {
            selectedState = pick_up_state.getText().toString();
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
                            && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                            && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                            && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                        Ok_PostLoad.setEnabled(true);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    } else {
                        Ok_PostLoad.setEnabled(false);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                    }
                    pick_up_city.setText(selectDistrictArray.getItem(i)); //Set Selected Credentials
                    selectDistrictDialog.dismiss();
                    selectedDistrict = selectDistrictArray.getItem(i).toString();
                }
            });
        }
    }

    private void selectStateDrop() {
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
                        && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                        && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                        && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                    Ok_PostLoad.setEnabled(true);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                } else {
                    Ok_PostLoad.setEnabled(false);
                    Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                }
                drop_state.setText(selectStateUnionCode.getItem(i)); //Set Selected Credentials
                selectStateDialog.dismiss();
                drop_city.performClick();
            }
        });
    }

    private void selectCityDrop() {
        if (!drop_state.getText().toString().isEmpty()) {
            selectedState = drop_state.getText().toString();
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
                            && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                            && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                            && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                        Ok_PostLoad.setEnabled(true);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
                    } else {
                        Ok_PostLoad.setEnabled(false);
                        Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
                    }
                    drop_city.setText(selectDistrictArray.getItem(i)); //Set Selected Credentials
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
        postLoadRequest.setPick_add(pick_up_address.getText().toString());
        postLoadRequest.setPick_city(pick_up_city.getText().toString());
        postLoadRequest.setPick_pin_code(pick_up_pinCode.getText().toString());
        postLoadRequest.setPick_state(pick_up_state.getText().toString());
        postLoadRequest.setPick_country(pick_up_state.getText().toString());
        postLoadRequest.setDrop_add(drop_address.getText().toString());
        postLoadRequest.setDrop_city(drop_city.getText().toString());
        postLoadRequest.setDrop_pin_code(drop_pinCode.getText().toString());
        postLoadRequest.setDrop_state(drop_state.getText().toString());
        postLoadRequest.setDrop_country(drop_state.getText().toString());
        postLoadRequest.setUser_id(userId);
//        postLoadRequest.setKm_approx(select_capacity.getText().toString());
        postLoadRequest.setNotes_meterial_des(note_to_post_load.getText().toString());
        postLoadRequest.setBid_status("pending");
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

            if (!pick_up_address.getText().toString().isEmpty()) {
                pick_up_address.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                pick_up_address.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }

            if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                    && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                    && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                    && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                    && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                Ok_PostLoad.setEnabled(true);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            } else {
                Ok_PostLoad.setEnabled(false);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
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
            if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                    && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                    && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                    && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                    && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                Ok_PostLoad.setEnabled(true);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            } else {
                Ok_PostLoad.setEnabled(false);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher DropPinCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (drop_pinCode.getText().toString().length() == 6) {
                getDropStateAndDistrict(drop_pinCode.getText().toString());
                drop_pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                drop_pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }

            if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                    && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                    && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                    && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                    && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                Ok_PostLoad.setEnabled(true);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            } else {
                Ok_PostLoad.setEnabled(false);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher DropAddressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!drop_address.getText().toString().isEmpty()) {
                drop_address.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                drop_address.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }

            if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                    && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                    && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                    && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                    && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                Ok_PostLoad.setEnabled(true);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            } else {
                Ok_PostLoad.setEnabled(false);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
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

    private TextWatcher PickPinCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (pick_up_pinCode.getText().toString().length() == 6) {
                getPickStateAndDistrict(pick_up_pinCode.getText().toString());
                pick_up_pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
            } else {
                pick_up_pinCode.setBackground(getResources().getDrawable(R.drawable.edit_text_border_red));
            }

            if (!pick_up_date.getText().toString().isEmpty() && !pick_up_time.getText().toString().isEmpty() && !select_budget.getText().toString().isEmpty()
                    && !select_model.getText().toString().isEmpty() && !select_feet.getText().toString().isEmpty() && !select_capacity.getText().toString().isEmpty()
                    && !select_truck_body_type.getText().toString().isEmpty() && !pick_up_address.getText().toString().isEmpty() && !pick_up_city.getText().toString().isEmpty()
                    && !pick_up_pinCode.getText().toString().isEmpty() && !pick_up_state.getText().toString().isEmpty() && !drop_address.getText().toString().isEmpty()
                    && !drop_city.getText().toString().isEmpty() && !drop_pinCode.getText().toString().isEmpty() && !drop_state.getText().toString().isEmpty()) {
                Ok_PostLoad.setEnabled(true);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_active));
            } else {
                Ok_PostLoad.setEnabled(false);
                Ok_PostLoad.setBackgroundResource((R.drawable.button_de_active));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //--------------------------------------Get State and city by PinCode---------------------------
    private void getPickStateAndDistrict(String enteredPin) {

        Log.i("Entered PIN", enteredPin);

        String url = getString(R.string.baseURL) + "/user/locationData/" + enteredPin;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject object = response.getJSONObject("data");

                    stateByPinCode = object.getString("stateCode");
                    distByPinCode = object.getString("district");

                    Log.i("state By PIncode", stateByPinCode);
                    Log.i("Dist By PIncode", distByPinCode);

                    pick_up_state.setText(stateByPinCode);
                    pick_up_city.setText(distByPinCode);

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

    //--------------------------------------Get State and city by PinCode---------------------------

    private void getDropStateAndDistrict(String enteredPin) {

        Log.i("Entered PIN", enteredPin);

        String url = getString(R.string.baseURL) + "/user/locationData/" + enteredPin;
        Log.i("url for truckByTruckId", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject object = response.getJSONObject("data");

                    stateByPinCode = object.getString("stateCode");
                    distByPinCode = object.getString("district");

                    Log.i("state By PIncode", stateByPinCode);
                    Log.i("Dist By PIncode", distByPinCode);

                    drop_state.setText(stateByPinCode);
                    drop_city.setText(distByPinCode);

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
        String pickUpAddress = pick_up_address.getText().toString();
        String pickUpCity = pick_up_city.getText().toString();
        String pickUpPinCode = pick_up_pinCode.getText().toString();
        String pickUpState = pick_up_state.getText().toString();
        String pickUpCountry = pick_up_state.getText().toString();
        String dropAddress = drop_address.getText().toString();
        String dropCity = drop_city.getText().toString();
        String dropPinCode = drop_pinCode.getText().toString();
        String dropState = drop_state.getText().toString();
        String dropCountry = drop_state.getText().toString();
        String notesFromLP = note_to_post_load.getText().toString();

        UpdateLoadPostPickUpDate updateLoadPost = new UpdateLoadPostPickUpDate(pickUpDate, pickUpTime, budget, vehicleModel, vehicleFeet, vehicleCapacity, vehicleBodyType, pickUpAddress, pickUpPinCode, pickUpCity, pickUpState, pickUpCountry, dropAddress, dropPinCode, dropCity, dropState, dropCountry, "123", notesFromLP);

        Call<UpdateLoadPostPickUpDate> call = postLoadService.updateLoadPost("" + loadId, updateLoadPost);

        call.enqueue(new Callback<UpdateLoadPostPickUpDate>() {
            @Override
            public void onResponse(Call<UpdateLoadPostPickUpDate> call, Response<UpdateLoadPostPickUpDate> response) {
                if (response.isSuccessful()) {
                    Log.i("Successful", "Load Post Details Updated");
                }
            }

            @Override
            public void onFailure(Call<UpdateLoadPostPickUpDate> call, Throwable t) {
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
                        String pickUpAddress = obj.getString("pick_add");
                        String pickUpCity = obj.getString("pick_city");
                        String pickUpPinCode = obj.getString("pick_pin_code");
                        String pickUpState = obj.getString("pick_state");
                        String pickUpCountry = obj.getString("pick_country");
                        String dropAddress = obj.getString("drop_add");
                        String dropCity = obj.getString("drop_city");
                        String dropPinCode = obj.getString("drop_pin_code");
                        String dropState = obj.getString("drop_state");
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
                        pick_up_state.setText(pickUpState);
                        pick_up_city.setText(pickUpCity);
                        drop_state.setText(dropState);
                        drop_city.setText(dropCity);
                        auto_calculated_KM.setText(approxKM);
                        pick_up_address.setText(pickUpAddress);
                        drop_address.setText(dropAddress);
                        pick_up_pinCode.setText(pickUpPinCode);
                        drop_pinCode.setText(dropPinCode);
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
}