package com.mholodniuk.searchthedocs.management.document.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record AssignTagsRequest(
        @NotNull
        List<String> tags
) {
}
