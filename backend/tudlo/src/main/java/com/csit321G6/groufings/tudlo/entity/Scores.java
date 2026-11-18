package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scores", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_name", "class_section"})
})
@Getter
@Setter
public class Scores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    @Column(name = "score", nullable = false)
    private Double score;

    // Optional: link to User entity for referential integrity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id_number", insertable = false, updatable = false)
    private User student;

    // Optional: link to ClassList for referential integrity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "subject_name", referencedColumnName = "subject_name", insertable = false, updatable = false),
        @JoinColumn(name = "class_section", referencedColumnName = "class_section", insertable = false, updatable = false),
        @JoinColumn(name = "student_id", referencedColumnName = "student_id", insertable = false, updatable = false)
    })
    private ClassList classList;
}