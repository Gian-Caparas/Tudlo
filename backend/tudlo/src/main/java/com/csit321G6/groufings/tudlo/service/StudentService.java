package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.StudentDto;
import com.csit321G6.groufings.tudlo.entity.Student;
import com.csit321G6.groufings.tudlo.entity.User;
import com.csit321G6.groufings.tudlo.entity.UserType;
import com.csit321G6.groufings.tudlo.repo.StudentRepository;
import com.csit321G6.groufings.tudlo.repo.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService extends CrudService<Student, StudentDto.Request, StudentDto.Response> {

    private final StudentRepository repository;
    private final UserRepository userRepository;

    public StudentService(StudentRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    protected JpaRepository<Student, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Student";
    }

    @Override
    protected Student newEntity() {
        return new Student();
    }

    @Override
    protected void apply(Student entity, StudentDto.Request request) {
        entity.setUser(find(userRepository, request.userId(), "User"));
        if (entity.getUser().getUserType() != UserType.STUDENT) {
            throw new BadRequestException("User " + entity.getUser().getId() + " is not a STUDENT");
        }
        entity.setStudentNo(request.studentNo());
    }

    @Override
    protected StudentDto.Response toResponse(Student entity) {
        return new StudentDto.Response(entity.getId(), entity.getUser().getId(), entity.getStudentNo());
    }
}
