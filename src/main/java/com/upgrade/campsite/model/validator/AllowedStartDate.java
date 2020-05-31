package com.upgrade.campsite.model.validator;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AllowedStartDateValidator.class})
@Documented
public @interface AllowedStartDate {

    String message() default "Booking start date must be 1 day ahead of arrival and up to 1 month in advance";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
