package com.mholodniuk.searchthedocs.file.dto;

public enum SaveStatus {
    FAILURE("Failed to save file"),
    FULL("Successfully saved file"),
    NO_THUMBNAIL("Failed to generate thumbnail");

    public final String status;

    SaveStatus(String status) {
        this.status = status;
    }
}
