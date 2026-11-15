package com.csit321G6.groufings.tudlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class TudloApplicationTests {

    private static final String DEMO_PASSWORD_HASH =
            "$2b$10$HkoY1lU1EeAY0Tb1lPGB9eFUIODTWEVCkekoL5PXrcyMyxNVzrrtu";

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void bootstrappedDeveloperCanLogInAndHasNoProfileId() {
        AuthDto.LoginResponse response = authService.login(
                new AuthDto.LoginRequest("developer@tudlo.test", "TestDeveloper123!"));

        assertEquals("DEVELOPER", response.getUserType());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getToken());
        assertNull(response.getProfileId());
        assertTrue(response.getExpiresInSeconds() > 0);
    }

    @Test
    void invalidDeveloperPasswordIsRejected() {
        assertThrows(BadCredentialsException.class,
                () -> authService.login(new AuthDto.LoginRequest("developer@tudlo.test", "incorrect")));
    }

    @Test
    void seedPasswordHashMatchesTheDocumentedDemoPassword() {
        assertTrue(passwordEncoder.matches("ChangeMe123!", DEMO_PASSWORD_HASH));
    }

    @Test
    void userRolesUseStrictHierarchy() {
        assertTrue(UserType.DEVELOPER.canManage(UserType.ADMIN));
        assertTrue(UserType.ADMIN.canManage(UserType.STUDENT));
        assertEquals(false, UserType.ADMIN.canManage(UserType.ADMIN));
        assertEquals(false, UserType.STUDENT.canManage(UserType.TEACHER));
    }
}
