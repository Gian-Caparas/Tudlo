package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.config.JwtUtil;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.AdminRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import com.csit321G6.groufings.tudlo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void loginWithSeededAdminReturnsTokenAndNumericProfileId() throws Exception {
        var adminUser = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        var adminProfile = adminRepository.findByUserId(adminUser.getId()).orElseThrow();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userIdNumber": "2026-0001",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.profileId").value(adminProfile.getId()))
                .andExpect(jsonPath("$.userType").value("ADMIN"))
                .andExpect(jsonPath("$.firstName").value("System"))
                .andExpect(jsonPath("$.lastName").value("Administrator"))
                .andExpect(jsonPath("$.fullName").value("System Administrator"))
                .andExpect(jsonPath("$.userIdNumber").value("2026-0001"));
    }

    @Test
    void loginReturnsTeacherIdAndStudentStringIdAsProfileId() throws Exception {
        UserDto.Response teacherUser = userService.create(
                new UserDto.Request("Maria", "Clara", "teacherPass1", UserType.TEACHER, null));
        var teacherProfile = teacherRepository.findByUserId(teacherUser.id()).orElseThrow();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userIdNumber": "%s",
                                  "password": "teacherPass1"
                                }
                                """.formatted(teacherUser.userIdNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(teacherProfile.getId()))
                .andExpect(jsonPath("$.userType").value("TEACHER"));

        UserDto.Response studentUser = userService.create(
                new UserDto.Request("Juan", "Dela Cruz", "studentPass1", UserType.STUDENT, null));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userIdNumber": "%s",
                                  "password": "studentPass1"
                                }
                                """.formatted(studentUser.userIdNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(studentUser.userIdNumber()))
                .andExpect(jsonPath("$.userType").value("STUDENT"));
    }

    @Test
    void loginWithInvalidCredentialsReturns401Json() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userIdNumber": "2026-0001",
                                  "password": "wrongpassword"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    void meWithoutTokenOrWithStaleTokenReturns401Json() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/auth/me"));

        String deletedUserToken = jwtUtil.generateToken("2026-9999", "STUDENT", 9999L);
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + deletedUserToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void meWithValidTokenReturnsProfileDetails() throws Exception {
        var adminUser = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        var adminProfile = adminRepository.findByUserId(adminUser.getId()).orElseThrow();
        String token = jwtUtil.generateToken(adminUser.getUserIdNumber(), "ADMIN", adminUser.getId());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.profileId").value(adminProfile.getId()))
                .andExpect(jsonPath("$.firstName").value("System"))
                .andExpect(jsonPath("$.lastName").value("Administrator"))
                .andExpect(jsonPath("$.userIdNumber").value("2026-0001"))
                .andExpect(jsonPath("$.userType").value("ADMIN"));
    }
}
