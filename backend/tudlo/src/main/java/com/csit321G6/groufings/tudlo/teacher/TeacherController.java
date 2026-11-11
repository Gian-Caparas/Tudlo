package com.csit321G6.groufings.tudlo.teacher;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
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
