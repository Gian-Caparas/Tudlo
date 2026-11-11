package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.TeacherDto;
import com.csit321G6.groufings.tudlo.service.TeacherService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController extends CrudController<TeacherDto.Request, TeacherDto.Response> {

    private final TeacherService service;

    public TeacherController(TeacherService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, TeacherDto.Request, TeacherDto.Response> service() {
        return service;
    }
}
