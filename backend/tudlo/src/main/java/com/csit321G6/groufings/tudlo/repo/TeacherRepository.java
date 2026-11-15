package com.csit321G6.groufings.tudlo.repo;

import com.csit321G6.groufings.tudlo.entity.Teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUser_Id(Long userId);

}
