package com.mholodniuk.searchthedocs.file.dto;

import lombok.Builder;

@Builder
public record FileUploadResponse(
        String id,
        String filename,
        String owner,
        String room,
        String info
) {
}
