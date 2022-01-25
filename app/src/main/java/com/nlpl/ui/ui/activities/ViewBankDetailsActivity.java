package com.nlpl.ui.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.nlpl.model.ModelForRecyclerView.BankModel;
import com.nlpl.ui.ui.adapters.BanksAdapter;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewBankDetailsActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<BankModel> bankList = new ArrayList<>();
    private BanksAdapter bankListAdapter;
    private RecyclerView bankListRecyclerView;

    String phone, userId;
    Dialog previewDialogCancelledCheque;
    ImageView previewDialogCancelledChequeImageView;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    ConstraintLayout spDashboard, customerDashboard;

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
        getUserDetails();
        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.view_bank_details_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText("My Bank");
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewBankDetailsActivity.this.finish();
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.view_bank_details_bottom_nav_bar);
        spDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        customerDashboard = (ConstraintLayout) bottomNav.findViewById(R.id.bottom_nav_customer_dashboard);
        spDashboard.setBackgroundColor(getResources().getColor(R.color.nav_unselected_blue));
        customerDashboard.setBackgroundColor(getResources().getColor(R.color.nav_selected_blue));
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
    }

    public void onClickBackViewBankDetails(View view) {
        ViewBankDetailsActivity.this.finish();
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
        Intent intent = new Intent(ViewBankDetailsActivity.this, BankDetailsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isEdit", true);
        intent.putExtra("bankDetailsID", obj.getBank_id());
        Log.i("Bank Id in P and R", obj.getBank_id());
        intent.putExtra("mobile", phone);
        startActivity(intent);
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
        Intent intent2 = new Intent(ViewBankDetailsActivity.this, BankDetailsActivity.class);
        intent2.putExtra("userId", userId);
        intent2.putExtra("isEdit", false);
        intent2.putExtra("mobile", phone);

        startActivity(intent2);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                Intent intent = new Intent(ViewBankDetailsActivity.this, DashboardActivity.class);
                intent.putExtra("mobile2", phone);
                startActivity(intent);
                break;

            case R.id.bottom_nav_customer_dashboard:

                break;
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
                        String roleAPI = obj.getString("user_type");
                        String emailAPI = obj.getString("email_id");

//                        name.setText(nameAPI);
//
//                        String s1 = mobileAPI.substring(2, 12);
//                        mobileEdit.setText(s1);

                        if (roleAPI.equals("Customer")) {
                            bottomNav.setVisibility(View.GONE);
                        }else {
                            bottomNav.setVisibility(View.VISIBLE);
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

}