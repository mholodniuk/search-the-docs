package com.mholodniuk.searchthedocs.management.access.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mholodniuk.searchthedocs.management.access.AccessRight;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GrantAccessRequest(
        @NotNull
        Long invitedId,
        @NotBlank
        String keyName,
        @NotNull
        AccessRight accessRight,
        @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
        @Future(message = "Valid to date must be in future")
        LocalDate validTo
) {
}
