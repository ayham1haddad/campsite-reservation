package com.upgrade.campsite.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import com.upgrade.campsite.exception.BookingDatesNotAvailableException;
import com.upgrade.campsite.exception.BookingNotFoundException;
import com.upgrade.campsite.exception.IllegalBookingStateException;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class BookingControllerExceptionHandler extends ResponseEntityExceptionHandler {

     @ExceptionHandler(BookingNotFoundException.class)
     public ResponseEntity<Object> handleBookingNotFound(BookingNotFoundException exception){
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return constructResponseEntity(apiError);
     }

    @ExceptionHandler({IllegalBookingStateException.class,
            BookingDatesNotAvailableException.class,
            IllegalArgumentException.class})
    protected ResponseEntity<Object> handleBookingDatesNotAvailable(RuntimeException ex) {

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return constructResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

         List<String> errorDetails = ex.getBindingResult().getFieldErrors()
                 .stream()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .collect(Collectors.toList());

        errorDetails.addAll(ex.getBindingResult().getGlobalErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList()));

         ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message("Error in validating the booking submitted")
                 .errorDetails(errorDetails)
                .build();
        return constructResponseEntity(apiError);
    }
    private ResponseEntity<Object> constructResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
@Data
@Builder
class ApiError {

    private HttpStatus status;

    private LocalDateTime timestamp;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errorDetails;
}