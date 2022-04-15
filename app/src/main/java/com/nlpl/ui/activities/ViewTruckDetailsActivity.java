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
    private TrucksAdapter truckListAdapter;
    private RecyclerView truckListRecyclerView;

    ArrayList<MainResponse.Data.DriverDetails> driverList = new ArrayList<>();
    private DriversListAdapter driverListAdapter;
    private RecyclerView driverListRecyclerView;

    SwipeRefreshLayout swipeRefreshLayout;

    Dialog previewDialogRcBook, previewDialogInsurance;
    String phone, userId, truckIdPass;
    ImageView previewRcBook, previewInsurance;

    Dialog previewDialogDL, previewDialogSelfie, previewDialogSpinner;
    ImageView previewDL, previewSelfie;

    ArrayList<String> arrayuserAllDrivers;

    View actionBar;
    TextView actionBarTitle;
    ImageView actionBarBackButton, actionBarMenuButton;

    View bottomNav;
    EditText searchVehicle;

    Dialog previewDialogDriverDetails;
    TextView previewDriverDetailsTitle, previewDriverDetailsDriverName, previewDriverDetailsDriverNumber, previewDriverDetailsDriverEmails, previewDriverDetailsDriverLicence, previewDriverDetailsDriverSelfie, previewDriverDetailsAssignDriverButton, previewDriverDetailsOKButton, previewDriverDetailsMessage;

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
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
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

        //---------------------------- Get Truck Details -------------------------------------------
        truckListRecyclerView = (RecyclerView) findViewById(R.id.trucks_list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        truckListRecyclerView.setLayoutManager(linearLayoutManager);
        truckListRecyclerView.setHasFixedSize(true);

        arrayuserAllDrivers = new ArrayList<>();

        truckListAdapter = new TrucksAdapter(ViewTruckDetailsActivity.this, truckList);
        truckListRecyclerView.setAdapter(truckListAdapter);

        //------------------------------------------------------------------------------------------
        previewDialogDL = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogDL.setContentView(R.layout.dialog_preview_images);
        previewDialogDL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDL = (ImageView) previewDialogDL.findViewById(R.id.dialog_preview_image_view);

        previewDialogSelfie = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogSelfie.setContentView(R.layout.dialog_preview_images);
        previewDialogSelfie.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewSelfie = (ImageView) previewDialogSelfie.findViewById(R.id.dialog_preview_image_view);
        //----------------------- Alert Dialog -------------------------------------------------
        previewDialogDriverDetails = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogDriverDetails.setContentView(R.layout.dialog_preview_driver_truck_details);
        previewDialogDriverDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewDriverDetailsTitle = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_title);
        previewDriverDetailsDriverName = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_vehicle_number);
        previewDriverDetailsDriverNumber = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_vehicle_model);
        previewDriverDetailsDriverEmails = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_capacity);
        previewDriverDetailsDriverLicence = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_rc_book_preview);
        previewDriverDetailsDriverSelfie = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_insurance_preview);
        previewDriverDetailsAssignDriverButton = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_reassign_button);
        previewDriverDetailsOKButton = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_ok_button);
        previewDriverDetailsMessage = previewDialogDriverDetails.findViewById(R.id.dialog_driver_truck_details_label_add_driver_bank);
        //------------------------------------------------------------------------------------------
        previewDialogSpinner = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogSpinner.setContentView(R.layout.dialog_spinner_bind);
        previewDialogSpinner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView previewSpinnerTitle = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_title);
        TextView previewSpinnerAddTruck = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_add_details);
        TextView previewSpinnerOkButton = (TextView) previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_cancel);
        //------------------------------------------------------------------------------------------

        //---------------------------- Get Driver Details ------------------------------------------
        driverListRecyclerView = previewDialogSpinner.findViewById(R.id.dialog_spinner_bind_recycler_view);

        LinearLayoutManager linearLayoutManagerDriver = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerDriver.setReverseLayout(true);
        driverListRecyclerView.setLayoutManager(linearLayoutManagerDriver);
        driverListRecyclerView.setHasFixedSize(true);

        driverListAdapter = new DriversListAdapter(ViewTruckDetailsActivity.this, driverList);
        driverListRecyclerView.setAdapter(driverListAdapter);

        //------------------------------------------------------------------------------------------

        previewDialogRcBook = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogRcBook.setContentView(R.layout.dialog_preview_images);
        previewDialogRcBook.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewRcBook = (ImageView) previewDialogRcBook.findViewById(R.id.dialog_preview_image_view);

        previewDialogInsurance = new Dialog(ViewTruckDetailsActivity.this);
        previewDialogInsurance.setContentView(R.layout.dialog_preview_images);
        previewDialogInsurance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        previewInsurance = (ImageView) previewDialogInsurance.findViewById(R.id.dialog_preview_image_view);

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
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
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
                    truckListAdapter.updateData(truckList);
