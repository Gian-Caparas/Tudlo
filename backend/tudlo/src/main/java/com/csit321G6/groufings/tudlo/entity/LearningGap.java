package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_gap", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_learning_gap_offering_student",
        columnNames = {"offering_id", "student_id"}
    )
})
@Getter
@Setter
public class LearningGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learning_gap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private SubjectOffering offering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", referencedColumnName = "Student_ID", nullable = false)
    private Student student;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore;

    @Column(name = "max_possible_score", nullable = false)
    private Double maxPossibleScore;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "is_at_risk", nullable = false)
    private Boolean isAtRisk;

    @Column(name = "gap_severity", length = 20)
    private String gapSeverity; // "CRITICAL", "HIGH", "MODERATE", "LOW", "UNKNOWN"

    @Column(name = "last_calculated")
    private LocalDateTime lastCalculated;

    public void calculateGapSeverity() {
        if (percentage == null) {
            this.gapSeverity = "UNKNOWN";
            this.isAtRisk = false;
            return;
        }
        if (percentage < 40) {
            this.gapSeverity = "CRITICAL";
        } else if (percentage < 50) {
            this.gapSeverity = "HIGH";
        } else if (percentage < 60) {
            this.gapSeverity = "MODERATE";
        } else {
            this.gapSeverity = "LOW";
        }
        this.isAtRisk = percentage < 60.0;
    }
}
