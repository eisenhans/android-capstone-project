package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;

import com.gmail.maloef.rememberme.BuildConfig;

import java.io.IOException;

import javax.inject.Inject;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class GoogleTranslateService {

    private static final String BASE_URL = "https://www.googleapis.com";

    private String apiKey;
    GoogleTranslateRestApi restApi;

    @Inject
    public GoogleTranslateService() {
        this(BuildConfig.ANDROID_TRANSLATE_API_KEY);
    }

    /**
     * Used by tests: there we have to use a different apiKey
     */
    protected GoogleTranslateService(String apiKey) {
        this.apiKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        restApi = retrofit.create(GoogleTranslateRestApi.class);
    }

    public String translate(String foreignWord, String foreignLanguage, String nativeLanguage) {
        logInfo("translating word " + foreignWord + " from " + foreignLanguage + " to " + nativeLanguage + ", using apiKey " + apiKey);
        Call<TranslateTextResponse> translateCall = restApi.translate(foreignWord, foreignLanguage, nativeLanguage, "text", apiKey);
        try {
            // ToDo: exception handling
            Response<TranslateTextResponse> response = translateCall.execute();
            logInfo("response: " + response);
            logInfo("response message: " + response.message());
            logInfo("response body: " + response.body());
            logInfo("response error body: " + response.errorBody());
            logInfo("raw response: " + response.raw());
            TranslateTextResponse translateTextResponse = response.body();

            if (translateTextResponse == null) {
                return "Google doesn't know";
            }

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
