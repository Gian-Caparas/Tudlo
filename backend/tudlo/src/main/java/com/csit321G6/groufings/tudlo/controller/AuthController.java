package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.dto.AuthDto;
<<<<<<< HEAD
import com.csit321G6.groufings.tudlo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
=======
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
>>>>>>> origin/main

@RestController
@RequestMapping("/api/auth")
public class AuthController {

<<<<<<< HEAD
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDto.MeResponse> me(Authentication authentication) {
        AuthDto.MeResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
=======
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.Response> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(request));
    }
}
>>>>>>> origin/main
