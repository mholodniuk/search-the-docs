package com.mholodniuk.searchthedocs.management.exception;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;

import java.util.List;

public class InvalidResourceUpdateException extends RuntimeException {
    private final List<ErrorMessage> errors;
    public InvalidResourceUpdateException(String message, List<ErrorMessage> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }
}
