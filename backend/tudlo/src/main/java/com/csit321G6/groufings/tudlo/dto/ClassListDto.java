package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public final class ClassListDto {

    private ClassListDto() {
    }

    public record Request(
            Long offeringId,
            String subjectName,
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
            Long offeringId,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            String studentId,
            String firstName,
            String lastName,
            LocalDateTime createdAt
    ) {
    }

    public record BulkImportRequest(
            Long offeringId,
            String subjectName,
            String classSection,
            Long teacherId,
            @NotEmpty(message = "Student list cannot be empty")
            @Valid
            List<StudentRecord> students
    ) {
    }

    public record StudentRecord(
            @NotBlank(message = "Student ID is required") String studentId,
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName
    ) {
    }

    // Excel Import DTOs
    public record ExcelImportResult(
            int importedCount,
            int createdAccountsCount,
            int skippedDuplicatesCount,
            List<OfferingInfo> offerings
    ) {
    }

    public record OfferingInfo(
            Long offeringId,
            String subjectName,
            String classSection
    ) {
    }
}
