package com.csit321G6.groufings.tudlo.user;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService extends CrudService<Teacher, TeacherDto.Request, TeacherDto.Response> {

    private final TeacherRepository repository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
