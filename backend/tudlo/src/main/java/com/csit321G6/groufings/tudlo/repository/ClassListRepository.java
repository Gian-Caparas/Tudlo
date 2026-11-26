package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.ClassList;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("null")
public interface ClassListRepository extends JpaRepository<ClassList, Long> {

    @Override
    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<ClassList> findAll();

    @Override
    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    Optional<ClassList> findById(Long id);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<ClassList> findByOfferingId(Long offeringId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<ClassList> findByStudentStudentId(String studentId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<ClassList> findByOfferingTeacherId(Long teacherId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<ClassList> findByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);

    Optional<ClassList> findByOfferingIdAndStudentStudentId(Long offeringId, String studentId);

    boolean existsByOfferingIdAndStudentStudentId(Long offeringId, String studentId);

    boolean existsByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCaseAndStudentStudentId(
            String subjectName, String sectionName, String studentId);
}
