package com.gmail.maloef.rememberme.util;

public class StringUtils {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
