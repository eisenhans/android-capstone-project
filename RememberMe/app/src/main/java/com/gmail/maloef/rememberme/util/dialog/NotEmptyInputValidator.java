package com.gmail.maloef.rememberme.util.dialog;

import com.gmail.maloef.rememberme.util.StringUtils;

public class NotEmptyInputValidator implements InputValidator {

    @Override
    public boolean isValid(String input) {
        return StringUtils.isNotBlank(input);
    }
}
