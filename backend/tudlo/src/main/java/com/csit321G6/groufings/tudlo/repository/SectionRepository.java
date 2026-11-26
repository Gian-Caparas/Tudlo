package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findBySectionNameIgnoreCase(String sectionName);
    boolean existsBySectionNameIgnoreCase(String sectionName);
}
