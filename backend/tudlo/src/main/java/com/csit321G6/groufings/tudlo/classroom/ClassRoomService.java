package com.csit321G6.groufings.tudlo.classroom;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.teacher.TeacherRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassRoomService extends CrudService<ClassRoom, ClassRoomDto.Request, ClassRoomDto.Response> {

    private final ClassRoomRepository repository;
    private final TeacherRepository teacherRepository;

    public ClassRoomService(
            ClassRoomRepository repository,
            TeacherRepository teacherRepository) {
        this.repository = repository;
        this.teacherRepository = teacherRepository;
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
