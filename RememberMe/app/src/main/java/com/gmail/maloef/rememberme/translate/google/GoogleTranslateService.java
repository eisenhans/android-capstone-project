package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.BuildConfig;
import com.gmail.maloef.rememberme.domain.Language;

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

    public Translation translate(String foreignWord, String nativeLanguage) {
        return translate(foreignWord, null, nativeLanguage);
    }

    public Translation translate(String foreignWord, String foreignLanguage, String nativeLanguage) {
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
                return Translation.create("Google doesn't know");
            }

            Translation translation = translateTextResponse.getTranslation();
            logInfo("received translation: " + translation);
            return translation;
        } catch (IOException e) {
            e.printStackTrace();
            return Translation.create(e.getMessage());
        }
    }

    public String detectLanguage(String foreignWord) {
        logInfo("detecting language for word " + foreignWord + ", using apiKey " + apiKey);
        Call<DetectLanguageResponse> detectCall = restApi.detect(foreignWord, "text", apiKey);
        try {
            // ToDo: exception handling
            Response<DetectLanguageResponse> response = detectCall.execute();
            logInfo("response error body: " + response.errorBody());
            logInfo("raw response: " + response.raw());
            DetectLanguageResponse detectLanguageResponse = response.body();

            if (detectLanguageResponse == null) {
                return "Google doesn't know";
            }
            return detectLanguageResponse.getDetectedLanguage();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    // ToDo 07.03.16: delete this
    public Language[] getLanguages(String nameCode) {
        logInfo("looking up available language with name in language " + nameCode + ", using apiKey " + apiKey);
        Call<AvailableLanguageResponse> languageCall = restApi.availableLanguages(nameCode, "text", apiKey);

        try {
            Response<AvailableLanguageResponse> response = languageCall.execute();
            AvailableLanguageResponse availableLanguageResponse = response.body();
            return availableLanguageResponse.getLanguages();
        } catch (IOException e) {
            e.printStackTrace();
            return new Language[0];
        }
    }

    @Override
    public Pair<String, String>[] getCodeLanguagePairs(String nameCode) {
        logInfo("looking up available language with name in language " + nameCode + ", using apiKey " + apiKey);
        Call<AvailableLanguageResponse> languageCall = restApi.availableLanguages(nameCode, "text", apiKey);

        try {
            Response<AvailableLanguageResponse> response = languageCall.execute();
            AvailableLanguageResponse availableLanguageResponse = response.body();
            return availableLanguageResponse.getCodeLanguagePairs();
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair[0];
        }
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
