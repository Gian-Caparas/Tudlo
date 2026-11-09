package com.csit321G6.groufings.tudlo.student;

import com.csit321G6.groufings.tudlo.classroom.*;
import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService extends CrudService<Enrollment, EnrollmentDto.Request, EnrollmentDto.Response> {

    private final EnrollmentRepository repository;
    private final StudentRepository studentRepository;
    private final ClassRoomRepository classRoomRepository;

    public EnrollmentService(
            EnrollmentRepository repository,
            StudentRepository studentRepository,
            ClassRoomRepository classRoomRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    protected JpaRepository<Enrollment, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Enrollment";
    }

    @Override
    protected Enrollment newEntity() {
        return new Enrollment();
    }

    @Override
    protected void apply(Enrollment entity, EnrollmentDto.Request request) {
        entity.setStudent(find(studentRepository, request.studentId(), "Student"));
        entity.setClassRoom(find(classRoomRepository, request.classId(), "Class"));
    }

    @Override
    protected EnrollmentDto.Response toResponse(Enrollment entity) {
        return new EnrollmentDto.Response(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getClassRoom().getId(),
                entity.getEnrolledAt());
    }
}
