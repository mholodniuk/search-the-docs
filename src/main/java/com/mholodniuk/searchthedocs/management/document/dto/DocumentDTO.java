package com.mholodniuk.searchthedocs.management.document.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DocumentDTO(
        UUID id,
        String name,
        List<String> tags,
        String contentType,
        String filePath,
        String storage,
        LocalDateTime uploadedAt
) {
}
