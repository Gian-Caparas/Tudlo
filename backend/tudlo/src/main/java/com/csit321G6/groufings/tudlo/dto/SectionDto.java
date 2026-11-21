package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class SectionDto {

    private SectionDto() {
    }

    public record Request(
            @NotBlank(message = "Section name is required")
            @Size(max = 50, message = "Section name must not exceed 50 characters")
            String sectionName
    ) {
    }

    public record Response(
            Long id,
            String sectionName
    ) {
    }
}
