package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.ItemScoreDto;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.entity.ItemScore;
import com.csit321G6.groufings.tudlo.entity.Submission;
import com.csit321G6.groufings.tudlo.repo.AssessmentItemRepository;
import com.csit321G6.groufings.tudlo.repo.ItemScoreRepository;
import com.csit321G6.groufings.tudlo.repo.SubmissionRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemScoreService extends CrudService<ItemScore, ItemScoreDto.Request, ItemScoreDto.Response> {

    private final ItemScoreRepository repository;
    private final SubmissionRepository submissionRepository;
    private final AssessmentItemRepository itemRepository;

    public ItemScoreService(
            ItemScoreRepository repository,
            SubmissionRepository submissionRepository,
            AssessmentItemRepository itemRepository) {
        this.repository = repository;
        this.submissionRepository = submissionRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    protected JpaRepository<ItemScore, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Item score";
    }

    @Override
    protected ItemScore newEntity() {
        return new ItemScore();
    }

    @Override
    protected void apply(ItemScore entity, ItemScoreDto.Request request) {
        entity.setSubmission(find(submissionRepository, request.submissionId(), "Submission"));
        entity.setItem(find(itemRepository, request.itemId(), "Assessment item"));
        entity.setPointsEarned(request.pointsEarned());
        entity.setMissing(request.missing());
    }

    @Override
    protected ItemScoreDto.Response toResponse(ItemScore entity) {
        return new ItemScoreDto.Response(
                entity.getId(),
                entity.getSubmission().getId(),
                entity.getItem().getId(),
                entity.getPointsEarned(),
                entity.isMissing());
    }
}
