package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.SubjectDto;
import com.csit321G6.groufings.tudlo.entity.Subject;
import com.csit321G6.groufings.tudlo.entity.Teacher;
import com.csit321G6.groufings.tudlo.repository.SubjectRepository;
import com.csit321G6.groufings.tudlo.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService extends CrudService<Subject, SubjectDto.Request, SubjectDto.Response> {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    public SubjectService(SubjectRepository subjectRepository, TeacherRepository teacherRepository) {
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    protected SubjectRepository repository() {
        return subjectRepository;
    }

    @Override
    protected String entityName() {
        return "Subject";
    }

    @Override
    protected Subject newEntity() {
        return new Subject();
    }

    @Override
    protected void apply(Subject entity, SubjectDto.Request request) {
        entity.setSubjectName(request.subjectName());
        entity.setClassSection(request.classSection());

        Teacher teacher = teacherRepository.findById(request.teacherId())
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + request.teacherId()));
        entity.setTeacher(teacher);
    }

    @Override
    protected SubjectDto.Response toResponse(Subject entity) {
        return new SubjectDto.Response(
            entity.getId(),
            entity.getSubjectName(),
            entity.getClassSection(),
            entity.getTeacher() != null ? entity.getTeacher().getId() : null,
            entity.getTeacherName(),
            null // createdAt not in entity
        );
    }

    public List<SubjectDto.Response> getByTeacherId(Long teacherId) {
        return subjectRepository.findByTeacherId(teacherId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<SubjectDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return subjectRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}