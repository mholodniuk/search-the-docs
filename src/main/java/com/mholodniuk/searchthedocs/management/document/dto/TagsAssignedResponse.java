package com.mholodniuk.searchthedocs.management.document.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TagsAssignedResponse(
        String document,
        List<String> tags) {
}
