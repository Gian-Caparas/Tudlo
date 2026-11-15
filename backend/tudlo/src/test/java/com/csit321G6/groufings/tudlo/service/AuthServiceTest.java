package com.csit321G6.groufings.tudlo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csit321G6.groufings.tudlo.common.JwtUtil;
import com.csit321G6.groufings.tudlo.common.UserProfileLookup;
import com.csit321G6.groufings.tudlo.common.exception.InvalidCredentialsException;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final UserProfileLookup userProfileLookup = mock(UserProfileLookup.class);
    private final AuthService service = new AuthService(repository, passwordEncoder, jwtUtil, userProfileLookup);

    @Test
    void loginReturnsUserWithoutPasswordHash() {
        User user = activeUser();
        when(repository.findByEmailIgnoreCase("maya@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test-password", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "maya@example.com", "ADMIN", null)).thenReturn("signed-token");
        when(jwtUtil.getExpirySeconds()).thenReturn(86400L);

        AuthDto.LoginResponse response = service.login(new AuthDto.LoginRequest("maya@example.com", "test-password"));

        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("Maya Santos", response.getFullName());
        assertEquals("ADMIN", response.getUserType());
        assertEquals("signed-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(86400L, response.getExpiresInSeconds());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(user.getCreatedAt(), response.getCreatedAt());
        verify(passwordEncoder).matches("test-password", "encoded-password");
    }

    @Test
    void loginRejectsIncorrectPassword() {
        User user = activeUser();
        when(repository.findByEmailIgnoreCase("maya@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new AuthDto.LoginRequest("maya@example.com", "wrong-password")));
    }

    @Test
    void loginNormalizesEmailBeforeLookingUpUser() {
        User user = activeUser();
        when(repository.findByEmailIgnoreCase("maya@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test-password", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "maya@example.com", "ADMIN", null)).thenReturn("signed-token");
        when(jwtUtil.getExpirySeconds()).thenReturn(86400L);

        service.login(new AuthDto.LoginRequest("Maya@Example.COM", "test-password"));

        verify(repository).findByEmailIgnoreCase("maya@example.com");
    }

    @Test
    void loginRejectsInactiveUserWithoutCheckingPassword() {
        User user = activeUser();
        user.setStatus(UserStatus.INACTIVE);
        when(repository.findByEmailIgnoreCase("maya@example.com")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new AuthDto.LoginRequest("maya@example.com", "test-password")));
        verify(passwordEncoder, never()).matches("test-password", "encoded-password");
    }

    private User activeUser() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Maya Santos");
        user.setEmail("maya@example.com");
        user.setPasswordHash("encoded-password");
        user.setUserType(UserType.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
