package com.upgrade.campsite.model.validator;
import com.upgrade.campsite.model.Booking;
import static com.upgrade.campsite.constant.BookingConstant.*;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class MaximumDaysStayValidator
        implements ConstraintValidator<MaximumDaysStay, Booking> {
    @Override
    public void initialize(MaximumDaysStay constraintAnnotation) {

    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
        if(booking == null){
            return false;
        }
        return Period.between(booking.getStartDate(), booking.getEndDate()).getDays() <= MAX_ALLOWED_DAYS_TO_STAY;
    }
}
