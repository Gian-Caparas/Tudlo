package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.ClassRoomDto;
import com.csit321G6.groufings.tudlo.service.ClassRoomService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classes")
public class ClassRoomController extends CrudController<ClassRoomDto.Request, ClassRoomDto.Response> {

    private final ClassRoomService service;

    public ClassRoomController(ClassRoomService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, ClassRoomDto.Request, ClassRoomDto.Response> service() {
        return service;
    }
}
