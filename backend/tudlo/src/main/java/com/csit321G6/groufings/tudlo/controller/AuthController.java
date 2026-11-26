package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.config.JwtUtil;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.repository.AdminRepository;
import com.csit321G6.groufings.tudlo.repository.StudentRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@SuppressWarnings("null")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            TeacherRepository teacherRepository,
            AdminRepository adminRepository,
            StudentRepository studentRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userIdNumber(), request.password())
        );

        User user = userRepository.findByUserIdNumber(request.userIdNumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.userIdNumber()));

        String token = jwtUtil.generateToken(user.getUserIdNumber(), user.getUserType().name(), user.getId());
        Object profileId = resolveProfileId(user);
        String fullName = user.getFirstName() + " " + user.getLastName();

        return ResponseEntity.ok(new AuthDto.LoginResponse(
                token,
                "Bearer",
                jwtUtil.getExpirationMs(),
                user.getId(),
                profileId,
                user.getUserType().name(),
                fullName,
                user.getFirstName(),
                user.getLastName(),
                user.getUserIdNumber()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDto.MeResponse> me(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        String userIdNumber = authentication.getName();
        User user = userRepository.findByUserIdNumber(userIdNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userIdNumber));

        Object profileId = resolveProfileId(user);

        return ResponseEntity.ok(new AuthDto.MeResponse(
                user.getId(),
                profileId,
                user.getFirstName(),
                user.getLastName(),
                user.getUserIdNumber(),
                user.getUserType(),
                user.getCreatedAt()
        ));
    }

    private Object resolveProfileId(User user) {
        String studentId = java.util.Objects.requireNonNull(user.getUserIdNumber(), "User ID number is required");
        return switch (user.getUserType()) {
            case ADMIN -> adminRepository.findByUserId(user.getId()).map(a -> a.getId()).orElse(null);
            case TEACHER -> teacherRepository.findByUserId(user.getId()).map(t -> t.getId()).orElse(null);
            case STUDENT -> studentRepository.findById(studentId)
                    .map(s -> s.getStudentId())
                    .orElse(studentId);
        };
    }
}
