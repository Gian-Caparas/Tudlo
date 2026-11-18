package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class ClassListDto {

    private ClassListDto() {
    }

    public record Request(
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection,
            @NotBlank(message = "Student ID is required")
            String studentId,
            @NotBlank(message = "First name is required")
            String firstName,
            @NotBlank(message = "Last name is required")
            String lastName
    ) {
    }

    public record Response(
            Long id,
            String subjectName,
            String classSection,
            String studentId,
            String firstName,
            String lastName,
            LocalDateTime createdAt
    ) {
    }

    public record BulkImportRequest(
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection,
            @NotEmpty(message = "Student list cannot be empty")
            java.util.List<StudentRecord> students
    ) {
    }

    public record StudentRecord(
            @NotBlank String studentId,
            @NotBlank String firstName,
            @NotBlank String lastName
    ) {
    }
}