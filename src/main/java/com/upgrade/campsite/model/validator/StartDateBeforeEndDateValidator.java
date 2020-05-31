package com.upgrade.campsite.model.validator;

import com.upgrade.campsite.model.Booking;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartDateBeforeEndDateValidator
        implements ConstraintValidator<StartDateBeforeEndDate, Booking> {
    @Override
    public void initialize(StartDateBeforeEndDate constraintAnnotation) {

    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
        if(booking == null){
            return false;
        }
        return booking.getStartDate().isBefore(booking.getEndDate());
    }
}
