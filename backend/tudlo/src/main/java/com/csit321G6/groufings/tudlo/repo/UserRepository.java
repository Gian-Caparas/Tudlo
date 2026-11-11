package com.csit321G6.groufings.tudlo.repo;

import com.csit321G6.groufings.tudlo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
