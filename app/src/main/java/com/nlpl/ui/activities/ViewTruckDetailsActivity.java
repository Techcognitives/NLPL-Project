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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.MainResponse;
import com.nlpl.model.ModelForRecyclerView.DriverModel;
import com.nlpl.model.ModelForRecyclerView.TruckModel;
import com.nlpl.model.Responses.AddTruckResponse;
import com.nlpl.model.Responses.BankResponse;
import com.nlpl.model.UpdateMethods.UpdateTruckDetails;
import com.nlpl.ui.adapters.DriversListAdapter;
import com.nlpl.ui.adapters.TrucksAdapter;
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

public class ViewTruckDetailsActivity extends AppCompat {

    private RequestQueue mQueue;
    ArrayList<MainResponse.Data.TruckDetails> truckList = new ArrayList<>();


    ArrayList<MainResponse.Data.DriverDetails> driverList = new ArrayList<>();


    SwipeRefreshLayout swipeRefreshLayout;


    String phone, userId;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    EditText searchVehicle;

    Dialog loadingDialog;


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

        //------------------------------------------------------------------------------------------
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(loadingDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp2.gravity = Gravity.CENTER;
        ImageView loading_img = loadingDialog.findViewById(R.id.dialog_loading_image_view);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setAttributes(lp2);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.clockwiserotate);
        loading_img.startAnimation(rotate);
        //------------------------------------------------------------------------------------------

        mQueue = Volley.newRequestQueue(ViewTruckDetailsActivity.this);

        getUserDetailsMain();

        //-------------------------------- Action Bar ----------------------------------------------
        actionBar = findViewById(R.id.view_truck_details_action_bar);
        actionBarTitle = (TextView) actionBar.findViewById(R.id.action_bar_title);
        actionBarBackButton = (ImageView) actionBar.findViewById(R.id.action_bar_back_button);
        actionBarMenuButton = (ImageView) actionBar.findViewById(R.id.action_bar_menu);

