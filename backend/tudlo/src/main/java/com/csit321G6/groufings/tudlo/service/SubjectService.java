package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.SubjectDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.SubjectOfferingRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Backward-compatible facade over normalized {@link SubjectOffering} records.
 * Allows existing {@code /api/subjects/**} endpoints to operate cleanly without
 * cross-teacher collisions.
 */
@Service
public class SubjectService extends CrudService<SubjectOffering, SubjectDto.Request, SubjectDto.Response> {

    private final SubjectOfferingRepository offeringRepository;
    private final SubjectOfferingService offeringService;
    private final SectionService sectionService;
    private final TeacherRepository teacherRepository;
    private final AccessPolicy accessPolicy;

    public SubjectService(
            SubjectOfferingRepository offeringRepository,
            SubjectOfferingService offeringService,
            SectionService sectionService,
            TeacherRepository teacherRepository,
            AccessPolicy accessPolicy) {
        this.offeringRepository = offeringRepository;
        this.offeringService = offeringService;
        this.sectionService = sectionService;
        this.teacherRepository = teacherRepository;
        this.accessPolicy = accessPolicy;
    }

    @Override
    protected JpaRepository<SubjectOffering, Long> repository() {
        return offeringRepository;
    }

    @Override
    protected String entityName() {
        return "SubjectOffering";
    }

    @Override
    protected SubjectOffering newEntity() {
        return new SubjectOffering();
    }

    @Override
    protected List<SubjectOffering> filterForRead(List<SubjectOffering> entities) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return entities.stream()
                    .filter(o -> o.getTeacher() != null && teacher.getId().equals(o.getTeacher().getId()))
                    .toList();
        }
        return entities;
    }

    @Override
    protected void beforeRead(SubjectOffering entity) {
        accessPolicy.requireOfferingReadAccess(entity);
    }

    @Override
    protected void beforeCreate(SubjectOffering entity, SubjectDto.Request request) {
        accessPolicy.requireTeacherAccess(request.teacherId());
    }

    @Override
    protected void beforeUpdate(SubjectOffering existing, SubjectDto.Request request) {
        accessPolicy.requireOfferingWriteAccess(existing);
        accessPolicy.requireTeacherAccess(request.teacherId());
    }

    @Override
    protected void beforeDelete(SubjectOffering existing) {
        accessPolicy.requireOfferingWriteAccess(existing);
    }

    @Override
    protected void apply(SubjectOffering entity, SubjectDto.Request request) {
        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.teacherId()));

        Subject subject = offeringService.findOrCreateSubject(request.subjectName());
        Section section = sectionService.findOrCreate(request.classSection());

        offeringRepository.findByTeacherIdAndSubjectIdAndSectionId(
                        teacher.getId(), subject.getId(), section.getId())
                .filter(existing -> entity.getId() == null || !existing.getId().equals(entity.getId()))
                .ifPresent(existing -> {
                    throw new ConflictException("Offering already exists for teacher " + teacher.getId()
                            + ", subject '" + subject.getSubjectName()
                            + "', section '" + section.getSectionName() + "'");
                });

        entity.setTeacher(teacher);
        entity.setSubject(subject);
        entity.setSection(section);
    }

    @Override
    protected SubjectDto.Response toResponse(SubjectOffering entity) {
        return new SubjectDto.Response(
                entity.getId(),
                entity.getId(),
                entity.getSubjectName(),
                entity.getClassSection(),
                entity.getTeacher() != null ? entity.getTeacher().getId() : null,
                entity.getTeacherName(),
                null
        );
    }

    public List<SubjectDto.Response> getByTeacherId(Long teacherId) {
        accessPolicy.requireTeacherAccess(teacherId);
        return offeringRepository.findByTeacherId(teacherId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SubjectDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
