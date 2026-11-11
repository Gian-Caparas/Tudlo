package com.csit321G6.groufings.tudlo.dto;

import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.entity.UserType;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class UserDto {

    private UserDto() {
    }

    /** The password is write-only: it is hashed and never returned. */
    public record Request(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @Size(min = 8, max = 72) String password,
            @NotNull UserType userType,
            UserStatus status
    ) {
    }

    public record Response(
            Long id,
            String fullName,
            String email,
            UserType userType,
            UserStatus status,
            LocalDateTime createdAt
    ) {
    }
}

