package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.config.JwtUtil;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.StudentRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RbacAndOwnershipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void unauthenticatedRequestsReturn401() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/subjects")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/my-progress/student/2026-0002")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminCannotCreateStudentsButCanCreateTeachersAndUpdateUserNames() throws Exception {
        var adminUser = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        String adminToken = jwtUtil.generateToken(adminUser.getUserIdNumber(), "ADMIN", adminUser.getId());

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ana",
                                  "lastName": "Reyes",
                                  "password": "password123",
                                  "userType": "STUDENT"
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertFalse(userRepository.existsByUserIdNumber("2026-0002"));

        MvcResult teacherResult = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Pedro",
                                  "lastName": "Santos",
                                  "password": "teacherPass1",
                                  "userType": "TEACHER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userType").value("TEACHER"))
                .andReturn();
        JsonNode teacherJson = objectMapper.readTree(teacherResult.getResponse().getContentAsString());
        long teacherId = teacherJson.get("id").asLong();
        String teacherIdNumber = teacherJson.get("userIdNumber").asText();
        assertTrue(teacherRepository.findByUserId(teacherId).isPresent());

        var student = userService.ensureStudent("2026-5001", "Jane", "Student");
        mockMvc.perform(put("/api/users/{id}", teacherId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Peter",
                                  "lastName": "Santos",
                                  "userType": "TEACHER"
                                }
                                """))
                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("Peter"))
                                .andExpect(jsonPath("$.lastName").value("Santos"));

        mockMvc.perform(put("/api/users/{id}", student.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Janet",
                                  "lastName": "Student",
                                  "userType": "STUDENT"
                                }
                                """))
                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("Janet"))
                                .andExpect(jsonPath("$.lastName").value("Student"));

        var updatedStudent = studentRepository.findById(student.getUserIdNumber()).orElseThrow();
        assertEquals("Janet", updatedStudent.getFirstName());
        assertEquals("Student", updatedStudent.getLastName());

        String teacherToken = jwtUtil.generateToken(teacherIdNumber, "TEACHER", teacherId);
        String studentToken = jwtUtil.generateToken(student.getUserIdNumber(), "STUDENT", student.getId());
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void enforcesTeacherAndStudentOwnershipAcrossOfferingsAndSupport() throws Exception {
        UserDto.Response teacherAUser = userService.create(
                new UserDto.Request("Alice", "Gomez", "teacherPass1", UserType.TEACHER, null));
        UserDto.Response teacherBUser = userService.create(
                new UserDto.Request("Bob", "Navarro", "teacherPass1", UserType.TEACHER, null));
        Long teacherAId = teacherRepository.findByUserId(teacherAUser.id()).orElseThrow().getId();
        Long teacherBId = teacherRepository.findByUserId(teacherBUser.id()).orElseThrow().getId();

        String teacherAToken = jwtUtil.generateToken(teacherAUser.userIdNumber(), "TEACHER", teacherAUser.id());
        String teacherBToken = jwtUtil.generateToken(teacherBUser.userIdNumber(), "TEACHER", teacherBUser.id());

        // Teacher A creates offering (Math 101, G6)
        MvcResult subjectRes = mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + teacherAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "subjectName": "Math 101",
                                  "classSection": "G6",
                                  "teacherId": %d
                                }
                                """.formatted(teacherAId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.offeringId").isNumber())
                .andReturn();
        long offeringAId = objectMapper.readTree(subjectRes.getResponse().getContentAsString()).get("offeringId").asLong();

        // Teacher B creates offering with SAME (Math 101, G6) -> succeeds with distinct offeringId!
        MvcResult subjectBRes = mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + teacherBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "subjectName": "Math 101",
                                  "classSection": "G6",
                                  "teacherId": %d
                                }
                                """.formatted(teacherBId)))
                .andExpect(status().isCreated())
                .andReturn();
        long offeringBId = objectMapper.readTree(subjectBRes.getResponse().getContentAsString()).get("offeringId").asLong();
        assertTrue(offeringAId != offeringBId);

        // Teacher A bulk-imports two students (auto-created with default password "student123")
        mockMvc.perform(post("/api/class-lists/bulk-import")
                        .header("Authorization", "Bearer " + teacherAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "offeringId": %d,
                                  "students": [
                                    {"studentId": "2026-0100", "firstName": "Low", "lastName": "Scorer"},
                                    {"studentId": "2026-0101", "firstName": "High", "lastName": "Scorer"}
                                  ]
                                }
                                """.formatted(offeringAId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Auto-created student can log in with "student123"
        MvcResult studentLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userIdNumber": "2026-0100",
                                  "password": "student123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value("2026-0100"))
                .andReturn();
        JsonNode studentLoginJson = objectMapper.readTree(studentLogin.getResponse().getContentAsString());
        String student100Token = studentLoginJson.get("token").asString();

        // Teacher A creates Quiz 1 (maxScore 100) and grades 2026-0100 at 55 (<60%) and 2026-0101 at 60 (>=60%)
        MvcResult assessRes = mockMvc.perform(post("/api/assessments")
                        .header("Authorization", "Bearer " + teacherAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "offeringId": %d,
                                  "assessmentItem": "Quiz 1",
                                  "learningOutcome": "Algebra",
                                  "maxScore": 100.0
                                }
                                """.formatted(offeringAId)))
                .andExpect(status().isCreated())
                .andReturn();
        long assessmentId = objectMapper.readTree(assessRes.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + teacherAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assessmentId": %d,
                                  "studentId": "2026-0100",
                                  "score": 55.0
                                }
                                """.formatted(assessmentId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + teacherAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assessmentId": %d,
                                  "studentId": "2026-0101",
                                  "score": 60.0
                                }
                                """.formatted(assessmentId)))
                .andExpect(status().isCreated());

        // Support endpoint returns strictly < 60% (only 2026-0100, excluding 60.0%)
        mockMvc.perform(get("/api/learning-gaps/at-risk/teacher/" + teacherAId)
                        .header("Authorization", "Bearer " + teacherAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentId").value("2026-0100"))
                .andExpect(jsonPath("$[0].isAtRisk").value(true));

        // Teacher B gets 403 accessing Teacher A's offering/class-list/at-risk endpoints
        mockMvc.perform(get("/api/class-lists/offering/" + offeringAId)
                        .header("Authorization", "Bearer " + teacherBToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/learning-gaps/at-risk/teacher/" + teacherAId)
                        .header("Authorization", "Bearer " + teacherBToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/assessments")
                        .header("Authorization", "Bearer " + teacherBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "offeringId": %d,
                                  "assessmentItem": "Hacked Quiz",
                                  "learningOutcome": "None",
                                  "maxScore": 10.0
                                }
                                """.formatted(offeringAId)))
                .andExpect(status().isForbidden());

        // Student can read own progress & activities, gets 403 on other student & on academic writes
        mockMvc.perform(get("/api/my-progress/student/2026-0100")
                        .header("Authorization", "Bearer " + student100Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offeringId").value(offeringAId));

        mockMvc.perform(get("/api/activities/student/2026-0100")
                        .header("Authorization", "Bearer " + student100Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/my-progress/student/2026-0101")
                        .header("Authorization", "Bearer " + student100Token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/activities/student/2026-0101")
                        .header("Authorization", "Bearer " + student100Token))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + student100Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "subjectName": "Hack",
                                  "classSection": "G1",
                                  "teacherId": %d
                                }
                                """.formatted(teacherAId)))
                .andExpect(status().isForbidden());
    }
}
