package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class LearningOutcomeDto {

    private LearningOutcomeDto() {
    }

    public record Request(
            @NotNull Long topicId,
            @NotBlank @Size(max = 500) String description
    ) {
    }

    public record Response(
            Long id,
            Long topicId,
            String description
    ) {
    }
}
