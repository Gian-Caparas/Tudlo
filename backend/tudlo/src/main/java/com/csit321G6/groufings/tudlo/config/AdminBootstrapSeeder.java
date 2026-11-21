package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.entity.Admin;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.AdminRepository;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Component
public class AdminBootstrapSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapSeeder.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    private final String adminUserIdNumber;
    private final String adminPassword;
    private final String adminFirstName;
    private final String adminLastName;

    public AdminBootstrapSeeder(
            UserRepository userRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            TransactionTemplate transactionTemplate,
            @Value("${app.seed.admin.user-id-number:2026-0001}") String adminUserIdNumber,
            @Value("${app.seed.admin.password:admin123}") String adminPassword,
            @Value("${app.seed.admin.first-name:System}") String adminFirstName,
            @Value("${app.seed.admin.last-name:Administrator}") String adminLastName) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
        this.adminUserIdNumber = adminUserIdNumber;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfEmpty();
    }

    public void seedIfEmpty() {
        transactionTemplate.executeWithoutResult(status -> {
            if (userRepository.count() > 0) {
                return;
            }
            User adminUser = new User();
            adminUser.setUserIdNumber(adminUserIdNumber);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFirstName(adminFirstName);
            adminUser.setLastName(adminLastName);
            adminUser.setUserType(UserType.ADMIN);
            adminUser.setCreatedAt(LocalDateTime.now());

            User savedUser = userRepository.saveAndFlush(adminUser);

            Admin adminProfile = new Admin();
            adminProfile.setUser(savedUser);
            savedUser.setAdmin(adminProfile);
            adminRepository.save(adminProfile);

            log.info("Seeded default Admin user: {}", adminUserIdNumber);
        });
    }
}
