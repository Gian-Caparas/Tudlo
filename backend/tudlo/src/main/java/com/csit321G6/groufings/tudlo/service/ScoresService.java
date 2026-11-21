package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.ScoresDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.ScoresRepository;
import com.csit321G6.groufings.tudlo.repository.StudentRepository;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoresService extends CrudService<Scores, ScoresDto.Request, ScoresDto.Response> {

    private final ScoresRepository scoresRepository;
    private final StudentRepository studentRepository;
    private final SubjectOfferingService offeringService;
    private final AccessPolicy accessPolicy;

    public ScoresService(
            ScoresRepository scoresRepository,
            StudentRepository studentRepository,
            SubjectOfferingService offeringService,
            AccessPolicy accessPolicy) {
        this.scoresRepository = scoresRepository;
        this.studentRepository = studentRepository;
        this.offeringService = offeringService;
        this.accessPolicy = accessPolicy;
    }

    @Override
    protected JpaRepository<Scores, Long> repository() {
        return scoresRepository;
    }

    @Override
    protected String entityName() {
        return "Scores";
    }

    @Override
    protected Scores newEntity() {
        return new Scores();
    }

    @Override
    protected List<Scores> filterForRead(List<Scores> entities) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return entities.stream()
                    .filter(s -> s.getOffering() != null
                            && s.getOffering().getTeacher() != null
                            && teacher.getId().equals(s.getOffering().getTeacher().getId()))
                    .toList();
        }
        return entities;
    }

    @Override
    protected void beforeRead(Scores entity) {
        accessPolicy.requireOfferingReadAccess(entity.getOffering());
    }

    @Override
    protected void beforeUpdate(Scores existing, ScoresDto.Request request) {
        accessPolicy.requireOfferingWriteAccess(existing.getOffering());
    }

    @Override
    protected void beforeDelete(Scores existing) {
        accessPolicy.requireOfferingWriteAccess(existing.getOffering());
    }

    @Override
    protected void apply(Scores entity, ScoresDto.Request request) {
        SubjectOffering offering = offeringService.resolveOffering(
                request.offeringId(), request.subjectName(), request.classSection());
        accessPolicy.requireOfferingWriteAccess(offering);

        String studentId = request.studentId().trim();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        scoresRepository.findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .filter(existing -> entity.getId() == null || !existing.getId().equals(entity.getId()))
                .ifPresent(existing -> {
                    throw new ConflictException("Scores record already exists for student "
                            + student.getStudentId() + " in offering " + offering.getId());
                });

        entity.setOffering(offering);
        entity.setStudent(student);
        entity.setScore(request.score());
    }

    @Override
    protected ScoresDto.Response toResponse(Scores entity) {
        SubjectOffering offering = entity.getOffering();
        Student student = entity.getStudent();
        return new ScoresDto.Response(
                entity.getId(),
                offering != null ? offering.getId() : null,
                student != null ? student.getStudentId() : null,
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                entity.getScore(),
                null
        );
    }

    @Transactional(readOnly = true)
    public List<ScoresDto.Response> getByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return scoresRepository.findByOfferingId(offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScoresDto.Response> getByStudentId(String studentId) {
        accessPolicy.requireStudentReadAccess(studentId);
        User current = accessPolicy.requireCurrentUser();
        List<Scores> list = scoresRepository.findByStudentStudentId(studentId);
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            list = list.stream()
                    .filter(s -> s.getOffering() != null
                            && s.getOffering().getTeacher() != null
                            && teacher.getId().equals(s.getOffering().getTeacher().getId()))
                    .toList();
        }
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ScoresDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> scoresRepository.findByOfferingId(offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScoresDto.Response> getByStudentSubjectAndSection(
            String studentId, String subjectName, String classSection) {
        accessPolicy.requireStudentReadAccess(studentId);
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> scoresRepository
                        .findByOfferingIdAndStudentStudentId(offering.getId(), studentId).stream())
                .map(this::toResponse)
                .toList();
    }
}
