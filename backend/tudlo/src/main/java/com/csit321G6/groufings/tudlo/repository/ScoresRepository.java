package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Scores;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("null")
public interface ScoresRepository extends JpaRepository<Scores, Long> {

    @Override
    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Scores> findAll();

    @Override
    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    Optional<Scores> findById(Long id);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Scores> findByStudentStudentId(String studentId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Scores> findByOfferingId(Long offeringId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Scores> findByOfferingTeacherId(Long teacherId);

    Optional<Scores> findByOfferingIdAndStudentStudentId(Long offeringId, String studentId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Scores> findByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Scores> findByStudentStudentIdAndOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCase(
            String studentId, String subjectName, String sectionName);
}
