package com.mholodniuk.searchthedocs.management.exception;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class InvalidResourceUpdateException extends RuntimeException {
    private final List<ErrorMessage> errors;
    public InvalidResourceUpdateException(String message, List<ErrorMessage> errors) {
        super(message);
        this.errors = errors;
    }

}
