package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class GoogleTranslateService {

    private static final String BASE_URL = "https://www.googleapis.com";

    private String apiKey;
    GoogleTranslateRestApi restApi;

    public GoogleTranslateService(String apiKey) {
        this.apiKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        restApi = retrofit.create(GoogleTranslateRestApi.class);
    }

    public String translate(String foreignWord, String foreignLanguage, String nativeLanguage) {
        Call<TranslateTextResponse> translateCall = restApi.translate(foreignWord, foreignLanguage, nativeLanguage, "text", apiKey);
        try {
            // ToDo: exception handling
            Response<TranslateTextResponse> response = translateCall.execute();
            logInfo("response: " + response);
            logInfo("response body: " + response.body());
            logInfo("response message: " + response.message());
            TranslateTextResponse translateTextResponse = response.body();

            return translateTextResponse.getTranslation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
