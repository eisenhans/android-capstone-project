package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.BuildConfig;

import java.io.IOException;

import javax.inject.Inject;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class GoogleTranslateService implements LanguageProvider {

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

    public Translation translate(String foreignWord, String nativeLanguage) throws IOException {
        return translate(foreignWord, null, nativeLanguage);
    }

    public Translation translate(String foreignWord, String foreignLanguage, String nativeLanguage) throws IOException {
        logInfo("translating word " + foreignWord + " from " + foreignLanguage + " to " + nativeLanguage + ", using apiKey " + apiKey);
        Call<TranslateTextResponse> translateCall = restApi.translate(foreignWord, foreignLanguage, nativeLanguage, "text", apiKey);

        long start = System.currentTimeMillis();
        Response<TranslateTextResponse> response = translateCall.execute();
        long end = System.currentTimeMillis();
        logInfo("response: " + response + ", duration " + (end - start) + " ms");
        logInfo("response message: " + response.message());
        logInfo("response body: " + response.body());
        logInfo("response error body: " + response.errorBody());
        logInfo("raw response: " + response.raw());
        TranslateTextResponse translateTextResponse = response.body();

        if (translateTextResponse == null) {
            return Translation.create("Google doesn't know");
        }

        Translation translation = translateTextResponse.getTranslation();
        logInfo("received translation: " + translation);
        return translation;
    }

    @Override
    public Pair<String, String>[] getLanguages(String nameCode) throws IOException {
        logInfo("looking up available language with name in language " + nameCode + ", using apiKey " + apiKey);
        Call<AvailableLanguageResponse> languageCall = restApi.availableLanguages(nameCode, "text", apiKey);

        long start = System.currentTimeMillis();
        Response<AvailableLanguageResponse> response = languageCall.execute();
        long end = System.currentTimeMillis();
        logInfo("response: " + response + ", duration " + (end - start) + " ms");
        AvailableLanguageResponse availableLanguageResponse = response.body();
        return availableLanguageResponse.getCodeLanguagePairs();
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
