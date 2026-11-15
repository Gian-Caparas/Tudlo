package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.UserDto;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import com.csit321G6.groufings.tudlo.security.RolePolicy;
import java.util.Locale;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService extends CrudService<User, UserDto.Request, UserDto.Response> {

    private static final Logger AUDIT = LoggerFactory.getLogger("TudloAudit");

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RolePolicy rolePolicy;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, RolePolicy rolePolicy) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.rolePolicy = rolePolicy;
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
    protected void beforeCreate(User entity, UserDto.Request request) {
        var actor = rolePolicy.currentActor();
        rolePolicy.assertCanCreate(actor, request.userType());
        entity.setCreatedBy(repository.findById(actor.userId())
                .orElseThrow(() -> new BadRequestException("Acting user no longer exists")));
    }

    @Override
    protected void beforeUpdate(User existing, UserDto.Request request) {
        var actor = rolePolicy.currentActor();
        UserStatus requestedStatus = request.status() == null ? existing.getStatus() : request.status();
        rolePolicy.assertCanModify(actor, existing, request.userType(), requestedStatus);
    }

    @Override
    protected void beforeDelete(User existing) {
        rolePolicy.assertCanDelete(rolePolicy.currentActor(), existing);
    }

    @Override
    protected void afterCreate(User created, UserDto.Request request) {
        audit(rolePolicy.currentActor().userId(), "CREATE", created);
    }

    @Override
    protected void afterUpdate(User updated, UserDto.Request request) {
        audit(rolePolicy.currentActor().userId(), "UPDATE", updated);
    }

    @Override
    protected void afterDelete(User deleted) {
        audit(rolePolicy.currentActor().userId(), "DELETE", deleted);
    }

    @Override
    protected void apply(User entity, UserDto.Request request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        repository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(entity.getId()))
                .ifPresent(existing -> {
                    throw new BadRequestException("A user with this email already exists");
                });

        entity.setFullName(request.fullName());
        entity.setEmail(email);
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

    private void audit(Long actorId, String action, User target) {
        AUDIT.info("actorId={} action={} targetId={} targetRole={} timestamp={}",
                actorId, action, target.getId(), target.getUserType(), Instant.now());
    }
}
