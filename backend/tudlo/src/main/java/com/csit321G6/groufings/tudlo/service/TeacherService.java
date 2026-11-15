package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.TeacherDto;
import com.csit321G6.groufings.tudlo.entity.Teacher;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.TeacherRepository;
import com.csit321G6.groufings.tudlo.repo.UserRepository;
import com.csit321G6.groufings.tudlo.security.RolePolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService extends CrudService<Teacher, TeacherDto.Request, TeacherDto.Response> {

    private final TeacherRepository repository;
    private final UserRepository userRepository;
    private final RolePolicy rolePolicy;

    public TeacherService(TeacherRepository repository, UserRepository userRepository, RolePolicy rolePolicy) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.rolePolicy = rolePolicy;
    }

    @Override
    protected JpaRepository<Teacher, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Teacher";
    }

    @Override
    protected Teacher newEntity() {
        return new Teacher();
    }

    @Override
    protected void beforeCreate(Teacher entity, TeacherDto.Request request) {
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(),
                find(userRepository, request.userId(), "User"));
    }

    @Override
    protected void beforeUpdate(Teacher existing, TeacherDto.Request request) {
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(), existing.getUser());
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(),
                find(userRepository, request.userId(), "User"));
    }

    @Override
    protected void beforeDelete(Teacher existing) {
        rolePolicy.assertCanManageProfile(rolePolicy.currentActor(), existing.getUser());
    }

    @Override
    protected void apply(Teacher entity, TeacherDto.Request request) {
        entity.setUser(find(userRepository, request.userId(), "User"));
        if (entity.getUser().getUserType() != UserType.TEACHER) {
            throw new BadRequestException("User " + entity.getUser().getId() + " is not a TEACHER");
        }
    }

    @Override
    protected TeacherDto.Response toResponse(Teacher entity) {
        return new TeacherDto.Response(entity.getId(), entity.getUser().getId());
    }
}
