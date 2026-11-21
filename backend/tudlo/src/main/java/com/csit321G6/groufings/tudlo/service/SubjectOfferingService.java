package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ForbiddenException;
import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.SubjectOfferingDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectOfferingService {

    private final SubjectOfferingRepository offeringRepository;
    private final SubjectRepository subjectRepository;
    private final SectionService sectionService;
    private final TeacherRepository teacherRepository;
    private final ClassListRepository classListRepository;
    private final AccessPolicy accessPolicy;

    public SubjectOfferingService(
            SubjectOfferingRepository offeringRepository,
            SubjectRepository subjectRepository,
            SectionService sectionService,
            TeacherRepository teacherRepository,
            ClassListRepository classListRepository,
            AccessPolicy accessPolicy) {
        this.offeringRepository = offeringRepository;
        this.subjectRepository = subjectRepository;
        this.sectionService = sectionService;
        this.teacherRepository = teacherRepository;
        this.classListRepository = classListRepository;
        this.accessPolicy = accessPolicy;
    }

    @Transactional(readOnly = true)
    public List<SubjectOfferingDto.Response> findAllForCurrentUser() {
        User current = accessPolicy.requireCurrentUser();
        List<SubjectOffering> offerings = switch (current.getUserType()) {
            case ADMIN -> offeringRepository.findAll();
            case TEACHER -> {
                Teacher teacher = accessPolicy.requireCurrentTeacher();
                yield offeringRepository.findByTeacherId(teacher.getId());
            }
            case STUDENT -> offeringRepository.findEnrolledByStudentId(current.getUserIdNumber());
        };
        return offerings.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SubjectOfferingDto.Response findById(Long offeringId) {
        SubjectOffering offering = getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return toResponse(offering);
    }

    @Transactional(readOnly = true)
    public List<SubjectOfferingDto.Response> findByTeacherId(Long teacherId) {
        accessPolicy.requireTeacherAccess(teacherId);
        return offeringRepository.findByTeacherId(teacherId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SubjectOfferingDto.Response create(SubjectOfferingDto.Request request) {
        Long targetTeacherId = resolveTargetTeacherId(request.teacherId());
        Teacher teacher = teacherRepository.findById(targetTeacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", targetTeacherId));

        Subject subject = findOrCreateSubject(request.subjectName());
        Section section = sectionService.findOrCreate(request.classSection());

        if (offeringRepository.existsByTeacherIdAndSubjectIdAndSectionId(
                teacher.getId(), subject.getId(), section.getId())) {
            throw new ConflictException("Offering already exists for teacher " + teacher.getId()
                    + ", subject '" + subject.getSubjectName()
                    + "', section '" + section.getSectionName() + "'");
        }

        SubjectOffering offering = new SubjectOffering();
        offering.setTeacher(teacher);
        offering.setSubject(subject);
        offering.setSection(section);

        return toResponse(offeringRepository.saveAndFlush(offering));
    }

    @Transactional
    public void delete(Long offeringId) {
        SubjectOffering offering = getOfferingEntity(offeringId);
        accessPolicy.requireOfferingWriteAccess(offering);
        offeringRepository.delete(offering);
        offeringRepository.flush();
    }

    @Transactional
    public Subject findOrCreateSubject(String subjectName) {
        String normalized = subjectName != null ? subjectName.trim() : "";
        if (normalized.isEmpty()) {
            throw new BadRequestException("Subject name is required");
        }
        return subjectRepository.findBySubjectNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Subject subject = new Subject();
                    subject.setSubjectName(normalized);
                    return subjectRepository.saveAndFlush(subject);
                });
    }

    public SubjectOffering getOfferingEntity(Long offeringId) {
        return offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectOffering", offeringId));
    }

    public SubjectOffering resolveOffering(Long offeringId, String subjectName, String classSection) {
        if (offeringId != null) {
            return getOfferingEntity(offeringId);
        }
        String subj = subjectName != null ? subjectName.trim() : "";
        String sect = classSection != null ? classSection.trim() : "";
        if (subj.isEmpty() || sect.isEmpty()) {
            throw new BadRequestException("Either offeringId or both subjectName and classSection are required");
        }

        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return offeringRepository
                    .findByTeacherIdAndSubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(
                            teacher.getId(), subj, sect)
                    .orElseThrow(() -> {
                        List<SubjectOffering> others = offeringRepository
                                .findBySubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(subj, sect);
                        if (!others.isEmpty()) {
                            return new ForbiddenException("You do not own the offering for " + subj + " - " + sect);
                        }
                        return new ResourceNotFoundException(
                                "SubjectOffering not found for subject '" + subj + "' and section '" + sect + "'");
                    });
        }

        List<SubjectOffering> matches = offeringRepository
                .findBySubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(subj, sect);
        if (matches.isEmpty()) {
            throw new ResourceNotFoundException(
                    "SubjectOffering not found for subject '" + subj + "' and section '" + sect + "'");
        }

        if (current.getUserType() == UserType.STUDENT) {
            List<SubjectOffering> enrolledMatches = matches.stream()
                    .filter(o -> classListRepository.existsByOfferingIdAndStudentStudentId(
                            o.getId(), current.getUserIdNumber()))
                    .toList();
            if (enrolledMatches.isEmpty()) {
                throw new ForbiddenException("You are not enrolled in " + subj + " - " + sect);
            }
            if (enrolledMatches.size() > 1) {
                throw new ConflictException("Multiple offerings match " + subj + " - " + sect + "; please specify offeringId");
            }
            return enrolledMatches.get(0);
        }

        if (matches.size() > 1) {
            throw new ConflictException("Multiple offerings match " + subj + " - " + sect + "; please specify offeringId");
        }
        return matches.get(0);
    }

    public List<SubjectOffering> resolveOfferingsForRead(String subjectName, String classSection) {
        String subj = subjectName != null ? subjectName.trim() : "";
        String sect = classSection != null ? classSection.trim() : "";
        User current = accessPolicy.requireCurrentUser();

        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return offeringRepository
                    .findByTeacherIdAndSubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(
                            teacher.getId(), subj, sect)
                    .map(List::of)
                    .orElseGet(() -> {
                        List<SubjectOffering> others = offeringRepository
                                .findBySubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(subj, sect);
                        if (!others.isEmpty()) {
                            throw new ForbiddenException("You do not own the offering for " + subj + " - " + sect);
                        }
                        return List.of();
                    });
        }

        List<SubjectOffering> matches = offeringRepository
                .findBySubjectSubjectNameIgnoreCaseAndSectionSectionNameIgnoreCase(subj, sect);

        if (current.getUserType() == UserType.STUDENT) {
            List<SubjectOffering> enrolled = matches.stream()
                    .filter(o -> classListRepository.existsByOfferingIdAndStudentStudentId(
                            o.getId(), current.getUserIdNumber()))
                    .toList();
            if (!matches.isEmpty() && enrolled.isEmpty()) {
                throw new ForbiddenException("You are not enrolled in " + subj + " - " + sect);
            }
            return enrolled;
        }

        return matches;
    }

    private Long resolveTargetTeacherId(Long requestedTeacherId) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher currentTeacher = accessPolicy.requireCurrentTeacher();
            if (requestedTeacherId != null && !currentTeacher.getId().equals(requestedTeacherId)) {
                throw new ForbiddenException("Teachers may only manage their own offerings");
            }
            return currentTeacher.getId();
        }
        if (current.getUserType() == UserType.ADMIN) {
            if (requestedTeacherId == null) {
                throw new BadRequestException("Teacher ID is required");
            }
            return requestedTeacherId;
        }
        throw new ForbiddenException("Teacher or Admin role required");
    }

    public SubjectOfferingDto.Response toResponse(SubjectOffering entity) {
        return new SubjectOfferingDto.Response(
                entity.getId(),
                entity.getId(),
                entity.getSubject() != null ? entity.getSubject().getId() : null,
                entity.getSubjectName(),
                entity.getSection() != null ? entity.getSection().getId() : null,
                entity.getClassSection(),
                entity.getTeacher() != null ? entity.getTeacher().getId() : null,
                entity.getTeacherName()
        );
    }
}
