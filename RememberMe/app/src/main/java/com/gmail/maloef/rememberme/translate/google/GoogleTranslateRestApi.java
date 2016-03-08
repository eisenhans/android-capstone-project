package com.gmail.maloef.rememberme.translate.google;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleTranslateRestApi {

    /**
     * Example: GET https://www.googleapis.com/language/translate/v2?q=hello&target=de&source=en&format=text&key={YOUR_API_KEY}
     */
    @GET("/language/translate/v2")
    Call<TranslateTextResponse> translate(
            @Query("q") String word,
            @Query("source") String foreignLanguage,
            @Query("target") String nativeLanguage,
            @Query("format") String format,
            @Query("key") String apiKey);

    /**
     * Example: GET https://www.googleapis.com/language/translate/v2?q=hello&target=de&format=text&key={YOUR_API_KEY}
     */
    @GET("/language/translate/v2")
    Call<TranslateTextResponse> translate(
            @Query("q") String word,
            @Query("target") String nativeLanguage,
            @Query("format") String format,
            @Query("key") String apiKey);

    /**
     * Example: https://www.googleapis.com/language/translate/v2/detect?q=hello&format=text&key={YOUR_API_KEY}
     */
    @GET("/language/translate/v2/detect")
    Call<DetectLanguageResponse> detect(
            @Query("q") String word,
            @Query("format") String format,
            @Query("key") String apiKey);

    /**
     * Example: https://www.googleapis.com/language/translate/v2/languages?target=en&format=text&key={YOUR_API_KEY}
     */
    @GET("/language/translate/v2/languages")
    Call<AvailableLanguageResponse> availableLanguages(
            @Query("target") String languageNameCode,
            @Query("format") String format,
            @Query("key") String apiKey);

}
