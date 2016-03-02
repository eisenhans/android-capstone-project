package com.gmail.maloef.rememberme.translate.google;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Example: GET https://www.googleapis.com/language/translate/v2?q=hello&target=de&source=en&key={YOUR_API_KEY}
 */
public interface GoogleTranslateRestInterface {

    @GET("/language/translate/v2")
    Call<GoogleTranslateResponse<Object>> translate(@Query("q") String word, @Query("source") String foreignLanguage, @Query("target") String nativeLanguage, @Query("key") String apiKey);
}
