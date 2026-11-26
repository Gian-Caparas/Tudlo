package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByStudentIdIgnoreCase(String studentId);
    List<Student> findByStudentIdIn(Collection<String> studentIds);
}
