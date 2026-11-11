package com.csit321G6.groufings.tudlo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "admin")
@Getter 
@Setter 
public class Admin {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "admin_id")
    private Long id;

    @OneToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "user_id", nullable = false, unique = true)
    private User user;
}
