package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_activity", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_activity_assessment_student",
        columnNames = {"assessment_id", "student_id"}
    )
})
@Getter
@Setter
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", referencedColumnName = "Student_ID", nullable = false)
    private Student student;

    @Column(name = "score", nullable = false)
    private Double score;
}
