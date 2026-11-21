package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.MyProgressDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.MyProgressRepository;
import com.csit321G6.groufings.tudlo.repository.StudentRepository;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MyProgressService {

    private final MyProgressRepository myProgressRepository;
    private final StudentRepository studentRepository;
    private final SubjectOfferingService offeringService;
    private final AccessPolicy accessPolicy;

    public MyProgressService(
            MyProgressRepository myProgressRepository,
            StudentRepository studentRepository,
            SubjectOfferingService offeringService,
            AccessPolicy accessPolicy) {
        this.myProgressRepository = myProgressRepository;
        this.studentRepository = studentRepository;
        this.offeringService = offeringService;
        this.accessPolicy = accessPolicy;
    }

    private MyProgressDto.Response toResponse(MyProgress entity) {
        SubjectOffering offering = entity.getOffering();
        Student student = entity.getStudent();
        double completionRate = computeCompletionRate(entity);

        return new MyProgressDto.Response(
                entity.getId(),
                offering != null ? offering.getId() : null,
                student != null ? student.getStudentId() : null,
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                entity.getSubjectAverage(),
                entity.getOverallGrade(),
                entity.getTotalAssessments(),
                entity.getCompletedAssessments(),
                completionRate,
                entity.getLastUpdated()
        );
    }

    private MyProgressDto.SummaryResponse toSummaryResponse(MyProgress entity) {
        SubjectOffering offering = entity.getOffering();
        double completionRate = computeCompletionRate(entity);

        return new MyProgressDto.SummaryResponse(
                offering != null ? offering.getId() : null,
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                entity.getSubjectAverage(),
                entity.getOverallGrade(),
                entity.getTotalAssessments(),
                entity.getCompletedAssessments(),
                completionRate
        );
    }

    private double computeCompletionRate(MyProgress entity) {
        if (entity.getTotalAssessments() != null && entity.getTotalAssessments() > 0) {
            double completed = entity.getCompletedAssessments() != null ? entity.getCompletedAssessments() : 0.0;
            return (completed / entity.getTotalAssessments()) * 100.0;
        }
        return 0.0;
    }

    @Transactional(readOnly = true)
    public List<MyProgressDto.Response> getByStudentId(String studentId) {
        accessPolicy.requireStudentReadAccess(studentId);
        return filterForCurrentTeacher(
                myProgressRepository.findByStudentStudentIdOrderByOfferingSubjectSubjectNameAsc(studentId))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyProgressDto.SummaryResponse> getSummaryByStudentId(String studentId) {
        accessPolicy.requireStudentReadAccess(studentId);
        return filterForCurrentTeacher(
                myProgressRepository.findByStudentStudentIdOrderByOfferingSubjectSubjectNameAsc(studentId))
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyProgressDto.Response> getByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return myProgressRepository.findByOfferingId(offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyProgressDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> myProgressRepository.findByOfferingId(offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MyProgressDto.Response calculateAndSaveProgress(
            Long offeringId,
            String studentId,
            String subjectName,
            String classSection,
            Double subjectAverage,
            Double overallGrade,
            Integer totalAssessments,
            Integer completedAssessments) {
        SubjectOffering offering = offeringService.resolveOffering(offeringId, subjectName, classSection);
        accessPolicy.requireOfferingWriteAccess(offering);

        Student student = studentRepository.findById(studentId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        MyProgress progress = myProgressRepository
                .findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .orElseGet(MyProgress::new);

        progress.setOffering(offering);
        progress.setStudent(student);
        progress.setSubjectAverage(subjectAverage);
        progress.setOverallGrade(overallGrade);
        progress.setTotalAssessments(totalAssessments);
        progress.setCompletedAssessments(completedAssessments);
        progress.setLastUpdated(LocalDateTime.now());

        return toResponse(myProgressRepository.save(progress));
    }

    private List<MyProgress> filterForCurrentTeacher(List<MyProgress> list) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return list.stream()
                    .filter(mp -> mp.getOffering() != null
                            && mp.getOffering().getTeacher() != null
                            && teacher.getId().equals(mp.getOffering().getTeacher().getId()))
                    .toList();
        }
        return list;
    }
}
