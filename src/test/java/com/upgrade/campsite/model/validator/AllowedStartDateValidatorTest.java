package com.upgrade.campsite.model.validator;

import com.upgrade.campsite.TestHelper;
import com.upgrade.campsite.model.Booking;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AllowedStartDateValidatorTest {

    private TestHelper testHelper = new TestHelper();
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void bookingWithinAllowedStartDateRange_noValidationError(){

        Booking booking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void bookingStartDateOneMonthAhead_noValidationError(){

        Booking booking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(1).plusDays(3));

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void bookingStartDateOneMonthAndOneDayAhead_allowedStartDateThrown(){

        Booking booking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusMonths(1).plusDays(1),
                LocalDate.now().plusMonths(1).plusDays(3));

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertThat(violations.size()).isEqualTo(1);
        ConstraintViolation<Booking> violation = violations.iterator().next();
        assertThat(violation.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(AllowedStartDate.class);
    }


}
