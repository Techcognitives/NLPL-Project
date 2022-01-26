package com.nlpl.services;

import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.PostLoadResponse;
import com.nlpl.model.UpdateCustomerBudget;
import com.nlpl.model.UpdateLoadPost.UpdateLoadPostPickUpDate;
import com.nlpl.model.UpdateLoadStatusSubmitted;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostLoadService {
    @POST("/loadpost/postAload")
    Call<PostLoadResponse> saveLoad(@Body PostLoadRequest postLoadRequest);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadStatusSubmitted> updateBidStatusSubmitted(@Path("loadId") String loadId, @Body UpdateLoadStatusSubmitted updateLoadStatusSubmitted);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPostPickUpDate> updateLoadPost(@Path("loadId") String loadId, @Body UpdateLoadPostPickUpDate updateLoadPost);

    @PUT("/spbid/updateBidByBID/{loadId}")
    Call<UpdateCustomerBudget> updateCustomerBudget(@Path("loadId") String loadId, @Body UpdateCustomerBudget updateCustomerBudget);

}
