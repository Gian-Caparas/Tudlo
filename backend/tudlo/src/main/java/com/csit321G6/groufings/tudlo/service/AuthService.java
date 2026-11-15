package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.JwtUtil;
import com.csit321G6.groufings.tudlo.common.UserProfileLookup;
import com.csit321G6.groufings.tudlo.common.exception.InvalidCredentialsException;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserProfileLookup userProfileLookup;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UserProfileLookup userProfileLookup) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userProfileLookup = userProfileLookup;
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(email)
                .filter(candidate -> candidate.getStatus() == UserStatus.ACTIVE)
                .filter(candidate -> passwordEncoder.matches(request.getPassword(), candidate.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        String userType = user.getUserType().name();
        Long profileId = userProfileLookup.findProfileId(user.getId(), user.getUserType()).orElse(null);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), userType, profileId);
        return new AuthDto.LoginResponse(
                token,
                jwtUtil.getExpirySeconds(),
                profileId,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                userType,
                user.getStatus().name(),
                user.getCreatedAt());
    }

    public AuthDto.MeResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .filter(candidate -> candidate.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(InvalidCredentialsException::new);

        Long profileId = userProfileLookup.findProfileId(user.getId(), user.getUserType()).orElse(null);
        return new AuthDto.MeResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                profileId);
    }
}
