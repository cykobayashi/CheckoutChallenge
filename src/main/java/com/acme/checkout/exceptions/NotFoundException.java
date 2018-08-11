package com.acme.checkout.exceptions;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends ServiceException {

    private static final long serialVersionUID = 7053566393654994274L;

    private final List<String> messagesCode;

    public NotFoundException(final String messageCode) {
        this.messagesCode = Collections.singletonList(messageCode);
    }

    public List<String> getMessagesCode() {
        return messagesCode;
    }

}
