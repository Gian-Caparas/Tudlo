package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_activity", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"activity_name", "student_id", "subject_name", "class_section"})
})
@Getter
@Setter
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @Column(name = "activity_name", nullable = false, length = 200)
    private String activityName;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    // Optional: link to User for referential integrity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id_number", insertable = false, updatable = false)
    private User student;

    // Optional: link to Assessment for referential integrity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "activity_name", referencedColumnName = "assessment_item", insertable = false, updatable = false),
        @JoinColumn(name = "subject_name", referencedColumnName = "subject_name", insertable = false, updatable = false),
        @JoinColumn(name = "class_section", referencedColumnName = "class_section", insertable = false, updatable = false)
    })
    private Assessment assessment;
}