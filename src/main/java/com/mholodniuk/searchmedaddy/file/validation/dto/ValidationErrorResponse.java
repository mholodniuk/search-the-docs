package com.mholodniuk.searchmedaddy.file.validation.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}
