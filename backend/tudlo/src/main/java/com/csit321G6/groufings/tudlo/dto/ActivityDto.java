package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class ActivityDto {

    private ActivityDto() {
    }

    public record Request(
            @NotBlank(message = "Activity name is required")
            String activityName,
            @NotBlank(message = "Student ID is required")
            String studentId,
            @NotNull(message = "Score is required")
            @DecimalMin(value = "0.0", message = "Score cannot be negative")
            Double score,
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection
    ) {
    }

    public record Response(
            Long id,
            String activityName,
            String studentId,
            Double score,
            String subjectName,
            String classSection,
            LocalDateTime createdAt
    ) {
    }
}