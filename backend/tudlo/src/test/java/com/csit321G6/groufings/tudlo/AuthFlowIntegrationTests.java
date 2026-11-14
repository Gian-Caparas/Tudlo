package com.csit321G6.groufings.tudlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTests {

    @Value("${local.server.port}")
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void defaultAdminCanCreateUsersAndOnlyAdminCanManageUsers() throws Exception {
        assertEquals(401, send("GET", "/api/users", null, null).statusCode());

        JsonNode adminLogin = loginResponse("username", "admin@gmail.com", "Admin123");
        assertEquals("ADMIN", adminLogin.get("userType").asText());
        String adminToken = adminLogin.get("token").asText();

        String newAdminEmail = uniqueEmail("admin");
        HttpResponse<String> newAdmin = createUser(
                adminToken, newAdminEmail, "Tudlo Admin", "ADMIN", "AdminPass123");
        assertEquals(201, newAdmin.statusCode());
        assertFalse(newAdmin.body().contains("passwordHash"));

        JsonNode newAdminLogin = loginResponse("email", newAdminEmail, "AdminPass123");
        assertEquals("ADMIN", newAdminLogin.get("userType").asText());
        assertNotNull(newAdminLogin.get("token").asText());

        String teacherEmail = uniqueEmail("teacher");
        assertEquals(201, createUser(
                adminToken, teacherEmail, "Tudlo Teacher", "TEACHER", "TeacherPass123").statusCode());
        JsonNode teacherLogin = loginResponse("email", teacherEmail, "TeacherPass123");
        assertEquals("TEACHER", teacherLogin.get("userType").asText());
        String teacherToken = teacherLogin.get("token").asText();
        assertEquals(200, send("GET", "/api/auth/me", null, teacherToken).statusCode());

        String studentEmail = uniqueEmail("student");
        assertEquals(201, createUser(
                adminToken, studentEmail, "Tudlo Student", "STUDENT", "StudentPass123").statusCode());
        HttpResponse<String> teacherCreate = createUser(
                teacherToken, uniqueEmail("blocked"), "Blocked User", "STUDENT", "BlockedPass123");
        assertEquals(403, teacherCreate.statusCode(), teacherCreate.body());
        assertEquals(403, send("GET", "/api/users", null, teacherToken).statusCode());
        assertEquals(200, send("GET", "/api/users", null, adminToken).statusCode());
    }

    @Test
    void badCredentialsReturnUnauthorized() throws Exception {
        HttpResponse<String> response = send(
                "POST",
                "/api/auth/login",
                objectMapper.writeValueAsString(Map.of("email", "admin@gmail.com", "password", "wrong")),
                null);

        assertEquals(401, response.statusCode());
    }

    private JsonNode loginResponse(String loginField, String email, String password) throws Exception {
        HttpResponse<String> response = send(
                "POST",
                "/api/auth/login",
                objectMapper.writeValueAsString(Map.of(loginField, email, "password", password)),
                null);
        assertEquals(200, response.statusCode(), response.body());
        return objectMapper.readTree(response.body());
    }

    private HttpResponse<String> createUser(
            String token, String email, String fullName, String userType, String password)
            throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "fullName", fullName,
                "email", email,
                "password", password,
                "userType", userType));
        return send("POST", "/api/users", body, token);
    }

    private HttpResponse<String> send(String method, String path, String body, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json");
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        request.method(method, body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body));
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@tudlo.test";
    }
}
