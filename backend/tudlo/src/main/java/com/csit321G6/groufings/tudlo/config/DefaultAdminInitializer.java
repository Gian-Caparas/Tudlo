package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@gmail.com";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public DefaultAdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.password:}") String defaultPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        var existingAdmin = userRepository.findByEmailIgnoreCase(DEFAULT_ADMIN_EMAIL);
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            if (!isBcryptHash(admin.getPasswordHash())) {
                admin.setPasswordHash(passwordEncoder.encode(admin.getPasswordHash()));
                userRepository.saveAndFlush(admin);
            }
            return;
        }
        if (defaultPassword.isBlank()) {
            throw new IllegalStateException(
                    "Set APP_BOOTSTRAP_ADMIN_PASSWORD to create the initial admin account.");
        }

        User admin = new User();
        admin.setFullName("Tudlo Admin");
        admin.setEmail(DEFAULT_ADMIN_EMAIL);
        admin.setPasswordHash(passwordEncoder.encode(defaultPassword));
        admin.setUserType(UserType.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        userRepository.saveAndFlush(admin);
    }

    private boolean isBcryptHash(String value) {
        return value != null
                && value.length() == 60
                && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }
}