//                    textView.setText("\n user_id :" + list.truckdetails.get(0).getUser_id()+
//                            "\n Vehicle number :" + list.truckdetails.get(0).getVehicle_no()+
//                            "\n RC Book :" + list.truckdetails.get(0).getRc_book()+
//                            "\n Vehicle insurance :"+list.truckdetails.get(0).getVehicle_insurance()+
//                            "\n Truck Type :" + list.truckdetails.get(0).getTruck_type()+
//                            "\n Vehicle Type :" + list.truckdetails.get(0).getVehicle_type()+
//                            "\n Driver_id :" + list.truckdetails.get(0).getDriver_id());

                    //GET DRIVER DETAILS
                    driverList.addAll(list.getDriverDetails());
                    driverListAdapter.updateData(driverList);
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

    public void getTruckDetails(MainResponse.Data.TruckDetails obj) {
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true, false, false, false, null, obj.getTruck_id());
    }

    public void getOnClickPreviewTruckRcBook(MainResponse.Data.TruckDetails obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogRcBook.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogRcBook.show();
        previewDialogRcBook.getWindow().setAttributes(lp);

        String rcBookURL = obj.getRc_book();
        Log.i("IMAGE RC URL", rcBookURL);
        new DownloadImageTask(previewRcBook).execute(rcBookURL);
    }

    public void getOnClickPreviewTruckInsurance(MainResponse.Data.TruckDetails obj) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogInsurance.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogInsurance.show();
        previewDialogInsurance.getWindow().setAttributes(lp);

        String insuranceURL = obj.getVehicle_insurance();
        Log.i("IMAGE INSURANCE URL", insuranceURL);
        new DownloadImageTask(previewInsurance).execute(insuranceURL);
    }

    public void onClickAddTruckDetails(View view) {
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToVehicleDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, false, false, false, false, null, null);
    }

    public void onClickBottomNavigation(View view) {
        switch (view.getId()) {
            case R.id.bottom_nav_sp_dashboard:
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
                JumpTo.goToServiceProviderDashboard(ViewTruckDetailsActivity.this, phone, true, true);
                break;

            case R.id.bottom_nav_customer_dashboard:
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
                JumpTo.goToFindLoadsActivity(ViewTruckDetailsActivity.this, userId, phone, false);

                break;

            case R.id.bottom_nav_track:
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
                JumpTo.goToSPTrackActivity(ViewTruckDetailsActivity.this, phone, false);
                break;

            case R.id.bottom_nav_profile:
                ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
                JumpTo.goToViewPersonalDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToViewPersonalDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, true);
    }

    public void getDriverDetailsOnTruckActivity(MainResponse.Data.TruckDetails obj) {
        truckIdPass = obj.getTruck_id();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDriverDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogDriverDetails.show();
        previewDialogDriverDetails.getWindow().setAttributes(lp);
        previewDialogDriverDetails.setCancelable(true);

        previewDriverDetailsTitle.setText(getString(R.string.Driver_Details));
        previewDriverDetailsDriverLicence.setText(getString(R.string.Driver_Licence));
        previewDriverDetailsDriverSelfie.setText(getString(R.string.Driver_Selfie));
        previewDriverDetailsMessage.setText(getString(R.string.Please_add_a_Driver));
        previewDriverDetailsMessage.setVisibility(View.INVISIBLE);
        String driverIdAPI = obj.getDriver_id();

        if (driverIdAPI.equals("null") || driverIdAPI == null || driverIdAPI.equals("0")) {
            previewDriverDetailsMessage.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverLicence.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverSelfie.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverName.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverNumber.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverEmails.setVisibility(View.INVISIBLE);
            previewDriverDetailsAssignDriverButton.setText(getString(R.string.Assign_Driver));
        } else {
            previewDriverDetailsAssignDriverButton.setText(getString(R.string.ReAssign_Driver));
            previewDriverDetailsMessage.setVisibility(View.INVISIBLE);
            previewDriverDetailsDriverLicence.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverSelfie.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverName.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverNumber.setVisibility(View.VISIBLE);
            previewDriverDetailsDriverEmails.setVisibility(View.VISIBLE);
            getDriverDetailsAssigned(obj.getDriver_id());
        }
    }

    public void getDriverDetailsAssigned(String driverId) {
        //---------------------------- Get Driver Details -------------------------------------------
        String url1 = getString(R.string.baseURL) + "/driver/driverId/" + driverId;
        Log.i("URL: ", url1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    truckList = new ArrayList<>();
                    JSONArray truckLists = response.getJSONArray("data");
                    for (int i = 0; i < truckLists.length(); i++) {
                        JSONObject obj = truckLists.getJSONObject(i);
                        previewDriverDetailsDriverName.setText(obj.getString("driver_name"));

                        String driverNumber = obj.getString("driver_number");
                        String s1 = driverNumber.substring(2, 12);

                        String driverAltNumber = obj.getString("alternate_ph_no");

                        try {
                            String s2 = driverAltNumber.substring(2, 12);
                            previewDriverDetailsDriverNumber.setText("+91 " + s1 + "\n" + "+91 " + s2);
                        } catch (Exception e) {
                            previewDriverDetailsDriverNumber.setText("+91 " + s1);
                        }

                        String driverEmail = obj.getString("driver_emailId");
                        String driverDlURL = obj.getString("upload_dl");
                        String driverSelfieURL = obj.getString("driver_selfie");

                        try {
                            new DownloadImageTask(previewDL).execute(driverDlURL);
                            new DownloadImageTask(previewSelfie).execute(driverSelfieURL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (driverEmail == null) {
                            previewDriverDetailsDriverEmails.setVisibility(View.INVISIBLE);
                        } else {
                            previewDriverDetailsDriverEmails.setVisibility(View.VISIBLE);
                            previewDriverDetailsDriverEmails.setText(driverEmail);
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
        //-------------------------------------------------------------------------------------------
    }

    public void onClickPreviewRcBookAssigned(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogDL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogDL.show();
        previewDialogDL.getWindow().setAttributes(lp);
    }

    public void onClickPreviewInsuranceAssigned(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogSelfie.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogSelfie.show();
        previewDialogSelfie.getWindow().setAttributes(lp);
    }

    public void onClickCloseDialogDriverBankDetails(View view) {
        previewDialogDriverDetails.dismiss();
        RearrangeItems();
    }

    public void onClickReAssignTruck(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(previewDialogSpinner.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        previewDialogSpinner.show();
        previewDialogSpinner.getWindow().setAttributes(lp);
    }

    public void onClickAddDriverDetailsAssigned(View view) {
        previewDialogSpinner.dismiss();
        previewDialogDriverDetails.dismiss();
        ShowAlert.loadingDialog(ViewTruckDetailsActivity.this);
        JumpTo.goToDriverDetailsActivity(ViewTruckDetailsActivity.this, userId, phone, false, false, true, truckIdPass, null);
    }

    public void onClickCancelSelectBind(View view) {
        previewDialogSpinner.dismiss();
        previewDialogDriverDetails.dismiss();
        RearrangeItems();
    }

    public void onClickReAssignDriver(MainResponse.Data.DriverDetails obj) {
        UpdateTruckDetails.updateTruckDriverId(truckIdPass, obj.getDriver_id());
        RearrangeItems();
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
            filter(editable.toString());
        }
    };

    private void filter(String text) {
        ArrayList<MainResponse.Data.TruckDetails> searchVehicleList = new ArrayList<>();

        for (MainResponse.Data.TruckDetails item : truckList) {
            if (item.getVehicle_no().toLowerCase().contains(text.toLowerCase())) {
                searchVehicleList.add(item);
            }
        }
        truckListAdapter.updateData(searchVehicleList);
    }

    public void deleteTruckDetails(MainResponse.Data.TruckDetails obj) {
        //----------------------- Alert Dialog -------------------------------------------------
        Dialog alert = new Dialog(ViewTruckDetailsActivity.this);
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
        TextView alertPositiveButton = (TextView) alert.findViewById(R.id.dialog_alert_positive_button);
        TextView alertNegativeButton = (TextView) alert.findViewById(R.id.dialog_alert_negative_button);

        alertTitle.setText(getString(R.string.Delete_Truck_Details));
        alertMessage.setText(getString(R.string.Truck_Delete_message));
        alertPositiveButton.setText(getString(R.string.yes));
        alertNegativeButton.setText(getString(R.string.no));

        alertPositiveButton.setOnClickListener(view -> {
            alert.dismiss();
            deleteTruckDetails(obj.getTruck_id());
            RearrangeItems();
        });

        alertNegativeButton.setOnClickListener(view -> alert.dismiss());
    }

    private void deleteTruckDetails(String truckId) {
        Call<AddTruckResponse> call = ApiClient.addTruckService().deleteTruckDetails(truckId);
        call.enqueue(new Callback<AddTruckResponse>() {
            @Override
            public void onResponse(Call<AddTruckResponse> call, retrofit2.Response<AddTruckResponse> response) {

            }

            @Override
            public void onFailure(Call<AddTruckResponse> call, Throwable t) {
            }
        });
    }
}