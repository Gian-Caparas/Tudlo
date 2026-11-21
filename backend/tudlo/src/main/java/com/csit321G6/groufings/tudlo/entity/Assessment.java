package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessment", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_assessment_offering_item",
        columnNames = {"offering_id", "assessment_item"}
    )
})
@Getter
@Setter
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private SubjectOffering offering;

    @Column(name = "assessment_item", nullable = false, length = 200)
    private String assessmentItem;

    @Column(name = "learning_outcome", nullable = false, length = 500)
    private String learningOutcome;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;
}
