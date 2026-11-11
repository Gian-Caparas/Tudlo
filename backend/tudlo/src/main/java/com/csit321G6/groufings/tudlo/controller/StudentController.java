package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.StudentDto;
import com.csit321G6.groufings.tudlo.service.StudentService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController extends CrudController<StudentDto.Request, StudentDto.Response> {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, StudentDto.Request, StudentDto.Response> service() {
        return service;
    }
}
