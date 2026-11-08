package com.csit321G6.groufings.tudlo.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class AssessmentItemDto {

    private AssessmentItemDto() {
    }

    public record Request(
            @NotNull Long assessmentId,
            @NotNull Long outcomeId,
            @NotBlank @Size(max = 50) String itemLabel,
            @NotNull @Positive Double maxPoints
    ) {
    }

    public record Response(
            Long id,
            Long assessmentId,
            Long outcomeId,
            String itemLabel,
            Double maxPoints
    ) {
    }
}
