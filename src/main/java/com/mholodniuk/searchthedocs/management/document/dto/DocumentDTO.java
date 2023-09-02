package com.mholodniuk.searchthedocs.management.document.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DocumentDTO(
        String id,
        String name,
        List<String> tags,
        String contentType,
        String filePath,
        String storage,
        LocalDateTime uploadedAt
) {
}
