package com.csit321G6.groufings.tudlo.dto;

import com.csit321G6.groufings.tudlo.entity.UserType;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class UserDto {

    private UserDto() {
    }

    /** The password is write-only: it is hashed and never returned. */
    public record Request(
            @NotBlank(message = "First name is required")
            String firstName,
            @NotBlank(message = "Last name is required")
            String lastName,
            @NotBlank(message = "ID number is required")
            String idNumber,
            @NotNull(message = "User type is required")
            UserType userType

    ) {
    }

    public record Response(
            Long id,
            String firstName,
            String lastName,
            String idNumber,
            UserType userType,
            LocalDateTime createdAt
    ) {
    }
}