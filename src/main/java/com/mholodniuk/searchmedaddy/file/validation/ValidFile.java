package com.mholodniuk.searchmedaddy.file.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static com.mholodniuk.searchmedaddy.file.validation.Constraints.INVALID_CONTENT_TYPE;

@Documented
@Constraint(validatedBy = FileValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidFile {
    String message() default INVALID_CONTENT_TYPE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}