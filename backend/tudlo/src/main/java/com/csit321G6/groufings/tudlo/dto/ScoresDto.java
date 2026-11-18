package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class ScoresDto {

    private ScoresDto() {
    }

    public record Request(
            @NotBlank(message = "Student ID is required")
            String studentId,
            @NotBlank(message = "Subject name is required")
            String subjectName,
            @NotBlank(message = "Class section is required")
            String classSection,
            @NotNull(message = "Score is required")
            @DecimalMin(value = "0.0", message = "Score cannot be negative")
            Double score
    ) {
    }

    public record Response(
            Long id,
            String studentId,
            String subjectName,
            String classSection,
            Double score,
            LocalDateTime createdAt
    ) {
    }
}