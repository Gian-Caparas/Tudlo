package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdNumber(String userIdNumber);
    boolean existsByUserIdNumber(String userIdNumber);
}