package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subject", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"subject_name", "class_section", "teacher_id"})
})
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "class_section", nullable = false, length = 50)
    private String classSection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    // Transient field - not persisted, computed from teacher relationship
    @Transient
    public String getTeacherName() {
        if (teacher != null && teacher.getUser() != null) {
            User user = teacher.getUser();
            return user.getFirstName() + " " + user.getLastName();
        }
        return null;
    }
}