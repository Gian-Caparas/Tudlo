package com.csit321G6.groufings.tudlo.submission;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController extends CrudController<SubmissionDto.Request, SubmissionDto.Response> {

    private final SubmissionService service;

    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, SubmissionDto.Request, SubmissionDto.Response> service() {
        return service;
    }
}
