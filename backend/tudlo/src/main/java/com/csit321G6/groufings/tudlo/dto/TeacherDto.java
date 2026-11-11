package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotNull;

public final class TeacherDto {

        private TeacherDto() {
        }

        public record Request(
                        @NotNull Long userId) {
        }

        public record Response(
                        Long id,
                        Long userId) {
        }
}
