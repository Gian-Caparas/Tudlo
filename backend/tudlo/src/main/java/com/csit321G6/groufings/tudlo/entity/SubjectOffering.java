package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subject_offering", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_offering_teacher_subject_section",
        columnNames = {"teacher_id", "subject_id", "section_id"}
    )
})
@Getter
@Setter
public class SubjectOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offering_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Transient
    public String getSubjectName() {
        return subject != null ? subject.getSubjectName() : null;
    }

    @Transient
    public String getClassSection() {
        return section != null ? section.getSectionName() : null;
    }

    @Transient
    public String getTeacherName() {
        if (teacher != null && teacher.getUser() != null) {
            User user = teacher.getUser();
            return user.getFirstName() + " " + user.getLastName();
        }
        return null;
    }
}
