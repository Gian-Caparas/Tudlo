package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.LearningGap;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningGapRepository extends JpaRepository<LearningGap, Long> {

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<LearningGap> findByOfferingId(Long offeringId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<LearningGap> findByStudentStudentId(String studentId);

    Optional<LearningGap> findByOfferingIdAndStudentStudentId(Long offeringId, String studentId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<LearningGap> findByOfferingIdAndPercentageIsNotNullAndPercentageLessThanOrderByPercentageAsc(
            Long offeringId, Double threshold);

    @Query("SELECT lg FROM LearningGap lg " +
           "JOIN FETCH lg.student " +
           "JOIN FETCH lg.offering o " +
           "JOIN FETCH o.subject " +
           "JOIN FETCH o.section " +
           "JOIN FETCH o.teacher t " +
           "JOIN FETCH t.user " +
           "WHERE o.teacher.id = :teacherId AND lg.percentage IS NOT NULL AND lg.percentage < 60.0 " +
           "ORDER BY lg.percentage ASC")
    List<LearningGap> findAtRiskByTeacherId(@Param("teacherId") Long teacherId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<LearningGap> findByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<LearningGap> findByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCaseAndPercentageIsNotNullAndPercentageLessThanOrderByPercentageAsc(
            String subjectName, String sectionName, Double threshold);
}
