package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.ItemScoreDto;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.entity.ItemScore;
import com.csit321G6.groufings.tudlo.entity.Submission;
import com.csit321G6.groufings.tudlo.repo.AssessmentItemRepository;
import com.csit321G6.groufings.tudlo.repo.ItemScoreRepository;
import com.csit321G6.groufings.tudlo.repo.SubmissionRepository;
import com.csit321G6.groufings.tudlo.security.DataOwnershipPolicy;
import com.csit321G6.groufings.tudlo.security.RolePolicy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemScoreService extends CrudService<ItemScore, ItemScoreDto.Request, ItemScoreDto.Response> {

    private final ItemScoreRepository repository;
    private final SubmissionRepository submissionRepository;
    private final AssessmentItemRepository itemRepository;
    private final DataOwnershipPolicy ownershipPolicy;
    private final RolePolicy rolePolicy;

    public ItemScoreService(
            ItemScoreRepository repository,
            SubmissionRepository submissionRepository,
            AssessmentItemRepository itemRepository,
            DataOwnershipPolicy ownershipPolicy,
            RolePolicy rolePolicy) {
        this.repository = repository;
        this.submissionRepository = submissionRepository;
        this.itemRepository = itemRepository;
        this.ownershipPolicy = ownershipPolicy;
        this.rolePolicy = rolePolicy;
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
    protected List<ItemScore> filterForRead(List<ItemScore> scores) {
        var actor = rolePolicy.currentActor();
        return scores.stream().filter(score -> ownershipPolicy.canReadItemScore(actor, score)).toList();
    }

    @Override
    protected void beforeRead(ItemScore score) {
        ownershipPolicy.assertCanReadItemScore(rolePolicy.currentActor(), score);
    }

    @Override
    protected void beforeCreate(ItemScore score, ItemScoreDto.Request request) {
        assertWritableReferences(request);
    }

    @Override
    protected void beforeUpdate(ItemScore existing, ItemScoreDto.Request request) {
        ownershipPolicy.assertTeacherOwns(rolePolicy.currentActor(), existing);
        assertWritableReferences(request);
    }

    @Override
    protected void beforeDelete(ItemScore existing) {
        ownershipPolicy.assertTeacherOwns(rolePolicy.currentActor(), existing);
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

    private void assertWritableReferences(ItemScoreDto.Request request) {
        Submission submission = find(submissionRepository, request.submissionId(), "Submission");
        var item = find(itemRepository, request.itemId(), "Assessment item");
        if (!item.getAssessment().getId().equals(submission.getAssessment().getId())) {
            throw new BadRequestException("The score item must belong to the submission's assessment");
        }
        ownershipPolicy.assertTeacherOwns(rolePolicy.currentActor(), submission);
        ownershipPolicy.assertTeacherOwns(rolePolicy.currentActor(), item.getAssessment().getClassRoom());
    }
}
