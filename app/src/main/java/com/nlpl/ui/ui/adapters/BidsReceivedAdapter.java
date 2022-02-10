package com.nlpl.ui.ui.adapters;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.model.ModelForRecyclerView.BidsResponsesModel;
import com.nlpl.model.ModelForRecyclerView.LoadNotificationModel;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadStatusSubmitted;
import com.nlpl.ui.ui.activities.CustomerDashboardActivity;
import com.nlpl.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;

public class BidsReceivedAdapter extends RecyclerView.Adapter<BidsReceivedAdapter.BidsReceivedViewHolder> {

    private ArrayList<BidsReceivedModel> loadList;
    private CustomerDashboardActivity activity;

    String sortBy = "Sort By" , bidEndsAt, currentTimeToCompare, bidEndsAtStringTime, finalBidEndsAt, finalDate;
    private RequestQueue mQueue;
    int timeLeftToExpire, timeInMillisec, minLeftToExpire, months;

    public BidsReceivedAdapter(CustomerDashboardActivity activity, ArrayList<BidsReceivedModel> loadList) {
        this.loadList = loadList;
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public BidsReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_received_list, parent, false);
        return new BidsReceivedAdapter.BidsReceivedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BidsReceivedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsReceivedModel obj = loadList.get(position);

        if (obj.getBid_ends_at().equals("null")) {
            bidEndsAt = "2022-02-01 12:05:11.598";
        } else {
            bidEndsAt = obj.getBid_ends_at();
        }

