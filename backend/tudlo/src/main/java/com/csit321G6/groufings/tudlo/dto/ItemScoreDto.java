package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public final class ItemScoreDto {

    private ItemScoreDto() {
    }

    public record Request(
            @NotNull Long submissionId,
            @NotNull Long itemId,
            @PositiveOrZero Double pointsEarned,
            boolean missing
    ) {
    }

    public record Response(
            Long id,
            Long submissionId,
            Long itemId,
            Double pointsEarned,
            boolean missing
    ) {
    }
}
