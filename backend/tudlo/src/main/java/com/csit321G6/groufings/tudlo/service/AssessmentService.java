package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.AssessmentDto;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.entity.ClassRoom;
import com.csit321G6.groufings.tudlo.repo.AssessmentRepository;
import com.csit321G6.groufings.tudlo.repo.ClassRoomRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AssessmentService extends CrudService<Assessment, AssessmentDto.Request, AssessmentDto.Response> {

    private final AssessmentRepository repository;
    private final ClassRoomRepository classRoomRepository;

    public AssessmentService(
            AssessmentRepository repository,
            ClassRoomRepository classRoomRepository) {
        this.repository = repository;
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    protected JpaRepository<Assessment, Long> repository() {
        return repository;
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
        entity.setClassRoom(find(classRoomRepository, request.classId(), "Class"));
        entity.setTitle(request.title());
        entity.setType(request.type());
        entity.setDueDate(request.dueDate());
    }

    @Override
    protected AssessmentDto.Response toResponse(Assessment entity) {
        ClassRoom classRoom = entity.getClassRoom();
        return new AssessmentDto.Response(
                entity.getId(),
                classRoom.getId(),
                entity.getTitle(),
                entity.getType(),
                entity.getDueDate());
    }
}
