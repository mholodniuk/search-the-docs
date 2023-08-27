package com.mholodniuk.searchthedocs.common.validation;

import lombok.Builder;

@Builder
public record ErrorMessage(String field, String message, Object invalidValue) {
}
