package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.ui.ui.adapters.TrucksAdapter;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewTruckDetailsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<TruckModel> truckList = new ArrayList<>();
    private TrucksAdapter truckListAdapter;
    private RecyclerView truckListRecyclerView;

    Dialog previewDialogTruckDetails;
    ImageView previewRcBook, previewInsurance;
    TextView previewTruckDetailsVehicleNumber, previewTruckDetailsTruckType, previewTruckDetailsVehicleType, previewTruckDetailsTruckFeet, previewTruckDetailsVehicleCapacity;

    String phone, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_truck_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Personal", phone);
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(ViewTruckDetailsActivity.this);
        //---------------------------- Get Truck Details -------------------------------------------
        truckListRecyclerView = (RecyclerView) findViewById(R.id.trucks_list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        truckListAdapter = new TrucksAdapter(ViewTruckDetailsActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);
        getTruckList();
        //------------------------------------------------------------------------------------------

        previewDialogTruckDetails = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogTruckDetails.setContentView(R.layout.dialog_preview_truck_details);
        previewDialogTruckDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewTruckDetailsVehicleNumber = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_vehicle_number_text_view);
        previewTruckDetailsTruckType = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_truck_type_text_view);
        previewTruckDetailsVehicleType = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_vehicle_type_text_view);
        previewTruckDetailsTruckFeet = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_truck_ft_text_view);
        previewTruckDetailsVehicleCapacity = (TextView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_capacity_text_view);
        previewRcBook = (ImageView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_rc_image_view);
        previewInsurance = (ImageView) previewDialogTruckDetails.findViewById(R.id.dialog_truck_details_insurance_image_view);
    }

    public void onClickBackViewTruckDetails(View view) {
        ViewTruckDetailsActivity.this.finish();
    }

    public void getTruckList() {
        //---------------------------- Get Truck Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/truck/truckbyuserID/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    truckList = new ArrayList<>();
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        TruckModel model = new TruckModel();
                        model.setUser_id(obj.getString("user_id"));
                        model.setVehicle_no(obj.getString("vehicle_no"));
                        model.setTruck_type(obj.getString("truck_type"));
                        model.setVehicle_type(obj.getString("vehicle_type"));
                        model.setTruck_ft(obj.getString("truck_ft"));
                        model.setTruck_carrying_capacity(obj.getString("truck_carrying_capacity"));
                        model.setRc_book(obj.getString("rc_book"));
                        model.setVehicle_insurance(obj.getString("vehicle_insurance"));
                        model.setTruck_id(obj.getString("truck_id"));
                        truckList.add(model);
                    }
                    if (truckList.size() > 0) {
                        truckListAdapter.updateData(truckList);
                    } else {
                    }

//                    if (truckList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        truckListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        truckListRecyclerView.setLayoutParams(params);
//                    }

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
        //-------------------------------------------------------------------------------------------
    }

    public void getTruckDetails(TruckModel obj) {
        Intent intent = new Intent(ViewTruckDetailsActivity.this, VehicleDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("truckId", obj.getTruck_id());
        intent.putExtra("mobile", phone);

        startActivity(intent);
    }

    public void getOnClickPreviewTruckRcBook(TruckModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogTruckDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        previewDialogTruckDetails.show();
        previewDialogTruckDetails.getWindow().setAttributes(lp);

        String rcBookURL = obj.getRc_book();
        Log.i("IMAGE RC URL", rcBookURL);
        new DownloadImageTask(previewRcBook).execute(rcBookURL);
    }

    public void getOnClickPreviewTruckInsurance(TruckModel obj) {

        String insuranceURL = obj.getVehicle_insurance();
        Log.i("IMAGE INSURANCE URL", insuranceURL);
        new DownloadImageTask(previewInsurance).execute(insuranceURL);
    }
}