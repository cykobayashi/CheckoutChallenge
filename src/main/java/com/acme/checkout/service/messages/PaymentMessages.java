package com.acme.checkout.service.messages;

public interface PaymentMessages {

    enum Validations {
        PAYMENT_NOT_FOUND("payments.validations.not_found"),
        PAYMENT_TYPE_NOT_SUPPORTED("payments.validations.type_not_supported"),
        BUYER_IS_EMPTY("payments.validations.buyer_is_empty"),
        PAYMENT_IS_EMPTY("payments.validations.payment_is_empty"),
        PAYMENT_TYPE_IS_EMPTY_OR_INVALID("payments.validations.payment_type_is_empty_or_invalid"),
        AMOUNT_IS_EMPTY("payments.validations.amount_is_empty"),

        BUYER_CPF_EMPTY_OR_INVALID("payments.validations.buyer_cpf_empty_or_invalid"),
        BUYER_NAME_EMPTY_OR_INVALID("payments.validations.buyer_name_empty_or_invalid"),
        BUYER_EMAIL_EMPTY_OR_INVALID("payments.validations.buyer_email_empty_or_invalid"),
        ZERO_OR_NEGATIVE_AMOUNT("payments.validations.zero_or_negative_amount"),
        CREDIT_CARD_MUST_BE_NULL_FOR_BOLETO("payments.validations.credit_card_must_be_null_for_boleto"),

        CREDIT_CARD_EMPTY("payments.validations.credit_card_empty"),
        CREDIT_CARD_EXPIRATION_EMPTY("payments.validations.credit_card_expiration_empty"),
        CREDIT_CARD_HOLDER_NAME_EMPTY("payments.validations.credit_card_holder_name_empty"),
        CREDIT_CARD_NUMBER_EMPTY_OR_INVALID("payments.validations.credit_card_number_empty_or_invalid"),
        CREDIT_CARD_CVV_EMPTY("payments.validations.credit_card_cvv_empty"),

        CREDIT_CARD_BRAND_NOT_SUPPORTED("payments.validations.credit_card_brand_not_supported"),

        INVALID_EXPIRATION_DATE_FORMAT("payments.validations.invalid_expiration_date_format");

        public String getCode() {
            return code;
        }

        private final String code;

        Validations(String code) {
            this.code = code;
        }
    }

    enum Errors {

        INVALID_CUSTOMER_GUID("payments.errors.invalid_customer_guid");

        public String getCode() {
            return code;
        }

        private final String code;

        Errors(String code) {
            this.code = code;
        }

    }

}
