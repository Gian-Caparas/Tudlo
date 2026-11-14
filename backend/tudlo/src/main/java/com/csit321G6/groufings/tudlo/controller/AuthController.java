package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.Response> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(request));
    }
}
