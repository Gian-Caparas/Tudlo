package com.csit321G6.groufings.tudlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTests {

    @Value("${local.server.port}")
    private int port;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository userRepository;

    @Test
    void rolesLoginAuthorizationManagementAndOwnershipWorkTogether() throws Exception {
        JsonNode anonymousUsers = sendJson("GET", "/api/users", null, null);
        assertEquals(401, anonymousUsers.get("status").asInt());
        assertEquals("Unauthorized", anonymousUsers.get("error").asText());
        assertEquals(401, send("GET", "/api/classes", null, null).statusCode());

        JsonNode developerLogin = login("developer@tudlo.test", "TestDeveloper123!");
        assertEquals("DEVELOPER", developerLogin.get("userType").asText());
        assertTrue(developerLogin.get("profileId").isNull());
        String developerToken = developerLogin.get("token").asText();

        String adminEmail = uniqueEmail("admin");
        JsonNode admin = createUser(developerToken, adminEmail, "Tudlo Admin", "ADMIN", "AdminPass123");
        long adminId = admin.get("id").asLong();
        assertEquals(developerLogin.get("id").asLong(), userRepository.findById(adminId)
                .orElseThrow().getCreatedBy().getId());
        assertEquals(201, createProfile(developerToken, "/api/admins", Map.of("userId", adminId)).statusCode());

        String secondAdminEmail = uniqueEmail("admin-secondary");
        JsonNode secondAdmin = createUser(developerToken, secondAdminEmail, "Second Admin", "ADMIN", "AdminPass123");
        long secondAdminId = secondAdmin.get("id").asLong();

        String teacherEmail = uniqueEmail("teacher");
        JsonNode teacher = createUserWithToken(adminEmail, "AdminPass123", teacherEmail,
                "Tudlo Teacher", "TEACHER", "TeacherPass123");
        long teacherId = teacher.get("id").asLong();
        assertEquals(adminId, userRepository.findById(teacherId)
                .orElseThrow().getCreatedBy().getId());
        JsonNode teacherProfile = createProfileAndRead(developerToken, "/api/teachers",
                Map.of("userId", teacherId));

        String otherTeacherEmail = uniqueEmail("teacher-other");
        JsonNode otherTeacher = createUserWithToken(adminEmail, "AdminPass123",
                otherTeacherEmail, "Other Teacher", "TEACHER", "TeacherPass123");
        JsonNode otherTeacherProfile = createProfileAndRead(developerToken, "/api/teachers",
                Map.of("userId", otherTeacher.get("id").asLong()));

        String studentEmail = uniqueEmail("student");
        JsonNode student = createUserWithToken(adminEmail, "AdminPass123", studentEmail,
                "Tudlo Student", "STUDENT", "StudentPass123");
        long studentId = student.get("id").asLong();
        JsonNode studentProfile = createProfileAndRead(developerToken, "/api/students",
                Map.of("userId", studentId, "studentNo", uniqueStudentNo()));

        String otherStudentEmail = uniqueEmail("student-other");
        JsonNode otherStudent = createUserWithToken(adminEmail, "AdminPass123",
                otherStudentEmail, "Other Student", "STUDENT", "StudentPass123");
        JsonNode otherStudentProfile = createProfileAndRead(developerToken, "/api/students",
                Map.of("userId", otherStudent.get("id").asLong(), "studentNo", uniqueStudentNo()));

        JsonNode adminLogin = login(adminEmail, "AdminPass123");
        JsonNode teacherLogin = login(teacherEmail, "TeacherPass123");
        JsonNode otherTeacherLogin = login(otherTeacherEmail, "TeacherPass123");
        JsonNode studentLogin = login(studentEmail, "StudentPass123");
        JsonNode otherStudentLogin = login(otherStudentEmail, "StudentPass123");
        assertEquals(adminId, adminLogin.get("id").asLong());
        assertEquals(teacherProfile.get("id").asLong(), teacherLogin.get("profileId").asLong());
        assertEquals(studentProfile.get("id").asLong(), studentLogin.get("profileId").asLong());
        assertNotNull(otherTeacherProfile.get("id"));
        assertNotNull(otherStudentProfile.get("id"));

        String adminToken = adminLogin.get("token").asText();
        String teacherToken = teacherLogin.get("token").asText();
        String otherTeacherToken = otherTeacherLogin.get("token").asText();
        String studentToken = studentLogin.get("token").asText();
        String otherStudentToken = otherStudentLogin.get("token").asText();

        assertEquals(403, createUserResponse(adminToken, uniqueEmail("admin-blocked"),
                "Blocked Admin", "ADMIN", "Password123!").statusCode());
        assertEquals(403, createUserResponse(adminToken, uniqueEmail("developer-blocked"),
                "Blocked Developer", "DEVELOPER", "Password123!").statusCode());
        assertEquals(403, createUserResponse(developerToken, uniqueEmail("developer-blocked"),
                "Blocked Developer", "DEVELOPER", "Password123!").statusCode());

        assertEquals(403, send("GET", "/api/users", null, teacherToken).statusCode());
        assertEquals(403, send("GET", "/api/users", null, studentToken).statusCode());
        assertEquals(403, send("GET", "/api/classes", null, adminToken).statusCode());
        assertEquals(200, send("GET", "/api/users", null, adminToken).statusCode());

        assertEquals(403, updateUser(adminToken, secondAdminId, secondAdminEmail,
                "Second Admin Edited", "ADMIN", "ACTIVE").statusCode());
        assertEquals(403, send("DELETE", "/api/users/" + secondAdminId, null, adminToken).statusCode());
        assertEquals(403, updateUser(adminToken, teacherId, teacherEmail,
                "Tudlo Teacher", "ADMIN", "ACTIVE").statusCode());
        assertEquals(403, updateUser(adminToken, adminId, adminEmail,
                "Tudlo Admin", "TEACHER", "ACTIVE").statusCode());
        assertEquals(403, updateUser(adminToken, adminId, adminEmail,
                "Tudlo Admin", "ADMIN", "INACTIVE").statusCode());
        assertEquals(403, send("DELETE", "/api/users/" + adminId, null, adminToken).statusCode());

        JsonNode classOne = createEntity(teacherToken, "/api/classes",
                Map.of("teacherId", teacherProfile.get("id").asLong(), "className", "Owned class"));
        JsonNode classTwo = createEntity(otherTeacherToken, "/api/classes",
                Map.of("teacherId", otherTeacherProfile.get("id").asLong(), "className", "Other class"));
        assertEquals(403, send("GET", "/api/classes/" + classTwo.get("id").asLong(), null, teacherToken)
                .statusCode());
        assertEquals(403, createEntityResponse(teacherToken, "/api/classes",
                Map.of("teacherId", otherTeacherProfile.get("id").asLong(), "className", "Not owned")).statusCode());
        assertEquals(classOne.get("id").asLong(), sendJson("GET", "/api/classes", null, teacherToken)
                .get(0).get("id").asLong());

        JsonNode assessment = createEntity(teacherToken, "/api/assessments",
                Map.of("classId", classOne.get("id").asLong(), "title", "Test assessment", "type", "Quiz"));
        JsonNode topic = createEntity(teacherToken, "/api/topics",
                Map.of("classId", classOne.get("id").asLong(), "name", "Topic"));
        JsonNode outcome = createEntity(teacherToken, "/api/learning-outcomes",
                Map.of("topicId", topic.get("id").asLong(), "description", "Outcome"));
        JsonNode item = createEntity(teacherToken, "/api/assessment-items",
                Map.of("assessmentId", assessment.get("id").asLong(),
                        "outcomeId", outcome.get("id").asLong(),
                        "itemLabel", "Q1", "maxPoints", 10));
        JsonNode enrollment = createEntity(teacherToken, "/api/enrollments",
                Map.of("studentId", studentProfile.get("id").asLong(), "classId", classOne.get("id").asLong()));
        JsonNode otherEnrollment = createEntity(teacherToken, "/api/enrollments",
                Map.of("studentId", otherStudentProfile.get("id").asLong(),
                        "classId", classOne.get("id").asLong()));
        JsonNode submission = createEntity(teacherToken, "/api/submissions",
                Map.of("enrollmentId", enrollment.get("id").asLong(),
                        "assessmentId", assessment.get("id").asLong(), "status", "SUBMITTED"));
        JsonNode otherSubmission = createEntity(teacherToken, "/api/submissions",
                Map.of("enrollmentId", otherEnrollment.get("id").asLong(),
                        "assessmentId", assessment.get("id").asLong(), "status", "SUBMITTED"));
        JsonNode score = createEntity(teacherToken, "/api/item-scores",
                Map.of("submissionId", submission.get("id").asLong(),
                        "itemId", item.get("id").asLong(), "pointsEarned", 8, "missing", false));
        JsonNode otherScore = createEntity(teacherToken, "/api/item-scores",
                Map.of("submissionId", otherSubmission.get("id").asLong(),
                        "itemId", item.get("id").asLong(), "pointsEarned", 5, "missing", false));
        assertEquals(200, send("GET", "/api/submissions/" + submission.get("id").asLong(), null, studentToken)
                .statusCode());
        assertEquals(403, send("GET", "/api/submissions/" + otherSubmission.get("id").asLong(), null, studentToken)
                .statusCode());
        assertEquals(200, send("GET", "/api/item-scores/" + score.get("id").asLong(), null, studentToken)
                .statusCode());
        assertEquals(403, send("GET", "/api/item-scores/" + otherScore.get("id").asLong(), null, studentToken)
                .statusCode());
        assertEquals(403, send("POST", "/api/submissions", "{}", studentToken).statusCode());
        assertEquals(403, send("GET", "/api/submissions/" + submission.get("id").asLong(),
                null, otherTeacherToken).statusCode());

        assertEquals(204, send("DELETE", "/api/users/" + secondAdminId, null, developerToken).statusCode());
        assertEquals(409, updateUser(developerToken, adminId, adminEmail,
                "Tudlo Admin", "ADMIN", "INACTIVE").statusCode());
        assertEquals(409, send("DELETE", "/api/users/" + adminId, null, developerToken).statusCode());
        assertEquals(409, updateUser(adminToken, userId(developerLogin), "developer@tudlo.test",
                "Tudlo Developer", "DEVELOPER", "INACTIVE").statusCode());
        assertEquals(409, send("DELETE", "/api/users/" + userId(developerLogin), null, adminToken).statusCode());

        assertEquals(401, loginResponse("admin@gmail.com", "wrong").statusCode());
        assertEquals(401, loginResponse(uniqueEmail("unknown"), "wrong").statusCode());
        User inactiveStudent = userRepository.findById(otherStudent.get("id").asLong()).orElseThrow();
        inactiveStudent.setStatus(UserStatus.INACTIVE);
        userRepository.saveAndFlush(inactiveStudent);
        assertEquals(401, loginResponse(otherStudentEmail, "StudentPass123").statusCode());

        assertEquals(401, send("GET", "/api/users", null, developerToken + "tampered").statusCode());
        assertEquals(401, send("GET", "/api/users", null, expiredToken(userId(developerLogin))).statusCode());
        JsonNode ownSubmissions = sendJson("GET", "/api/submissions", null, studentToken);
        assertTrue(ownSubmissions.isArray());
        assertEquals(1, ownSubmissions.size());
        assertFalse(ownSubmissions.get(0).has("passwordHash"));
    }

    private JsonNode login(String email, String password) throws Exception {
        HttpResponse<String> response = loginResponse(email, password);
        assertEquals(200, response.statusCode(), response.body());
        return objectMapper.readTree(response.body());
    }

    private HttpResponse<String> loginResponse(String email, String password) throws Exception {
        return send("POST", "/api/auth/login",
                objectMapper.writeValueAsString(Map.of("email", email, "password", password)), null);
    }

    private JsonNode createUser(
            String token, String email, String fullName, String userType, String password) throws Exception {
        HttpResponse<String> response = createUserResponse(token, email, fullName, userType, password);
        assertEquals(201, response.statusCode(), response.body());
        return objectMapper.readTree(response.body());
    }

    private JsonNode createUserWithToken(
            String adminEmail,
            String adminPassword,
            String email,
            String fullName,
            String userType,
            String password) throws Exception {
        JsonNode adminLogin = login(adminEmail, adminPassword);
        return createUser(adminLogin.get("token").asText(), email, fullName, userType, password);
    }

    private HttpResponse<String> createUserResponse(
            String token, String email, String fullName, String userType, String password) throws Exception {
        return send("POST", "/api/users", objectMapper.writeValueAsString(Map.of(
                "fullName", fullName,
                "email", email,
                "password", password,
                "userType", userType)), token);
    }

    private HttpResponse<String> createProfile(String token, String path, Map<String, ?> body) throws Exception {
        return send("POST", path, objectMapper.writeValueAsString(body), token);
    }

    private JsonNode createProfileAndRead(String token, String path, Map<String, ?> body) throws Exception {
        HttpResponse<String> response = createProfile(token, path, body);
        assertEquals(201, response.statusCode(), response.body());
        return objectMapper.readTree(response.body());
    }

    private JsonNode createEntity(String token, String path, Map<String, ?> body) throws Exception {
        HttpResponse<String> response = createEntityResponse(token, path, body);
        assertEquals(201, response.statusCode(), response.body());
        return objectMapper.readTree(response.body());
    }

    private HttpResponse<String> createEntityResponse(
            String token, String path, Map<String, ?> body) throws Exception {
        return send("POST", path, objectMapper.writeValueAsString(body), token);
    }

    private HttpResponse<String> updateUser(
            String token, long id, String email, String fullName, String userType, String status) throws Exception {
        return send("PUT", "/api/users/" + id, objectMapper.writeValueAsString(Map.of(
                "fullName", fullName,
                "email", email,
                "userType", userType,
                "status", status)), token);
    }

    private JsonNode sendJson(String method, String path, String body, String token) throws Exception {
        return objectMapper.readTree(send(method, path, body, token).body());
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

    private String expiredToken(long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("email", "developer@tudlo.test")
                .claim("role", "DEVELOPER")
                .claim("profileId", null)
                .claim("expiryMinutes", 480)
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(now.minusSeconds(60)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private long userId(JsonNode login) {
        return login.get("id").asLong();
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@tudlo.test";
    }

    private String uniqueStudentNo() {
        return "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
