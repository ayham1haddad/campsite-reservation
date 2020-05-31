package com.upgrade.campsite.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MaximumDaysStayValidator.class})
@Documented
public @interface MaximumDaysStay {

    String message() default "Booking stay length must be less or equal to three days";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
