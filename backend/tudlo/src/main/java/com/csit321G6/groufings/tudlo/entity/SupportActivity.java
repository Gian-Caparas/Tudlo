package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "support_activity")
@Getter
@Setter
public class SupportActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;
}
