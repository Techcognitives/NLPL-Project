package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.ui.adapters.LoadsCompletedAdapter;
import com.nlpl.ui.adapters.LoadsExpiredAdapter;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CustomerLoadsHistoryActivity extends AppCompat {

    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<BidsReceivedModel> expiredLoadList = new ArrayList<>();
    private ArrayList<BidsReceivedModel> completedLoadList = new ArrayList<>();
    private LoadsExpiredAdapter loadsExpiredAdapter;
    private LoadsCompletedAdapter loadsCompletedAdapter;
    private RecyclerView expiredLoadsRecyclerView, completedLoadsRecyclerView;
    private RequestQueue mQueue;
    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;
    String phone, userId;
    TextView loadsCompleted, loadsExpired, noLoadsExpiredTextView;
    ConstraintLayout loadExpiredConstrain, loadCompletedConstrain;
    Dialog previewDialogProfileOfSp, viewLoadDetailsDialog;
    ArrayList<String> arrayAssignedDriverId, arrayNotesFromSP, arraySpUserId, arrayBidId, arrayBidStatus;
    String fianlBidId, noteBySPToCustomer, assignedDriverId, finalSpUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_loads_history);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(CustomerLoadsHistoryActivity.this);

        arrayBidId = new ArrayList<>();
        arrayBidStatus = new ArrayList<>();
        arraySpUserId = new ArrayList<>();
        arrayAssignedDriverId = new ArrayList<>();
        arrayNotesFromSP = new ArrayList<>();

        getExpiredLoads();
        getCompletedLoads();
        //----------------------------Action Bar----------------------------------------------------
        actionBar = findViewById(R.id.customer_dashboard_load_history_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.Loads_History));
        actionBarBackButton.setVisibility(View.VISIBLE);
        actionBarMenuButton.setVisibility(View.GONE);

        previewDialogProfileOfSp = new Dialog(CustomerLoadsHistoryActivity.this);
        previewDialogProfileOfSp.setContentView(R.layout.dialog_preview_images);
        previewDialogProfileOfSp.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        viewLoadDetailsDialog = new Dialog(CustomerLoadsHistoryActivity.this);
        viewLoadDetailsDialog.setContentView(R.layout.dialog_acept_bid_customer);
        viewLoadDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));


        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(CustomerLoadsHistoryActivity.this);
                JumpTo.goToCustomerDashboard(CustomerLoadsHistoryActivity.this, phone, true);
            }
        });
        //------------------------------------------------------------------------------------------

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_load_history);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });

        loadsExpired = findViewById(R.id.customer_dashboard_loads_expired_button);
        loadsCompleted = findViewById(R.id.customer_dashboard_load_completed_button);
        loadExpiredConstrain = findViewById(R.id.customer_dashboard_loads_expired_constrain);
        loadCompletedConstrain = findViewById(R.id.customer_dashboard_loads_completed_constrain);
        completedLoadsRecyclerView = findViewById(R.id.customer_dashboard_loads_completed_recycler_view);
        expiredLoadsRecyclerView = findViewById(R.id.customer_dashboard_loads_expired_recycler_view);
        noLoadsExpiredTextView = (TextView) findViewById(R.id.customer_dashboard_no_load_expired_text);

        loadCompletedConstrain.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManagerBank1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank1.setReverseLayout(false);
        linearLayoutManagerBank1.setOrientation(LinearLayoutManager.VERTICAL);
        expiredLoadsRecyclerView.setLayoutManager(linearLayoutManagerBank1);
        expiredLoadsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(false);
        linearLayoutManagerBank.setOrientation(LinearLayoutManager.VERTICAL);
        completedLoadsRecyclerView.setLayoutManager(linearLayoutManagerBank);
        completedLoadsRecyclerView.setHasFixedSize(true);

        loadsExpiredAdapter = new LoadsExpiredAdapter(CustomerLoadsHistoryActivity.this, expiredLoadList);
        expiredLoadsRecyclerView.setAdapter(loadsExpiredAdapter);

        loadsCompletedAdapter = new LoadsCompletedAdapter(CustomerLoadsHistoryActivity.this, completedLoadList);
        completedLoadsRecyclerView.setAdapter(loadsCompletedAdapter);

    }

    private void RearrangeItems() {
        ShowAlert.loadingDialog(CustomerLoadsHistoryActivity.this);
        JumpTo.goToCustomerLoadHistoryActivity(CustomerLoadsHistoryActivity.this, userId, phone, true);
    }

    public void getCompletedLoads() {

        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidsLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsLists.length(); i++) {
                        JSONObject obj = bidsLists.getJSONObject(i);
                        BidsReceivedModel bidsReceivedModel = new BidsReceivedModel();
                        bidsReceivedModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsReceivedModel.setBudget(obj.getString("budget"));
                        bidsReceivedModel.setPick_city(obj.getString("pick_city"));
                        bidsReceivedModel.setDrop_city(obj.getString("drop_city"));
                        bidsReceivedModel.setUser_id(obj.getString("user_id"));
                        bidsReceivedModel.setCapacity(obj.getString("capacity"));
                        bidsReceivedModel.setBody_type(obj.getString("body_type"));

                        if (obj.getString("bid_status").equals("loadSubmitted")) {
                            completedLoadList.add(bidsReceivedModel);
                        }
                    }

                    Collections.reverse(completedLoadList);

                    if (completedLoadList.size() > 0) {
                        noLoadsExpiredTextView.setVisibility(View.GONE);
                        loadsCompletedAdapter.updateData(completedLoadList);
                    } else {
                        noLoadsExpiredTextView.setVisibility(View.VISIBLE);
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
        //-------------------------------------------------------------------------------------------
    }

    public void getExpiredLoads() {

        String url1 = getString(R.string.baseURL) + "/loadpost/getLoadDtByUser/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    expiredLoadList = new ArrayList<>();
                    JSONArray bidsLists = response.getJSONArray("data");
                    for (int i = 0; i < bidsLists.length(); i++) {
                        JSONObject obj = bidsLists.getJSONObject(i);
                        BidsReceivedModel bidsReceivedModel = new BidsReceivedModel();
                        bidsReceivedModel.setIdpost_load(obj.getString("idpost_load"));
                        bidsReceivedModel.setUser_id(obj.getString("user_id"));
                        bidsReceivedModel.setPick_up_date(obj.getString("pick_up_date"));
                        bidsReceivedModel.setPick_up_time(obj.getString("pick_up_time"));
                        bidsReceivedModel.setBudget(obj.getString("budget"));
                        bidsReceivedModel.setBid_status(obj.getString("bid_status"));
                        bidsReceivedModel.setCapacity(obj.getString("capacity"));
                        bidsReceivedModel.setBody_type(obj.getString("body_type"));
                        bidsReceivedModel.setPick_add(obj.getString("pick_add"));
                        bidsReceivedModel.setPick_pin_code(obj.getString("pick_pin_code"));
                        bidsReceivedModel.setPick_city(obj.getString("pick_city"));
                        bidsReceivedModel.setPick_state(obj.getString("pick_state"));
                        bidsReceivedModel.setPick_country(obj.getString("pick_country"));
                        bidsReceivedModel.setDrop_add(obj.getString("drop_add"));
                        bidsReceivedModel.setDrop_pin_code(obj.getString("drop_pin_code"));
                        bidsReceivedModel.setDrop_city(obj.getString("drop_city"));
                        bidsReceivedModel.setDrop_state(obj.getString("drop_state"));
                        bidsReceivedModel.setDrop_country(obj.getString("drop_country"));
                        bidsReceivedModel.setKm_approx(obj.getString("km_approx"));
                        bidsReceivedModel.setNotes_meterial_des(obj.getString("notes_meterial_des"));
                        bidsReceivedModel.setBid_ends_at(obj.getString("bid_ends_at"));

                        if (obj.getString("bid_status").equals("loadExpired")) {
                            expiredLoadList.add(bidsReceivedModel);
                        }
                    }

                    Collections.reverse(expiredLoadList);
                    TextView noLoadsExpiredTextView = (TextView) findViewById(R.id.customer_dashboard_no_load_expired_text);

                    if (expiredLoadList.size() > 0) {
                        noLoadsExpiredTextView.setVisibility(View.GONE);
                        loadsExpiredAdapter.updateData(expiredLoadList);
                    } else {
                        noLoadsExpiredTextView.setVisibility(View.VISIBLE);
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
        //-------------------------------------------------------------------------------------------
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(CustomerLoadsHistoryActivity.this);
        JumpTo.goToCustomerDashboard(CustomerLoadsHistoryActivity.this, phone, true);
    }

    public void onClickLoadsCompleted(View view) {
        loadCompletedConstrain.setVisibility(View.VISIBLE);
        loadExpiredConstrain.setVisibility(View.INVISIBLE);
        loadsCompleted.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
        loadsExpired.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
    }

    public void onClickLoadsExpired(View view) {
        loadCompletedConstrain.setVisibility(View.INVISIBLE);
        loadExpiredConstrain.setVisibility(View.VISIBLE);
        loadsExpired.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_active));
        loadsCompleted.setBackground(getResources().getDrawable(R.drawable.personal_details_buttons_de_active));
    }

    public void reActivateLoad(BidsReceivedModel obj) {

        //----------------------- Alert Dialog -------------------------------------------------
        Dialog reActivateLoad = new Dialog(CustomerLoadsHistoryActivity.this);
        reActivateLoad.setContentView(R.layout.dialog_alert_single_button);
        reActivateLoad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reActivateLoad.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        reActivateLoad.show();
        reActivateLoad.getWindow().setAttributes(lp);
        reActivateLoad.setCancelable(true);

        TextView alertTitle = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_title);
        TextView alertMessage = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_message);
        TextView alertPositiveButton = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) reActivateLoad.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText(getString(R.string.ReActivate_Load));
        alertMessage.setText(getString(R.string.Do_you_want_to_ReActivate_Load));
        alertNegativeButton.setText(getString(R.string.ReActivate_Load));
        alertPositiveButton.setVisibility(View.GONE);
        alertNegativeButton.setBackground(getResources().getDrawable(R.drawable.button_active));
        alertNegativeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_black)));

        alertNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reActivateLoad.dismiss();
                ShowAlert.loadingDialog(CustomerLoadsHistoryActivity.this);
                JumpTo.goToPostALoad(CustomerLoadsHistoryActivity.this, userId, phone, true,false, obj.getIdpost_load(), false);
            }
        });

    }

    public void getFinalSPId(String loadId) {


    }

    public void ViewProfileOfSPToCustomer(BidsReceivedModel obj) {

        String url3 = getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
        Log.i("URL: ", url3);

        JsonObjectRequest request3 = new JsonObjectRequest(Request.Method.GET, url3, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidResponsesLists = response.getJSONArray("data");
                    for (int i = 0; i < bidResponsesLists.length(); i++) {
                        JSONObject obj = bidResponsesLists.getJSONObject(i);
                        arrayBidId.add(obj.getString("sp_bid_id"));
                        arraySpUserId.add(obj.getString("user_id"));
                        arrayBidStatus.add(obj.getString("bid_status"));
                    }

                    for (int k = 0; k < arrayBidStatus.size(); k++) {
                        if (arrayBidStatus.get(k).equals("FinalAccepted")) {
                            fianlBidId = arrayBidId.get(k);
                            finalSpUserId = arraySpUserId.get(k);
                        }
                    }

                    String url1 = getString(R.string.baseURL) + "/imgbucket/Images/" + finalSpUserId;
                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray imageList = response.getJSONArray("data");
                                for (int i = 0; i < imageList.length(); i++) {
                                    JSONObject obj = imageList.getJSONObject(i);
                                    String imageType = obj.getString("image_type");

                                    String profileImgUrl = "";
                                    if (imageType.equals("profile")) {
                                        profileImgUrl = obj.getString("image_url");
                                        if (profileImgUrl.equals("null")) {

                                        } else {
                                            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                            lp2.copyFrom(previewDialogProfileOfSp.getWindow().getAttributes());
                                            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                            lp2.height = WindowManager.LayoutParams.MATCH_PARENT;
                                            lp2.gravity = Gravity.CENTER;

                                            previewDialogProfileOfSp.show();
                                            previewDialogProfileOfSp.getWindow().setAttributes(lp2);
                                            new DownloadImageTask((ImageView) previewDialogProfileOfSp.findViewById(R.id.dialog_preview_image_view)).execute(profileImgUrl);
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
                    mQueue.add(request1);


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
        mQueue.add(request3);
        //-------------------------------------------------------------------------------------------

    }

    public void ViewLoadDetails(BidsReceivedModel obj) {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(viewLoadDetailsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        viewLoadDetailsDialog.show();
        viewLoadDetailsDialog.setCancelable(true);
        viewLoadDetailsDialog.getWindow().setAttributes(lp);

        //-------------------------------------------Display Load Information---------------------------------------------
        TextView nameSP = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bit_service_provider_name);
        TextView capacityBySP = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_capacity_textview);
        TextView bodyTypeBySP = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_body_type_textview);
        TextView quoteBySP = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_bidder_quote_textview);
        TextView negotiableBySP = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_negotiable_textview);
        TextView notesBySP = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_received_notes_textview);
        TextView spNumber = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_sp_number);
        TextView driverNameHeading = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_driver_name_heading);
        TextView driverName = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_driver_name);
        TextView driverNumber = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_driver_number);

        spNumber.setVisibility(View.VISIBLE);
        driverName.setVisibility(View.VISIBLE);
        driverNumber.setVisibility(View.VISIBLE);
        driverNameHeading.setVisibility(View.VISIBLE);

        quoteBySP.setText(obj.getBudget());
        capacityBySP.setText(obj.getCapacity());
        bodyTypeBySP.setText(obj.getBody_type());
        negotiableBySP.setText(getString(R.string.no));
        notesBySP.setText(noteBySPToCustomer);

        String url3 = getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
        Log.i("URL: ", url3);

        JsonObjectRequest request3 = new JsonObjectRequest(Request.Method.GET, url3, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray bidResponsesLists = response.getJSONArray("data");
                    for (int i = 0; i < bidResponsesLists.length(); i++) {
                        JSONObject obj = bidResponsesLists.getJSONObject(i);
                        arrayBidId.add(obj.getString("sp_bid_id"));
                        arraySpUserId.add(obj.getString("user_id"));
                        arrayBidStatus.add(obj.getString("bid_status"));
                        arrayAssignedDriverId.add(obj.getString("assigned_driver_id"));
                        arrayNotesFromSP.add(obj.getString("notes"));
                    }

                    for (int k = 0; k < arrayBidStatus.size(); k++) {
                        if (arrayBidStatus.get(k).equals("FinalAccepted")) {
                            fianlBidId = arrayBidId.get(k);
                            finalSpUserId = arraySpUserId.get(k);
                            assignedDriverId = arrayAssignedDriverId.get(k);
                            noteBySPToCustomer = arrayNotesFromSP.get(k);
                        }
                    }

                    //----------------------------------------------------------
                    String url = getString(R.string.baseURL) + "/user/" + finalSpUserId;
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray truckLists = response.getJSONArray("data");
                                for (int i = 0; i < truckLists.length(); i++) {
                                    JSONObject obj = truckLists.getJSONObject(i);
                                    nameSP.setText(obj.getString("name"));
                                    spNumber.setText(obj.getString("phone_number"));
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
                    //----------------------------------------------------------

                    //----------------------------------------------------------
                    String url1 = getString(R.string.baseURL) + "/driver/driverId/" + assignedDriverId;
                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray truckLists = response.getJSONArray("data");
                                for (int i = 0; i < truckLists.length(); i++) {
                                    JSONObject obj = truckLists.getJSONObject(i);
                                    driverName.setText(obj.getString("driver_name"));
                                    driverNumber.setText(obj.getString("driver_number"));
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
                    mQueue.add(request1);
                    //----------------------------------------------------------

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
        mQueue.add(request3);
        //-------------------------------------------------------------------------------------------

        //----------------------------------------------------------------------------------------------------------------
        TextView customerQuote = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_customer_final_quote_textview);
        RadioButton negotiable_yes = viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_radio_btn_yes);
        RadioButton negotiable_no = viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_radio_btn_no);
        EditText notesCustomer = (EditText) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_notes_editText);
        TextView submitResponseBtn = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_submit_response_btn);
        TextView cancelBtn = (TextView) viewLoadDetailsDialog.findViewById(R.id.dialog_accept_bid_cancel_btn);
        TextView timeLeftTextview = viewLoadDetailsDialog.findViewById(R.id.accept_bid_time_left_textview);
        TextView timeLeft00 = viewLoadDetailsDialog.findViewById(R.id.accept_bid_time_left_00_textview);
        TextView noteHeading = viewLoadDetailsDialog.findViewById(R.id.notes_text_heading_view_consignment_customer);

        timeLeftTextview.setText(getString(R.string.Load_Details));
        timeLeft00.setVisibility(View.GONE);
        timeLeftTextview.setTextColor(getResources().getColorStateList(R.color.black));
        timeLeftTextview.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        noteHeading.setVisibility(View.GONE);
        notesCustomer.setVisibility(View.GONE);
        negotiable_yes.setChecked(false);
        negotiable_yes.setEnabled(false);
        negotiable_no.setChecked(true);
        customerQuote.setText(obj.getBudget());

        submitResponseBtn.setText(getString(R.string.Close));
        submitResponseBtn.setBackgroundResource((R.drawable.button_active));
        submitResponseBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_black));
        submitResponseBtn.setEnabled(true);

        cancelBtn.setVisibility(View.GONE);

    }
}