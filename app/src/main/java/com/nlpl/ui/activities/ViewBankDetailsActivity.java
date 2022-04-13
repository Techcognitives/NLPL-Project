package com.nlpl.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BankModel;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.Responses.PreferedLocationResponse;
import com.nlpl.ui.adapters.BanksAdapter;
import com.nlpl.utils.ApiClient;
import com.nlpl.utils.AppCompat;
import com.nlpl.utils.DownloadImageTask;
import com.nlpl.utils.JumpTo;
import com.nlpl.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ViewBankDetailsActivity extends AppCompat {

    private RequestQueue mQueue;
    private ArrayList<BankModel> bankList = new ArrayList<>();
    private BanksAdapter bankListAdapter;
    private RecyclerView bankListRecyclerView;

    SwipeRefreshLayout swipeRefreshLayout;
    String phone, userId, roleAPI;
    Dialog previewDialogCancelledCheque;
    ImageView previewDialogCancelledChequeImageView;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout truck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bank_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("mobile");
            Log.i("Mobile No View Bank", phone);
            userId = bundle.getString("userId");
        }

        mQueue = Volley.newRequestQueue(ViewBankDetailsActivity.this);
        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.view_bank_details_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.My_Bank));
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
//                if (roleAPI.equals("Customer")) {
//                    JumpTo.goToCustomerDashboard(ViewBankDetailsActivity.this, phone, true);
//                } else {
//                    JumpTo.goToServiceProviderDashboard(ViewBankDetailsActivity.this, phone, true, true);
//                }
                JumpTo.goToViewPersonalDetailsActivity(ViewBankDetailsActivity.this, userId, phone, true);
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.view_bank_details_bottom_nav_bar);
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileImageView.setImageDrawable(getDrawable(R.drawable.bottom_nav_search_small));
        ConstraintLayout customerDashboard = bottomNav.findViewById(R.id.bottom_nav_trip);
        customerDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
        spView.setVisibility(View.INVISIBLE);
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
        profileText.setText(getString(R.string.Trucks));
        truck = findViewById(R.id.bottom_nav_trip);
        getUserDetails();
        //---------------------------- Get Bank Details --------------------------------------------
        bankListRecyclerView = (RecyclerView) findViewById(R.id.bank_list_view);

        LinearLayoutManager linearLayoutManagerBank = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerBank.setReverseLayout(true);
        bankListRecyclerView.setLayoutManager(linearLayoutManagerBank);
        bankListRecyclerView.setHasFixedSize(true);

        bankListAdapter = new BanksAdapter(ViewBankDetailsActivity.this, bankList);
        bankListRecyclerView.setAdapter(bankListAdapter);
        getBankDetailsList();
        //------------------------------------------------------------------------------------------

        previewDialogCancelledCheque = new Dialog(ViewBankDetailsActivity.this);
        previewDialogCancelledCheque.setContentView(R.layout.dialog_preview_images);
        previewDialogCancelledCheque.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDialogCancelledChequeImageView = (ImageView) previewDialogCancelledCheque.findViewById(R.id.dialog_preview_image_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_bank_details_refresh_constrain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });
    }

    public void RearrangeItems() {
        ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
        JumpTo.goToViewBankDetailsActivity(ViewBankDetailsActivity.this, userId, phone, true);
    }

    public void getBankDetailsList() {
        //---------------------------- Get Bank Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/bank/getBkByUserId/" + userId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bankList = new ArrayList<>();
                    JSONArray bankLists = response.getJSONArray("data");
                    for (int i = 0; i < bankLists.length(); i++) {
                        JSONObject obj = bankLists.getJSONObject(i);
                        BankModel modelBank = new BankModel();
                        modelBank.setUser_id(obj.getString("user_id"));
                        modelBank.setAccountholder_name(obj.getString("accountholder_name"));
                        modelBank.setBank_name(obj.getString("bank_name"));
                        modelBank.setAccount_number(obj.getString("account_number"));
                        modelBank.setRe_enter_acc_num(obj.getString("re_enter_acc_num"));
                        modelBank.setIFSI_CODE(obj.getString("IFSI_CODE"));
                        modelBank.setBank_id(obj.getString("bank_id"));
                        modelBank.setCancelled_cheque(obj.getString("cancelled_cheque"));
                        bankList.add(modelBank);
                    }
                    if (bankList.size() > 0) {
                        bankListAdapter.updateData(bankList);
                    } else {
                    }
//                    if (bankList.size() > 5) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.height = 235; //height recycleviewer
//                        bankListRecyclerView.setLayoutParams(params);
//                    } else {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        bankListRecyclerView.setLayoutParams(params);
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

    public void getBankDetails(BankModel obj) {
        ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
        JumpTo.goToBankDetailsActivity(ViewBankDetailsActivity.this, userId, phone, true, false, obj.getBank_id());
    }

    public void onClickPreviewBankDetails(BankModel obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogCancelledCheque.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        previewDialogCancelledCheque.show();
        previewDialogCancelledCheque.getWindow().setAttributes(lp);

        String cancelledChequeURL = obj.getCancelled_cheque();
        Log.i("IMAGE CHEQUE URL", cancelledChequeURL);

        new DownloadImageTask((ImageView) previewDialogCancelledCheque.findViewById(R.id.dialog_preview_image_view)).execute(cancelledChequeURL);

    }

    public void onClickAddBankDetails(View view) {
        ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
        JumpTo.goToBankDetailsActivity(ViewBankDetailsActivity.this, userId, phone, false, false, null);
    }

    public void onClickBottomNavigation(View view) {
        if (roleAPI.equals("Customer")) {
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToCustomerDashboard(ViewBankDetailsActivity.this, phone, true);
                    break;

                case R.id.bottom_nav_customer_dashboard:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToFindTrucksActivity(ViewBankDetailsActivity.this, userId, phone);
                    break;

                case R.id.bottom_nav_track:
                    JumpTo.goToLPTrackActivity(ViewBankDetailsActivity.this, phone, false);
                    break;

                case R.id.bottom_nav_trip:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToFindTripLPActivity(ViewBankDetailsActivity.this, phone, userId, false);
                    break;

                case R.id.bottom_nav_profile:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToViewPersonalDetailsActivity(ViewBankDetailsActivity.this, userId, phone, true);
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.bottom_nav_sp_dashboard:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToServiceProviderDashboard(ViewBankDetailsActivity.this, phone, true, true);
                    break;

                case R.id.bottom_nav_customer_dashboard:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToFindLoadsActivity(ViewBankDetailsActivity.this, userId, phone, false);

                    break;

                case R.id.bottom_nav_track:
                    ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
                    JumpTo.goToSPTrackActivity(ViewBankDetailsActivity.this, phone, false);
                    break;
            }
        }

    }

    private void getUserDetails() {

        String url = getString(R.string.baseURL) + "/user/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        String nameAPI = obj.getString("name");
                        String mobileAPI = obj.getString("phone_number");
                        String addressAPI = obj.getString("address");
                        String stateAPI = obj.getString("state_code");
                        String cityAPI = obj.getString("preferred_location");
                        String pinCodeAPI = obj.getString("pin_code");
                        roleAPI = obj.getString("user_type");
                        String emailAPI = obj.getString("email_id");

//                        name.setText(nameAPI);
//
//                        String s1 = mobileAPI.substring(2, 12);
//                        mobileEdit.setText(s1);
                        if (roleAPI.equals("Customer")) {
                            truck.setVisibility(View.VISIBLE);
                        } else {
                            truck.setVisibility(View.GONE);
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
        ShowAlert.loadingDialog(ViewBankDetailsActivity.this);
//        if (roleAPI.equals("Customer")) {
//            JumpTo.goToCustomerDashboard(ViewBankDetailsActivity.this, phone, true);
//        } else {
//            JumpTo.goToServiceProviderDashboard(ViewBankDetailsActivity.this, phone, true, true);
//        }
        JumpTo.goToViewPersonalDetailsActivity(ViewBankDetailsActivity.this, userId, phone, true);
    }

    public void deleteBankDetails(BankModel obj) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(ViewBankDetailsActivity.this);
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
        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);
        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);

        alertTitle.setText("Delete Bank Details");
        alertMessage.setText("Are you sure?\nYou want to delete Bank Details?");
        alertPositiveButton.setText(getString(R.string.yes));
        alertNegativeButton.setText(getString(R.string.no));

        alertPositiveButton.setOnClickListener(view -> {
            alert.dismiss();
            deleteBankDetails(obj.getBank_id());
            RearrangeItems();
        });

        alertNegativeButton.setOnClickListener(view -> alert.dismiss());
    }

    private void deleteBankDetails(String bankId) {
        Call<BankResponse> call = ApiClient.getBankService().deleteBankDetails(bankId);
        call.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, retrofit2.Response<BankResponse> response) {

            }

            @Override
            public void onFailure(Call<BankResponse> call, Throwable t) {
            }
        });
    }
}