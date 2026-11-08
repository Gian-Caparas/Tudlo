package com.csit321G6.groufings.tudlo.support;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support-activities")
public class SupportActivityController extends CrudController<SupportActivityDto.Request, SupportActivityDto.Response> {

    private final SupportActivityService service;

    public SupportActivityController(SupportActivityService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, SupportActivityDto.Request, SupportActivityDto.Response> service() {
        return service;
    }
}
