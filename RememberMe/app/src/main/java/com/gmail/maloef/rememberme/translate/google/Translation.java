package com.gmail.maloef.rememberme.translate.google;

public class Translation {

    public String detectedSourceLanguage;
    public String translatedText;

    public static Translation create(String detectedSourceLanguage, String translatedText) {
        return new Translation(detectedSourceLanguage, translatedText);
    }

    public static Translation create(String translatedText) {
        return new Translation(null, translatedText);
    }

    private Translation(String detectedSourceLanguage, String translatedText) {
        this.detectedSourceLanguage = detectedSourceLanguage;
        this.translatedText = translatedText;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "detectedSourceLanguage='" + detectedSourceLanguage + '\'' +
                ", translatedText='" + translatedText + '\'' +
                '}';
    }
}
