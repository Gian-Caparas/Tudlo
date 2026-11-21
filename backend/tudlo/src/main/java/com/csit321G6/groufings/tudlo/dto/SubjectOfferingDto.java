package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;

public final class SubjectOfferingDto {

    private SubjectOfferingDto() {
    }

    public record Request(
            Long teacherId,
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection
    ) {
    }

    public record Response(
            Long id,
            Long offeringId,
            Long subjectId,
            String subjectName,
            Long sectionId,
            String classSection,
            Long teacherId,
            String teacherName
    ) {
    }
}
