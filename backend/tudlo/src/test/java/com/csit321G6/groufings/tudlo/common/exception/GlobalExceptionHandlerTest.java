package com.csit321G6.groufings.tudlo.common.exception;

import com.csit321G6.groufings.tudlo.config.JwtUtil;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.UserType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void validationErrorsReturn400WithFieldErrors() throws Exception {
        var adminUser = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        String adminToken = jwtUtil.generateToken(adminUser.getUserIdNumber(), "ADMIN", adminUser.getId());

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "User",
                                  "password": "short",
                                  "userType": "STUDENT"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        var adminUser = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        String adminToken = jwtUtil.generateToken(adminUser.getUserIdNumber(), "ADMIN", adminUser.getId());

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingResourceReturns404() throws Exception {
        var adminUser = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        String adminToken = jwtUtil.generateToken(adminUser.getUserIdNumber(), "ADMIN", adminUser.getId());

        mockMvc.perform(get("/api/users/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void duplicateOfferingReturns409Conflict() throws Exception {
        UserDto.Response teacherUser = userService.create(
                new UserDto.Request("Tessa", "Cruz", "teacherPass1", UserType.TEACHER, null));
        Long teacherId = teacherRepository.findByUserId(teacherUser.id()).orElseThrow().getId();
        String teacherToken = jwtUtil.generateToken(teacherUser.userIdNumber(), "TEACHER", teacherUser.id());

        String body = """
                {
                  "subjectName": "Science 101",
                  "classSection": "G8",
                  "teacherId": %d
                }
                """.formatted(teacherId);

        mockMvc.perform(post("/api/offerings")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/offerings")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
