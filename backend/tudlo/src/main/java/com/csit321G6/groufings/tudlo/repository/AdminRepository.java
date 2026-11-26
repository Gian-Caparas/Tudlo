package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserId(Long userId);
}
