package com.csit321G6.groufings.tudlo.assessment;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController extends CrudController<AssessmentDto.Request, AssessmentDto.Response> {

    private final AssessmentService service;

    public AssessmentController(AssessmentService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, AssessmentDto.Request, AssessmentDto.Response> service() {
        return service;
    }
}
