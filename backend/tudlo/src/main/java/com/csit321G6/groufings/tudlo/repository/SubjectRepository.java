package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByTeacherId(Long teacherId);

    List<Subject> findBySubjectNameAndClassSection(String subjectName, String classSection);

    Optional<Subject> findBySubjectNameAndClassSectionAndTeacherId(String subjectName, String classSection, Long teacherId);

    @Query("SELECT s FROM Subject s WHERE s.teacher.id = :teacherId")
    List<Subject> findByTeacher(@Param("teacherId") Long teacherId);

    boolean existsBySubjectNameAndClassSectionAndTeacherId(String subjectName, String classSection, Long teacherId);
}