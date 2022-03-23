package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.FindLoadsModel;
import com.nlpl.model.UpdateMethods.UpdatePostLoadDetails;
import com.nlpl.ui.activities.FindLoadsActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class FindLoadAdapter extends RecyclerView.Adapter<FindLoadAdapter.FindLoadsViewHolder> {

    private ArrayList<FindLoadsModel> loadList;
    private FindLoadsActivity activity;
    int timeLeftToExpire, timeInMillisec, minLeftToExpire, months;
    String bidEndsAt, currentTimeToCompare, bidEndsAtStringTime, finalBidEndsAt, finalDate;

    public FindLoadAdapter(FindLoadsActivity activity, ArrayList<FindLoadsModel> loadList) {
        this.loadList = loadList;
        this.activity = activity;
    }

    @Override
    public FindLoadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_list, parent, false);
        return new FindLoadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindLoadsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FindLoadsModel obj = loadList.get(position);

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
                    holder.timeLeft.setText(activity.getString(R.string.Load_Expired));
                }
            }.start();
        } else {
            UpdatePostLoadDetails.updateStatus(obj.getIdpost_load(), "loadExpired");
            holder.timeLeft.setText(activity.getString(R.string.Load_Expired));
        }
        //------------------------------------------------------------------------------------------

        String pickUpCity = obj.getPick_city();
        holder.destinationStart.setText("  " + pickUpCity);

        String dropCity = obj.getDrop_city();
        holder.destinationEnd.setText("  " + dropCity);

        String budget = obj.getBudget();
        holder.budget.setText("₹" + budget);

        String date = obj.getPick_up_date();
        holder.date.setText(activity.getString(R.string.pick_up_date_colon) + date);

        String time = obj.getPick_up_time();
        holder.time.setText(activity.getString(R.string.timeOnly) + time);

        String approxKms = obj.getKm_approx();
        holder.distance.setText(activity.getString(R.string.distanceWithColon) + approxKms);

        String capacity = obj.getCapacity();
        holder.capacity.setText(activity.getString(R.string.Load_Type) + capacity);

        String bodyType = obj.getBody_type();
        holder.body.setText(activity.getString(R.string.bodyType) + bodyType);

        String pickUpLocation = obj.getPick_add();
        holder.pickUpLocation.setText(" "+pickUpLocation);

        holder.bidNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onClickBidNow(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loadList.size();
    }

    public void updateData(ArrayList<FindLoadsModel> loadList) {
        this.loadList = loadList;
        notifyDataSetChanged();
    }

    public class FindLoadsViewHolder extends RecyclerView.ViewHolder {
        private TextView timeLeft, destinationStart, destinationEnd, budget, date, time, distance, capacity, body, pickUpLocation, bidNowButton;

        public FindLoadsViewHolder(@NonNull View itemView) {
            super(itemView);

            timeLeft = itemView.findViewById(R.id.load_list_time_left);
            destinationStart = itemView.findViewById(R.id.load_list_pick_up);
            destinationEnd = itemView.findViewById(R.id.load_list_drop);
            budget = itemView.findViewById(R.id.load_list_budget);
            date = itemView.findViewById(R.id.load_list_pick_up_date);
            time = itemView.findViewById(R.id.load_list_pick_up_time);
            distance = itemView.findViewById(R.id.load_list_kms_approx);
            capacity = itemView.findViewById(R.id.load_list_capacity);
            body = itemView.findViewById(R.id.load_list_body);
            pickUpLocation = itemView.findViewById(R.id.load_list_location);
            bidNowButton = itemView.findViewById(R.id.load_list_bid_now_button);

        }

    }
//--------------------------------------------------------------------------------------------------
}