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
            @Size(max = 150)
            String firstName,
            @NotBlank(message = "Last name is required")
            @Size(max = 150)
            String lastName,
            @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
            String password,
            @NotNull(message = "User type is required")
            UserType userType,
            // Optional: only used by Admin when creating users with specific ID numbers
            @Size(max = 20)
            String userIdNumber
    ) {
    }

    public record Response(
            Long id,
            String firstName,
            String lastName,
            String userIdNumber,
            UserType userType,
            LocalDateTime createdAt
    ) {
    }
}