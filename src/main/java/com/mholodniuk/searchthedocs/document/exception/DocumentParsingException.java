package com.mholodniuk.searchthedocs.document.exception;

public class DocumentParsingException extends RuntimeException {
    public DocumentParsingException(Throwable t) {
        super(t);
    }
}
