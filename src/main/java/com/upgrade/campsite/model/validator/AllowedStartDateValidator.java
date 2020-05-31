package com.upgrade.campsite.model.validator;

import com.upgrade.campsite.model.Booking;
import static com.upgrade.campsite.constant.BookingConstant.*;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AllowedStartDateValidator
   implements ConstraintValidator<AllowedStartDate, Booking> {

    @Override
    public void initialize(AllowedStartDate constraintAnnotation) {

    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
        if(booking == null){
            return false;
        }
        return LocalDate.now().isBefore(booking.getStartDate())
                && booking.getStartDate().isBefore(LocalDate.now().plusMonths(MAX_MONTHS_AHEAD_OF_ARRIVAL).plusDays(MIN_DAYS_AHEAD_OF_ARRIVAL));
    }
}
