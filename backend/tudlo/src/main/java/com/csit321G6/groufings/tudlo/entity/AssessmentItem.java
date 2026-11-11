package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessment_item")
@Getter
@Setter
public class AssessmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outcome_id", nullable = false)
    private LearningOutcome outcome;

    @Column(name = "item_label", nullable = false, length = 50)
    private String itemLabel;

    @Column(name = "max_points", nullable = false)
    private Double maxPoints;
}
