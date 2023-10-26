package com.mholodniuk.searchthedocs.document.dto;

import java.util.List;

public record SingleSearchResponse(
        String documentName,
        String documentId,
        String room,
        Integer page,
        List<String> hits,
        Integer count) {
}
