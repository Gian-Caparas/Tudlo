package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.SubjectOffering;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectOfferingRepository extends JpaRepository<SubjectOffering, Long> {

    @Override
    @EntityGraph(attributePaths = {"teacher", "teacher.user", "subject", "section"})
    List<SubjectOffering> findAll();

    @Override
    @EntityGraph(attributePaths = {"teacher", "teacher.user", "subject", "section"})
    Optional<SubjectOffering> findById(Long id);

    @EntityGraph(attributePaths = {"teacher", "teacher.user", "subject", "section"})
    List<SubjectOffering> findByTeacherId(Long teacherId);

    Optional<SubjectOffering> findByTeacherIdAndSubjectIdAndSectionId(Long teacherId, Long subjectId, Long sectionId);

    @EntityGraph(attributePaths = {"teacher", "teacher.user", "subject", "section"})
    Optional<SubjectOffering> findByTeacherIdAndSubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(
            Long teacherId, String subjectName, String sectionName);

    @EntityGraph(attributePaths = {"teacher", "teacher.user", "subject", "section"})
    List<SubjectOffering> findBySubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);

    boolean existsByTeacherIdAndSubjectIdAndSectionId(Long teacherId, Long subjectId, Long sectionId);

    @Query("SELECT cl.offering FROM ClassList cl " +
           "JOIN FETCH cl.offering.teacher t " +
           "JOIN FETCH t.user " +
           "JOIN FETCH cl.offering.subject " +
           "JOIN FETCH cl.offering.section " +
           "WHERE cl.student.studentId = :studentId")
    List<SubjectOffering> findEnrolledByStudentId(@Param("studentId") String studentId);
}
