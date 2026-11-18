package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.LearningGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningGapRepository extends JpaRepository<LearningGap, Long> {

    List<LearningGap> findBySubjectNameAndClassSection(String subjectName, String classSection);

    List<LearningGap> findByStudentId(String studentId);

    List<LearningGap> findByIsAtRiskTrue();

    List<LearningGap> findBySubjectNameAndClassSectionAndIsAtRiskTrue(String subjectName, String classSection);

    @Query("SELECT lg FROM LearningGap lg WHERE lg.subjectName = :subjectName AND lg.classSection = :classSection AND lg.isAtRisk = true ORDER BY lg.percentage ASC")
    List<LearningGap> findAtRiskStudentsOrderByPercentage(@Param("subjectName") String subjectName, @Param("classSection") String classSection);

    @Query("SELECT lg FROM LearningGap lg WHERE lg.gapSeverity = :severity")
    List<LearningGap> findByGapSeverity(@Param("severity") String severity);

    Optional<LearningGap> findByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);

    boolean existsByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);
}