package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class ActivityDto {

    private ActivityDto() {
    }

    public record Request(
            Long assessmentId,
            Long offeringId,
            String activityName,
            @NotBlank(message = "Student ID is required")
            String studentId,
            @NotNull(message = "Score is required")
            @DecimalMin(value = "0.0", message = "Score cannot be negative")
            Double score,
            String subjectName,
            String classSection
    ) {
    }

    public record Response(
            Long id,
            Long assessmentId,
            Long offeringId,
            String activityName,
            String studentId,
            Double score,
            Double maxScore,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            LocalDateTime createdAt
    ) {
    }
}
