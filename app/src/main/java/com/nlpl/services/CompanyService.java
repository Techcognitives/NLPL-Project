package com.nlpl.services;

import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyAddress;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyCity;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyGSTNumber;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyName;
import com.nlpl.model.Requests.CompanyRequest;
import com.nlpl.model.Responses.CompanyResponse;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyPAN;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyState;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyType;
import com.nlpl.model.UpdateModel.Models.UpdateCompanyDetails.UpdateCompanyZip;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CompanyService {

    @POST("/company/create")
    Call<CompanyResponse> saveCompany(@Body CompanyRequest companyRequest);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyName> updateCompanyName(@Path ("companyId") String companyId, @Body UpdateCompanyName updateCompanyName);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyGSTNumber> updateCompanyGSTNumber(@Path ("companyId") String companyId, @Body UpdateCompanyGSTNumber updateCompanyGSTNumber);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyPAN> updateCompanyPAN(@Path ("companyId") String companyId, @Body UpdateCompanyPAN updateCompanyPAN);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyState> updateCompanyState(@Path ("companyId") String companyId, @Body UpdateCompanyState updateCompanyState);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyCity> updateCompanyCity(@Path ("companyId") String companyId, @Body UpdateCompanyCity updateCompanyCity);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyZip> updateCompanyZip(@Path ("companyId") String companyId, @Body UpdateCompanyZip updateCompanyZip);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyAddress> updateCompanyAddress(@Path ("companyId") String companyId, @Body UpdateCompanyAddress updateCompanyAddress);

    @PUT("/company/{companyId}")
    Call<UpdateCompanyType> updateCompanyType(@Path ("companyId") String companyId, @Body UpdateCompanyType updateCompanyType);
}
