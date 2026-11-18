package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessment", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assessment_item", "subject_name", "class_section"})
})
@Getter
@Setter
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long id;

    @Column(name = "learning_outcome", nullable = false, length = 500)
    private String learningOutcome;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Column(name = "assessment_item", nullable = false, length = 200)
    private String assessmentItem;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    // Optional: link to Subject for referential integrity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "subject_name", referencedColumnName = "subject_name", insertable = false, updatable = false),
        @JoinColumn(name = "class_section", referencedColumnName = "class_section", insertable = false, updatable = false)
    })
    private Subject subject;
}