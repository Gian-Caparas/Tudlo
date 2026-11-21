package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class SubjectDto {

    private SubjectDto() {
    }

    public record Request(
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection,
            @NotNull(message = "Teacher ID is required")
            Long teacherId
    ) {
    }

    public record Response(
            Long id,
            Long offeringId,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            LocalDateTime createdAt
    ) {
    }
}
