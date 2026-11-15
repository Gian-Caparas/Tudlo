package com.csit321G6.groufings.tudlo.repo;

import com.csit321G6.groufings.tudlo.entity.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser_Id(Long userId);

}
