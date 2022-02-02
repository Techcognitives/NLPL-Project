package com.nlpl.services;

import com.nlpl.model.Requests.PostLoadRequest;
import com.nlpl.model.Responses.PostLoadResponse;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateCustomerNoteForSP;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateCustomerBudget;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadBodyType;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadCapacity;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropAdd;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropCity;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropCountry;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropPinCode;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadDropState;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadFeet;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadKmApprox;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickAdd;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickCity;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickCountry;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickPinCode;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPickState;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPostPickUpDate;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadPostPickUpTime;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadStatusSubmitted;
import com.nlpl.model.UpdateModel.Models.UpdateLoadPost.UpdateLoadVehicleModel;

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
    Call<UpdateLoadPostPickUpDate> updateLoadPostPickUpDate(@Path("loadId") String loadId, @Body UpdateLoadPostPickUpDate updateLoadPost);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPostPickUpTime> updateLoadPostPickUpTime(@Path("loadId") String loadId, @Body UpdateLoadPostPickUpTime updateLoadPostPickUpTime);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateCustomerBudget> updateCustomerBudget(@Path("loadId") String loadId, @Body UpdateCustomerBudget updateCustomerBudget);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadVehicleModel> updateLoadVehicleModel(@Path("loadId") String loadId, @Body UpdateLoadVehicleModel updateLoadVehicleModel);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadFeet> updateLoadFeet(@Path("loadId") String loadId, @Body UpdateLoadFeet updateLoadFeet);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadCapacity> updateLoadCapacity(@Path("loadId") String loadId, @Body UpdateLoadCapacity updateLoadCapacity);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadBodyType> updateLoadBodyType(@Path("loadId") String loadId, @Body UpdateLoadBodyType updateLoadBodyType);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPickAdd> updateLoadPickAdd(@Path("loadId") String loadId, @Body UpdateLoadPickAdd updateLoadPickAdd);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPickPinCode> updateLoadPickPinCode(@Path("loadId") String loadId, @Body UpdateLoadPickPinCode updateLoadPickPinCode);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPickState> updateLoadPickState(@Path("loadId") String loadId, @Body UpdateLoadPickState updateLoadPickState);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPickCity> updateLoadPickCity(@Path("loadId") String loadId, @Body UpdateLoadPickCity updateLoadPickCity);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadDropAdd> updateLoadDropAdd(@Path("loadId") String loadId, @Body UpdateLoadDropAdd updateLoadDropAdd);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadDropPinCode> updateLoadDropPinCode(@Path("loadId") String loadId, @Body UpdateLoadDropPinCode updateLoadDropPinCode);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadDropState> updateLoadDropState(@Path("loadId") String loadId, @Body UpdateLoadDropState updateLoadDropState);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadDropCity> updateLoadDropCity(@Path("loadId") String loadId, @Body UpdateLoadDropCity updateLoadDropCity);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadKmApprox> updateLoadKmApprox(@Path("loadId") String loadId, @Body UpdateLoadKmApprox updateLoadKmApprox);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadDropCountry> updateLoadDropCountry(@Path("loadId") String loadId, @Body UpdateLoadDropCountry updateLoadDropCountry);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateLoadPickCountry> updateLoadPickCountry(@Path("loadId") String loadId, @Body UpdateLoadPickCountry updateLoadPickCountry);

    @PUT("/loadpost/updatePostByPID/{loadId}")
    Call<UpdateCustomerNoteForSP> updateCustomerNoteForSP(@Path("loadId") String bidId, @Body UpdateCustomerNoteForSP updateCustomerNoteForSP);

}
