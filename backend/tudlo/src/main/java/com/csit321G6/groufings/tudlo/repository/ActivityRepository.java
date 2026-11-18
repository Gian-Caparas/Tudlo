package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByStudentId(String studentId);

    List<Activity> findBySubjectNameAndClassSection(String subjectName, String classSection);

    List<Activity> findByStudentIdAndSubjectNameAndClassSection(String studentId, String subjectName, String classSection);

    Optional<Activity> findByActivityNameAndStudentIdAndSubjectNameAndClassSection(
            String activityName, String studentId, String subjectName, String classSection);

    @Query("SELECT a FROM Activity a WHERE a.studentId = :studentId AND a.subjectName = :subjectName AND a.classSection = :classSection")
    List<Activity> findByStudentSubjectAndSection(@Param("studentId") String studentId, @Param("subjectName") String subjectName, @Param("classSection") String classSection);

    boolean existsByActivityNameAndStudentIdAndSubjectNameAndClassSection(
            String activityName, String studentId, String subjectName, String classSection);
}