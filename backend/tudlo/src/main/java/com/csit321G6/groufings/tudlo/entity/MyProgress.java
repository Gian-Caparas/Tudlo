package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "my_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_name", "class_section"})
})
@Getter
@Setter
public class MyProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    @Column(name = "subject_average", nullable = false)
    private Double subjectAverage;

    @Column(name = "overall_grade", nullable = false)
    private Double overallGrade;

    @Column(name = "total_assessments")
    private Integer totalAssessments;

    @Column(name = "completed_assessments")
    private Integer completedAssessments;

    @Column(name = "last_updated")
    private java.time.LocalDateTime lastUpdated;

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
}