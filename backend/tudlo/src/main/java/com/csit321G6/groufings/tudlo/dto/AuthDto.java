package com.csit321G6.groufings.tudlo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public final class AuthDto {

    private AuthDto() {
    }

    @Getter
    @Setter
    public static class LoginRequest {
        @JsonAlias("username")
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        public LoginRequest() {
        }

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String email() {
            return email;
        }

        public String password() {
            return password;
        }
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private String token;
        private String tokenType;
        private long expiresInSeconds;
        private Long profileId;
        private Long id;
        private String status;
        private LocalDateTime createdAt;
        // Retained response aliases for clients using the earlier login contract.
        private Long userId;
        private String fullName;
        private String email;
        private String userType;

        public LoginResponse(
                String token,
                long expiresInSeconds,
                Long profileId,
                Long userId,
                String fullName,
                String email,
                String userType,
                String status,
                LocalDateTime createdAt) {
            this.token = token;
            this.tokenType = "Bearer";
            this.expiresInSeconds = expiresInSeconds;
            this.profileId = profileId;
            this.id = userId;
            this.status = status;
            this.createdAt = createdAt;
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.userType = userType;
        }
    }

    @Getter
    @Setter
    public static class MeResponse {
        private Long id;
        private String fullName;
        private String email;
        private String userType;
        private String status;
        private LocalDateTime createdAt;
        private Long profileId;
        private Long userId;

        public MeResponse(
                Long userId,
                String fullName,
                String email,
                String userType,
                String status,
                LocalDateTime createdAt,
                Long profileId) {
            this.id = userId;
            this.fullName = fullName;
            this.createdAt = createdAt;
            this.profileId = profileId;
            this.userId = userId;
            this.email = email;
            this.userType = userType;
            this.status = status;
        }
    }
}
