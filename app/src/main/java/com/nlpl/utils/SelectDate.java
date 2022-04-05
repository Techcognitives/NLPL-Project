package com.nlpl.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nlpl.R;
import com.nlpl.ui.activities.PostALoadActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SelectDate {

    public static void selectDate(Activity activity, TextView date) {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = Calendar.getInstance().getTime();
        ;
        int count = 0;
        ArrayList currentSepDate = new ArrayList<>();
        String todayDate;

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


        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int sMonth = 0, startCount = 0;
                String monthS = "", sDate, startingDate;
                Date date1, date2;
                long startD, todayD, diff;

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
                date.setText(sDate);

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

    public static void selectTime(Activity activity, TextView time) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(activity, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                int sizeOfHr = String.valueOf(selectedHour).length();
                int sizeOfMin = String.valueOf(selectedMinute).length();

                if (sizeOfHr == 2 && sizeOfMin == 2) {
                    if (selectedHour > 12) {
                        selectedHour = selectedHour - 12;
                        time.setText(String.format(selectedHour + ":" + selectedMinute + " PM"));
                    } else {
                        time.setText(String.format(selectedHour + ":" + selectedMinute + " AM"));
                    }
                } else if (sizeOfHr == 1 && sizeOfMin == 2) {
                    String selectedHr = "0" + String.valueOf(selectedHour);
                    time.setText(selectedHr + ":" + selectedMinute + " AM");
                } else if (sizeOfHr == 1 && sizeOfMin == 1) {
                    String selectedHr = "0" + String.valueOf(selectedHour);
                    String selectedMin = "0" + String.valueOf(selectedMinute);
                    time.setText(selectedHr + ":" + selectedMin + " AM");
                } else if (sizeOfHr == 2 && sizeOfMin == 1) {
                    String selectedMin = "0" + String.valueOf(selectedMinute);
                    time.setText(selectedHour + ":" + selectedMin + " AM");
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}
