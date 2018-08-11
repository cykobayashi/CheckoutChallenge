package com.acme.checkout.domain.model;

import java.util.regex.Pattern;

public enum CreditCardBrand {

    UNKNOWN,
    VISA("^4[0-9]{12}(?:[0-9]{3}){0,2}$"),
    MASTERCARD("^(?:5[1-5]|2(?!2([01]|20)|7(2[1-9]|3))[2-7])\\d{14}$"),
    AMERICAN_EXPRESS("^3[47][0-9]{13}$"),
    HIPERCARD("^606282[0-9]{10}$"),
    DINNERS("3[0-9]{13}"),
    DISCOVER("^6(?:011|[45][0-9]{2})[0-9]{12}$"),
    JCB("^(?:2131|1800|35\\d{3})\\d{11}$"),
    CHINA_UNION_PAY("^62[0-9]{14,17}$");

    private final Pattern pattern;

    CreditCardBrand() {
        this.pattern = null;
    }

    CreditCardBrand(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public static CreditCardBrand detect(Long cardNumber) {

        for (CreditCardBrand cardType : CreditCardBrand.values()) {
            if (null == cardType.pattern) continue;
            if (cardType.pattern.matcher(Long.toString(cardNumber)).matches()) return cardType;
        }

        return UNKNOWN;
    }

}
