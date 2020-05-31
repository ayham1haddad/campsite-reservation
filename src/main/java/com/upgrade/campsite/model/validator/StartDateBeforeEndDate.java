package com.upgrade.campsite.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StartDateBeforeEndDateValidator.class})
@Documented
public @interface StartDateBeforeEndDate {

    String message() default "Booking start date must be before end date";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
