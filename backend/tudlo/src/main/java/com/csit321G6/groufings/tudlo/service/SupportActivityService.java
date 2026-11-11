package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.SupportActivityDto;
import com.csit321G6.groufings.tudlo.entity.Enrollment;
import com.csit321G6.groufings.tudlo.entity.SupportActivity;
import com.csit321G6.groufings.tudlo.entity.Teacher;
import com.csit321G6.groufings.tudlo.repo.EnrollmentRepository;
import com.csit321G6.groufings.tudlo.repo.SupportActivityRepository;
import com.csit321G6.groufings.tudlo.repo.TeacherRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SupportActivityService
        extends CrudService<SupportActivity, SupportActivityDto.Request, SupportActivityDto.Response> {

    private final SupportActivityRepository repository;
    private final TeacherRepository teacherRepository;
    private final EnrollmentRepository enrollmentRepository;

    public SupportActivityService(
            SupportActivityRepository repository,
            TeacherRepository teacherRepository,
            EnrollmentRepository enrollmentRepository) {
        this.repository = repository;
        this.teacherRepository = teacherRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    protected JpaRepository<SupportActivity, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Support activity";
    }

    @Override
    protected SupportActivity newEntity() {
        return new SupportActivity();
    }

    @Override
    protected void apply(SupportActivity entity, SupportActivityDto.Request request) {
        entity.setTeacher(find(teacherRepository, request.teacherId(), "Teacher"));
        entity.setEnrollment(find(enrollmentRepository, request.enrollmentId(), "Enrollment"));
        entity.setType(request.type());
        entity.setNotes(request.notes());
        entity.setActivityDate(request.activityDate());
    }

    @Override
    protected SupportActivityDto.Response toResponse(SupportActivity entity) {
        return new SupportActivityDto.Response(
                entity.getId(),
                entity.getTeacher().getId(),
                entity.getEnrollment().getId(),
                entity.getType(),
                entity.getNotes(),
                entity.getActivityDate());
    }
}
