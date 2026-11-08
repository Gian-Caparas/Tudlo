package com.csit321G6.groufings.tudlo.curriculum;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/learning-outcomes")
public class LearningOutcomeController extends CrudController<LearningOutcomeDto.Request, LearningOutcomeDto.Response> {

    private final LearningOutcomeService service;

    public LearningOutcomeController(LearningOutcomeService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, LearningOutcomeDto.Request, LearningOutcomeDto.Response> service() {
        return service;
    }
}
