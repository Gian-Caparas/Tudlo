package com.csit321G6.groufings.tudlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.csit321G6.groufings.tudlo.dto.AuthDto;
import com.csit321G6.groufings.tudlo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

@SpringBootTest
class TudloApplicationTests {

	@Autowired
	private AuthService authService;

	@Test
	void contextLoads() {
	}

	@Test
	void defaultAdminCanLogIn() {
		AuthDto.LoginRequest request = new AuthDto.LoginRequest();
		request.setEmail("admin@gmail.com");
		request.setPassword("Admin123");

		AuthDto.LoginResponse response = authService.login(request);

		assertEquals("ADMIN", response.getUserType());
		assertNotNull(response.getToken());
	}

	@Test
	void invalidAdminPasswordIsRejected() {
		AuthDto.LoginRequest request = new AuthDto.LoginRequest();
		request.setEmail("admin@gmail.com");
		request.setPassword("incorrect");

		assertThrows(BadCredentialsException.class, () -> authService.login(request));
	}
}
