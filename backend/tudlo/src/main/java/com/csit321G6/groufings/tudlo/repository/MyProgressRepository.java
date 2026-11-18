package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.MyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MyProgressRepository extends JpaRepository<MyProgress, Long> {

    List<MyProgress> findByStudentId(String studentId);

    List<MyProgress> findBySubjectNameAndClassSection(String subjectName, String classSection);

    Optional<MyProgress> findByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);

    @Query("SELECT mp FROM MyProgress mp WHERE mp.studentId = :studentId ORDER BY mp.subjectName")
    List<MyProgress> findByStudentOrderBySubject(@Param("studentId") String studentId);

    boolean existsByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);
}