        actionBarTitle.setText(getString(R.string.My_Trucks));
        actionBarMenuButton.setVisibility(View.GONE);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpTo.goToViewPersonalDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
            }
        });
        //---------------------------- Bottom Nav --------------------------------------------------
        bottomNav = (View) findViewById(R.id.view_truck_details_bottom_nav_bar);
        TextView profileText = (TextView) bottomNav.findViewById(R.id.bottom_nav_profile_text_view);
        ImageView profileImageView = (ImageView) bottomNav.findViewById(R.id.bottom_nav_profile_image_view);
        profileImageView.setImageDrawable(getDrawable(R.drawable.black_truck_small));
        ConstraintLayout customerDashboard = bottomNav.findViewById(R.id.bottom_nav_trip);
        customerDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        ConstraintLayout spDashboard = bottomNav.findViewById(R.id.bottom_nav_sp_dashboard);
        spDashboard.setBackgroundTintList(getResources().getColorStateList(R.color.light_white));
        View spView = bottomNav.findViewById(R.id.bottom_nav_bar_dashboard_underline);
        spView.setVisibility(View.INVISIBLE);
        View customerView = bottomNav.findViewById(R.id.bottom_nav_bar_find_underline);
        profileText.setText(getString(R.string.Trips));
        ConstraintLayout truck = findViewById(R.id.bottom_nav_trip);
        truck.setVisibility(View.GONE);



        //------------------------------------------------------------------------------------------

        //----------------------- Alert Dialog -------------------------------------------------

        //------------------------------------------------------------------------------------------

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_truck_details_refresh_constrain);
        searchVehicle = (EditText) findViewById(R.id.view_truck_details_search_truck_edit_text);
        searchVehicle.addTextChangedListener(searchVehicleWatcher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });

    }

    public void RearrangeItems() {
        JumpTo.goToViewVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
    }

    private void getUserDetailsMain() {
        Call<MainResponse> responseCall = ApiClient.getUserService().mainResponse(userId);
        responseCall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, retrofit2.Response<MainResponse> response) {
                try {
                    MainResponse response1 = response.body();
                    MainResponse.Data list = response1.getData();

                    //GET USER DETAILS
                    String userNameAPI, userPhoneNumberAPI, userAlternatePhoneNumberAPI, userUserTypeAPI, userCityAPI, userPreferredLanguageAPI, userAddressAPI, userStateAPI, userPinCode, userEmailIdAPI, userPayTypeAPI, userIsRegistrationDoneAPI, userIsProfilePicAddedAPI;
                    String userIsTruckAddedAPI, userIsDriverAddedAPI, userIsBankDetailsAddedAPI, userIsCompanyAddedAPI, userIsPersonalAddedAPI, userIsAadhaarVerifiedAPI, userIsPanVerifiedAPI, userIsUserVerifiedAPI, userIsAccountActiveAPI, userCreatedAtAPI, userUpdatedAtAPI;
                    String userUpdatedByAPI, userDeletedAtAPI, userDeletedByAPI, idAPI, userLatitudeAPI, userLongitudeAPI, userDeviceIdAPI, userPanNumberAPI, userAadhaarNumberAPI, userIsSelfAddedAsDriverAPI;

                    userNameAPI = list.getName();
                    userPhoneNumberAPI = String.valueOf(list.getPhone_number());
                    userAlternatePhoneNumberAPI = String.valueOf(list.getAlternate_ph_no());
                    userUserTypeAPI = list.getUser_type();
                    userCityAPI = list.getPreferred_location();
                    userPreferredLanguageAPI = list.getPreferred_language();
                    userAddressAPI = list.getAddress();
                    userStateAPI = list.getState_code();
                    userPinCode = String.valueOf(list.getPin_code());
                    userEmailIdAPI = list.getEmail_id();
                    userPayTypeAPI = list.getPay_type();
                    userIsRegistrationDoneAPI = String.valueOf(list.getIsRegistration_done());
                    userIsProfilePicAddedAPI = String.valueOf(list.getIsProfile_pic_added());
                    userIsTruckAddedAPI = String.valueOf(list.getIsTruck_added());
                    userIsDriverAddedAPI = String.valueOf(list.getIsDriver_added());
                    userIsBankDetailsAddedAPI = String.valueOf(list.getIsBankDetails_given());
                    userIsCompanyAddedAPI = String.valueOf(list.getIsCompany_added());
                    userIsPersonalAddedAPI = String.valueOf(list.getIsPersonal_dt_added());
                    userIsAadhaarVerifiedAPI = String.valueOf(list.getIs_Addhar_verfied());
                    userIsPanVerifiedAPI = String.valueOf(list.getIs_pan_verfied());
                    userIsUserVerifiedAPI = String.valueOf(list.getIs_user_verfied());
                    userIsAccountActiveAPI = String.valueOf(list.getIs_account_active());
                    userCreatedAtAPI = list.getCreated_at();
                    userUpdatedAtAPI = list.getUpdated_at();
                    userUpdatedByAPI = list.getUpdated_by();
                    userDeletedAtAPI = list.getDeleted_at();
                    userDeletedByAPI = list.getDeleted_by();
                    idAPI = String.valueOf(list.getId());
                    userLatitudeAPI = list.getLatitude();
                    userLongitudeAPI = list.getLongitude();
                    userDeviceIdAPI = list.getDevice_id();
                    userPanNumberAPI = list.getPan_number();
                    userAadhaarNumberAPI = list.getAadhaar_number();
                    userIsSelfAddedAsDriverAPI = String.valueOf(list.getIs_self_added_asDriver());

                    //GET TRUCK DETAILS
                    truckList.addAll(list.getTruckdetails());

//                    textView.setText("\n user_id :" + list.truckdetails.get(0).getUser_id()+
//                            "\n Vehicle number :" + list.truckdetails.get(0).getVehicle_no()+
//                            "\n RC Book :" + list.truckdetails.get(0).getRc_book()+
//                            "\n Vehicle insurance :"+list.truckdetails.get(0).getVehicle_insurance()+
//                            "\n Truck Type :" + list.truckdetails.get(0).getTruck_type()+
//                            "\n Vehicle Type :" + list.truckdetails.get(0).getVehicle_type()+
//                            "\n Driver_id :" + list.truckdetails.get(0).getDriver_id());

                    //GET DRIVER DETAILS
                    driverList.addAll(list.getDriverDetails());
                    /*textView.setText("\n user_id :" + list.driverdetails.get(0).getUser_id()+
                            "\n Driver name :" + list.driverdetails.get(0).getDriver_name()+
                            "\n License number :" + list.driverdetails.get(0).getDl_number()+
                            "\n Driver DOB :" + list.driverdetails.get(0).getDriver_dob()+
                            "\n Driver phone number :"+list.driverdetails.get(0).getDriver_number()+
                            "\n Driver Id :" + list.driverdetails.get(0).getDriver_id()+
                            "\n Driver selfie :" + list.driverdetails.get(0).getDriver_selfie()+
                            "\n Alternate number :" + list.driverdetails.get(0).getAlternate_ph_no());*/

                    //GET BANK DETAILS
                    ArrayList<MainResponse.Data.BankDetails> bankList = new ArrayList<>();
                    bankList.addAll(list.getBankDetails());
                    /*textView.setText("\n user_id :" + list.bankDetails.get(0).getUser_id()+
                            "\n Account name holder :" + list.bankDetails.get(0).getAccountholder_name()+
                            "\n Account number :" + list.bankDetails.get(0).getAccount_number()+
                            "\n Ifsc code :" + list.bankDetails.get(0).getIFSI_CODE()+
                            "\n Cancelled cheque :"+list.bankDetails.get(0).getCancelled_cheque()+
                            "\n Bank name :" + list.bankDetails.get(0).getBank_name()+
                            "\n Verification status :" + list.bankDetails.get(0).getVerification_status());*/

                    //GET COMPANY DETAILS
                    ArrayList<MainResponse.Data.CompanyDetails> companyDetails = new ArrayList<>();
                    companyDetails.addAll(list.getCompanyDetails());
                    /*textView.setText("\n Company_id :" + list.companyDetails.get(0).getCompany_id() +
                                     "\n Company name :" + list.companyDetails.get(0).getCompany_name() +
                                     "\n GST number :" + list.companyDetails.get(0).getCompany_gst_no() +
                                     "\n Company PAN :" + list.companyDetails.get(0).getCompany_pan() +
                                     "\n Company City :" + list.companyDetails.get(0).getComp_city() +
                                     "\n Company Address :" + list.companyDetails.get(0).getComp_add() +
                                     "\n Company Zip :" + list.companyDetails.get(0).getComp_zip() +
                                     "\n Company type :" + list.companyDetails.get(0).getCompany_type());*/

                    //GET POST LOAD DETAILS
                    ArrayList<MainResponse.Data.PostaLoadDetails> loadList = new ArrayList<>();
                    loadList.addAll(list.getPostaLoadDetails());
                    /*textView.setText("\n id post :" + list.postaLoadDetails.get(0).getIdpost_load()+
                            "\n Capacity :" + list.postaLoadDetails.get(0).getCapacity()+
                            "\n Drop city :" + list.postaLoadDetails.get(0).getDrop_city()+
                            "\n Pick city :" + list.postaLoadDetails.get(0).getPick_city()+
                            "\n Pick Country :"+list.postaLoadDetails.get(0).getPick_country()+
                            "\n Drop Country :" + list.postaLoadDetails.get(0).getDrop_country()+
                            "\n Pick address :" + list.postaLoadDetails.get(0).getPick_add()+
                            "\n Drop address :" + list.postaLoadDetails.get(0).getDrop_add());*/

                    //GET PREFERRED LOCATIONS
                    ArrayList<MainResponse.Data.PreferredLocation> preferredLocationList = new ArrayList<>();
                    preferredLocationList.addAll(list.getPreferredLocations());
                    /*textView.setText("\n Location Id:" + list.preferredLocations.get(0).getPref_locations_id()+
                            "\n Preferred State :" + list.preferredLocations.get(0).getPref_state()+
                            "\n Preferred city :" + list.preferredLocations.get(0).getPref_city()+
                            "\n User id :" + list.preferredLocations.get(0).getUser_id() +
                            "\n Preferred pincode:"+list.preferredLocations.get(0).getPref_pin_code());*/

                    //GET USER RATINGS
                    ArrayList<MainResponse.Data.UserRatings> ratingsList = new ArrayList<>();
                    ratingsList.addAll(list.getUserRatings());
                    /*textView.setText("\n Rating Id:" + list.userRatings.get(0).getRating_id()+
                            "\n Transaction Id :" + list.userRatings.get(0).getTransection_id()+
                            "\n Rated number :" + list.userRatings.get(0).getRated_no()+
                            "\n Rated comment :" + list.userRatings.get(0).getRatings_comment()+
                            "\n User id :" + list.userRatings.get(0).getUser_id() +
                            "\n Given by :"+list.userRatings.get(0).getGiven_by());*/

                    //GET USER IMAGE
                    ArrayList<MainResponse.Data.UserImages> imagesList = new ArrayList<>();
                    imagesList.addAll(list.getUserImages());
                    /*textView.setText("\n Image Id:" + list.userImages.get(0).getImage_id()+
                            "\n User id :" + list.userImages.get(0).getUser_id() +
                            "\n Image Type :" + list.userImages.get(0).getImage_type()+
                            "\n Image URL :" + list.userImages.get(0).getImage_url());*/

                    //GET SP BID DETAILS
                    ArrayList<MainResponse.Data.SpBidDetails> spBidDetailsList = new ArrayList<>();
                    spBidDetailsList.addAll(list.getSpBidDetails());
//                    textView.setText("\n Bid_id :" + list.spBidDetails.get(0).getSp_bid_id() +
//                            "\n Sp Quote :" + list.spBidDetails.get(0).getSp_quote() +
//                            "\n Capacity :" + list.spBidDetails.get(0).getCapacity() +
//                            "\n Body type :" + list.spBidDetails.get(0).getBody_type() +
//                            "\n bid Status :" + list.spBidDetails.get(0).getBid_status() +
//                            "\n Id Postload :" + list.spBidDetails.get(0).getIdpost_load() +
//                            "\n Bid accepted by sp :" + list.spBidDetails.get(0).getIs_bid_accpted_by_sp() +
//                            "\n Driver Id :" + list.spBidDetails.get(0).getAssigned_driver_id() +
//                            "\n Truck Id :" + list.spBidDetails.get(0).getAssigned_truck_id());
                } catch (Exception e) {
                    e.printStackTrace();
                    getUserDetailsMain();
                }
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                getUserDetailsMain();
            }
        });
    }

    public void onClickAddTruckDetails(View view) {

        JumpTo.goToVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, false, false, false, false, null, null);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:

                JumpTo.goToServiceProviderDashboard(ViewTruckDetailsActivity.this, phone, true, true);
                break;

            case R.id.bottom_nav_customer_dashboard:

                JumpTo.goToFindLoadsActivity(ViewTruckDetailsActivity.this, userId, phone, false);

                break;

            case R.id.bottom_nav_track:

                JumpTo.goToSPTrackActivity(ViewTruckDetailsActivity.this, phone, false);
                break;

            case R.id.bottom_nav_profile:
                JumpTo.goToViewPersonalDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        JumpTo.goToViewPersonalDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
    }


    private TextWatcher searchVehicleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                RearrangeItems();
            }
//            filter(editable.toString());
        }
    };

//    private void filter(String text) {
//        ArrayList<MainResponse.Data.TruckDetails> searchVehicleList = new ArrayList<>();
//
//        for (MainResponse.Data.TruckDetails item : truckList) {
//            if (item.getVehicle_no().toLowerCase().contains(text.toLowerCase())) {
//                searchVehicleList.add(item);
//            }
//        }
//        truckListAdapter.updateData(searchVehicleList);
//    }



    public void showLoading(){
        loadingDialog.show();
    }

    public void dismissLoading(){
        loadingDialog.dismiss();
    }
}