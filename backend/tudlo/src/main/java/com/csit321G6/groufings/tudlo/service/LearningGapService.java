package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.dto.LearningGapDto;
import com.csit321G6.groufings.tudlo.entity.LearningGap;
import com.csit321G6.groufings.tudlo.repository.LearningGapRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningGapService {

    private final LearningGapRepository learningGapRepository;

    public LearningGapService(LearningGapRepository learningGapRepository) {
        this.learningGapRepository = learningGapRepository;
    }

    private LearningGapDto.Response toResponse(LearningGap entity) {
        String studentName = null;
        if (entity.getStudent() != null) {
            studentName = entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName();
        }
        return new LearningGapDto.Response(
            entity.getId(),
            entity.getStudentId(),
            studentName,
            entity.getSubjectName(),
            entity.getClassSection(),
            entity.getOverallScore(),
            entity.getMaxPossibleScore(),
            entity.getPercentage(),
            entity.getIsAtRisk(),
            entity.getGapSeverity(),
            entity.getLastCalculated()
        );
    }

    public List<LearningGapDto.Response> getAtRiskStudents(String subjectName, String classSection) {
        return learningGapRepository.findAtRiskStudentsOrderByPercentage(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<LearningGapDto.Response> getAllGapsBySubjectAndSection(String subjectName, String classSection) {
        return learningGapRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<LearningGapDto.Response> getByStudentId(String studentId) {
        return learningGapRepository.findByStudentId(studentId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<LearningGapDto.HeatmapResponse> getHeatmapData(String subjectName, String classSection) {
        return learningGapRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(gap -> {
                String studentName = gap.getStudent() != null
                    ? gap.getStudent().getFirstName() + " " + gap.getStudent().getLastName()
                    : "Unknown";
                return new LearningGapDto.HeatmapResponse(
                    gap.getStudentId(),
                    studentName,
                    gap.getPercentage(),
                    gap.getGapSeverity(),
                    gap.getIsAtRisk()
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public LearningGapDto.Response calculateAndSaveGap(String studentId, String subjectName, String classSection,
                                                       Double overallScore, Double maxPossibleScore) {
        LearningGap gap = learningGapRepository.findByStudentIdAndSubjectNameAndClassSection(studentId, subjectName, classSection)
            .orElse(new LearningGap());

        gap.setStudentId(studentId);
        gap.setSubjectName(subjectName);
        gap.setClassSection(classSection);
        gap.setOverallScore(overallScore);
        gap.setMaxPossibleScore(maxPossibleScore);

        if (maxPossibleScore != null && maxPossibleScore > 0) {
            gap.setPercentage((overallScore / maxPossibleScore) * 100);
        } else {
            gap.setPercentage(0.0);
        }

        gap.calculateGapSeverity();
        gap.setLastCalculated(LocalDateTime.now());

        return toResponse(learningGapRepository.save(gap));
    }

    @Transactional
    public void recalculateAllGapsForSubject(String subjectName, String classSection) {
        // This would be implemented with a batch job to recalculate all gaps
        // For now, placeholder for future implementation
    }
}