package com.mholodniuk.searchthedocs.common;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceCreationException;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceDeletionException;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

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
                        .invalidValue(parseInvalidValue(fieldError.getInvalidValue()))
                        .build())
                .toList();
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    private String parseInvalidValue(Object value) {
        if (value instanceof MultipartFile file) {
            return file.getOriginalFilename();
        }
        return value.toString();
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

    @ExceptionHandler(InvalidResourceCreationException.class)
    public ProblemDetail onInvalidResourceCreationException(InvalidResourceCreationException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("message", e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(InvalidResourceDeletionException.class)
    public ProblemDetail onInvalidResourceDeletionException(InvalidResourceDeletionException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("message", e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("message", "Incorrect argument format: '%s'".formatted(e.getValue()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail onHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid request");
        problemDetail.setProperty("message", e.getMessage());
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
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail onAuthenticationException(AuthenticationException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Unauthorized");
        problemDetail.setProperty("message", e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail onAccessDeniedException(AccessDeniedException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Forbidden");
        problemDetail.setProperty("message", e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail onBadCredentialsException(BadCredentialsException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad request");
        problemDetail.setProperty("message", e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
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
