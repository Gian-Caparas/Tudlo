package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findBySubjectNameAndClassSection(String subjectName, String classSection);

    Optional<Assessment> findByAssessmentItemAndSubjectNameAndClassSection(String assessmentItem, String subjectName, String classSection);

    @Query("SELECT a FROM Assessment a WHERE a.subjectName = :subjectName AND a.classSection = :classSection")
    List<Assessment> findBySubjectAndSection(@Param("subjectName") String subjectName, @Param("classSection") String classSection);

    boolean existsByAssessmentItemAndSubjectNameAndClassSection(String assessmentItem, String subjectName, String classSection);
}