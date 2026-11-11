package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.AssessmentItemDto;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.entity.AssessmentItem;
import com.csit321G6.groufings.tudlo.entity.LearningOutcome;
import com.csit321G6.groufings.tudlo.repo.AssessmentItemRepository;
import com.csit321G6.groufings.tudlo.repo.AssessmentRepository;
import com.csit321G6.groufings.tudlo.repo.LearningOutcomeRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AssessmentItemService
        extends CrudService<AssessmentItem, AssessmentItemDto.Request, AssessmentItemDto.Response> {

    private final AssessmentItemRepository repository;
    private final AssessmentRepository assessmentRepository;
    private final LearningOutcomeRepository outcomeRepository;

    public AssessmentItemService(
            AssessmentItemRepository repository,
            AssessmentRepository assessmentRepository,
            LearningOutcomeRepository outcomeRepository) {
        this.repository = repository;
        this.assessmentRepository = assessmentRepository;
        this.outcomeRepository = outcomeRepository;
    }

    @Override
    protected JpaRepository<AssessmentItem, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "AssessmentItem";
    }

    @Override
    protected AssessmentItem newEntity() {
        return new AssessmentItem();
    }

    @Override
    protected void apply(AssessmentItem entity, AssessmentItemDto.Request request) {
        entity.setAssessment(find(assessmentRepository, request.assessmentId(), "Assessment"));
        entity.setOutcome(find(outcomeRepository, request.outcomeId(), "LearningOutcome"));
        entity.setItemLabel(request.itemLabel());
        entity.setMaxPoints(request.maxPoints());
    }

    @Override
    protected AssessmentItemDto.Response toResponse(AssessmentItem entity) {
        return new AssessmentItemDto.Response(
                entity.getId(),
                entity.getAssessment().getId(),
                entity.getOutcome().getId(),
                entity.getItemLabel(),
                entity.getMaxPoints());
    }
}
