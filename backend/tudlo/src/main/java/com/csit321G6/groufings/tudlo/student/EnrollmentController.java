package com.csit321G6.groufings.tudlo.student;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController extends CrudController<EnrollmentDto.Request, EnrollmentDto.Response> {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, EnrollmentDto.Request, EnrollmentDto.Response> service() {
        return service;
    }
}
