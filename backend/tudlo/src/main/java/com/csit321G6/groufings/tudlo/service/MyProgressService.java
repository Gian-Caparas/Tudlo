package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.dto.MyProgressDto;
import com.csit321G6.groufings.tudlo.entity.MyProgress;
import com.csit321G6.groufings.tudlo.repository.MyProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyProgressService {

    private final MyProgressRepository myProgressRepository;

    public MyProgressService(MyProgressRepository myProgressRepository) {
        this.myProgressRepository = myProgressRepository;
    }

    private MyProgressDto.Response toResponse(MyProgress entity) {
        Double completionRate = 0.0;
        if (entity.getTotalAssessments() != null && entity.getTotalAssessments() > 0) {
            completionRate = (entity.getCompletedAssessments() != null ? entity.getCompletedAssessments() : 0.0)
                / entity.getTotalAssessments() * 100;
        }

        return new MyProgressDto.Response(
            entity.getId(),
            entity.getStudentId(),
            entity.getSubjectName(),
            entity.getClassSection(),
            entity.getSubjectAverage(),
            entity.getOverallGrade(),
            entity.getTotalAssessments(),
            entity.getCompletedAssessments(),
            completionRate,
            entity.getLastUpdated()
        );
    }

    public List<MyProgressDto.Response> getByStudentId(String studentId) {
        return myProgressRepository.findByStudentOrderBySubject(studentId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<MyProgressDto.SummaryResponse> getSummaryByStudentId(String studentId) {
        return myProgressRepository.findByStudentOrderBySubject(studentId)
            .stream()
            .map(entity -> {
                Double completionRate = 0.0;
                if (entity.getTotalAssessments() != null && entity.getTotalAssessments() > 0) {
                    completionRate = (entity.getCompletedAssessments() != null ? entity.getCompletedAssessments() : 0.0)
                        / entity.getTotalAssessments() * 100;
                }
                return new MyProgressDto.SummaryResponse(
                    entity.getSubjectName(),
                    entity.getClassSection(),
                    entity.getSubjectAverage(),
                    entity.getOverallGrade(),
                    entity.getTotalAssessments(),
                    entity.getCompletedAssessments(),
                    completionRate
                );
            })
            .collect(Collectors.toList());
    }

    public List<MyProgressDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return myProgressRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public MyProgressDto.Response calculateAndSaveProgress(String studentId, String subjectName, String classSection,
                                                           Double subjectAverage, Double overallGrade,
                                                           Integer totalAssessments, Integer completedAssessments) {
        MyProgress progress = myProgressRepository.findByStudentIdAndSubjectNameAndClassSection(studentId, subjectName, classSection)
            .orElse(new MyProgress());

        progress.setStudentId(studentId);
        progress.setSubjectName(subjectName);
        progress.setClassSection(classSection);
        progress.setSubjectAverage(subjectAverage);
        progress.setOverallGrade(overallGrade);
        progress.setTotalAssessments(totalAssessments);
        progress.setCompletedAssessments(completedAssessments);
        progress.setLastUpdated(LocalDateTime.now());

        return toResponse(myProgressRepository.save(progress));
    }
}