package com.nlpl.services;

import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyAddress;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyCity;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyGSTNumber;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyName;
import com.nlpl.model.Requests.CompanyRequest;
import com.nlpl.model.Responses.CompanyResponse;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyPAN;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyState;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyType;
import com.nlpl.model.UpdateCompanyDetails.UpdateCompanyZip;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CompanyService {

    @POST("/company/create")
    Call<CompanyResponse> saveCompany(@Body CompanyRequest companyRequest);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyName> updateCompanyName(@Path ("companyId") String companyId, @Body UpdateCompanyName updateCompanyName);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyGSTNumber> updateCompanyGSTNumber(@Path ("companyId") String companyId, @Body UpdateCompanyGSTNumber updateCompanyGSTNumber);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyPAN> updateCompanyPAN(@Path ("companyId") String companyId, @Body UpdateCompanyPAN updateCompanyPAN);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyState> updateCompanyState(@Path ("companyId") String companyId, @Body UpdateCompanyState updateCompanyState);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyCity> updateCompanyCity(@Path ("companyId") String companyId, @Body UpdateCompanyCity updateCompanyCity);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyZip> updateCompanyZip(@Path ("companyId") String companyId, @Body UpdateCompanyZip updateCompanyZip);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyAddress> updateCompanyAddress(@Path ("companyId") String companyId, @Body UpdateCompanyAddress updateCompanyAddress);

    @PATCH("/company/{companyId}")
    Call<UpdateCompanyType> updateCompanyAddress(@Path ("companyId") String companyId, @Body UpdateCompanyType updateCompanyType);
}
