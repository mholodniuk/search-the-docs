package com.mholodniuk.searchthedocs.management.document.dto;

public record UpdateDocumentRequest(
        String name,
        Long roomId,
        Long ownerId
) {
}
