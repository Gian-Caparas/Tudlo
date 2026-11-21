package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.Student;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.AdminRepository;
import com.csit321G6.groufings.tudlo.repository.StudentRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void startsUserIdSequenceAt0001WhenDatabaseIsEmpty() {
        UserRepository userRepository = mock(UserRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);

        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById(any())).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserService service = new UserService(
                userRepository,
                mock(TeacherRepository.class),
                studentRepository,
                mock(AdminRepository.class),
                new BCryptPasswordEncoder()
        );

        UserDto.Response created = service.create(
                new UserDto.Request("Test", "Student", "test-password-123", UserType.STUDENT, null)
        );

        assertEquals("2026-0001", created.userIdNumber());

        ArgumentCaptor<Student> savedStudent = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(savedStudent.capture());
        assertEquals("2026-0001", savedStudent.getValue().getStudentId());
        assertEquals("Test", savedStudent.getValue().getFirstName());
        assertEquals("Student", savedStudent.getValue().getLastName());
    }

    @Test
    void createsUserWithNextSequentialIdAndEncodedPassword() {
        UserRepository userRepository = mock(UserRepository.class);
        TeacherRepository teacherRepository = mock(TeacherRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AdminRepository adminRepository = mock(AdminRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        when(userRepository.findAll()).thenReturn(List.of(
                userWithId("2026-0001"),
                userWithId("2026-0002"),
                userWithId("2026-0003"),
                userWithId("2026-0004"),
                userWithId("2026-0005"),
                userWithId("2026-0006"),
                userWithId("2026-0007"),
                userWithId("2026-0008")
        ));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById(any())).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserService service = new UserService(
                userRepository,
                teacherRepository,
                studentRepository,
                adminRepository,
                passwordEncoder
        );

        UserDto.Response created = service.create(
                new UserDto.Request("Test", "Student", "test-password-123", UserType.STUDENT, null)
        );

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(savedUser.capture());
        assertEquals("2026-0009", created.userIdNumber());
        assertEquals("Test", created.firstName());
        assertEquals("Student", created.lastName());
        assertEquals(UserType.STUDENT, created.userType());
        assertTrue(passwordEncoder.matches("test-password-123", savedUser.getValue().getPassword()));
    }

    @Test
    void ensureStudentCreatesStudentWithDefaultPasswordStudent123AndIsIdempotent() {
        UserRepository userRepository = mock(UserRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        when(userRepository.findByUserIdNumber("2026-5001")).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById("2026-5001")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserService service = new UserService(
                userRepository,
                mock(TeacherRepository.class),
                studentRepository,
                mock(AdminRepository.class),
                passwordEncoder
        );

        User created = service.ensureStudent("2026-5001", "Jane", "Doe");
        assertTrue(passwordEncoder.matches("student123", created.getPassword()));
        assertEquals(UserType.STUDENT, created.getUserType());

        User nonStudent = userWithId("2026-9999");
        nonStudent.setUserType(UserType.TEACHER);
        when(userRepository.findByUserIdNumber("2026-9999")).thenReturn(Optional.of(nonStudent));
        assertThrows(ConflictException.class, () -> service.ensureStudent("2026-9999", "Fake", "Student"));
    }

    private static User userWithId(String idNumber) {
        User user = new User();
        user.setUserIdNumber(idNumber);
        return user;
    }
}