        bidEndsAtStringTime = bidEndsAt.substring(11, 19);
//        12:05:11
        int newHr = 5 + Integer.valueOf(bidEndsAtStringTime.substring(0, 2));
        int newMin = 30 + Integer.valueOf(bidEndsAtStringTime.substring(3, 5));
        finalBidEndsAt = String.valueOf(newHr) + ":" + String.valueOf(newMin) + String.valueOf(bidEndsAt.substring(5, 8));

        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        int seconds = currentTime.get(Calendar.SECOND);
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH);
        int day = currentTime.get(Calendar.DAY_OF_MONTH);


        if (month == 0) {
            months = 1;
        } else if (month == 1) {
            months = 2;
        } else if (month == 2) {
            months = 3;
        } else if (month == 3) {
            months = 4;
        } else if (month == 4) {
            months = 5;
        } else if (month == 5) {
            months = 6;
        } else if (month == 6) {
            months = 7;
        } else if (month == 7) {
            months = 8;
        } else if (month == 8) {
            months = 9;
        } else if (month == 9) {
            months = 10;
        } else if (month == 10) {
            months = 11;
        } else if (month == 11) {
            months = 12;
        }

        int sizeOfDay = String.valueOf(day).length();
        int sizeOfMonth = String.valueOf(months).length();

        if (sizeOfDay == 2 && sizeOfMonth == 2) {
            finalDate = year + "-" + months + "-" + day;
        } else if (sizeOfDay == 1 && sizeOfMonth == 2) {
            finalDate = year + "-" + months + "-" + "0" + day;
        } else if (sizeOfDay == 1 && sizeOfMonth == 1) {
            finalDate = year + "-" + "0" + months + "-" + "0" + day;
        } else if (sizeOfDay == 2 && sizeOfMonth == 1) {
            finalDate = year + "-" + "0" + months + "-" + day;
        }

        String dateEndsAt = bidEndsAt.substring(0, 10);

        Log.i("Date from mobile", finalDate);
        Log.i("Date from API", dateEndsAt);

        int sizeOfHr = String.valueOf(hour).length();
        int sizeOfMin = String.valueOf(minute).length();
        int sizeOfSec = String.valueOf(seconds).length();

        if (sizeOfHr == 1 && sizeOfMin == 1 && sizeOfSec == 1) {
            String getHour = "0" + String.valueOf(hour);
            String getMin = "0" + String.valueOf(minute);
            String getSec = "0" + String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 2 && sizeOfMin == 1 && sizeOfSec == 1) {
            String getHour = String.valueOf(hour);
            String getMin = "0" + String.valueOf(minute);
            String getSec = "0" + String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 1 & sizeOfMin == 2 && sizeOfSec == 1) {
            String getHour = "0" + String.valueOf(hour);
            String getMin = String.valueOf(minute);
            String getSec = "0" + String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 1 & sizeOfMin == 2 && sizeOfSec == 2) {
            String getHour = "0" + String.valueOf(hour);
            String getMin = String.valueOf(minute);
            String getSec = String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 1 & sizeOfMin == 1 && sizeOfSec == 2) {
            String getHour = "0" + String.valueOf(hour);
            String getMin = "0" + String.valueOf(minute);
            String getSec = String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 2 & sizeOfMin == 1 && sizeOfSec == 2) {
            String getHour = String.valueOf(hour);
            String getMin = "0" + String.valueOf(minute);
            String getSec = String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 2 & sizeOfMin == 2 && sizeOfSec == 2) {
            String getHour = String.valueOf(hour);
            String getMin = String.valueOf(minute);
            String getSec = String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        } else if (sizeOfHr == 2 & sizeOfMin == 2 && sizeOfSec == 1) {
            String getHour = String.valueOf(hour);
            String getMin = String.valueOf(minute);
            String getSec = "0" + String.valueOf(seconds);
            currentTimeToCompare = getHour + ":" + getMin + ":" + getSec;
        }

        int endHr = Integer.valueOf(finalBidEndsAt.substring(0, 2));
        int currentHr = Integer.valueOf(currentTimeToCompare.substring(0, 2));

        int endMin = Integer.valueOf(finalBidEndsAt.substring(3, 5));
        int currentMin = Integer.valueOf(currentTimeToCompare.substring(3, 5));

        minLeftToExpire = endMin - currentMin;
        timeLeftToExpire = endHr - currentHr;
        timeInMillisec = (timeLeftToExpire * 60 * 60 * 1000) + (minLeftToExpire * 60 * 1000);

        //------------------------------------------------------------------------------------------
        if (dateEndsAt.equals(finalDate)) {
            new CountDownTimer(timeInMillisec, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        f = new DecimalFormat("00");
                    }
                    long hour = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.timeLeft.setText("  " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    }
                }

                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "loadExpired");
                    holder.timeLeft.setText("  Load Expired");
                }
            }.start();
        } else {
            UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "loadExpired");
            holder.timeLeft.setText("  Load Expired");
        }
        //------------------------------------------------------------------------------------------

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String budget = obj.getBudget();
        holder.budget.setText("₹ " + budget);

        String date = obj.getPick_up_date();
        holder.date.setText("Date: " + date);

        String time = obj.getPick_up_time();
        holder.time.setText("Time: " + time);

        String approxKms = obj.getKm_approx();
        holder.distance.setText("Distance: " + approxKms);

        String model = obj.getVehicle_model();
        holder.model.setText("Model: " + model);

        String feet = obj.getFeet();
        holder.feet.setText("Feet: " + feet);

        String capacity = obj.getCapacity();
        holder.capacity.setText("Capacity: " + capacity);

        String bodyType = obj.getBody_type();
        holder.body.setText("Body: " + bodyType);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(activity);
        linearLayoutManagerBank.setReverseLayout(false);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.VERTICAL);
        holder.bidsResponsesRecyclerView.setLayoutManager(linearLayoutManagerBank);
        holder.bidsResponsesRecyclerView.setHasFixedSize(true);

        holder.editLoadButton.setBackgroundTintList(activity.getResources().getColorStateList(R.color.orange));
        holder.editLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickEditLoadPost(obj);
            }
        });

        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView, holder.bidsReceived, holder.showRecyclerView, sortBy);



        if (obj.getSp_count()>3){

        } else {

            holder.sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Change the selected item's text color
                    try {
                        ((TextView) view).setTextColor(activity.getResources().getColor(R.color.white));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (parent.getSelectedItem().equals("By Recent Activity")){
                        sortBy = "Sort By";
                        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView, holder.bidsReceived, holder.showRecyclerView, sortBy);
                    }
                    if (parent.getSelectedItem().equals("By Price High-low")) {
                        sortBy = "Price High-low";
                        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView, holder.bidsReceived, holder.showRecyclerView, sortBy);
                    }
                    if (parent.getSelectedItem().equals("By Price Low-high")) {
                        sortBy = "Price Low-high";
                        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView, holder.bidsReceived, holder.showRecyclerView, sortBy);
                    }
                    if (parent.getSelectedItem().equals("By Latest Response")) {
                        sortBy = "Recent Responses";
                        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView, holder.bidsReceived, holder.showRecyclerView, sortBy);
                    }
                    if (parent.getSelectedItem().equals("By Initial Response")) {
                        sortBy = "Initial Responses";
                        activity.getBidsResponsesList(obj, holder.bidsResponsesRecyclerView, holder.bidsReceived, holder.showRecyclerView, sortBy);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return loadList.size();
    }

    public void updateData(ArrayList<BidsReceivedModel> loadList ) {
        this.loadList = loadList;
        notifyDataSetChanged();
    }

    public class BidsReceivedViewHolder extends RecyclerView.ViewHolder {
        private TextView timeLeft, destinationStart, destinationEnd, budget, date, time, distance, model, feet, capacity, body, editLoadButton, bidsReceived;
        RecyclerView bidsResponsesRecyclerView;
        Spinner sortBy;
        ConstraintLayout showRecyclerView;

        public BidsReceivedViewHolder(@NonNull View itemView) {
            super(itemView);

            sortBy = itemView.findViewById(R.id.bids_received_list_sort_by_textview);
            timeLeft = itemView.findViewById(R.id.bids_responses_time_left);
            destinationStart = itemView.findViewById(R.id.bids_received_pick_up);
            destinationEnd = itemView.findViewById(R.id.bids_responses_drop);
            budget = itemView.findViewById(R.id.bids_responses_budget);
            date = itemView.findViewById(R.id.bids_responses_pick_up_date);
            time = itemView.findViewById(R.id.bids_responses_pick_up_time);
            distance = itemView.findViewById(R.id.bids_responses_kms_approx);
            model = itemView.findViewById(R.id.bids_responses_model);
            feet = itemView.findViewById(R.id.bids_responses_feet);
            capacity = itemView.findViewById(R.id.bids_responses_capacity);
            body = itemView.findViewById(R.id.bids_responses_body);
            editLoadButton = itemView.findViewById(R.id.bids_responses_edit_load_button);
            bidsReceived = itemView.findViewById(R.id.bids_responses_no_of_responses);
            bidsResponsesRecyclerView = itemView.findViewById(R.id.bids_received_recycler_view);
            showRecyclerView = itemView.findViewById(R.id.bids_received_show_recycler_view_constrain);

        }

    }

}