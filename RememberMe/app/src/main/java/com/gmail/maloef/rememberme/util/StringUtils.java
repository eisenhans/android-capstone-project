package com.gmail.maloef.rememberme.util;

public class StringUtils {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}
