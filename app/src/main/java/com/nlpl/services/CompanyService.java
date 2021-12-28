package com.nlpl.services;

import com.nlpl.model.CompanyUpdate;
import com.nlpl.model.CompanyRequest;
import com.nlpl.model.CompanyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CompanyService {

    @POST("/company/create")
    Call<CompanyResponse> saveCompany(@Body CompanyRequest companyRequest);

    @PUT("/company/")
    Call<CompanyUpdate> updateCompanyDetails(@Query("id") String id, @Body CompanyUpdate companyUpdate);
}
