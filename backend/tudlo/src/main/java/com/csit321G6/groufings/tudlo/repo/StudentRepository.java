package com.csit321G6.groufings.tudlo.repo;

import com.csit321G6.groufings.tudlo.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser_Id(Long userId);

}
