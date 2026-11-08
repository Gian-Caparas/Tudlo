package com.csit321G6.groufings.tudlo.assessment;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assessment-items")
public class AssessmentItemController extends CrudController<AssessmentItemDto.Request, AssessmentItemDto.Response> {

    private final AssessmentItemService service;

    public AssessmentItemController(AssessmentItemService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, AssessmentItemDto.Request, AssessmentItemDto.Response> service() {
        return service;
    }
}
