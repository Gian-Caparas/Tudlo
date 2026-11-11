package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.repo.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudService<User, UserDto.Request, UserDto.Response> {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected JpaRepository<User, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "User";
    }

    @Override
    protected User newEntity() {
        return new User();
    }

    @Override
    protected void apply(User entity, UserDto.Request request) {
        entity.setFullName(request.fullName());
        entity.setEmail(request.email());
        entity.setUserType(request.userType());
        if (request.status() != null) {
            entity.setStatus(request.status());
        } else if (entity.getId() == null) {
            entity.setStatus(UserStatus.ACTIVE);
        }

        if (request.password() == null || request.password().isBlank()) {
            if (entity.getId() == null) {
                throw new BadRequestException("Password is required when creating a user");
            }
        } else {
            entity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
    }

    @Override
    protected UserDto.Response toResponse(User entity) {
        return new UserDto.Response(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getUserType(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
