package com.csit321G6.groufings.tudlo.student;

import com.csit321G6.groufings.tudlo.user.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "student")
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_no", nullable = false, length = 30)
    private String studentNo;
}
