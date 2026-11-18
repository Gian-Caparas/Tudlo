package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ScoresDto;
import com.csit321G6.groufings.tudlo.entity.Scores;
import com.csit321G6.groufings.tudlo.repository.ScoresRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoresService extends CrudService<Scores, ScoresDto.Request, ScoresDto.Response> {

    private final ScoresRepository scoresRepository;

    public ScoresService(ScoresRepository scoresRepository) {
        this.scoresRepository = scoresRepository;
    }

    @Override
    protected ScoresRepository repository() {
        return scoresRepository;
    }

    @Override
    protected String entityName() {
        return "Scores";
    }

    @Override
    protected Scores newEntity() {
        return new Scores();
    }

    @Override
    protected void apply(Scores entity, ScoresDto.Request request) {
        entity.setStudentId(request.studentId());
        entity.setSubjectName(request.subjectName());
        entity.setClassSection(request.classSection());
        entity.setScore(request.score());
    }

    @Override
    protected ScoresDto.Response toResponse(Scores entity) {
        return new ScoresDto.Response(
            entity.getId(),
            entity.getStudentId(),
            entity.getSubjectName(),
            entity.getClassSection(),
            entity.getScore(),
            null // createdAt not in entity
        );
    }

    public List<ScoresDto.Response> getByStudentId(String studentId) {
        return scoresRepository.findByStudentId(studentId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ScoresDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return scoresRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ScoresDto.Response> getByStudentSubjectAndSection(String studentId, String subjectName, String classSection) {
        return scoresRepository.findByStudentSubjectAndSection(studentId, subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}