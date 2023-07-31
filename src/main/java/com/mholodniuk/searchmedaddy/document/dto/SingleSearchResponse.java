package com.mholodniuk.searchmedaddy.document.dto;

import java.util.List;

public record SingleSearchResponse(String documentName, Integer page, List<String> hits, Integer foundNumber) {
}
