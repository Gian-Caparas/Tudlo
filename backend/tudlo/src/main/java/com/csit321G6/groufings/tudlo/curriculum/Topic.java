package com.csit321G6.groufings.tudlo.curriculum;

import com.csit321G6.groufings.tudlo.classroom.ClassRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "topic")
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassRoom classRoom;

    @Column(name = "name", nullable = false, length = 150)
    private String name;
}
