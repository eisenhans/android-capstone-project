package com.gmail.maloef.rememberme.domain;

public class VocabularyBox {

    static final int TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE = 0;
    static final int TRANSLATION_DIRECTION_NATIVE_TO_FOREIGN = 1;
    static final int TRANSLATION_DIRECTION_MIXED = 2;

    String name;
    String nativeLanguage;
    String foreignLanguage;
    int translationDirection;

}
