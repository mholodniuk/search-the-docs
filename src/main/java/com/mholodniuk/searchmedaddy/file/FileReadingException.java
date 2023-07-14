package com.mholodniuk.searchmedaddy.file;


public class FileReadingException extends RuntimeException {
    public FileReadingException(Throwable throwable) {
        super(throwable);
    }
}
