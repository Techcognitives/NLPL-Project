package com.nlpl.services;

import com.nlpl.model.CompanyUpdate;
import com.nlpl.model.CompanyRequest;
import com.nlpl.model.CompanyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyService {

    @POST("/company/create")
    Call<CompanyResponse> saveCompany(@Body CompanyRequest companyRequest);

    @PATCH("/company/{companyId}")
    Call<CompanyUpdate> updateCompanyDetails(@Path ("companyId") String companyId, @Body CompanyUpdate companyUpdate);
}
