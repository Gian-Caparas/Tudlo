package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "my_progress", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_my_progress_offering_student",
        columnNames = {"offering_id", "student_id"}
    )
})
@Getter
@Setter
public class MyProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private SubjectOffering offering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", referencedColumnName = "Student_ID", nullable = false)
    private Student student;

    @Column(name = "subject_average")
    private Double subjectAverage;

    @Column(name = "overall_grade")
    private Double overallGrade;

    @Column(name = "total_assessments")
    private Integer totalAssessments;

    @Column(name = "completed_assessments")
    private Integer completedAssessments;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
