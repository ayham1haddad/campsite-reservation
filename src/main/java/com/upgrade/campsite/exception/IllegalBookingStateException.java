package com.upgrade.campsite.exception;

public class IllegalBookingStateException extends RuntimeException{
    public IllegalBookingStateException(String message) {
        super(message);
    }
}

