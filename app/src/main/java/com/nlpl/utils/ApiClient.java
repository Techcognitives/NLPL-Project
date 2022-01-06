package com.nlpl.utils;

import com.nlpl.services.AddDriverService;
import com.nlpl.services.AddTruckService;
import com.nlpl.services.BankService;
import com.nlpl.services.CompanyService;
import com.nlpl.services.ImageService;
import com.nlpl.services.ImageUploadService;
import com.nlpl.services.UploadChequeService;
import com.nlpl.services.UploadDriverLicenseService;
import com.nlpl.services.UploadTruckRCService;
import com.nlpl.services.UserService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static String BASE_URL = "http://13.234.163.179:3000";

    private static Retrofit getRetrofit() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static UserService getUserService() {
        UserService userService = getRetrofit().create(UserService.class);
        return userService;
    }
    public static BankService getBankService() {
        BankService bankService = getRetrofit().create(BankService.class);
        return bankService;
    }
    public static AddTruckService addTruckService() {
        AddTruckService addTruckService = getRetrofit().create(AddTruckService.class);
        return addTruckService;
    }
    public static AddDriverService addDriverService() {
        AddDriverService addDriverService = getRetrofit().create(AddDriverService.class);
        return addDriverService;
    }
    public static CompanyService getCompanyService() {
        CompanyService companyService = getRetrofit().create(CompanyService.class);
        return companyService;
    }

    public static ImageService getImageService() {
        ImageService imageService = getRetrofit().create(ImageService.class);
        return imageService;
    }
    public static ImageUploadService getImageUploadService() {
        ImageUploadService imageUploadService = getRetrofit().create(ImageUploadService.class);
        return imageUploadService;
    }

    public static UploadChequeService getUploadChequeService() {
        UploadChequeService uploadChequeService = getRetrofit().create(UploadChequeService.class);
        return uploadChequeService;
    }

    public static UploadDriverLicenseService getUploadDriverLicenseService() {
        UploadDriverLicenseService uploadDriverLicenseService = getRetrofit().create(UploadDriverLicenseService.class);
        return uploadDriverLicenseService;
    }

    public static UploadTruckRCService getTruckRCService() {
        UploadTruckRCService uploadTruckRCService = getRetrofit().create(UploadTruckRCService.class);
        return uploadTruckRCService;
    }
}
