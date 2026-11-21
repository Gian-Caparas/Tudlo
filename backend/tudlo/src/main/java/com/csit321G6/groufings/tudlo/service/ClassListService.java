package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ClassListDto;
import com.csit321G6.groufings.tudlo.entity.ClassList;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repository.ClassListRepository;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassListService extends CrudService<ClassList, ClassListDto.Request, ClassListDto.Response> {

    private final ClassListRepository classListRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ClassListService(
            ClassListRepository classListRepository,
            UserRepository userRepository,
            UserService userService) {
        this.classListRepository = classListRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    protected ClassListRepository repository() {
        return classListRepository;
    }

    @Override
    protected String entityName() {
        return "ClassList";
    }

    @Override
    protected ClassList newEntity() {
        return new ClassList();
    }

    @Override
    protected void apply(ClassList entity, ClassListDto.Request request) {
        entity.setSubjectName(request.subjectName());
        entity.setClassSection(request.classSection());
        entity.setStudentId(request.studentId());
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
    }

    @Override
    protected void beforeCreate(ClassList entity, ClassListDto.Request request) {
        User student = userRepository.findByUserIdNumber(request.studentId())
                .orElseGet(() -> userService.registerStudent(
                        request.firstName(),
                        request.lastName(),
                        request.studentId(),
                        null
                ));
        if (student.getUserType() != UserType.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User ID " + request.studentId() + " already belongs to a non-student account"
            );
        }
    }

    @Override
    protected ClassListDto.Response toResponse(ClassList entity) {
        return new ClassListDto.Response(
            entity.getId(),
            entity.getSubjectName(),
            entity.getClassSection(),
            entity.getStudentId(),
            entity.getFirstName(),
            entity.getLastName(),
            null // createdAt not in entity
        );
    }

    @Transactional
    public List<ClassListDto.Response> bulkImport(ClassListDto.BulkImportRequest request) {
        return request.students().stream()
            .map(student -> {
                ClassListDto.Request itemRequest = new ClassListDto.Request(
                    request.subjectName(),
                    request.classSection(),
                    student.studentId(),
                    student.firstName(),
                    student.lastName()
                );
                return create(itemRequest);
            })
            .collect(Collectors.toList());
    }

    public List<ClassListDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return classListRepository.findBySubjectNameAndClassSection(subjectName, classSection)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ClassListDto.Response> getByStudentId(String studentId) {
        return classListRepository.findByStudentId(studentId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}