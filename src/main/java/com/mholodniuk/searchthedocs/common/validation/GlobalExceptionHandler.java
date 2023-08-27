package com.mholodniuk.searchthedocs.common.validation;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.mholodniuk.searchthedocs.common.utils.CommonUtils.extractAfterLastDot;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        var errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> ErrorMessage.builder()
                        .message(fieldError.getDefaultMessage())
                        .field(fieldError.getField())
                        .invalidValue(fieldError.getRejectedValue())
                        .build())
                .toList();
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail onConstraintValidationException(ConstraintViolationException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        var errors = e.getConstraintViolations()
                .stream()
                .map(fieldError -> ErrorMessage.builder()
                        .message(fieldError.getMessage())
                        .field(extractAfterLastDot(fieldError.getPropertyPath().toString()))
//                        .invalidValue(fieldError.getInvalidValue())
                        .build())
                .toList();
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onThrowable(Exception e) {
        e.printStackTrace();
        log.error("Internal server error" + e.getMessage());
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("disclaimer", "Sorry, we messed up :)");
        return problemDetail;
    }
}
