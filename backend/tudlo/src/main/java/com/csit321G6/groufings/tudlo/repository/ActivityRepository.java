package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Activity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Override
    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findAll();

    @Override
    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    Optional<Activity> findById(Long id);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByStudentStudentId(String studentId);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByAssessmentId(Long assessmentId);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByAssessmentOfferingId(Long offeringId);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByAssessmentOfferingTeacherId(Long teacherId);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByStudentStudentIdAndAssessmentOfferingId(String studentId, Long offeringId);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByAssessmentOfferingSubjectSubjectNameIgnoreCaseAndAssessmentOfferingSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);

    @EntityGraph(attributePaths = {"student", "assessment", "assessment.offering", "assessment.offering.subject", "assessment.offering.section", "assessment.offering.teacher", "assessment.offering.teacher.user"})
    List<Activity> findByStudentStudentIdAndAssessmentOfferingSubjectSubjectNameIgnoreCaseAndAssessmentOfferingSectionSectionNameIgnoreCase(
            String studentId, String subjectName, String sectionName);

    Optional<Activity> findByAssessmentIdAndStudentStudentId(Long assessmentId, String studentId);

    boolean existsByAssessmentIdAndStudentStudentId(Long assessmentId, String studentId);
}
