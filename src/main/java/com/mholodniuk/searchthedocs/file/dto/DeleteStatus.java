package com.mholodniuk.searchthedocs.file.dto;

public enum DeleteStatus {
    FAILURE("Failed to delete file"),
    SUCCESS("Successfully deleted file");

    public final String status;

    DeleteStatus(String status) {
        this.status = status;
    }
}
