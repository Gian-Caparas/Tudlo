package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.ClassList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassListRepository extends JpaRepository<ClassList, Long> {

    List<ClassList> findBySubjectNameAndClassSection(String subjectName, String classSection);

    List<ClassList> findByStudentId(String studentId);

    @Query("SELECT cl FROM ClassList cl WHERE cl.subjectName = :subjectName AND cl.classSection = :classSection")
    List<ClassList> findBySubjectAndSection(@Param("subjectName") String subjectName, @Param("classSection") String classSection);

    boolean existsBySubjectNameAndClassSectionAndStudentId(String subjectName, String classSection, String studentId);
}