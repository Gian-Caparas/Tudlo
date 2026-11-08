package com.csit321G6.groufings.tudlo.student;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public final class EnrollmentDto {

    private EnrollmentDto() {
    }

    public record Request(
            @NotNull Long studentId,
            @NotNull Long classId
    ) {
    }

    public record Response(
            Long id,
            Long studentId,
            Long classId,
            LocalDateTime enrolledAt
    ) {
    }
}
