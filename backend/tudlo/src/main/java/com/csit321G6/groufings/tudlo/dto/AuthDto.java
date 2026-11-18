package com.csit321G6.groufings.tudlo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AuthDto {

    public record LoginRequest(
            @NotBlank(message = "User ID number is required")
            @Size(min = 5, max = 20, message = "User ID number must be between 5 and 20 characters")
            String userIdNumber,

            @NotBlank(message = "Password is required")
            @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
            String password
    ) {}

    public record LoginResponse(
            String token,
            String tokenType,
            long expiresInMs,
            Long id,
            String userType,
            String fullName,
            String userIdNumber
    ) {}

    public record MeResponse(
            Long id,
            String firstName,
            String lastName,
            String userIdNumber,
            com.csit321G6.groufings.tudlo.entity.UserType userType,
            LocalDateTime createdAt
    ) {}
}