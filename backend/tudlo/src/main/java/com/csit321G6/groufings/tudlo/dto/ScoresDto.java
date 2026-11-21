package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class ScoresDto {

    private ScoresDto() {
    }

    public record Request(
            Long offeringId,
            @NotBlank(message = "Student ID is required")
            String studentId,
            String subjectName,
            String classSection,
            @NotNull(message = "Score is required")
            @DecimalMin(value = "0.0", message = "Score cannot be negative")
            Double score
    ) {
    }

    public record Response(
            Long id,
            Long offeringId,
            String studentId,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            Double score,
            LocalDateTime createdAt
    ) {
    }
}
