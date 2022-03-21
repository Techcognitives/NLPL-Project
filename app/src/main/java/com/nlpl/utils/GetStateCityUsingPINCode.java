package com.nlpl.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.ui.activities.PostALoadActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class GetStateCityUsingPINCode {

    public static void getStateAndDistrictForPickUp(Activity activity, String enteredPin, TextView state, TextView city) {
        RequestQueue mQueue = Volley.newRequestQueue(activity);

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

                    state.setText(stateByPinCode);
                    city.setText(distByPinCode);

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
}
