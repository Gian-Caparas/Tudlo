package com.csit321G6.groufings.tudlo.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public final class AssessmentDto {

    private AssessmentDto() {
    }

    public record Request(
            @NotNull Long classId,
            @NotBlank @Size(max = 150) String title,
            @Size(max = 50) String type,
            LocalDate dueDate
    ) {
    }

    public record Response(
            Long id,
            Long classId,
            String title,
            String type,
            LocalDate dueDate
    ) {
    }
}
