package com.acme.checkout.exceptions;

import java.util.Collections;
import java.util.List;

public class ValidationException extends ServiceException {

    private final List<String> messagesCode;

    public ValidationException(final String messageCode) {
        this.messagesCode = Collections.singletonList(messageCode);
    }

    public ValidationException(List<String> messagesCode) {
        this.messagesCode = messagesCode;
    }

    public List<String> getMessagesCode() {
        return messagesCode;
    }

}
