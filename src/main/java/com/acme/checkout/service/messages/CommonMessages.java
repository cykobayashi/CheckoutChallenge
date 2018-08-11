package com.acme.checkout.service.messages;

public interface CommonMessages {

    enum Validations {
        GUID_NULL("service.commons.guid_null");

        private final String code;

        public String getCode() {
            return code;
        }

        Validations(String code) {
            this.code = code;
        }
    }

}
