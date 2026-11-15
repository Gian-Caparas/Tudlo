package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.AdminDto;
import com.csit321G6.groufings.tudlo.entity.Admin;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.AdminRepository;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import com.csit321G6.groufings.tudlo.security.RolePolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends CrudService<Admin, AdminDto.Request, AdminDto.Response> {

    private final AdminRepository repository;
    private final UserRepository userRepository;
    private final RolePolicy rolePolicy;

    public AdminService(AdminRepository repository, UserRepository userRepository, RolePolicy rolePolicy) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.rolePolicy = rolePolicy;
    }

    @Override
    protected JpaRepository<Admin, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Admin";
    }

    @Override
    protected Admin newEntity() {
        return new Admin();
    }

    @Override
    protected void beforeCreate(Admin entity, AdminDto.Request request) {
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(),
                find(userRepository, request.userId(), "User"));
    }

    @Override
    protected void beforeUpdate(Admin existing, AdminDto.Request request) {
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(), existing.getUser());
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(),
                find(userRepository, request.userId(), "User"));
    }

    @Override
    protected void beforeDelete(Admin existing) {
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(), existing.getUser());
    }

    @Override
    protected void apply(Admin entity, AdminDto.Request request) {
        entity.setUser(find(userRepository, request.userId(), "User"));
        if (entity.getUser().getUserType() != UserType.ADMIN) {
            throw new BadRequestException("User " + entity.getUser().getId() + " is not an ADMIN");
        }
    }

    @Override
    protected AdminDto.Response toResponse(Admin entity) {
        return new AdminDto.Response(
                entity.getId(),
                entity.getUser().getId());
    }
}
