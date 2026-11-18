package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.config.JwtUtil;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userIdNumber(), request.password())
        );

        User user = userRepository.findByUserIdNumber(request.userIdNumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.userIdNumber()));

        String token = jwtUtil.generateToken(user.getUserIdNumber(), user.getUserType().name(), user.getId());

        return ResponseEntity.ok(new AuthDto.LoginResponse(
                token,
                "Bearer",
                jwtUtil.getExpirationMs(),
                user.getId(),
                user.getUserType().name(),
                user.getFirstName() + " " + user.getLastName(),
                user.getUserIdNumber()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDto.MeResponse> me(Authentication authentication) {
        String userIdNumber = authentication.getName();
        User user = userRepository.findByUserIdNumber(userIdNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userIdNumber));

        return ResponseEntity.ok(new AuthDto.MeResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserIdNumber(),
                user.getUserType(),
                user.getCreatedAt()
        ));
    }
}