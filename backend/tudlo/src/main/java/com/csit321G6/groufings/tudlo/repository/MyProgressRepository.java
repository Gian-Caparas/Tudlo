package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.MyProgress;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyProgressRepository extends JpaRepository<MyProgress, Long> {

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<MyProgress> findByStudentStudentIdOrderByOfferingSubjectSubjectNameAsc(String studentId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<MyProgress> findByOfferingId(Long offeringId);

    Optional<MyProgress> findByOfferingIdAndStudentStudentId(Long offeringId, String studentId);

    @EntityGraph(attributePaths = {"student", "offering", "offering.subject", "offering.section", "offering.teacher", "offering.teacher.user"})
    List<MyProgress> findByOfferingSubjectSubjectNameIgnoreCaseAndOfferingSectionSectionNameIgnoreCase(
            String subjectName, String sectionName);
}
