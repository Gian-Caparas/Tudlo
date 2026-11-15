package com.csit321G6.groufings.tudlo.common;

import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.AdminRepository;
import com.csit321G6.groufings.tudlo.repo.StudentRepository;
import com.csit321G6.groufings.tudlo.repo.TeacherRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserProfileLookup {

    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public UserProfileLookup(
            AdminRepository adminRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository) {
        this.adminRepository = adminRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public Optional<Long> findProfileId(Long userId, UserType userType) {
        return switch (userType) {
            case ADMIN -> adminRepository.findByUser_Id(userId).map(profile -> profile.getId());
            case DEVELOPER -> Optional.empty();
            case TEACHER -> teacherRepository.findByUser_Id(userId).map(profile -> profile.getId());
            case STUDENT -> studentRepository.findByUser_Id(userId).map(profile -> profile.getId());
        };
    }
}
