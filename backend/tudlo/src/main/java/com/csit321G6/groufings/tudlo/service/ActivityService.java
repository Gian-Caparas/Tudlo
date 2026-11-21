package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ActivityDto;
import com.csit321G6.groufings.tudlo.entity.Activity;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.entity.LearningGap;
import com.csit321G6.groufings.tudlo.entity.Scores;
import com.csit321G6.groufings.tudlo.repository.ActivityRepository;
import com.csit321G6.groufings.tudlo.repository.AssessmentRepository;
import com.csit321G6.groufings.tudlo.repository.LearningGapRepository;
import com.csit321G6.groufings.tudlo.repository.ScoresRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService extends CrudService<Activity, ActivityDto.Request, ActivityDto.Response> {

    private final ActivityRepository activityRepository;
    private final AssessmentRepository assessmentRepository;
    private final ScoresRepository scoresRepository;
    private final LearningGapRepository learningGapRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            AssessmentRepository assessmentRepository,
            ScoresRepository scoresRepository,
            LearningGapRepository learningGapRepository
    ) {
        this.activityRepository = activityRepository;
        this.assessmentRepository = assessmentRepository;
        this.scoresRepository = scoresRepository;
        this.learningGapRepository = learningGapRepository;
    }

    @Override
    protected ActivityRepository repository() {
        return activityRepository;
    }

    @Override
    protected String entityName() {
        return "Activity";
    }

    @Override
    protected Activity newEntity() {
        return new Activity();
    }

    @Override
    protected void apply(Activity entity, ActivityDto.Request request) {
        entity.setActivityName(request.activityName());
        entity.setStudentId(request.studentId());
        entity.setScore(request.score());
        entity.setSubjectName(request.subjectName());
        entity.setClassSection(request.classSection());
    }

    @Override
    @Transactional
    public ActivityDto.Response create(ActivityDto.Request request) {
        ActivityDto.Response response = super.create(request);
        syncScoresAndGaps(request.studentId(), request.subjectName(), request.classSection());
        return response;
    }

    @Override
    @Transactional
    public ActivityDto.Response update(Long id, ActivityDto.Request request) {
        ActivityDto.Response response = super.update(id, request);
        syncScoresAndGaps(request.studentId(), request.subjectName(), request.classSection());
        return response;
    }

    private void syncScoresAndGaps(String studentId, String subjectName, String classSection) {
        if (studentId == null || subjectName == null || classSection == null) return;

        List<Activity> studentActivities = activityRepository.findByStudentSubjectAndSection(studentId, subjectName, classSection);
        double totalScore = studentActivities.stream()
                .filter(a -> a.getScore() != null)
                .mapToDouble(Activity::getScore)
                .sum();

        List<Assessment> assessments = assessmentRepository.findBySubjectNameAndClassSection(subjectName, classSection);
        double totalMax = assessments.stream()
                .filter(a -> a.getMaxScore() != null)
                .mapToDouble(Assessment::getMaxScore)
                .sum();

        // 1. Update Scores record
        List<Scores> existingScores = scoresRepository.findByStudentIdAndSubjectNameAndClassSection(studentId, subjectName, classSection);
        Scores scoreEntity = existingScores.isEmpty() ? new Scores() : existingScores.get(0);
        scoreEntity.setStudentId(studentId);
        scoreEntity.setSubjectName(subjectName);
        scoreEntity.setClassSection(classSection);
        scoreEntity.setScore(totalScore);
        scoresRepository.save(scoreEntity);

        // 2. Update LearningGap record
        LearningGap gap = learningGapRepository.findByStudentIdAndSubjectNameAndClassSection(studentId, subjectName, classSection)
                .orElse(new LearningGap());
        gap.setStudentId(studentId);
        gap.setSubjectName(subjectName);
        gap.setClassSection(classSection);
        gap.setOverallScore(totalScore);
        gap.setMaxPossibleScore(totalMax);
        gap.setPercentage(totalMax > 0 ? (totalScore / totalMax) * 100 : 0.0);
        gap.calculateGapSeverity();
        gap.setLastCalculated(LocalDateTime.now());
        learningGapRepository.save(gap);
    }

    @Override
    protected ActivityDto.Response toResponse(Activity entity) {
        return new ActivityDto.Response(
            entity.getId(),
            entity.getActivityName(),
            entity.getStudentId(),
            entity.getScore(),
            entity.getSubjectName(),
            entity.getClassSection(),
            null // createdAt not in entity
        );
    }

    public List<ActivityDto.Response> getByStudentId(String studentId) {
        return activityRepository.findByStudentId(studentId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ActivityDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return activityRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ActivityDto.Response> getByStudentSubjectAndSection(String studentId, String subjectName, String classSection) {
        return activityRepository.findByStudentSubjectAndSection(studentId, subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}