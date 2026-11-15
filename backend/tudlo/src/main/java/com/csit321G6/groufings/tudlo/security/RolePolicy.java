package com.csit321G6.groufings.tudlo.security;

import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ForbiddenException;
import com.csit321G6.groufings.tudlo.config.AuthenticatedUser;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserStatus;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RolePolicy {

    private final UserRepository userRepository;

    public RolePolicy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthenticatedUser currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser actor)) {
            throw new ForbiddenException("An authenticated user is required.");
        }
        return actor;
    }

    public void assertCanCreate(AuthenticatedUser actor, UserType targetRole) {
        if (targetRole == UserType.DEVELOPER || !actor.role().canManage(targetRole)) {
            throw new ForbiddenException("You cannot create an account with this role.");
        }
    }

    public void assertCanModify(AuthenticatedUser actor, User existingUser, UserType newRole) {
        assertCanModify(actor, existingUser, newRole, existingUser.getStatus());
    }

    public void assertCanModify(
            AuthenticatedUser actor, User existingUser, UserType newRole, UserStatus newStatus) {
        assertNotSelf(actor, existingUser);
        assertKeepsMinimumPrivilegedUsers(existingUser, newRole, newStatus);
        if (existingUser.getUserType() == UserType.DEVELOPER
                || !actor.role().canManage(existingUser.getUserType())
                || newRole == UserType.DEVELOPER
                || !actor.role().canManage(newRole)) {
            throw new ForbiddenException("You cannot modify this account or assign this role.");
        }
    }

    public void assertCanDelete(AuthenticatedUser actor, User existingUser) {
        assertNotSelf(actor, existingUser);
        assertKeepsMinimumPrivilegedUsers(existingUser, null, null);
        if (existingUser.getUserType() == UserType.DEVELOPER
                || !actor.role().canManage(existingUser.getUserType())) {
            throw new ForbiddenException("You cannot delete this account.");
        }
    }

    public void assertNotSelf(AuthenticatedUser actor, User target) {
        if (actor.userId().equals(target.getId())) {
            throw new ForbiddenException("You cannot modify or delete your own account.");
        }
    }

    public void assertKeepsMinimumPrivilegedUsers(
            User existingUser, UserType newRole, UserStatus newStatus) {
        if (existingUser == null) {
            return;
        }
        boolean remainsActive = newRole != null && newStatus == UserStatus.ACTIVE;
        if (existingUser.getUserType() == UserType.ADMIN
                && existingUser.getStatus() == UserStatus.ACTIVE
                && (!remainsActive || newRole != UserType.ADMIN)
                && userRepository.countByUserTypeAndStatus(UserType.ADMIN, UserStatus.ACTIVE) <= 1) {
            throw new ConflictException("The system must keep at least one active ADMIN account.");
        }
        if (existingUser.getUserType() == UserType.DEVELOPER
                && existingUser.getStatus() == UserStatus.ACTIVE
                && (!remainsActive || newRole != UserType.DEVELOPER)
                && userRepository.countByUserTypeAndStatus(UserType.DEVELOPER, UserStatus.ACTIVE) <= 1) {
            throw new ConflictException("The system must keep at least one active DEVELOPER account.");
        }
    }

    public void assertCanManageProfile(AuthenticatedUser actor, User target) {
        assertNotSelf(actor, target);
        if (target.getUserType() == UserType.DEVELOPER || !actor.role().canManage(target.getUserType())) {
            throw new ForbiddenException("You cannot manage this profile.");
        }
    }
}
