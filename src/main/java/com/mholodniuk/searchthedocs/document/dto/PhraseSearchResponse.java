package com.mholodniuk.searchthedocs.document.dto;

import java.util.List;

public record PhraseSearchResponse(List<SingleSearchResponse> hits) {
}

