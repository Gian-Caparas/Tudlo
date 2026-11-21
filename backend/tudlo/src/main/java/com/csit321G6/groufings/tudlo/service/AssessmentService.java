package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.dto.AssessmentDto;
import com.csit321G6.groufings.tudlo.repository.AssessmentRepository;
import com.csit321G6.groufings.tudlo.repository.ClassListRepository;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssessmentService extends CrudService<Assessment, AssessmentDto.Request, AssessmentDto.Response> {

    private final AssessmentRepository assessmentRepository;
    private final SubjectOfferingService offeringService;
    private final ClassListRepository classListRepository;
    private final AccessPolicy accessPolicy;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            SubjectOfferingService offeringService,
            ClassListRepository classListRepository,
            AccessPolicy accessPolicy) {
        this.assessmentRepository = assessmentRepository;
        this.offeringService = offeringService;
        this.classListRepository = classListRepository;
        this.accessPolicy = accessPolicy;
    }

    @Override
    protected JpaRepository<Assessment, Long> repository() {
        return assessmentRepository;
    }

    @Override
    protected String entityName() {
        return "Assessment";
    }

    @Override
    protected Assessment newEntity() {
        return new Assessment();
    }

    @Override
    protected List<Assessment> filterForRead(List<Assessment> entities) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return entities.stream()
                    .filter(a -> a.getOffering() != null
                            && a.getOffering().getTeacher() != null
                            && teacher.getId().equals(a.getOffering().getTeacher().getId()))
                    .toList();
        }
        if (current.getUserType() == UserType.STUDENT) {
            return entities.stream()
                    .filter(a -> a.getOffering() != null
                            && classListRepository.existsByOfferingIdAndStudentStudentId(
                                    a.getOffering().getId(), current.getUserIdNumber()))
                    .toList();
        }
        return entities;
    }

    @Override
    protected void beforeRead(Assessment entity) {
        accessPolicy.requireOfferingReadAccess(entity.getOffering());
    }

    @Override
    protected void beforeUpdate(Assessment existing, AssessmentDto.Request request) {
        accessPolicy.requireOfferingWriteAccess(existing.getOffering());
    }

    @Override
    protected void beforeDelete(Assessment existing) {
        accessPolicy.requireOfferingWriteAccess(existing.getOffering());
    }

    @Override
    protected void apply(Assessment entity, AssessmentDto.Request request) {
        SubjectOffering offering = offeringService.resolveOffering(
                request.offeringId(), request.subjectName(), request.classSection());
        accessPolicy.requireOfferingWriteAccess(offering);

        String item = request.assessmentItem().trim();
        assessmentRepository.findByOfferingIdAndAssessmentItemIgnoreCase(offering.getId(), item)
                .filter(existing -> entity.getId() == null || !existing.getId().equals(entity.getId()))
                .ifPresent(existing -> {
                    throw new ConflictException("Assessment '" + item + "' already exists in offering " + offering.getId());
                });

        entity.setOffering(offering);
        entity.setLearningOutcome(request.learningOutcome().trim());
        entity.setMaxScore(request.maxScore());
        entity.setAssessmentItem(item);
    }

    @Override
    protected AssessmentDto.Response toResponse(Assessment entity) {
        SubjectOffering offering = entity.getOffering();
        return new AssessmentDto.Response(
                entity.getId(),
                offering != null ? offering.getId() : null,
                entity.getLearningOutcome(),
                entity.getMaxScore(),
                entity.getAssessmentItem(),
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<AssessmentDto.Response> getByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return assessmentRepository.findByOfferingId(offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssessmentDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> assessmentRepository.findByOfferingId(offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }
}
