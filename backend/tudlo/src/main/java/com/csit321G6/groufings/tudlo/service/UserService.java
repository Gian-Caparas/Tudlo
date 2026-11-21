package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService extends CrudService<User, UserDto.Request, UserDto.Response> {

    public static final String DEFAULT_STUDENT_PASSWORD = "student123";

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       TeacherRepository teacherRepository,
                       StudentRepository studentRepository,
                       AdminRepository adminRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected UserRepository repository() {
        return userRepository;
    }

    @Override
    protected String entityName() {
        return "User";
    }

    @Override
    protected User newEntity() {
        return new User();
    }

    @Override
    protected void apply(User entity, UserDto.Request request) {
        entity.setFirstName(request.firstName().trim());
        entity.setLastName(request.lastName().trim());
        entity.setUserType(request.userType());
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected UserDto.Response toResponse(User entity) {
        return new UserDto.Response(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getUserIdNumber(),
                entity.getUserType(),
                entity.getCreatedAt()
        );
    }

    @Override
    protected void beforeCreate(User entity, UserDto.Request request) {
        // Allow Admin to specify userIdNumber; otherwise auto-generate
        if (request.userIdNumber() != null && !request.userIdNumber().trim().isEmpty()) {
            String providedId = request.userIdNumber().trim();
            if (userRepository.existsByUserIdNumber(providedId)) {
                throw new ConflictException("User ID number already exists: " + providedId);
            }
            entity.setUserIdNumber(providedId);
        } else {
            entity.setUserIdNumber(nextUserIdNumber());
        }
        entity.setPassword(passwordEncoder.encode(request.password()));
    }

    @Override
    protected void beforeUpdate(User entity, UserDto.Request request) {
        if (entity.getUserType() != null && entity.getUserType() != request.userType()) {
            throw new BadRequestException("User type cannot be changed after creation");
        }
        // Password is optional on update - only re-encode if provided
        if (request.password() != null && !request.password().trim().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(request.password()));
        }
    }

    @Override
    protected void afterCreate(User created, UserDto.Request request) {
        createProfile(created);
    }

    @Override
    protected void afterUpdate(User updated, UserDto.Request request) {
        if (updated.getUserType() == UserType.STUDENT) {
            Student student = studentRepository.findById(updated.getUserIdNumber()).orElseGet(Student::new);
            student.setStudentId(updated.getUserIdNumber());
            student.setFirstName(updated.getFirstName());
            student.setLastName(updated.getLastName());
            studentRepository.save(student);
        }
    }

    @Override
    protected void beforeDelete(User existing) {
        if (existing.getUserType() == UserType.STUDENT && studentRepository.existsById(existing.getUserIdNumber())) {
            studentRepository.deleteById(existing.getUserIdNumber());
        }
    }

    private void createProfile(User created) {
        switch (created.getUserType()) {
            case TEACHER -> {
                if (teacherRepository.findByUserId(created.getId()).isEmpty()) {
                    Teacher teacher = new Teacher();
                    teacher.setUser(created);
                    created.setTeacher(teacher);
                    teacherRepository.save(teacher);
                }
            }
            case STUDENT -> {
                Student student = studentRepository.findById(created.getUserIdNumber()).orElseGet(Student::new);
                student.setStudentId(created.getUserIdNumber());
                student.setFirstName(created.getFirstName());
                student.setLastName(created.getLastName());
                studentRepository.save(student);
            }
            case ADMIN -> {
                if (adminRepository.findByUserId(created.getId()).isEmpty()) {
                    Admin admin = new Admin();
                    admin.setUser(created);
                    created.setAdmin(admin);
                    adminRepository.save(admin);
                }
            }
        }
    }

    private String nextUserIdNumber() {
        long highestSequence = userRepository.findAll().stream()
                .map(User::getUserIdNumber)
                .filter(id -> id != null && id.startsWith("2026-"))
                .map(id -> id.substring("2026-".length()))
                .filter(suffix -> suffix.matches("[0-9]+"))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0);

        if (highestSequence >= 999_999_999_999_999L) {
            throw new IllegalStateException("No user ID numbers remain in the 2026 sequence");
        }
        return String.format(Locale.ROOT, "2026-%04d", highestSequence + 1);
    }

    @Transactional
    public User ensureStudent(String studentId, String firstName, String lastName) {
        return registerWithId(firstName, lastName, studentId, UserType.STUDENT);
    }

    @Transactional
    public User registerWithId(String firstName, String lastName, String idNumber, UserType userType) {
        String normalizedId = idNumber != null ? idNumber.trim() : "";
        String normalizedFirst = firstName != null ? firstName.trim() : "";
        String normalizedLast = lastName != null ? lastName.trim() : "";
        if (normalizedId.isEmpty() || normalizedFirst.isEmpty() || normalizedLast.isEmpty()) {
            throw new BadRequestException("Student ID, first name, and last name are required");
        }

        Optional<User> existingOpt = userRepository.findByUserIdNumber(normalizedId);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            if (existing.getUserType() != userType) {
                throw new ConflictException("User ID " + normalizedId + " already belongs to a non-" + userType.name().toLowerCase(Locale.ROOT) + " account");
            }
            createProfile(existing);
            return existing;
        }

        User user = new User();
        user.setFirstName(normalizedFirst);
        user.setLastName(normalizedLast);
        user.setUserIdNumber(normalizedId);
        String defaultRawPassword = userType == UserType.STUDENT ? DEFAULT_STUDENT_PASSWORD : "password123";
        user.setPassword(passwordEncoder.encode(defaultRawPassword));
        user.setUserType(userType);
        user.setCreatedAt(LocalDateTime.now());
        User created = userRepository.saveAndFlush(user);
        createProfile(created);
        return created;
    }
}
