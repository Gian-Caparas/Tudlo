package com.csit321G6.groufings.tudlo.security;

import com.csit321G6.groufings.tudlo.common.exception.ForbiddenException;
import com.csit321G6.groufings.tudlo.entity.SubjectOffering;
import com.csit321G6.groufings.tudlo.entity.Teacher;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.ClassListRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccessPolicy {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final ClassListRepository classListRepository;

    public AccessPolicy(
            UserRepository userRepository,
            TeacherRepository teacherRepository,
            ClassListRepository classListRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.classListRepository = classListRepository;
    }

    public Optional<User> currentUserOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return userRepository.findByUserIdNumber(auth.getName());
    }

    public User requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        return userRepository.findByUserIdNumber(auth.getName())
                .orElseThrow(() -> new InsufficientAuthenticationException("Authenticated user not found"));
    }

    public boolean isAdmin(User user) {
        return user.getUserType() == UserType.ADMIN;
    }

    public boolean isTeacher(User user) {
        return user.getUserType() == UserType.TEACHER;
    }

    public boolean isStudent(User user) {
        return user.getUserType() == UserType.STUDENT;
    }

    public Teacher requireCurrentTeacher() {
        User user = requireCurrentUser();
        if (user.getUserType() != UserType.TEACHER) {
            throw new ForbiddenException("Teacher role required");
        }
        return teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForbiddenException("Teacher profile not found for current user"));
    }

    public Long currentTeacherIdOrNull() {
        User user = requireCurrentUser();
        if (user.getUserType() == UserType.TEACHER) {
            return teacherRepository.findByUserId(user.getId())
                    .map(Teacher::getId)
                    .orElseThrow(() -> new ForbiddenException("Teacher profile not found for current user"));
        }
        return null;
    }

    public void requireTeacherOrAdmin() {
        User user = requireCurrentUser();
        if (user.getUserType() != UserType.ADMIN && user.getUserType() != UserType.TEACHER) {
            throw new ForbiddenException("Teacher or Admin role required");
        }
    }

    public void requireTeacherAccess(Long targetTeacherId) {
        User user = requireCurrentUser();
        if (user.getUserType() == UserType.ADMIN) {
            return;
        }
        if (user.getUserType() == UserType.TEACHER) {
            Teacher currentTeacher = requireCurrentTeacher();
            if (!currentTeacher.getId().equals(targetTeacherId)) {
                throw new ForbiddenException("You are not authorized to access another teacher's resources");
            }
            return;
        }
        throw new ForbiddenException("Teacher or Admin role required");
    }

    public void requireOfferingWriteAccess(SubjectOffering offering) {
        User user = requireCurrentUser();
        if (user.getUserType() == UserType.ADMIN) {
            return;
        }
        if (user.getUserType() == UserType.TEACHER) {
            Teacher currentTeacher = requireCurrentTeacher();
            if (offering.getTeacher() == null || !currentTeacher.getId().equals(offering.getTeacher().getId())) {
                throw new ForbiddenException("You are not authorized to modify another teacher's offering");
            }
            return;
        }
        throw new ForbiddenException("Teacher or Admin role required");
    }

    public void requireOfferingReadAccess(SubjectOffering offering) {
        User user = requireCurrentUser();
        if (user.getUserType() == UserType.ADMIN) {
            return;
        }
        if (user.getUserType() == UserType.TEACHER) {
            Teacher currentTeacher = requireCurrentTeacher();
            if (offering.getTeacher() == null || !currentTeacher.getId().equals(offering.getTeacher().getId())) {
                throw new ForbiddenException("You are not authorized to access another teacher's offering");
            }
            return;
        }
        if (user.getUserType() == UserType.STUDENT) {
            boolean enrolled = classListRepository.existsByOfferingIdAndStudentStudentId(
                    offering.getId(), user.getUserIdNumber());
            if (!enrolled) {
                throw new ForbiddenException("You are not enrolled in this subject offering");
            }
            return;
        }
        throw new ForbiddenException("Access denied");
    }

    public void requireStudentReadAccess(String studentId) {
        User user = requireCurrentUser();
        if (user.getUserType() == UserType.ADMIN || user.getUserType() == UserType.TEACHER) {
            return;
        }
        if (user.getUserType() == UserType.STUDENT) {
            if (!user.getUserIdNumber().equalsIgnoreCase(studentId)) {
                throw new ForbiddenException("Students may only access their own records");
            }
            return;
        }
        throw new ForbiddenException("Access denied");
    }
}
