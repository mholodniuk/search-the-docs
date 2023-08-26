package com.mholodniuk.searchthedocs.file.validation;

import com.mholodniuk.searchthedocs.file.validation.dto.ValidationErrorResponse;
import com.mholodniuk.searchthedocs.file.validation.dto.Violation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;


@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<?> onConstraintValidationException(ConstraintViolationException e) {
        var error = new ValidationErrorResponse(new ArrayList<>());
        for (var violation : e.getConstraintViolations()) {
            error.violations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
