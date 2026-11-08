package com.csit321G6.groufings.tudlo.curriculum;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
public class TopicController extends CrudController<TopicDto.Request, TopicDto.Response> {

    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, TopicDto.Request, TopicDto.Response> service() {
        return service;
    }
}
