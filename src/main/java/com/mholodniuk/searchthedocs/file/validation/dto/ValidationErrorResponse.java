package com.mholodniuk.searchthedocs.file.validation.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}
