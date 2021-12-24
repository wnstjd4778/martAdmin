package com.spring.martadmin.advice.exception;

public class SMSException  extends RuntimeException{
    public SMSException() {
        super();
    }

    public SMSException(String message) {
        super(message);
    }

    public SMSException(String message, Throwable cause) {
        super(message, cause);
    }

    public SMSException(Throwable cause) {
        super(cause);
    }

    protected  SMSException(String message, Throwable cause, boolean enableSuppression, boolean wirtableStackTrace) {
        super(message, cause, enableSuppression, wirtableStackTrace);
    }
}
