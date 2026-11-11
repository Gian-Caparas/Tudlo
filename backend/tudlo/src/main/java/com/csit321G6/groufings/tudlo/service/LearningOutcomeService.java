package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.LearningOutcomeDto;
import com.csit321G6.groufings.tudlo.entity.LearningOutcome;
import com.csit321G6.groufings.tudlo.entity.Topic;
import com.csit321G6.groufings.tudlo.repo.LearningOutcomeRepository;
import com.csit321G6.groufings.tudlo.repo.TopicRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class LearningOutcomeService
        extends CrudService<LearningOutcome, LearningOutcomeDto.Request, LearningOutcomeDto.Response> {

    private final LearningOutcomeRepository repository;
    private final TopicRepository topicRepository;

    public LearningOutcomeService(
            LearningOutcomeRepository repository,
            TopicRepository topicRepository) {
        this.repository = repository;
        this.topicRepository = topicRepository;
    }

    @Override
    protected JpaRepository<LearningOutcome, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "LearningOutcome";
    }

    @Override
    protected LearningOutcome newEntity() {
        return new LearningOutcome();
    }

    @Override
    protected void apply(LearningOutcome entity, LearningOutcomeDto.Request request) {
        entity.setTopic(find(topicRepository, request.topicId(), "Topic"));
        entity.setDescription(request.description());
    }

    @Override
    protected LearningOutcomeDto.Response toResponse(LearningOutcome entity) {
        return new LearningOutcomeDto.Response(
                entity.getId(),
                entity.getTopic().getId(),
                entity.getDescription());
    }
}
