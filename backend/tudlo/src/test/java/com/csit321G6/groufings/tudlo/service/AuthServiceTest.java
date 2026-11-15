package com.csit321G6.groufings.tudlo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csit321G6.groufings.tudlo.common.exception.InvalidCredentialsException;
import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.dto.UserDto;
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
    private final AuthService service = new AuthService(repository, passwordEncoder);

    @Test
    void loginReturnsUserWithoutPasswordHash() {
        User user = activeUser();
        when(repository.findByEmailIgnoreCase("maya@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test-password", "encoded-password")).thenReturn(true);

        UserDto.Response response = service.login(new AuthDto.LoginRequest("maya@example.com", "test-password"));

        assertEquals(1L, response.id());
        assertEquals("Maya Santos", response.fullName());
        assertEquals(UserType.ADMIN, response.userType());
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
