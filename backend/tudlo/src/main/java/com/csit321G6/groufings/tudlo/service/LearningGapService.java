package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.LearningGapDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.LearningGapRepository;
import com.csit321G6.groufings.tudlo.repository.StudentRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearningGapService {

    private static final double AT_RISK_THRESHOLD = 60.0;

    private final LearningGapRepository learningGapRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectOfferingService offeringService;
    private final AccessPolicy accessPolicy;

    public LearningGapService(
            LearningGapRepository learningGapRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            SubjectOfferingService offeringService,
            AccessPolicy accessPolicy) {
        this.learningGapRepository = learningGapRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.offeringService = offeringService;
        this.accessPolicy = accessPolicy;
    }

    private LearningGapDto.Response toResponse(LearningGap entity) {
        Student student = entity.getStudent();
        SubjectOffering offering = entity.getOffering();
        String studentName = student != null ? student.getFullName() : null;
        return new LearningGapDto.Response(
                entity.getId(),
                offering != null ? offering.getId() : null,
                student != null ? student.getStudentId() : null,
                studentName,
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                entity.getOverallScore(),
                entity.getMaxPossibleScore(),
                entity.getPercentage(),
                entity.getIsAtRisk(),
                entity.getGapSeverity(),
                entity.getLastCalculated()
        );
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.Response> getAtRiskByTeacherId(Long teacherId) {
        accessPolicy.requireTeacherAccess(teacherId);
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", teacherId);
        }
        return learningGapRepository.findAtRiskByTeacherId(teacherId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.Response> getAtRiskByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return learningGapRepository
                .findByOfferingIdAndPercentageIsNotNullAndPercentageLessThanOrderByPercentageAsc(
                        offeringId, AT_RISK_THRESHOLD)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.Response> getByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return learningGapRepository.findByOfferingId(offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.HeatmapResponse> getHeatmapByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return learningGapRepository.findByOfferingId(offeringId).stream()
                .map(this::toHeatmapResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.Response> getAtRiskStudents(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> learningGapRepository
                        .findByOfferingIdAndPercentageIsNotNullAndPercentageLessThanOrderByPercentageAsc(
                                offering.getId(), AT_RISK_THRESHOLD)
                        .stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.Response> getAllGapsBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> learningGapRepository.findByOfferingId(offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.Response> getByStudentId(String studentId) {
        accessPolicy.requireStudentReadAccess(studentId);
        User current = accessPolicy.requireCurrentUser();
        List<LearningGap> gaps = learningGapRepository.findByStudentStudentId(studentId);
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            gaps = gaps.stream()
                    .filter(lg -> lg.getOffering() != null
                            && lg.getOffering().getTeacher() != null
                            && teacher.getId().equals(lg.getOffering().getTeacher().getId()))
                    .toList();
        }
        return gaps.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LearningGapDto.HeatmapResponse> getHeatmapData(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> learningGapRepository.findByOfferingId(offering.getId()).stream())
                .map(this::toHeatmapResponse)
                .toList();
    }

    @Transactional
    public LearningGapDto.Response calculateAndSaveGap(
            Long offeringId,
            String studentId,
            String subjectName,
            String classSection,
            Double overallScore,
            Double maxPossibleScore) {
        SubjectOffering offering = offeringService.resolveOffering(offeringId, subjectName, classSection);
        accessPolicy.requireOfferingWriteAccess(offering);

        Student student = studentRepository.findById(studentId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        LearningGap gap = learningGapRepository
                .findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .orElseGet(LearningGap::new);

        gap.setOffering(offering);
        gap.setStudent(student);
        gap.setOverallScore(overallScore != null ? overallScore : 0.0);
        gap.setMaxPossibleScore(maxPossibleScore != null ? maxPossibleScore : 0.0);

        if (maxPossibleScore != null && maxPossibleScore > 0) {
            gap.setPercentage((gap.getOverallScore() / maxPossibleScore) * 100.0);
        } else {
            gap.setPercentage(null);
        }

        gap.calculateGapSeverity();
        gap.setLastCalculated(LocalDateTime.now());

        return toResponse(learningGapRepository.save(gap));
    }

    private LearningGapDto.HeatmapResponse toHeatmapResponse(LearningGap gap) {
        String studentName = gap.getStudent() != null ? gap.getStudent().getFullName() : "Unknown";
        return new LearningGapDto.HeatmapResponse(
                gap.getStudent() != null ? gap.getStudent().getStudentId() : null,
                studentName,
                gap.getPercentage(),
                gap.getGapSeverity(),
                gap.getIsAtRisk()
        );
    }
}
