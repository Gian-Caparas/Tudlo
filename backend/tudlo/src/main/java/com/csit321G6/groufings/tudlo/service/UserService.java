package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class UserService extends CrudService<User, UserDto.Request, UserDto.Response> {

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

    // --- CrudService abstract methods ---

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
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
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
        entity.setUserIdNumber(nextUserIdNumber());
        entity.setPassword(passwordEncoder.encode(request.password()));
    }

    @Override
    protected void beforeUpdate(User entity, UserDto.Request request) {
        entity.setPassword(passwordEncoder.encode(request.password()));
    }

    @Override
    protected void afterCreate(User created, UserDto.Request request) {
        createProfile(created);
    }

    private void createProfile(User created) {
        switch (created.getUserType()) {
            case TEACHER -> {
                Teacher teacher = new Teacher();
                teacher.setUser(created);
                created.setTeacher(teacher);
                teacherRepository.save(teacher);
            }
            case STUDENT -> {
                Student student = new Student();
                student.setUser(created);
                created.setStudent(student);
                studentRepository.save(student);
            }
            case ADMIN -> {
                Admin admin = new Admin();
                admin.setUser(created);
                created.setAdmin(admin);
                adminRepository.save(admin);
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

    // --- Custom registration methods (kept for backward compatibility) ---

    @Transactional
    public User registerTeacher(String firstName, String lastName, String idNumber, String department) {
        return registerWithId(firstName, lastName, idNumber, UserType.TEACHER);
    }

    @Transactional
    public User registerStudent(String firstName, String lastName, String idNumber, String program) {
        return registerWithId(firstName, lastName, idNumber, UserType.STUDENT);
    }

    @Transactional
    public User registerAdmin(String firstName, String lastName, String idNumber, String roleTitle) {
        return registerWithId(firstName, lastName, idNumber, UserType.ADMIN);
    }

    private User registerWithId(String firstName, String lastName, String idNumber, UserType userType) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserIdNumber(idNumber);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setUserType(userType);
        user.setCreatedAt(LocalDateTime.now());
        User created = userRepository.saveAndFlush(user);
        createProfile(created);
        return created;
    }
}