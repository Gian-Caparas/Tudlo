package com.csit321G6.groufings.tudlo.classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ClassRoomDto {

    private ClassRoomDto() {
    }

    public record Request(
            @NotNull Long teacherId,
            @NotBlank @Size(max = 150) String className,
            @Size(max = 100) String subject,
            @Size(max = 50) String term
    ) {
    }

    public record Response(
            Long id,
            Long teacherId,
            String className,
            String subject,
            String term
    ) {
    }
}
