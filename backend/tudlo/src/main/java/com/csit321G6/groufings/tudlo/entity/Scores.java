package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scores", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_scores_offering_student",
        columnNames = {"offering_id", "student_id"}
    )
})
@Getter
@Setter
public class Scores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private SubjectOffering offering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", referencedColumnName = "Student_ID", nullable = false)
    private Student student;

    @Column(name = "score", nullable = false)
    private Double score;
}
