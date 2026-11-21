package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student implements Persistable<String> {

    @Id
    @Column(name = "Student_ID", nullable = false, length = 20)
    private String studentId;

    @Column(name = "Firstname", nullable = false, length = 100)
    private String firstName;

    @Column(name = "Lastname", nullable = false, length = 100)
    private String lastName;

    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    @Transient
    public String getId() {
        return studentId;
    }

    @Override
    @Transient
    public boolean isNew() {
        return isNew;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
