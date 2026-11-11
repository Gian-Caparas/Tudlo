package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public final class SupportActivityDto {

    private SupportActivityDto() {
    }

    public record Request(
            @NotNull Long teacherId,
            @NotNull Long enrollmentId,
            @NotBlank @Size(max = 100) String type,
            @Size(max = 1000) String notes,
            @NotNull LocalDate activityDate
    ) {
    }

    public record Response(
            Long id,
            Long teacherId,
            Long enrollmentId,
            String type,
            String notes,
            LocalDate activityDate
    ) {
    }
}
