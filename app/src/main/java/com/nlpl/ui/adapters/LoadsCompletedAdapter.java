package com.nlpl.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nlpl.R;
import com.nlpl.model.ModelForRecyclerView.BidsReceivedModel;
import com.nlpl.ui.activities.CustomerLoadsHistoryActivity;
import com.nlpl.utils.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadsCompletedAdapter extends RecyclerView.Adapter<LoadsCompletedAdapter.LoadCompletedViewHolder> {

    private ArrayList<BidsReceivedModel> completedLoadList;
    private CustomerLoadsHistoryActivity activity;
    private RequestQueue mQueue;
    ArrayList<String> arraySpUserId, arrayBidId, arrayBidStatus;
    String fianlBidId, finalSpUserId;

    public LoadsCompletedAdapter(CustomerLoadsHistoryActivity activity, ArrayList<BidsReceivedModel> completedLoadList) {
        this.completedLoadList = completedLoadList;
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public LoadsCompletedAdapter.LoadCompletedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        arrayBidId = new ArrayList<>();
        arrayBidStatus = new ArrayList<>();
        arraySpUserId = new ArrayList<>();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loads_completed_list, parent, false);
        return new LoadsCompletedAdapter.LoadCompletedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadsCompletedAdapter.LoadCompletedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BidsReceivedModel obj = completedLoadList.get(position);

        holder.city.setText(obj.getPick_city() + " - " + obj.getDrop_city());
        holder.budget.setText(obj.getBudget());

        String url3 = activity.getString(R.string.baseURL) + "/spbid/getBidDtByPostId/" + obj.getIdpost_load();
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

                    //----------------------------------------------------------
                    String url2 = activity.getString(R.string.baseURL) + "/user/" + finalSpUserId;
                    JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray truckLists = response.getJSONArray("data");
                                for (int i = 0; i < truckLists.length(); i++) {
                                    JSONObject obj1 = truckLists.getJSONObject(i);
                                    holder.spName.setText(obj1.getString("name"));
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
                    mQueue.add(request2);
                    //----------------------------------------------------------

                    //-----------------------------------------------------------------
                    String url1 = activity.getString(R.string.baseURL) + "/imgbucket/Images/" + finalSpUserId;
                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray imageList = response.getJSONArray("data");
                                for (int i = 0; i < imageList.length(); i++) {
                                    JSONObject obj = imageList.getJSONObject(i);
                                    String imageType = obj.getString("image_type");

                                    String profileImgUrl;
                                    if (imageType.equals("profile")) {
                                        profileImgUrl = obj.getString("image_url");
                                        new DownloadImageTask(holder.profilePictureSP).execute(profileImgUrl);
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
                    //-----------------------------------------------------------------------


//                            //----------------------------------------------------------
//                            String url = activity.getString(R.string.baseURL) + "/spbid/bidDtByBidId/" + fianlBidId;
//                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        JSONArray truckLists = response.getJSONArray("data");
//                                        for (int i = 0; i < truckLists.length(); i++) {
//                                            JSONObject obj1 = truckLists.getJSONObject(i);
//                                            String bid_status = obj1.getString("bid_status");
//
//                                        }
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }, new com.android.volley.Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    error.printStackTrace();
//                                }
//                            });
//
//                            mQueue.add(request);
//                            //----------------------------------------------------------

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

        holder.profilePictureSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.ViewProfileOfSPToCustomer(obj);
            }
        });

        holder.acceptViewBidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.ViewLoadDetails(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return completedLoadList.size();
    }

    public void updateData(ArrayList<BidsReceivedModel> completedLoadList) {
        this.completedLoadList = completedLoadList;
        notifyDataSetChanged();
    }

    public class LoadCompletedViewHolder extends RecyclerView.ViewHolder {
        private TextView spName, city, budget, acceptViewBidButton;
        private ImageView profilePictureSP;

        public LoadCompletedViewHolder(@NonNull View itemView) {
            super(itemView);
            spName = itemView.findViewById(R.id.loads_completed_sp_name);
            city = itemView.findViewById(R.id.loads_completed_pick_city);
            budget = itemView.findViewById(R.id.loads_completed_budget_sp);
            acceptViewBidButton = itemView.findViewById(R.id.load_completed_view_load_history);
            profilePictureSP = itemView.findViewById(R.id.loads_completed_sp_profilePhto);
        }

    }
}
