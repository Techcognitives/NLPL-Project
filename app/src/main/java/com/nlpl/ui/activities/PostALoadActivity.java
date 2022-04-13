package com.nlpl.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.PostLoadResponse;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;

import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.EnglishNumberToWords;
import com.nlpl.utils.GetCurrentLocation;
import com.nlpl.utils.GetLocationDrop;
import com.nlpl.utils.GetLocationPickUp;
import com.nlpl.utils.GetStateCityUsingPINCode;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.SelectCity;
import com.nlpl.utils.SelectDate;
import com.nlpl.utils.SelectState;
import com.nlpl.utils.SelectVehicleType;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostALoadActivity extends AppCompat {

    View action_bar;
    TextView actionBarTitle;
    ImageView actionBarBackButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

    double latitude1, latitude2, longitude1, longitude2;
    String isPickDrop = "0", pickUpAddress, pickUpPinCode, pickupState, pickUpCity, dropAddress, dropPinCode, dropState, dropCiy;

    TextView pick_up_date, dropAddressText, pickAddressText, pick_up_time, select_budget, selectModel, select_capacity;
    EditText note_to_post_load, pickUpAddressEdit, pickupPinCodeEdit, dropAddressEdit, dropPinCodeEdit;

    String phone, advancePercentageInt, userId, paymentMethod = "", selectedState, vehicle_typeAPI, truck_ftAPI, truck_carrying_capacityAPI, customerBudget, sDate, eDate, monthS, monthE, startingDate, endingDate, todayDate;
    int sMonth, eMonth, count, startCount;
    Date currentDate, date1, date2, date3, date4;
    ArrayList currentSepDate;
    TextView setApproxDistance, paymentMethodText, deleteLoad, pickUpStateText, pickUpCityText, dropStateText, dropCityText;
    long startD, endD, todayD, diff, diff1;
    Dialog setBudget;

    Button Ok_PostLoad;

    ArrayList<String> arrayTruckBodyType, arrayVehicleType, updatedArrayTruckFt, arrayCapacityForCompare, arrayTruckFtForCompare, arrayToDisplayCapacity, arrayTruckFt, arrayCapacity;

    private RequestQueue mQueue;
    Boolean isEdit, reActivate;
    String loadId;

    GetCurrentLocation getCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_aload);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
            isEdit = bundle.getBoolean("isEdit");
            reActivate = bundle.getBoolean("reActivate");
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
        actionBarTitle.setText(getString(R.string.Post_a_Load));
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToCustomerDashboard(PostALoadActivity.this, phone, true);
            }
        });
