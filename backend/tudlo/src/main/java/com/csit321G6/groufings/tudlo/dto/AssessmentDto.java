package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class AssessmentDto {

    private AssessmentDto() {
    }

    public record Request(
            Long offeringId,
            @NotBlank(message = "Learning outcome is required")
            String learningOutcome,
            @NotNull(message = "Max score is required")
            @DecimalMin(value = "0.01", message = "Max score must be positive")
            Double maxScore,
            @NotBlank(message = "Assessment item is required")
            String assessmentItem,
            String subjectName,
            String classSection
    ) {
    }

    public record Response(
            Long id,
            Long offeringId,
            String learningOutcome,
            Double maxScore,
            String assessmentItem,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            LocalDateTime createdAt
    ) {
    }
}
