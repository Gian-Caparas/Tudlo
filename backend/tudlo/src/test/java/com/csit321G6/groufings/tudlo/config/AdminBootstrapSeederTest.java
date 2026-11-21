package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AdminBootstrapSeederTest {

    @Autowired
    private AdminBootstrapSeeder adminBootstrapSeeder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectOfferingRepository offeringRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void seedsExactlyOneAdminAndNoAcademicOrDemoRows() {
        assertEquals(1, userRepository.count());
        assertEquals(1, adminRepository.count());
        assertEquals(0, teacherRepository.count());
        assertEquals(0, studentRepository.count());
        assertEquals(0, offeringRepository.count());

        User admin = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        assertEquals(UserType.ADMIN, admin.getUserType());
        assertEquals("System", admin.getFirstName());
        assertEquals("Administrator", admin.getLastName());
        assertTrue(passwordEncoder.matches("admin123", admin.getPassword()));
    }

    @Test
    void reRunningSeederWhenDatabaseIsNonEmptyIsIdempotent() {
        long initialUsers = userRepository.count();
        long initialAdmins = adminRepository.count();

        adminBootstrapSeeder.seedIfEmpty();

        assertEquals(initialUsers, userRepository.count());
        assertEquals(initialAdmins, adminRepository.count());
    }
}
