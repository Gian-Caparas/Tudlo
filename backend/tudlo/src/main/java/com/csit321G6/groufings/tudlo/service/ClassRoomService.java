package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ClassRoomDto;
import com.csit321G6.groufings.tudlo.entity.ClassRoom;
import com.csit321G6.groufings.tudlo.entity.Teacher;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.ClassRoomRepository;
import com.csit321G6.groufings.tudlo.repo.TeacherRepository;
import com.csit321G6.groufings.tudlo.security.DataOwnershipPolicy;
import com.csit321G6.groufings.tudlo.security.RolePolicy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassRoomService extends CrudService<ClassRoom, ClassRoomDto.Request, ClassRoomDto.Response> {

    private final ClassRoomRepository repository;
    private final TeacherRepository teacherRepository;
    private final DataOwnershipPolicy ownershipPolicy;
    private final RolePolicy rolePolicy;

    public ClassRoomService(
            ClassRoomRepository repository,
            TeacherRepository teacherRepository,
            DataOwnershipPolicy ownershipPolicy,
            RolePolicy rolePolicy) {
        this.repository = repository;
        this.teacherRepository = teacherRepository;
        this.ownershipPolicy = ownershipPolicy;
        this.rolePolicy = rolePolicy;
    }

    @Override
    protected JpaRepository<ClassRoom, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "ClassRoom";
    }

    @Override
    protected ClassRoom newEntity() {
        return new ClassRoom();
    }

    @Override
    protected List<ClassRoom> filterForRead(List<ClassRoom> classes) {
        var actor = rolePolicy.currentActor();
        if (actor.role() == UserType.DEVELOPER) {
            return classes;
        }
        return classes.stream().filter(classRoom -> ownershipPolicy.canReadClass(actor, classRoom)).toList();
    }

    @Override
    protected void beforeRead(ClassRoom classRoom) {
        ownershipPolicy.assertCanReadClass(rolePolicy.currentActor(), classRoom);
    }

    @Override
    protected void beforeCreate(ClassRoom classRoom, ClassRoomDto.Request request) {
        ownershipPolicy.assertTeacherOwns(
                rolePolicy.currentActor(), find(teacherRepository, request.teacherId(), "Teacher"));
    }

    @Override
    protected void beforeUpdate(ClassRoom existing, ClassRoomDto.Request request) {
        var actor = rolePolicy.currentActor();
        ownershipPolicy.assertTeacherOwns(actor, existing);
        ownershipPolicy.assertTeacherOwns(actor, find(teacherRepository, request.teacherId(), "Teacher"));
    }

    @Override
    protected void beforeDelete(ClassRoom existing) {
        ownershipPolicy.assertTeacherOwns(rolePolicy.currentActor(), existing);
    }

    @Override
    protected void apply(ClassRoom entity, ClassRoomDto.Request request) {
        entity.setTeacher(find(teacherRepository, request.teacherId(), "Teacher"));
        entity.setClassName(request.className());
        entity.setSubject(request.subject());
        entity.setTerm(request.term());
    }

    @Override
    protected ClassRoomDto.Response toResponse(ClassRoom entity) {
        return new ClassRoomDto.Response(
                entity.getId(),
                entity.getTeacher().getId(),
                entity.getClassName(),
                entity.getSubject(),
                entity.getTerm());
    }

}
