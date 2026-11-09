package com.csit321G6.groufings.tudlo.curriculum;

import com.csit321G6.groufings.tudlo.classroom.ClassRoom;
import com.csit321G6.groufings.tudlo.classroom.ClassRoomRepository;
import com.csit321G6.groufings.tudlo.common.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TopicService extends CrudService<Topic, TopicDto.Request, TopicDto.Response> {

    private final TopicRepository repository;
    private final ClassRoomRepository classRoomRepository;

    public TopicService(TopicRepository repository, ClassRoomRepository classRoomRepository) {
        this.repository = repository;
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    protected JpaRepository<Topic, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Topic";
    }

    @Override
    protected Topic newEntity() {
        return new Topic();
    }

    @Override
    protected void apply(Topic entity, TopicDto.Request request) {
        entity.setClassRoom(find(classRoomRepository, request.classId(), "Class"));
        entity.setName(request.name());
    }

    @Override
    protected TopicDto.Response toResponse(Topic entity) {
        ClassRoom classRoom = entity.getClassRoom();
        return new TopicDto.Response(
                entity.getId(),
                classRoom.getId(),
                entity.getName());
    }
}
