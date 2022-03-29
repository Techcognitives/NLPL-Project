package com.nlpl.services;

import com.nlpl.model.Responses.AadharIdResponse;
import com.nlpl.model.Responses.AadharInfoResponse;
import com.nlpl.model.Responses.BankVerificationResponse;
import com.nlpl.model.Responses.DLVerificationResponse;
import com.nlpl.model.Responses.PANVerificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VerificationService {
    @GET("/userPan/panDt/{userId}/{panId}")
    Call<PANVerificationResponse> checkPAN(@Path("userId") String userId, @Path("panId") String panId);

    @GET("/userBankAc/bankDetails/{userId}/{accountNumber}/{ifscCode}")
    Call<BankVerificationResponse> checkBankDetail(@Path("userId") String userId, @Path("accountNumber") String accountNumber, @Path("ifscCode") String ifscCode);

    @GET("/userDl/dlDt/{userId}/{dlNumber}/{dob}")
    Call<DLVerificationResponse> checkDL(@Path("userId") String userId, @Path("dlNumber") String dlNumber, @Path("dob") String dob);

    @GET("/userAadhaar/aadhaarDetails/{userId}/{userAadhaar}")
    Call<AadharIdResponse> checkAadhar(@Path("userId") String userId, @Path("userAadhaar") String userAadhaar);

    @GET("/aadhaar/sendAadhaarOTP/{userId}/{userAadhaar}/{referenceId}/{codeOTP}")
    Call<AadharInfoResponse> checkAadharWithOTP(@Path("userId") String userId, @Path("userAadhaar") String userAadhaar, @Path("referenceId") String referenceId, @Path("codeOTP") String codeOTP);
}
