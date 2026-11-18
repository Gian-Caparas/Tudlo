package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class AssessmentDto {

    private AssessmentDto() {
    }

    public record Request(
            @NotBlank(message = "Learning outcome is required")
            String learningOutcome,
            @NotNull(message = "Max score is required")
            @DecimalMin(value = "0.01", message = "Max score must be positive")
            Double maxScore,
            @NotBlank(message = "Assessment item is required")
            String assessmentItem,
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection
    ) {
    }

    public record Response(
            Long id,
            String learningOutcome,
            Double maxScore,
            String assessmentItem,
            String subjectName,
            String classSection,
            LocalDateTime createdAt
    ) {
    }
}