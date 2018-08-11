package com.acme.checkout.domain.model;

public enum PaymentType {

    BOLETO,
    CREDIT_CARD;

    public static PaymentType fromString(String name) {
        for (PaymentType type: PaymentType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

}
