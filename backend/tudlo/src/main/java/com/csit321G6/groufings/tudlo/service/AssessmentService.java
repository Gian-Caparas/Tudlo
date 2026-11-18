package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.AssessmentDto;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.repository.AssessmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService extends CrudService<Assessment, AssessmentDto.Request, AssessmentDto.Response> {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    protected AssessmentRepository repository() {
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
    protected void apply(Assessment entity, AssessmentDto.Request request) {
        entity.setLearningOutcome(request.learningOutcome());
        entity.setMaxScore(request.maxScore());
        entity.setAssessmentItem(request.assessmentItem());
        entity.setSubjectName(request.subjectName());
        entity.setClassSection(request.classSection());
    }

    @Override
    protected AssessmentDto.Response toResponse(Assessment entity) {
        return new AssessmentDto.Response(
            entity.getId(),
            entity.getLearningOutcome(),
            entity.getMaxScore(),
            entity.getAssessmentItem(),
            entity.getSubjectName(),
            entity.getClassSection(),
            null // createdAt not in entity
        );
    }

    public List<AssessmentDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return assessmentRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}