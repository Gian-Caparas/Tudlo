package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.exception.InvalidCredentialsException;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto.Response login(AuthDto.LoginRequest request) {
        User user = repository.findByEmailIgnoreCase(request.email())
                .filter(candidate -> candidate.getStatus() == UserStatus.ACTIVE)
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        return new UserDto.Response(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
