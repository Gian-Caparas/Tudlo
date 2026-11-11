package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessment")
@Getter
@Setter
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassRoom classRoom;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "due_date")
    private LocalDate dueDate;
}
