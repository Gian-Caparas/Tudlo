package com.csit321G6.groufings.tudlo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class AuthDto {

    @Getter
    @Setter
    public static class LoginRequest {
        @JsonAlias("username")
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private String token;
        private Long userId;
        private String fullName;
        private String email;
        private String userType;

        public LoginResponse(String token, Long userId, String fullName, String email, String userType) {
            this.token = token;
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.userType = userType;
        }
    }

    @Getter
    @Setter
    public static class MeResponse {
        private Long userId;
        private String fullName;
        private String email;
        private String userType;
        private String status;

        public MeResponse(Long userId, String fullName, String email, String userType, String status) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.userType = userType;
            this.status = status;
        }
    }
}