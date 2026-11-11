package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class StudentDto {

    private StudentDto() {
    }

    public record Request(
            @NotNull Long userId,
            @NotBlank @Size(max = 30) String studentNo
    ) {
    }

    public record Response(
            Long id,
            Long userId,
            String studentNo
    ) {
    }
}

