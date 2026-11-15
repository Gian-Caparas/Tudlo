package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DeveloperBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String developerEmail;
    private final String developerPassword;

    public DeveloperBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.developer-email:}") String developerEmail,
            @Value("${app.bootstrap.developer-password:}") String developerPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.developerEmail = developerEmail;
        this.developerPassword = developerPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (developerEmail.isBlank() || developerPassword.isBlank()
                || userRepository.existsByUserType(UserType.DEVELOPER)) {
            return;
        }

        String normalizedEmail = developerEmail.trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalStateException(
                    "The configured developer bootstrap email is already assigned to another account.");
        }

        User developer = new User();
        developer.setFullName("Tudlo Developer");
        developer.setEmail(normalizedEmail);
        developer.setPasswordHash(passwordEncoder.encode(developerPassword));
        developer.setUserType(UserType.DEVELOPER);
        developer.setStatus(UserStatus.ACTIVE);
        userRepository.saveAndFlush(developer);
    }
}
