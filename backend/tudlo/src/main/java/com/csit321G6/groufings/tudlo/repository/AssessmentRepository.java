package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Assessment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    @Override
    @EntityGraph(attributePaths = {"offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Assessment> findAll();

    @Override
    @EntityGraph(attributePaths = {"offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    Optional<Assessment> findById(Long id);

    @EntityGraph(attributePaths = {"offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Assessment> findByOfferingId(Long offeringId);

    @EntityGraph(attributePaths = {"offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Assessment> findByOfferingTeacherId(Long teacherId);

    @EntityGraph(attributePaths = {"offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<Assessment> findByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);

    Optional<Assessment> findByOfferingIdAndAssessmentItemIgnoreCase(Long offeringId, String assessmentItem);

    boolean existsByOfferingIdAndAssessmentItemIgnoreCase(Long offeringId, String assessmentItem);

    long countByOfferingId(Long offeringId);
}
