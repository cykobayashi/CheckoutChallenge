package com.acme.checkout.exceptions;

public class ServiceException extends RuntimeException {

    /**
     * Default class constructor.
     */
    public ServiceException() {}

    /**
     * Constructor to receive the exception message.
     *
     * @param   message
     *          {@code String} exception message
     */
    public ServiceException(final String message) {
        super(message);
    }

    /**
     * Constructor to receive the exception cause.
     *
     * @param   cause
     *          {@code Throwable} exception cause.
     */
    public ServiceException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor to receive the exception message and cause.
     *
     * @param   message
     *          {@code String} exception message
     *
     * @param   cause
     *          {@code Throwable} exception cause
     */
    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor to receive the message and the cause of an exception
     * and to able the suppression and the writable stack trace.
     *
     * @param   message
     *          {@code String} exception message
     *
     * @param   cause
     *          {@code Throwable} exception cause
     *
     * @param   enableSuppression
     *          {@code boolean} exception suppression
     *
     * @param   writableStackTrace
     *          {@code boolean} exception stack trace
     */
    public ServiceException(final String message, final Throwable cause,
                            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
