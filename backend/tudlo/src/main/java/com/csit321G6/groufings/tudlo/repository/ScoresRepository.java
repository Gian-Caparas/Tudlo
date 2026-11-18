package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Scores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoresRepository extends JpaRepository<Scores, Long> {

    List<Scores> findByStudentId(String studentId);

    List<Scores> findBySubjectNameAndClassSection(String subjectName, String classSection);

    List<Scores> findByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);

    @Query("SELECT s FROM Scores s WHERE s.studentId = :studentId AND s.subjectName = :subjectName AND s.classSection = :classSection")
    List<Scores> findByStudentSubjectAndSection(@Param("studentId") String studentId, @Param("subjectName") String subjectName, @Param("classSection") String classSection);

    boolean existsByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);
}