package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.CrudController;
import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.SubjectDto;
import com.csit321G6.groufings.tudlo.service.SubjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController extends CrudController<SubjectDto.Request, SubjectDto.Response> {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, SubjectDto.Request, SubjectDto.Response> service() {
        return service;
    }

    @GetMapping("/teacher/{teacherId}")
    public List<SubjectDto.Response> getByTeacherId(@PathVariable Long teacherId) {
        return service.getByTeacherId(teacherId);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<SubjectDto.Response> getBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getBySubjectAndSection(subjectName, classSection);
    }
}