package com.csit321G6.groufings.tudlo.curriculum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class TopicDto {

    private TopicDto() {
    }

    public record Request(
            @NotNull Long classId,
            @NotBlank @Size(max = 150) String name
    ) {
    }

    public record Response(
            Long id,
            Long classId,
            String name
    ) {
    }
}
