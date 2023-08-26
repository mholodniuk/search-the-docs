package com.mholodniuk.searchthedocs.file.exception;


public class FileReadingException extends RuntimeException {
    public FileReadingException(Throwable throwable) {
        super(throwable);
    }
}
