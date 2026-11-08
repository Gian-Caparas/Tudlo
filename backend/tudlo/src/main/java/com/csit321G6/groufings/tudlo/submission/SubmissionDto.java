package com.csit321G6.groufings.tudlo.submission;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public final class SubmissionDto {

    private SubmissionDto() {
    }

    public record Request(
            @NotNull Long enrollmentId,
            @NotNull Long assessmentId,
            LocalDateTime submittedAt,
            @NotNull SubmissionStatus status
    ) {
    }

    public record Response(
            Long id,
            Long enrollmentId,
            Long assessmentId,
            LocalDateTime submittedAt,
            SubmissionStatus status
    ) {
    }
}
