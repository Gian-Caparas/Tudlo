package com.csit321G6.groufings.tudlo.classroom;

import com.csit321G6.groufings.tudlo.teacher.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "classes")
@Getter
@Setter
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "class_name", nullable = false, length = 150)
    private String className;

    @Column(name = "subject", length = 100)
    private String subject;

    @Column(name = "term", length = 50)
    private String term;
}
