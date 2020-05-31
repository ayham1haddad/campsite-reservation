package com.upgrade.campsite.exception;

public class BookingDatesNotAvailableException extends RuntimeException{
    public BookingDatesNotAvailableException(String message){
        super(message);
    }
}
