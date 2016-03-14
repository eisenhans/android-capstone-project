package com.gmail.maloef.rememberme.util.dialog;

public class NotEmptyInputValidator implements InputValidator {

    @Override
    public boolean isValid(String input) {
        return input != null && input.trim().length() > 0;
    }
}
