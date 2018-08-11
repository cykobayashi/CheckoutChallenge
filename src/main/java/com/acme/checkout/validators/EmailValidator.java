package com.acme.checkout.validators;

import org.springframework.util.StringUtils;

public class EmailValidator {

    private static final String PATTERN_MAIL
            = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isValid(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return email.matches(PATTERN_MAIL);
    }

}