package com.mholodniuk.searchthedocs.file.validation;

import java.util.List;

public interface Constraints {
    List<String> SUPPORTED_CONTENT_TYPES
            = List.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf");
    String INVALID_CONTENT_TYPE = "Invalid content type. Currently supporting pdf and docx";
}
