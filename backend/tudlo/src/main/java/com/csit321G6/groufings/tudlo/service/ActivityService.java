package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ActivityDto;
import com.csit321G6.groufings.tudlo.entity.Activity;
import com.csit321G6.groufings.tudlo.repository.ActivityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService extends CrudService<Activity, ActivityDto.Request, ActivityDto.Response> {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
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