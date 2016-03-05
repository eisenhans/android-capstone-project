package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;

import java.util.List;
import java.util.Map;

public class DetectLanguageResponse extends GoogleTranslateResponse<Map<String, List<List<Map<String, String>>>>> {

    // ToDo 05.03.2016: error handling
    public String getDetectedLanguage() {
        List<List<Map<String, String>>> detections = data.get("detections");
        List<Map<String, String>> detectionList = detections.get(0);
        Map<String, String> detection = detectionList.get(0);

        logInfo("detected language: " + detection);
        String language = detection.get("language");

        return language;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
