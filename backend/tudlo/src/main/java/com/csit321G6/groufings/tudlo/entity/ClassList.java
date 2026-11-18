package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "class_list", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"subject_name", "class_section", "student_id"})
})
@Getter
@Setter
public class ClassList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_list_id")
    private Long id;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 150)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    // Optional: link to User entity for referential integrity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id_number", insertable = false, updatable = false)
    private User student;
}