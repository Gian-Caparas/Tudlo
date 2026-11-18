package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserService extends CrudService<User, UserDto.Request, UserDto.Response> {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    public UserService(UserRepository userRepository,
                       TeacherRepository teacherRepository,
                       StudentRepository studentRepository,
                       AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
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
        entity.setUserIdNumber(request.idNumber());
        entity.setUserType(request.userType());
        // Password is handled separately (hashed, not exposed in DTO)
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
        // Set default password for new users (should be hashed in production)
        entity.setPassword("password123");
    }

    @Override
    protected void afterCreate(User created, UserDto.Request request) {
        // Create the corresponding profile entity based on userType
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

    // --- Custom registration methods (kept for backward compatibility) ---

    @Transactional
    public User registerTeacher(String firstName, String lastName, String idNumber, String department) {
        UserDto.Request request = new UserDto.Request(firstName, lastName, idNumber, UserType.TEACHER);
        UserDto.Response response = create(request);
        return userRepository.findById(response.id()).orElseThrow();
    }

    @Transactional
    public User registerStudent(String firstName, String lastName, String idNumber, String program) {
        UserDto.Request request = new UserDto.Request(firstName, lastName, idNumber, UserType.STUDENT);
        UserDto.Response response = create(request);
        return userRepository.findById(response.id()).orElseThrow();
    }

    @Transactional
    public User registerAdmin(String firstName, String lastName, String idNumber, String roleTitle) {
        UserDto.Request request = new UserDto.Request(firstName, lastName, idNumber, UserType.ADMIN);
        UserDto.Response response = create(request);
        return userRepository.findById(response.id()).orElseThrow();
    }
}