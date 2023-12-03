package com.mholodniuk.searchthedocs.document.dto;

import java.util.Collections;
import java.util.List;

public record PhraseSearchResponse(List<SingleSearchResponse> hits, int size) {
    public static PhraseSearchResponse empty() {
        return new PhraseSearchResponse(Collections.emptyList(), 0);
    }
}

