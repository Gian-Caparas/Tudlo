package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String firstName, String lastName, String userIdNumber, String password, com.csit321G6.groufings.tudlo.entity.UserType userType) {
        if (userRepository.findByUserIdNumber(userIdNumber).isPresent()) {
            throw new IllegalArgumentException("User ID number already exists: " + userIdNumber);
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserIdNumber(userIdNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserType(userType);
        user.setCreatedAt(java.time.LocalDateTime.now());

        return userRepository.save(user);
    }

    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}