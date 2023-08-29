package com.mholodniuk.searchthedocs.common.validation;

import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail onResourceNotFoundException(ResourceNotFoundException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Resource Not found");
        problemDetail.setProperty("message", e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(InvalidResourceUpdateException.class)
    public ProblemDetail onInvalidResourceUpdateException(InvalidResourceUpdateException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Cannot perform update of a resource");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("errors", e.getErrors());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("message", "Incorrect argument type: '%s'".formatted(e.getValue()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    // should I return low level sql exception to the client (???)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail onDataIntegrityViolationException(DataIntegrityViolationException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Cannot process");
        if (e.getMostSpecificCause() instanceof PSQLException psqlException) {
            var serverErrorMessage = psqlException.getServerErrorMessage();
            problemDetail.setProperty("message", serverErrorMessage != null ? serverErrorMessage.getDetail() : "Unknown");
        }
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onThrowable(Exception e) {
        e.printStackTrace();
        log.error("Internal server error " + e.getMessage());
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("disclaimer", "Sorry, we messed up :)");
        return problemDetail;
    }
}