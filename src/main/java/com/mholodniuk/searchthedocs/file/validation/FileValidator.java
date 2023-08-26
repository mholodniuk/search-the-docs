package com.mholodniuk.searchthedocs.file.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import static com.mholodniuk.searchthedocs.file.validation.Constraints.SUPPORTED_CONTENT_TYPES;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    @Override
    public void initialize(ValidFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        String contentType = multipartFile.getContentType();
        return SUPPORTED_CONTENT_TYPES.contains(contentType);
    }
}