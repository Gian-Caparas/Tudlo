package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.JwtUtil;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import java.util.Locale;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        if (user.getStatus() != com.csit321G6.groufings.tudlo.entity.UserStatus.ACTIVE
                || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getUserType().name());
        return new AuthDto.LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getUserType().name());
    }

    public AuthDto.MeResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getStatus() != com.csit321G6.groufings.tudlo.entity.UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }

        return new AuthDto.MeResponse(user.getId(), user.getFullName(), user.getEmail(), user.getUserType().name(), user.getStatus().name());
    }
}