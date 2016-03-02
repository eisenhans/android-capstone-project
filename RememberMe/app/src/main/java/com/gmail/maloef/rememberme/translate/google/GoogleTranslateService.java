package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class GoogleTranslateService {

    private static final String BASE_URL = "https://www.googleapis.com";

    private String apiKey;
    GoogleTranslateRestInterface restInterface;

    public GoogleTranslateService(String apiKey) {
        this.apiKey = apiKey;
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();

        restInterface = retrofit.create(GoogleTranslateRestInterface.class);
    }

    public String translate(String foreignWord, String foreignLanguage, String nativeLanguage) {
        Call<GoogleTranslateResponse<Object>> translateCall = restInterface.translate(foreignWord, foreignLanguage, nativeLanguage, apiKey);
        try {
            Response<GoogleTranslateResponse<Object>> response = translateCall.execute();
            logInfo("response: " + response);
            logInfo("response body: " + response.body());
            logInfo("response message: " + response.message());
            GoogleTranslateResponse<Object> responseBody = response.body();
            List<Object> results = responseBody.results;
            logInfo("results: " + results);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
