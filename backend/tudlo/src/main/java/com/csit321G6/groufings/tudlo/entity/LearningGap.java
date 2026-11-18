package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "learning_gap", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_name", "class_section"})
})
@Getter
@Setter
public class LearningGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learning_gap_id")
    private Long id;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore;

    @Column(name = "max_possible_score", nullable = false)
    private Double maxPossibleScore;

    @Column(name = "percentage", nullable = false)
    private Double percentage;

    @Column(name = "is_at_risk", nullable = false)
    private Boolean isAtRisk;

    @Column(name = "gap_severity", length = 20)
    private String gapSeverity; // "CRITICAL", "HIGH", "MODERATE", "LOW"

    @Column(name = "last_calculated")
    private java.time.LocalDateTime lastCalculated;

    // Optional: link to User for student details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id_number", insertable = false, updatable = false)
    private User student;

    // Optional: link to Subject for subject details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "subject_name", referencedColumnName = "subject_name", insertable = false, updatable = false),
        @JoinColumn(name = "class_section", referencedColumnName = "class_section", insertable = false, updatable = false)
    })
    private Subject subject;

    // Helper method to determine gap severity based on percentage
    public void calculateGapSeverity() {
        if (percentage == null) {
            this.gapSeverity = "UNKNOWN";
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
        this.isAtRisk = percentage < 60;
    }
}