package com.mholodniuk.searchmedaddy.document.dto;

import java.util.List;

public record PhraseSearchResponse(List<SingleSearchResponse> hits) {
}

