package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotNull;

public final class AdminDto {

    private AdminDto() {
    }

    public record Request(
            @NotNull Long userId
    ) {
    }

    public record Response(
            Long id,
            Long userId
    ) {
    }
}