//-------------------------------------- Today's Date ----------------------------------------------
        currentDate = Calendar.getInstance().getTime();
        Log.i("Current/Today's Date", String.valueOf(currentDate));
        pick_up_date = (TextView) findViewById(R.id.post_a_load_date_text_view);
        pick_up_time = (TextView) findViewById(R.id.post_a_load_time_text_view);
        select_budget = (TextView) findViewById(R.id.post_a_load_budget_text_view);
        selectModel = (TextView) findViewById(R.id.post_a_load_vehicle_model);
        select_capacity = (TextView) findViewById(R.id.post_a_load_capacity_text_view);
        note_to_post_load = (EditText) findViewById(R.id.post_a_load_notes_edit_text);
        setApproxDistance = (TextView) findViewById(R.id.post_a_load_auto_calculated_km_edit_text);
        deleteLoad = findViewById(R.id.delete_load_in_post_a_load);
        dropAddressText = findViewById(R.id.post_a_load_enter_drop_location);
        pickAddressText = findViewById(R.id.post_a_load_enter_pick_location);
        paymentMethodText = (TextView) findViewById(R.id.post_a_load_payment_method_text);

        pickUpAddressEdit = (EditText) findViewById(R.id.post_a_load_address_edit_pick_up);
        pickupPinCodeEdit = (EditText) findViewById(R.id.post_a_load_pin_code_edit_pick_up);
        pickUpStateText = (TextView) findViewById(R.id.post_a_load_select_state_pick_up);
        pickUpCityText = (TextView) findViewById(R.id.post_a_load_select_city_pick_up);

        dropAddressEdit = (EditText) findViewById(R.id.post_a_load_address_edit_drop);
        dropPinCodeEdit = (EditText) findViewById(R.id.post_a_load_pin_code_edit_drop);
        dropStateText = (TextView) findViewById(R.id.post_a_load_select_state_drop);
        dropCityText = (TextView) findViewById(R.id.post_a_load_select_city_drop);

        pickUpAddressEdit.addTextChangedListener(addressWatcherPick);
        dropAddressEdit.addTextChangedListener(addressWatcherDrop);

        pickupPinCodeEdit.addTextChangedListener(pickUpPinCodeWatcher);
        dropPinCodeEdit.addTextChangedListener(pickUpPinCodeWatcher);

        pickUpAddress = pickUpAddressEdit.getText().toString();
        pickUpPinCode = pickupPinCodeEdit.getText().toString();
        pickupState = pickUpStateText.getText().toString();
        pickUpCity = pickUpCityText.getText().toString();

        dropAddress = dropAddressEdit.getText().toString();
        dropPinCode = dropPinCodeEdit.getText().toString();
        dropState = dropStateText.getText().toString();
        dropCiy = dropCityText.getText().toString();

        Ok_PostLoad = (Button) findViewById(R.id.post_a_load_ok_button);
        //------------------------------------------------------------------------------------------

        if (isEdit) {
            actionBarTitle.setText(getString(R.string.Edit_a_Load));
            Ok_PostLoad.setText(getString(R.string.Update_a_Load));
        }

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

        pickUpAddressEdit.setFilters(new InputFilter[]{filter});
        dropAddressEdit.setFilters(new InputFilter[]{filter});

        mQueue = Volley.newRequestQueue(PostALoadActivity.this);

        getCurrentLocation = new GetCurrentLocation();
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

        if (isEdit || reActivate) {
            deleteLoad.setVisibility(View.VISIBLE);
            getLoadDetails();
        } else {
            deleteLoad.setVisibility(View.GONE);
        }

        pick_up_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                SelectDate.selectDate(PostALoadActivity.this, pick_up_date);
            }
        });

        pick_up_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDate.selectTime(PostALoadActivity.this, pick_up_time);
            }
        });

        select_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetSet(select_budget.getText().toString());
            }
        });

        Ok_PostLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pick_up_date.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Pick-up Date", Toast.LENGTH_SHORT).show();
                } else if (pick_up_time.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Pick-up Time", Toast.LENGTH_SHORT).show();
                } else if (selectModel.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Body Type", Toast.LENGTH_SHORT).show();
                } else if (select_capacity.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Load Type", Toast.LENGTH_SHORT).show();
                } else if (pickUpAddressEdit.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select / enter Pick-up Address", Toast.LENGTH_SHORT).show();
                } else if (pickupPinCodeEdit.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please enter Pick-up PIN Code", Toast.LENGTH_SHORT).show();
                } else if (pickUpStateText.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Pick-up State", Toast.LENGTH_SHORT).show();
                } else if (pickUpCityText.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Pick-up City", Toast.LENGTH_SHORT).show();
                } else if (dropAddressEdit.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select / enter drop Address", Toast.LENGTH_SHORT).show();
                } else if (dropPinCodeEdit.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please enter Drop PIN Code", Toast.LENGTH_SHORT).show();
                } else if (dropStateText.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Drop State", Toast.LENGTH_SHORT).show();
                } else if (dropCityText.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please select Drop City", Toast.LENGTH_SHORT).show();
                } else if (paymentMethodText.getText().toString().equals("Payment Method:") || paymentMethodText.getText().toString().equals("")) {
                    Toast.makeText(PostALoadActivity.this, "Please select Payment Method", Toast.LENGTH_SHORT).show();
                } else if (setApproxDistance.getText().toString().isEmpty()) {
                    Toast.makeText(PostALoadActivity.this, "Please wait until approx KM calculation", Toast.LENGTH_SHORT).show();
                } else {
                    if (isEdit || reActivate) {
                        if (isEdit) {
                            UpdatePostLoadDetails.updatePickUpDate(loadId, pick_up_date.getText().toString());
                            UpdatePostLoadDetails.updatePickUpTime(loadId, pick_up_time.getText().toString());
                            UpdatePostLoadDetails.updateBudget(loadId, select_budget.getText().toString());
                            UpdatePostLoadDetails.updateVehicleCapacity(loadId, select_capacity.getText().toString());
                            UpdatePostLoadDetails.updateVehicleBodyType(loadId, selectModel.getText().toString());
                            UpdatePostLoadDetails.updatePickUpCountry(loadId, "India");
                            UpdatePostLoadDetails.updatePickUpAddress(loadId, pickUpAddressEdit.getText().toString());
                            UpdatePostLoadDetails.updatePickUpPinCode(loadId, pickupPinCodeEdit.getText().toString());
                            UpdatePostLoadDetails.updatePickUpState(loadId, pickUpStateText.getText().toString());
                            UpdatePostLoadDetails.updatePickUpCity(loadId, pickUpCityText.getText().toString());
                            UpdatePostLoadDetails.updateDropCountry(loadId, "India");
                            UpdatePostLoadDetails.updateDropAddress(loadId, dropAddressEdit.getText().toString());
                            UpdatePostLoadDetails.updateDropPinCode(loadId, dropPinCodeEdit.getText().toString());
                            UpdatePostLoadDetails.updateDropState(loadId, dropStateText.getText().toString());
                            UpdatePostLoadDetails.updateDropCity(loadId, dropCityText.getText().toString());
                            UpdatePostLoadDetails.updateApproxKM(loadId, setApproxDistance.getText().toString());
                            UpdatePostLoadDetails.updateNotes(loadId, note_to_post_load.getText().toString());
                            if (paymentMethod.equals("PayNow")) {
                                UpdatePostLoadDetails.updatePaymentMethod(loadId, advancePercentageInt);
                            } else {
                                UpdatePostLoadDetails.updatePaymentMethod(loadId, paymentMethod);
                            }
                        }
                        if (reActivate) {
                            UpdatePostLoadDetails.updateStatus(loadId, "delete");
                            saveLoad(createLoadRequest());
                        }
                        //----------------------- Alert Dialog -------------------------------------------------
                        Dialog alert = new Dialog(PostALoadActivity.this);
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

                        alertTitle.setText(getString(R.string.Post_a_Load));
                        alertMessage.setText(getString(R.string.Load_Updated_Successfully));
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText(getString(R.string.ok));
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                JumpTo.goToCustomerDashboard(PostALoadActivity.this, phone, true);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    } else {
                        saveLoad(createLoadRequest());
                        //----------------------- Alert Dialog -------------------------------------------------
                        Dialog alert = new Dialog(PostALoadActivity.this);
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

                        alertTitle.setText(getString(R.string.Post_a_Load));
                        alertMessage.setText(getString(R.string.Load_Posted_Successfully));
                        alertPositiveButton.setVisibility(View.GONE);
                        alertNegativeButton.setText(getString(R.string.ok));
                        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
                        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

                        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                JumpTo.goToCustomerDashboard(PostALoadActivity.this, phone, true);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    }
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
        Button cancelButton = setBudget.findViewById(R.id.dialog_budget_cancel_button);

        budget.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String newPreviousBudget = previousBudget.replaceAll(",", "");
        budget.setText(newPreviousBudget);

        cancelButton.setOnClickListener(view -> setBudget.dismiss());
//        if (!previousBudget.isEmpty()) {
        okBudget.setEnabled(true);
        okBudget.setBackgroundResource((R.drawable.button_active));
//        } else {
//
//            okBudget.setEnabled(false);
//            okBudget.setBackgroundResource((R.drawable.button_de_active));
//        }

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                select_budget.setText("0");
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
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
                } else {
                    select_budget.setText("0");
                    okBudget.setEnabled(true);
                    okBudget.setBackgroundResource((R.drawable.button_active));
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
                if (budget.getText().toString().isEmpty()) {
                    select_budget.setText("0");
                }
                setBudget.dismiss();
            }
        });

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

    //--------------------------------------create Bank Details in API -------------------------------------
    public PostLoadRequest createLoadRequest() {
        PostLoadRequest postLoadRequest = new PostLoadRequest();
        postLoadRequest.setPick_up_date(pick_up_date.getText().toString());
        postLoadRequest.setPick_up_time(pick_up_time.getText().toString());
        postLoadRequest.setBudget(select_budget.getText().toString());
        postLoadRequest.setCapacity(select_capacity.getText().toString());
        postLoadRequest.setBody_type(selectModel.getText().toString());
        postLoadRequest.setPick_add(pickUpAddressEdit.getText().toString());
        postLoadRequest.setPick_city(pickUpCityText.getText().toString());
        postLoadRequest.setPick_pin_code(pickupPinCodeEdit.getText().toString());
        postLoadRequest.setPick_state(pickUpStateText.getText().toString());
        postLoadRequest.setPick_country("India");
        postLoadRequest.setDrop_add(dropAddressEdit.getText().toString());
        postLoadRequest.setDrop_city(dropCityText.getText().toString());
        postLoadRequest.setDrop_pin_code(dropPinCodeEdit.getText().toString());
        postLoadRequest.setDrop_state(dropStateText.getText().toString());
        postLoadRequest.setDrop_country("India");
        postLoadRequest.setUser_id(userId);
        postLoadRequest.setSp_count(0);
        postLoadRequest.setKm_approx(setApproxDistance.getText().toString());
        postLoadRequest.setNotes_meterial_des(note_to_post_load.getText().toString());
        if (paymentMethod.equals("PayNow")) {
            postLoadRequest.setPayment_type(advancePercentageInt);
        } else {
            postLoadRequest.setPayment_type(paymentMethod);
        }

        if (reActivate) {
            postLoadRequest.setBid_status("loadReactivated");
        } else {
            postLoadRequest.setBid_status("loadPosted");
        }
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
                        String paymentMethodAPI = obj.getString("payment_type");

                        pick_up_date.setText(pickUpDate);
                        pick_up_time.setText(pickUpTime);
                        select_budget.setText(budget);
                        selectModel.setText(vehicleBodyType);
                        select_capacity.setText(vehicleCapacity);
                        pickupState = pickUpStates;
                        pickUpStateText.setText(pickUpStates);
                        pickUpCity = pickUpCities;
                        pickUpCityText.setText(pickUpCities);
                        dropState = dropStates;
                        dropStateText.setText(dropStates);
                        dropCiy = dropCities;
                        dropCityText.setText(dropCities);
                        setApproxDistance.setText(approxKM);
                        pickUpAddress = pickUpAddress1;
                        pickUpAddressEdit.setText(pickUpAddress1);
                        dropAddress = dropAddress1;
                        dropAddressEdit.setText(dropAddress1);
                        pickUpPinCode = pickUpPinCodes;
                        pickupPinCodeEdit.setText(pickUpPinCodes);
                        dropPinCode = dropPinCodes;
                        dropPinCodeEdit.setText(dropPinCodes);
                        note_to_post_load.setText(notesFromLP);
                        paymentMethod = paymentMethodAPI;

                        if (paymentMethodAPI.equals("ToPay")) {
                            paymentMethodText.setText(getString(R.string.Payment_Method) + getString(R.string.To_Pay));
                        } else if (paymentMethodAPI.equals("ToBeBilled")) {
                            paymentMethodText.setText(getString(R.string.Payment_Method) + getString(R.string.To_be_billed));
                        } else {
                            paymentMethodText.setText(getString(R.string.Payment_Method) + getString(R.string.Pay) + paymentMethodAPI + getString(R.string.in_Advance));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(PostALoadActivity.this);
        JumpTo.goToCustomerDashboard(PostALoadActivity.this, phone, true);
    }

    private void getPickUpLocation(String pickUpPinCode) {
        GetLocationPickUp geoLocation = new GetLocationPickUp();
        geoLocation.geLatLongPickUp(pickUpPinCode, getApplicationContext(), new GeoHandlerLatitude());
    }

    public void calculateApproxKM() {
        if (pickupPinCodeEdit.getText().toString().length() == 6 && dropPinCodeEdit.getText().toString().length() == 6) {
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
            setApproxDistance.setText("");
        }
    }

    public void deleteLoad(View view) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog deleteLoad = new Dialog(PostALoadActivity.this);
        deleteLoad.setContentView(R.layout.dialog_alert);
        deleteLoad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(deleteLoad.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;


        deleteLoad.show();
        deleteLoad.getWindow().setAttributes(lp);
        deleteLoad.setCancelable(true);

        TextView alertTitle = (TextView) deleteLoad.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = (TextView) deleteLoad.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = (TextView) deleteLoad.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) deleteLoad.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText(getString(R.string.Delete_Load));
        alertMessage.setText(getString(R.string.Do_you_really_want_to_delete_load));
        alertPositiveButton.setText(getString(R.string.Delete_Load));
        alertPositiveButton.setVisibility(View.VISIBLE);
        alertNegativeButton.setText(getString(R.string.cancel));
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

        alertPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePostLoadDetails.updateStatus(loadId, "delete");
                deleteLoad.dismiss();
                ShowAlert.loadingDialog(PostALoadActivity.this);
                JumpTo.goToCustomerDashboard(PostALoadActivity.this, phone, true);
            }
        });

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLoad.dismiss();
            }
        });
        //------------------------------------------------------------------------------------------
    }

    public void onClickGetCurrentLocationPickUp(View view) {
        GetCurrentLocation.getCurrentLocation(PostALoadActivity.this, pickUpAddressEdit, pickupPinCodeEdit);
        isPickDrop = "1";
    }

    public void onClickGetCurrentLocationDrop(View view) {
        GetCurrentLocation.getCurrentLocation(PostALoadActivity.this, dropAddressEdit, dropPinCodeEdit);
        isPickDrop = "2";
    }

    public void onClickPickUpState(View view) {
        SelectState.selectState(PostALoadActivity.this, pickUpStateText, pickUpCityText);
    }

    public void onClickPickUpCity(View view) {
        if (!pickUpStateText.getText().toString().isEmpty()) {
            SelectCity.selectCity(PostALoadActivity.this, pickUpStateText.getText().toString(), pickUpCityText);
        }
    }

    public void onClickDropState(View view) {
        SelectState.selectState(PostALoadActivity.this, dropStateText, dropCityText);
    }

    public void onClickDropCity(View view) {
        if (!dropStateText.getText().toString().isEmpty()) {
            SelectCity.selectCity(PostALoadActivity.this, dropStateText.getText().toString(), dropCityText);
        }
    }

    public void onClickLoadTypeAndBodyType(View view) {
        switch (view.getId()) {
            case R.id.post_a_load_vehicle_model:
                SelectVehicleType.selectBodyType(PostALoadActivity.this, selectModel, select_capacity);
                break;

            case R.id.post_a_load_capacity_text_view:
                SelectVehicleType.selectLoadType(PostALoadActivity.this, selectModel.getText().toString(), select_capacity);
                break;
        }
    }

    public void onClickPaymentMethod(View view) {
        //----------------------- Alert Dialog ---------------------------------------------
        Dialog alert = new Dialog(PostALoadActivity.this);
        alert.setContentView(R.layout.dialog_payment);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        alert.show();
        alert.getWindow().setAttributes(lp);
        alert.setCancelable(false);

        RadioButton toPay, payNow, toBeBilled;
        toPay = alert.findViewById(R.id.dialog_payment_to_pay_radio_button);
        payNow = alert.findViewById(R.id.dialog_payment_pay_now_radio_button);
        toBeBilled = alert.findViewById(R.id.dialog_payment_to_be_billed_radio_button);

        RadioButton threePercentage, onePercentage;
        threePercentage = alert.findViewById(R.id.dialog_payment_three_percent_radio_button);
        threePercentage.setVisibility(View.GONE);
        onePercentage = alert.findViewById(R.id.dialog_payment_one_percent_radio_button);
        onePercentage.setVisibility(View.GONE);

        TextView chargesText;
        chargesText = alert.findViewById(R.id.dialog_payment_charges_text);
        chargesText.setVisibility(View.GONE);

        View stepOne, stepTwo, underlineBetween;
        stepOne = alert.findViewById(R.id.dialog_payment_step_one_view);
        stepOne.setVisibility(View.INVISIBLE);
        stepTwo = alert.findViewById(R.id.dialog_payment_step_two_view);
        stepTwo.setVisibility(View.INVISIBLE);
        underlineBetween = alert.findViewById(R.id.dialog_payment_underline);
        underlineBetween.setVisibility(View.GONE);

        TextView payButton, cancelButton;
        payButton = alert.findViewById(R.id.dialog_payment_pay_button);
        payButton.setText(getString(R.string.ok));
        cancelButton = alert.findViewById(R.id.dialog_payment_cancel_button);

        ImageView infoThreePercentage, infoOnePercentage;
        infoOnePercentage = alert.findViewById(R.id.dialog_payment_info_one_percent_button);
        infoOnePercentage.setVisibility(View.GONE);
        infoThreePercentage = alert.findViewById(R.id.dialog_payment_info_three_percent_button);
        infoThreePercentage.setVisibility(View.GONE);

        EditText advancePercentage;
        advancePercentage = alert.findViewById(R.id.dialog_payment_advance_percentages);
        advancePercentage.setEnabled(false);

        toPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod = "ToPay";
                advancePercentage.setEnabled(false);
                toPay.setChecked(true);
                payNow.setChecked(false);
                toBeBilled.setChecked(false);
            }
        });

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod = "PayNow";
                advancePercentage.setEnabled(true);
                toPay.setChecked(false);
                payNow.setChecked(true);
                toBeBilled.setChecked(false);
            }
        });

        toBeBilled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod = "ToBeBilled";
                advancePercentage.setEnabled(false);
                toPay.setChecked(false);
                payNow.setChecked(false);
                toBeBilled.setChecked(true);
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (paymentMethod) {
                    case "ToPay":
                        alert.dismiss();
                        paymentMethodText.setText(getString(R.string.Payment_Method) + getString(R.string.To_Pay));
                        break;
                    case "PayNow":
                        if (advancePercentage.getText().toString().isEmpty()) {
                            Toast.makeText(PostALoadActivity.this, "Please enter advance payment percentage (%)", Toast.LENGTH_SHORT).show();
                        } else {
                            alert.dismiss();
                            paymentMethodText.setText(getString(R.string.Payment_Method) + getString(R.string.Pay) + advancePercentage.getText().toString() + getString(R.string.in_Advance));
                        }
                        advancePercentageInt = advancePercentage.getText().toString();
                        break;
                    case "ToBeBilled":
                        alert.dismiss();
                        paymentMethodText.setText(getString(R.string.Payment_Method) + getString(R.string.To_be_billed));
                        break;
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    public void onClickOpenMapsPick(View view) {
        GetCurrentLocation.searchOnMap(PostALoadActivity.this);
        isPickDrop = "1";
    }

    public void onClickOpenMapsDrop(View view) {
        GetCurrentLocation.searchOnMap(PostALoadActivity.this);
        isPickDrop = "2";
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

    private void getDropLocation(String dropPinCode) {
        GetLocationDrop geoLocation = new GetLocationDrop();
        geoLocation.geLatLongDrop(dropPinCode, getApplicationContext(), new GeoHandlerLongitude());
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

        try {
            setApproxDistance.setText(String.format(Locale.US, "%2f KM", distanceApprox));
            String str = String.valueOf(distanceApprox);
            String[] res = str.split("[.]", 0);
            setApproxDistance.setText(res[0] + " KM");
        } catch (Exception e) {

        }

    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1 * Math.PI / 180.0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPickDrop.equals("1")) {
            getCurrentLocation.setAddressAndPin(PostALoadActivity.this, data, pickUpAddressEdit, pickupPinCodeEdit);
        } else if (isPickDrop.equals("2")) {
            getCurrentLocation.setAddressAndPin(PostALoadActivity.this, data, dropAddressEdit, dropPinCodeEdit);
        }
    }


    public void onClickShowList(View view) {
        Dialog selectNoteDialog = new Dialog(PostALoadActivity.this);
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
                note_to_post_load.setText(adapter1.getItem(i));
                selectNoteDialog.dismiss();
            }
        });
    }

    private TextWatcher addressWatcherPick = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String addressText = pickUpAddressEdit.getText().toString().trim();

            if (addressText.length() == 0) {
                pickupPinCodeEdit.setVisibility(View.GONE);
                pickUpStateText.setVisibility(View.GONE);
                pickUpCityText.setVisibility(View.GONE);
            } else {
                pickupPinCodeEdit.setVisibility(View.VISIBLE);
                pickUpStateText.setVisibility(View.VISIBLE);
                pickUpCityText.setVisibility(View.VISIBLE);
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

    private TextWatcher addressWatcherDrop = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String addressText = dropAddressEdit.getText().toString().trim();

            if (addressText.length() == 0) {
                dropPinCodeEdit.setVisibility(View.GONE);
                dropStateText.setVisibility(View.GONE);
                dropCityText.setVisibility(View.GONE);
            } else {
                dropPinCodeEdit.setVisibility(View.VISIBLE);
                dropStateText.setVisibility(View.VISIBLE);
                dropCityText.setVisibility(View.VISIBLE);
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

    private TextWatcher pickUpPinCodeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String pinCodeWatcherPickUp = pickupPinCodeEdit.getText().toString().trim();

            if (pinCodeWatcherPickUp.length() == 6) {
                GetStateCityUsingPINCode.getStateAndDistrictForPickUp(PostALoadActivity.this, pickupPinCodeEdit.getText().toString(), pickUpStateText, pickUpCityText);
                getPickUpLocation(pickupPinCodeEdit.getText().toString());
            }

            String pinCodeWatcherDrop = dropPinCodeEdit.getText().toString().trim();
            if (pinCodeWatcherDrop.length() == 6) {
                GetStateCityUsingPINCode.getStateAndDistrictForPickUp(PostALoadActivity.this, dropPinCodeEdit.getText().toString(), dropStateText, dropCityText);
                getDropLocation(dropPinCodeEdit.getText().toString());
            }

            if (pinCodeWatcherPickUp.length() == 6 && pinCodeWatcherDrop.length() == 6) {
                calculateApproxKM();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}