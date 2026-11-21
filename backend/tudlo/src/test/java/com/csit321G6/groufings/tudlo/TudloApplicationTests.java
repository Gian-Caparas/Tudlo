package com.csit321G6.groufings.tudlo;

import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class TudloApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoadsAndSeedsSingleAdmin() {
        assertEquals(1, userRepository.count());
        User admin = userRepository.findByUserIdNumber("2026-0001").orElseThrow();
        assertEquals(UserType.ADMIN, admin.getUserType());
        assertTrue(admin.getFirstName().equals("System"));
    }
}
