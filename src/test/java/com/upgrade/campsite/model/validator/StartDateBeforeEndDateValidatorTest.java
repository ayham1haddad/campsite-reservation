package com.upgrade.campsite.model.validator;

import com.upgrade.campsite.TestHelper;
import com.upgrade.campsite.model.Booking;
import org.junit.Before;
import org.junit.Test;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class StartDateBeforeEndDateValidatorTest {


    private TestHelper testHelper = new TestHelper();
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void startDateBeforeEndDate_noValidationErrors(){

        Booking booking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void startDateEqualsEndDate_startDateBeforeEndDateThrown(){

        Booking booking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(2));

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertThat(violations.size()).isEqualTo(1);
        ConstraintViolation<Booking> violation = violations.iterator().next();
        assertThat(violation.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(StartDateBeforeEndDate.class);

    }


    @Test
    public void startDateAfterEndDate_startDateBeforeEndDateThrown(){

        Booking booking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(2));

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertThat(violations.size()).isEqualTo(1);
        ConstraintViolation<Booking> violation = violations.iterator().next();
        assertThat(violation.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(StartDateBeforeEndDate.class);

    }




}
