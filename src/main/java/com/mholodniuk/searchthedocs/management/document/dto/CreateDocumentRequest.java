package com.mholodniuk.searchthedocs.management.document.dto;

import lombok.Builder;

@Builder
public record CreateDocumentRequest(
        String name,
        String contentType,
        String filePath,
        String storage,
        Long roomId,
        Long ownerId
) {
}
